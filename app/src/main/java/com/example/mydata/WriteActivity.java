package com.example.mydata;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WriteActivity extends AppCompatActivity {

    EditText model_name;
    EditText month_elec;
    EditText Co2;
    EditText capacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        Intent intent = getIntent();
        String get_model_name = intent.getStringExtra("model_name");
        String get_month_elec = intent.getStringExtra("month_elec");
        //String get_Co2 = intent.getStringExtra("Co2");
        String get_capacity = intent.getStringExtra("capacity");




        model_name = (EditText)findViewById(R.id.model_name_edit);
        month_elec = (EditText)findViewById(R.id.month_elec_edit);
        //Co2 = (EditText)findViewById(R.id.Co2_edit);
        capacity = (EditText)findViewById(R.id.capacity_edit);

        if (get_model_name != null){
            model_name.setText(get_model_name);
        }
        if (get_month_elec != null){
            month_elec.setText(get_month_elec);
        }

        if (get_capacity != null){
            capacity.setText(get_capacity);
        }


        Button button_search = findViewById(R.id.send);
        button_search.setOnClickListener(v -> {

            if(model_name.getText().toString().getBytes().length <= 0){
                Toast.makeText(this.getApplicationContext(),"모델명을 입력해주세요.",Toast.LENGTH_SHORT).show();
            }
            else if(month_elec.getText().toString().getBytes().length <= 0){
                Toast.makeText(this.getApplicationContext(),"전력소비량을 입력해주세요.",Toast.LENGTH_SHORT).show();
            }
            else if(capacity.getText().toString().getBytes().length <= 0){
                Toast.makeText(this.getApplicationContext(),"용량을 입력해주세요.",Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent_to = new Intent(getApplicationContext(), OptionActivity.class);
                intent_to.putExtra("model_name", model_name.getText().toString());
                intent_to.putExtra("month_elec", month_elec.getText().toString());
                //intent_to.putExtra("Co2",Co2.getText().toString());
                intent_to.putExtra("capacity", capacity.getText().toString());

                startActivity(intent_to);
                finish();
            }
        });
    }
}
