package com.example.venture.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.models.Event;
import com.example.venture.models.People;

import java.util.ArrayList;
import java.util.List;

public class PeopleExpandableAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "PeopleExpandableAdapter";

    private List<String> mGroup;
    private List<People> mPeople;
    private Context mContext;

    public PeopleExpandableAdapter(Context mContext, List<People> mPeople) {
        mGroup = new ArrayList<>();
        mGroup.add("People");
        this.mPeople = mPeople;
        this.mContext = mContext;
    }

    public void setmPeople(List<People> people) {
        mPeople = people;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int i) {
        return mPeople.size();
    }

    @Override
    public Object getGroup(int i) {
        return mGroup.get(0);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mPeople.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

//        if (b) {
//            groupHolder.img.setImageResource(R.drawable.group_down);
//        } else {
//            groupHolder.img.setImageResource(R.drawable.group_up);
//        }
        String group = (String) getGroup(i);
        String finalTitle = group + " (" + getChildrenCount(i) +")";
        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.layout_group, null);

        }
        TextView groupText = view.findViewById(R.id.group);
        groupText.setText(finalTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        People person = (People) getChild(i, i1);
        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.layout_list_people, null);
        }
        TextView personName = view.findViewById(R.id.personName);
        personName.setText(person.getUserName());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


}
