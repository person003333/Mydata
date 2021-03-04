package com.example.mydata;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.AbsListView;
import android.widget.Toast;



public class ResultActivity extends AppCompatActivity implements AbsListView.OnScrollListener{

    private static final String TAG = "phptest";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_company = "company";
    private static final String TAG_cool = "cool";
    private static final String TAG_freeze = "freeze";

    private static final String TAG_model = "model";
    private static final String TAG_date = "date";
    private static final String TAG_door = "door";
    private static final String TAG_capacity = "capacity";
    private static final String TAG_elect = "elect";
    private static final String TAG_image = "img";

    private static final String TAG_speck = "speck";
    private static final String TAG_elect_year = "elect_year";
    private static final String TAG_price = "price";



    private TextView mTextViewResult;
    ArrayList<HashMap<String, Object>> mArrayList;
    ListView mListViewList;

    String mJsonString;
    SimpleAdapter adapter;

    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    public int page = 0;                           // 페이징변수. 초기 값은 0 이다.
    private final int OFFSET = 20;                  // 한 페이지마다 로드할 데이터 갯수.
    private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private boolean check_last = true;              //마지막 데이터인지 확인하는 변수

    TextView my_model;
    TextView my_elect;
    TextView my_volume;
    TextView my_pay;

    String sort;


    private RadioGroup radioGroup;
    RadioButton rb1;
    RadioButton rb2;
    RadioButton rb3;
    RadioButton rb4;
    ArrayList<String> set_option;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Intent intent = getIntent();//받는다
        String brand = intent.getStringExtra("제조사");
        String capacity = intent.getStringExtra("용량");
        String door = intent.getStringExtra("도어수");
        String option = intent.getStringExtra("세부사항");

        String model = intent.getStringExtra("model_name");
        String month_elect = intent.getStringExtra("month_elec");
        String volume = intent.getStringExtra("capacity");

        String sort_option = intent.getStringExtra("sort_option");

        set_option = (ArrayList<String>) intent.getSerializableExtra("set_option");









        my_model = findViewById(R.id.my_model);
        my_elect = findViewById(R.id.my_elect);
        my_volume = findViewById(R.id.my_volume);
        my_pay = findViewById(R.id.my_pay);

        my_model.setText(model);
        my_elect.setText(month_elect);
        my_volume.setText(volume);

        float my_ep = Float.parseFloat(month_elect);
        my_ep = my_ep * 12 *160;
        String m_pay = Integer.toString(Math.round(my_ep/1000)*1000);

        my_pay.setText(m_pay);


        //mTextViewResult = findViewById(R.id.textView_main_result);
        mListViewList = findViewById(R.id.listView_main_list);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        progressBar.setVisibility(View.VISIBLE);

        mListViewList.requestFocusFromTouch();
        mListViewList.deferNotifyDataSetChanged();

        GetData task = new GetData();

        System.out.println(brand);
        System.out.println(capacity);
        System.out.println(door);
        //mListViewList.setAdapter(adapter);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        rb1 = findViewById(R.id.rg_btn1);
        rb2 = findViewById(R.id.rg_btn2);
        rb3 = findViewById(R.id.rg_btn3);
        rb4 = findViewById(R.id.rg_btn4);

        switch (sort_option){
            case "1":
                rb1.setChecked(true);
                sort = "order by 가성비*1";
                break;
            case "2":
                rb2.setChecked(true);
                sort = "order by 소비전력*1";
                break;
            case "3":
                rb3.setChecked(true);
                sort = "order by 가격*1";
                break;
            case "4":
                rb4.setChecked(true);
                sort = "";
                break;
        }

        Log.d(TAG, "sort - " + sort);

        task.execute(brand,capacity,door,option,sort);


        mListViewList.setOnScrollListener(this);

        mArrayList = new ArrayList<>();



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            String so;
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                if(i == R.id.rg_btn1){
                    so = "1";

                }
                else if(i == R.id.rg_btn2){
                    so = "2";
                }

                else if(i == R.id.rg_btn3){
                    so = "3";
                }
                else if(i == R.id.rg_btn4){
                    so = "4";
                }


                Intent intent2 = new Intent(getApplicationContext(), ResultActivity.class);

                intent2.putExtra("제조사",brand);
                intent2.putExtra("용량",capacity);
                intent2.putExtra("도어수",door);
                intent2.putExtra("세부사항",option);

                intent2.putExtra("model_name",model);
                intent2.putExtra("month_elec",month_elect);
                //intent2.putExtra("Co2",Co2);
                intent2.putExtra("capacity",volume);
                intent2.putExtra("sort_option",so);
                intent2.putExtra("set_option",set_option);


