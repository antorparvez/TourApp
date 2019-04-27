package com.example.mahmud.travelmate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.mahmud.travelmate.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpandableListViewCustomAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> listHeaders;
    private HashMap<String, ArrayList<String>> listAllChilds;

    public ExpandableListViewCustomAdapter(Context context, ArrayList<String> listHeaders,
                                           HashMap<String, ArrayList<String>> listAllChilds) {
        this.context = context;
        this.listHeaders = listHeaders;
        this.listAllChilds = listAllChilds;
    }

    @Override
    public int getGroupCount() {
        return listHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listAllChilds.get(listHeaders.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listAllChilds.get(listHeaders.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.group_header_exp_layout,parent,false);
        TextView listHeaderTV = convertView.findViewById(R.id.group_header_tv);
        listHeaderTV.setText(listHeaders.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.single_item_exp_layout,parent,false);
        TextView listChildTV = convertView.findViewById(R.id.single_list_item_tv);
        listChildTV.setText(listAllChilds.get(listHeaders.get(groupPosition)).get(childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
