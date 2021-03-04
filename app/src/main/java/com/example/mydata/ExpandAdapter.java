package com.example.mydata;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;


public class ExpandAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<MyGroup> groups;
    private ArrayList<ArrayList<Model>> children;
    private LayoutInflater inflater;

    public ExpandAdapter(Context context, ArrayList<MyGroup> gropus, ArrayList<ArrayList<Model>> children)
    {
        this.groups = gropus;
        this.children = children;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public ArrayList<ArrayList<Model>> getAllList()
    {
        return this.children;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override
    public Model getChild(int groupPosition, int childPosition)
    {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return children.get(groupPosition).get(childPosition).getId();
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final Model model = getChild(groupPosition, childPosition);
        MyGroup group = getGroup1(groupPosition);
        View view = convertView;

        if (view == null)
        {
            view = inflater.inflate(R.layout.child_row, null);

        }
        if (model != null)
        {
            TextView childName = (TextView) view.findViewById(R.id.childName);
            CheckBox cb = (CheckBox) view.findViewById(R.id.check1);
            OnCheckedChangeListener chHandler = new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean flag)
                {
                    if (flag)
                    {
                        model.setCheckboxSelected(true);
                        System.out.println(model.getGroup()+" "+model.getName());
                    } else
                    {
                        model.setCheckboxSelected(false);
                        group.setCheckboxSelected(false);
                        notifyDataSetChanged();

                    }
                }
            };
            cb.setOnCheckedChangeListener(chHandler);
            childName.setText(model.getName());
            cb.setChecked(model.isCheckboxSelected());
        }

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return children.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition)
    {
        // TODO Auto-generated method stub
        return groups.get(groupPosition).getName();
    }

    public MyGroup getGroup1(int groupPosition)
    {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        // TODO Auto-generated method stub
        return groups.size();
    }
    @Override
    public long getGroupId(int groupPosition)
    {// TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        final boolean[] check_all = {true};
        final boolean[] check_sub = {true};
        View view = convertView;
        String group = (String) getGroup(groupPosition);
        MyGroup group_model = getGroup1(groupPosition);
        if (view == null)
        {
            view = inflater.inflate(R.layout.group_row, null);

        }

        TextView groupName = (TextView) view.findViewById(R.id.groupName);
        CheckBox cb1 = (CheckBox) view.findViewById(R.id.filter_select_all);
        OnCheckedChangeListener chHandler1 = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean flag)
            {
                if (flag)
                {
                    group_model.setCheckboxSelected(true);

                    for (int i = 0;i<getChildrenCount(groupPosition);i++){
                        Model child = getChild(groupPosition,i);
                        child.setCheckboxSelected(true);
                        notifyDataSetChanged();
                    }

                } else
                {
                    group_model.setCheckboxSelected(false);
                    for (int i = 0;i<getChildrenCount(groupPosition);i++){
                        Model child = getChild(groupPosition,i);
                        if(check_all[0] == true){

                            if(child.isCheckboxSelected()==false) {
                                check_all[0] = false;
                                break;
                            }
                        }

                    }
                    if(check_all[0] == true) {
                        for (int i = 0; i < getChildrenCount(groupPosition); i++) {
                            Model child = getChild(groupPosition, i);
                            child.setCheckboxSelected(false);
                            notifyDataSetChanged();
                        }
                    }
                }
            }
        };

        cb1.setOnCheckedChangeListener(chHandler1);
        cb1.setChecked(group_model.isCheckboxSelected());



        TextView tv = (TextView) view.findViewById(R.id.groupName);
        tv.setText(group);
        return view;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        // TODO Auto-generated method stub
        return true;
    }
}