                startActivity(intent2);
                finish();
            }

        });



    }



    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<String, Void, String> {


        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressDialog = ProgressDialog.show(ResultActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            page=0;

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null||result==""||result.length()<2){
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(),"검색 결과가 없습니다.",Toast.LENGTH_SHORT).show();
                //mTextViewResult.setText(errorString);
            }
            else {
                mJsonString = result;


                showResult();


            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];
            String searchKeyword3 = params[2];
            String searchKeyword4 = params[3];
            String searchKeyword5 = params[4];

            String serverURL = "youraddress";
            String postParameters = "brand=" + searchKeyword1 + "&door=" + searchKeyword3 + "&capacity=" + searchKeyword2 + "&option="+searchKeyword4 + "&sort="+searchKeyword5;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class toData extends AsyncTask<String, Void, String> {


        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ResultActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null||result==""||result.length()<2){
                progressBar.setVisibility(View.GONE);
            }
            else {

            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "youradress";
            String postParameters = "model=" + searchKeyword1;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(1000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            if(page*OFFSET>jsonArray.length()){
                check_last = false;
                page--;
                return;
            }
            for(int i=0;i<20;i++){

                if(((page * OFFSET) + i)>=jsonArray.length()) {
                    check_last = false;
                    break;
                }

                JSONObject item = jsonArray.getJSONObject((page * OFFSET) + i);

                String company = item.getString(TAG_company);
                String model = item.getString(TAG_model);
                String date = item.getString(TAG_date);
                String door = item.getString(TAG_door);
                String capacity = item.getString(TAG_capacity);
                String cool = item.getString(TAG_cool);
                String freeze = item.getString(TAG_freeze);
                String elect = item.getString(TAG_elect);
                Bitmap img = StringToBitmap(item.getString(TAG_image));
                String speck = item.getString(TAG_speck);
                String price = item.getString(TAG_price);

                float ep = Float.parseFloat(elect);
                ep = ep * 12 *160;

                String pay = Integer.toString(Math.round(ep/1000)*1000);


                HashMap<String,Object> hashMap = new HashMap<>();
                //HashMap<String,Bitmap> bdMap = new HashMap<>();

                hashMap.put(TAG_company, company);
                hashMap.put(TAG_model, model);
                hashMap.put(TAG_date, date);
                hashMap.put(TAG_door, door);
                hashMap.put(TAG_capacity, capacity);
                hashMap.put(TAG_cool, cool);
                hashMap.put(TAG_freeze, freeze);
                hashMap.put(TAG_elect, elect);
                hashMap.put(TAG_image, img);
                hashMap.put(TAG_speck, speck);
                hashMap.put(TAG_elect_year, pay);
                hashMap.put(TAG_price, price);


                mArrayList.add(hashMap);


            }


            adapter = new SimpleAdapter(
                    ResultActivity.this, mArrayList, R.layout.item_list,
                    new String[]{TAG_company,TAG_model,TAG_date,TAG_door,TAG_capacity,TAG_cool,TAG_freeze,TAG_elect,TAG_image,TAG_speck,TAG_elect_year,TAG_price},
                    new int[]{R.id.textView_list_company, R.id.textView_list_model,
                            R.id.textView_list_date, R.id.textView_list_door, R.id.textView_list_capacity, R.id.textView_list_cool, R.id.textView_list_freeze, R.id.textView_list_elect,R.id.list_image, R.id.textView_list_speck,R.id.textView_list_elect_year,R.id.textView_list_price}

            ){
                @Override
                public View getView (int position, View convertView, ViewGroup parent)
                {
                    View v = super.getView(position, convertView, parent);

                    TextView wordId = v.findViewById(R.id.textView_list_model);

                    String url = wordId.getText().toString();

                    Button b=(Button)v.findViewById(R.id.button_list);
                    b.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View view) {
                            toData todata = new toData();
                            todata.execute(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://search.danawa.com/dsearch.php?k1="+ url +"&module=goods&act=dispMain"));
                            startActivity(intent);
                        }
                    });




                    return v;
                }
            };

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(check_last) {
                        page++;
                        adapter.notifyDataSetChanged();
                        mLockListView = false;
                    }
                    progressBar.setVisibility(View.GONE);
                }
            },500);



            adapter.setViewBinder(new SimpleAdapter.ViewBinder(){

                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if( (view.getId()==R.id.list_image) & (data instanceof Bitmap) ) {
                        ImageView iv = (ImageView) view;
                        Bitmap bm = (Bitmap) data;
                        iv.setImageBitmap(bm);
                        return true;
                    }
                    else if((view.getId()==R.id.textView_list_speck)){

                        TextView option = (TextView) view;
                        String content = (String)data;
                        SpannableString spannableString = new SpannableString(content);
                        option.setText(content);
                        for(int i=0;i<set_option.size();i++){
                            String word = set_option.get(i);
                            int start = content.indexOf(word);
                            int end = start + word.length();
                            if(start > 0) {

                                spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#FFFF00")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                option.setText(spannableString);
                            }

                        }
                        return true;

                    }
                    return false;

                }


            });

            mListViewList.setAdapter(adapter);



            /*
            Button OptionButton = (Button) mListViewList.findViewById(R.id.button_list);
                OptionButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.naver.com"));
                        startActivity(intent);

                    }
                });
             */





        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }



    }


    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
        // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
        // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
        // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false && check_last) {
            // 화면이 바닦에 닿을때 처리
            int i = mListViewList.getLastVisiblePosition();


            // 로딩중을 알리는 프로그레스바를 보인다.
            progressBar.setVisibility(View.VISIBLE);

            // 다음 데이터를 불러온다.
           showResult();
           mListViewList.setSelection(i);


        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // firstVisibleItem : 화면에 보이는 첫번째 리스트의 아이템 번호.
        // visibleItemCount : 화면에 보이는 리스트 아이템의 갯수
        // totalItemCount : 리스트 전체의 총 갯수
        // 리스트의 갯수가 0개 이상이고, 화면에 보이는 맨 하단까지의 아이템 갯수가 총 갯수보다 크거나 같을때.. 즉 리스트의 끝일때. true
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

}