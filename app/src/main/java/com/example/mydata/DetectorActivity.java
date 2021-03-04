/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mydata;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.example.mydata.customview.OverlayView;
import com.example.mydata.customview.OverlayView.DrawCallback;
import com.example.mydata.env.BorderedText;
import com.example.mydata.env.ImageUtils;
import com.example.mydata.env.Logger;
import com.example.mydata.tflite.Classifier;
import com.example.mydata.tflite.YoloV4Classifier;
import com.example.mydata.tracking.MultiBoxTracker;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    private static final int TF_OD_API_INPUT_SIZE = 416;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "yolov4-tiny-416-2.tflite";

    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labels.txt";

    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;

    private Classifier detector;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private BorderedText borderedText;

    private ImageView model;
    boolean model_search = true;
    boolean elect_search = true;
    boolean volume_search = true;

    private TextView model_name;
    private TextView month_elect;
    private TextView capacity;
    Bitmap finalSuber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model_name = findViewById(R.id.model_info);
        month_elect = findViewById(R.id.elect_info);
        capacity = findViewById(R.id.volume_info);


        tessBaseAPI = new TessBaseAPI();
        String dir = getFilesDir() + "/tesseract";
        if(checkLanguageFile(dir+"/tessdata"))
            tessBaseAPI.init(dir, "eng");
    }

    boolean checkLanguageFile(String dir)
    {
        File file = new File(dir);
        if(!file.exists() && file.mkdirs())
            createFiles(dir);
        else if(file.exists()){
            String filePath = dir + "/eng.traineddata";
            File langDataFile = new File(filePath);
            if(!langDataFile.exists())
                createFiles(dir);
        }
        return true;
    }

    private void createFiles(String dir)
    {
        AssetManager assetMgr = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetMgr.open("eng.traineddata");

            String destFile = dir + "/eng.traineddata";

            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        int cropSize = 416;

        try {
            detector =
                    YoloV4Classifier.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED);
            //detector = TFLiteObjectDetectionAPIModel.create(
//                    getAssets(),
//                    TF_OD_API_MODEL_FILE,
//                    TF_OD_API_LABELS_FILE,
//                    TF_OD_API_INPUT_SIZE,
//                    TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }

    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }



        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {

                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        Log.e("CHECK", "run: " + results.size());

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);

                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;
                        }

                        final List<Classifier.Recognition> mappedRecognitions =
                                new LinkedList<Classifier.Recognition>();




                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence) {
                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);

                                result.setLocation(location);
                                mappedRecognitions.add(result);
                            }
                            Log.e("title","Title : "+result.getTitle());


                            if(result.getTitle().equals("model") && model_search){
                                //search=false;
                                Bitmap suber=null;
                                int x = (int)result.getLocation().top;
                                int y = (int)result.getLocation().left;

                                //model = findViewById(R.id.model_view);

                                suber = Bitmap.createBitmap(rgbFrameBitmap,y,x,
                                        (int)result.getLocation().width(),(int)result.getLocation().height());
                                Bitmap finalSuber = GetRotatedBitmap(suber, 90);
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                //model.setImageBitmap(finalSuber);
                                                new model_AsyncTess().execute(finalSuber);
                                            }
                                        }
                                );
                            }

                            if(result.getTitle().equals("elect") && elect_search){
                                //search=false;
                                Bitmap suber=null;
                                int x = (int)result.getLocation().top;
                                int y = (int)result.getLocation().left;

                                //model = findViewById(R.id.model_view);

                                suber = Bitmap.createBitmap(rgbFrameBitmap,y,x,
                                        (int)result.getLocation().width(),(int)result.getLocation().height());
                                Bitmap finalSuber = GetRotatedBitmap(suber, 90);
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                //model.setImageBitmap(finalSuber);
                                                new elect_AsyncTess().execute(finalSuber);
                                            }
                                        }
                                );
                            }

                            if(result.getTitle().equals("volume") && volume_search){
                                //search=false;
                                Bitmap suber=null;
                                int x = (int)result.getLocation().top;
                                int y = (int)result.getLocation().left;

                                //model = findViewById(R.id.model_view);

                                suber = Bitmap.createBitmap(rgbFrameBitmap,y,x,
                                        (int)result.getLocation().width(),(int)result.getLocation().height());
                                finalSuber = GetRotatedBitmap(suber, 90);
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                //model.setImageBitmap(finalSuber);
                                                new volume_AsyncTess().execute(finalSuber);


                                            }
                                        }
                                );
                                //finalSuber = GetRotatedBitmap(suber,0);
                            }
                        }

                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        computingDetection = false;


                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {

                                        if((model_search||elect_search||volume_search)==false) {

                                            model_search = elect_search = volume_search = true;

                                            Intent intent_to = new Intent(getApplicationContext(), WriteActivity.class);
                                            intent_to.putExtra("model_name",model_name.getText().toString());
                                            intent_to.putExtra("month_elec",month_elect.getText().toString());
                                            intent_to.putExtra("capacity",capacity.getText().toString());

                                            startActivity(intent_to);
                                            finish();

                                        }


                                    }
                                });
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tfe_od_camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(() -> detector.setUseNNAPI(isChecked));
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }

    TessBaseAPI tessBaseAPI;
    TextView textView;

    private class model_AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result) {
            //완료 후 버튼 속성 변경 및 결과 출력
            //textView = findViewById(R.id.model_info);

            if(result.charAt(0)==':'&&(result.length()>1))
                result = result.substring(1);


            if (check_model(result)&& (result.length() >= model_name.getText().length())&&(result.length()>4)) {
                model_search=false;

                model_name.setText(result);
                //Toast.makeText(com.example.mydata.DetectorActivity.this, ""+result, Toast.LENGTH_LONG).show();
            }

        }
    }

    private class elect_AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result) {
            //완료 후 버튼 속성 변경 및 결과 출력

            if(check_elect(result)&&elect_search) {
                elect_search = false;

                //textView = findViewById(R.id.elect_info);
                month_elect.setText(result);
            }
            //Toast.makeText(com.example.mydata.DetectorActivity.this, ""+result, Toast.LENGTH_LONG).show();

        }
    }

    private class volume_AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result) {

            if (result.length() > 2) {

                char last = result.charAt(result.length() - 1);
                String volume = result.substring(0, result.length() - 1);
                //완료 후 버튼 속성 변경 및 결과 출력
                if (last == 'L' && check_volume(volume) && volume_search) {
                    volume_search = false;

                    //textView = findViewById(R.id.volume_info);
                    capacity.setText(volume);
                }
                //Toast.makeText(com.example.mydata.DetectorActivity.this, ""+result, Toast.LENGTH_LONG).show();

            }
        }
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    public static boolean check_volume(String s) {
        double d;
        char chrInput;
        for (int i = 0; i < s.length(); i++) {
            chrInput = s.charAt(i); // 입력받은 텍스트에서 문자 하나하나 가져와서 체크
            if (chrInput >= 0x30 && chrInput <= 0x39) {
                                // 숫자 OK!
            } else {
                return false;   // 영문자도 아니고 숫자도 아님!
            }
        }
        try {
            d=Double.parseDouble(s);
            if(d<80||d>1300)
                return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean check_elect(String s) {
        double d;
        char chrInput;
        for (int i = 0; i < s.length(); i++) {
            chrInput = s.charAt(i); // 입력받은 텍스트에서 문자 하나하나 가져와서 체크
            if (chrInput >= 0x30 && chrInput <= 0x39) {
                                // 숫자 OK!
            }
            else if (chrInput=='.') {
                // 숫자 OK!
            }
            else {
                return false;   // 영문자도 아니고 숫자도 아님!
            }
        }
        try {
            d = Double.parseDouble(s);
            if(d>60)
                return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean check_model(String textInput) {
        char chrInput;
        for (int i = 0; i < textInput.length(); i++) {
            chrInput = textInput.charAt(i); // 입력받은 텍스트에서 문자 하나하나 가져와서 체크

            if (chrInput >=0x41 && chrInput <= 0x5A) {
                // 영문(대문자) OK!
            }
            else if (chrInput =='-') {
                if (i==0)
                    return false;
                // - OK!
            }
            else if (chrInput >= 0x30 && chrInput <= 0x39) {
                if (i==0)
                    return false;
                // 숫자 OK!
            }
            else {
                return false;   // 영문자도 아니고 숫자도 아님!
            }
        }
        return true;

    }
}
