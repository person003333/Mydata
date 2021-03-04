package com.example.mydata;

/**
 * @FileName  : Model.java
 * @Project     : ExpandableList
 * @Date         : 2011. 11. 25.
 * @작성자      : 이정모 Administrator
 * @변경이력 :
 * @프로그램 설명 : 하위 항목을 표현하는 Model
 */

public class Model {
    private long id;
    private String group;
    private String name;

    private boolean checkboxSelected;
    public boolean isCheckboxSelected()
    {
        return checkboxSelected;
    }
    public void setCheckboxSelected(boolean checkboxSelected)
    {
        this.checkboxSelected = checkboxSelected;
    }

    public String getGroup()
    {
        return group;
    }
    public void setGroup(String group)
    {
        this.group = group;
    }
    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
}