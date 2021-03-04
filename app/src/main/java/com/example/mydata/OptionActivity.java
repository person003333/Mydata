package com.example.mydata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class OptionActivity extends Activity {

    ArrayList<ArrayList<Model>> childList;
    ArrayList<MyGroup> groups;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        Intent intent = getIntent();
        String model_name = intent.getStringExtra("model_name");
        String month_elec = intent.getStringExtra("month_elec");
        //String Co2 = intent.getStringExtra("Co2");
        String capacity = intent.getStringExtra("capacity");
        
        long volume = Long.parseLong(capacity);
        ArrayList<String> set_option = new ArrayList<>();


        Display newDisplay = getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();

        groups = new ArrayList<MyGroup>();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.mylist);
        MyGroup brand = new MyGroup("제조사");
        groups.add(brand);

        childList = new ArrayList<ArrayList<Model>>();
        ArrayList<Model> list = new ArrayList<Model>();

        Model m = new Model();
        m.setGroup("제조사");
        m.setName("삼성전자");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("LG전자");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("매직쉐프");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("롯데하이마트");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("위니아딤채");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("위니아전자");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("캐리어");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("키친에이드");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("하이얼");
        list.add(m);

        m = new Model();
        m.setGroup("제조사");
        m.setName("AEG");
        list.add(m);



        childList.add(list);
        //여기까지 브랜드


        MyGroup litter = new MyGroup("용량");
        groups.add(litter);
        list = new ArrayList<Model>();

        m = new Model();
        m.setGroup("용량");
        m.setName("801L 이상    (3~4인 가구용)");
        m.setId(4);
        list.add(m);

        m = new Model();
        m.setGroup("용량");
        m.setName("601L - 800L (2~3인 가구용)");
        m.setId(3);
        list.add(m);

        m = new Model();
        m.setGroup("용량");
        m.setName("401L - 600L (2인 가구용)");
        m.setId(2);
        list.add(m);

        m = new Model();
        m.setGroup("용량");
        m.setName("201L - 400L (1인 가구용)");
        m.setId(1);
        list.add(m);

        m = new Model();
        m.setGroup("용량");
        m.setName("    0L - 200L (1인 가구용)");
        m.setId(0);
        list.add(m);

        childList.add(list);
        //여기까지 용량

        MyGroup doortype = new MyGroup("도어수");
        groups.add(doortype);
        list = new ArrayList<Model>();

        m = new Model();
        m.setGroup("도어수");
        m.setName("1도어");
        list.add(m);

        m = new Model();
        m.setGroup("도어수");
        m.setName("2도어(일반)");
        list.add(m);

        m = new Model();
        m.setGroup("도어수");
        m.setName("2도어(양문)");
        list.add(m);

        m = new Model();
        m.setGroup("도어수");
        m.setName("3도어");
        list.add(m);

        m = new Model();
        m.setGroup("도어수");
        m.setName("4도어");
        list.add(m);

        childList.add(list);


        MyGroup option = new MyGroup("세부사항");
        groups.add(option);
        list = new ArrayList<Model>();

        m = new Model();
        m.setGroup("세부사항");
        m.setName("정수기형");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("전문변온실");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("김치전문보관");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("상냉장하냉동");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("아이스메이커");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("야채실");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("대용량바스켓");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("긴채소보관실");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("독립냉각");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("미세자동정온");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("무빙 바스켓");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("높이조절선반");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("접이식선반");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("슬라이드선반");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("탈취");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("제균");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("참숯");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("UV자외선");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("이온플라즈마");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("스마트진단");
        list.add(m);

        m = new Model();
        m.setGroup("세부사항");
        m.setName("Wifi");
        list.add(m);

        childList.add(list);




        ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), groups, childList);
        listView.setIndicatorBounds(width - 50, width); //이 코드를 지우면 화살표 위치가 바뀐다.
        listView.setAdapter(adapter);




        Button button_search = findViewById(R.id.sch_btn);
        button_search.setOnClickListener(v -> {

            /*for(int i =0; i<childList.size();i++){
                ArrayList<String> str;
                str = new ArrayList<>();
                for(int j = 0 ;j<childList.get(i).size();j++) {
                    if(childList.get(i).get(j).isCheckboxSelected())
                        //System.out.println(childList.get(i).get(j).getGroup() + childList.get(i).get(j).getName());
                        str.add(childList.get(i).get(j).getName());
                }
                if(str.size()!=0) {

                    System.out.println(childList.get(i).get(0).getGroup() + str);
                }

            }*/
            Intent intent2 = new Intent(getApplicationContext(), ResultActivity.class);
            for(int i =0; i<childList.size();i++) {
                String str;
                if (childList.get(i).get(0).getGroup() == "용량") {

                    str = "(";
                    int count = 0;
                    for (int j = 0; j < childList.get(i).size(); j++) {
                        if (childList.get(i).get(j).isCheckboxSelected()) {
                            if (count == 0) {
                                str = str + childList.get(i).get(j).getId();
                            } else {
                                str = str + ", " + childList.get(i).get(j).getId();
                            }
                            count++;
                        }

                    }
                    if (count == 0) {
                        str = str + (volume/200);
                    }
                    str = str + ")";

                }
                else if(childList.get(i).get(0).getGroup() == "세부사항"){
                    str = "'";
                    int count = 0;
                    for (int j = 0; j < childList.get(i).size(); j++) {
                        if (childList.get(i).get(j).isCheckboxSelected()) {
                            if (count == 0) {
                                str = str + "\"" + childList.get(i).get(j).getName() + "\"";
                            } else {
                                str = str + " \"" + childList.get(i).get(j).getName() + "\"";
                            }
                            set_option.add(childList.get(i).get(j).getName());
                            count++;
                        }

                    }
                    if (count == 0) {
                        str = "'"+"\"특징\" \"보관\" \"편의\" \"부가\"";
                    }
                    str = str + "'";
                }
                else {


                    str = "(";
                    int count = 0;
                    for (int j = 0; j < childList.get(i).size(); j++) {
                        if (childList.get(i).get(j).isCheckboxSelected()) {
                            if (count == 0) {
                                str = str + "'" + childList.get(i).get(j).getName() + "'";
                            } else {
                                str = str + ", '" + childList.get(i).get(j).getName() + "'";
                            }
                            count++;
                        }

                    }
                    if (count == 0) {
                        for (int j = 0; j < childList.get(i).size(); j++) {
                            if (count == 0) {
                                str = str + "'" + childList.get(i).get(j).getName() + "'";
                            } else {
                                str = str + ", '" + childList.get(i).get(j).getName() + "'";
                            }
                            count++;


                        }
                    }
                    str = str + ")";
                }

                System.out.println(childList.get(i).get(0).getGroup());
                System.out.println(str);

                intent2.putExtra(childList.get(i).get(0).getGroup(), str);

            }
            intent2.putExtra("set_option",set_option);

            intent2.putExtra("model_name",model_name);
            intent2.putExtra("month_elec",month_elec);
            //intent2.putExtra("Co2",Co2);
            intent2.putExtra("capacity",capacity);
            intent2.putExtra("sort_option","1");


            startActivity(intent2);
            //finish();

        });
    }
}
