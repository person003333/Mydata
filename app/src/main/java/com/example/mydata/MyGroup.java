package com.example.mydata;


import java.util.ArrayList;

public class MyGroup {
    public ArrayList<String> child;
    public String groupName;
    MyGroup(String name){
        groupName = name;
        child = new ArrayList<String>();
    }
    public String getName()
    {
        return groupName;
    }
    private boolean checkboxSelected;
    public boolean isCheckboxSelected()
    {
        return checkboxSelected;
    }
    public void setCheckboxSelected(boolean checkboxSelected)
    {
        this.checkboxSelected = checkboxSelected;
    }
}