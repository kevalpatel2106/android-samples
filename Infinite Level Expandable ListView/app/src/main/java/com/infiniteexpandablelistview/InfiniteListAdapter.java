package com.infiniteexpandablelistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Keval on 16-Feb-17.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class InfiniteListAdapter extends BaseExpandableListAdapter {

    private final Context mContext;
    private final ArrayList<Subjects> mSubjects;

    public InfiniteListAdapter(Context context, ArrayList<Subjects> subjects) {
        mContext = context;
        mSubjects = subjects;
    }

    @Override
    public int getGroupCount() {
        return mSubjects.size();
    }

    @Override
    public int getChildrenCount(int groupItemPos) {
        return mSubjects.get(groupItemPos).getSubList() == null
                || mSubjects.get(groupItemPos).getSubList().size() == 0 ? 0 : 1;
    }

    @Override
    public Subjects getGroup(int groupItemPos) {
        return mSubjects.get(groupItemPos);
    }

    @Override
    public Subjects getChild(int groupItemPos, int childItemPos) {
        return mSubjects.get(groupItemPos).getSubList().get(childItemPos);
    }

    @Override
    public long getGroupId(int groupItemPos) {
        return groupItemPos;
    }

    @Override
    public long getChildId(int groupItemPos, int childItemPos) {
        return childItemPos;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupItemPos, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group, viewGroup, false);

            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mTextView = (TextView) convertView.findViewById(R.id.row_subject_title);

            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        Subjects parentItem = getGroup(groupItemPos);
        if (parentItem != null) {
            groupViewHolder.mTextView.setText(parentItem.getTitle());
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_child, viewGroup, false);

            childViewHolder = new ChildViewHolder();
            childViewHolder.mExpandableListView = (SecondLevelExpandableListView) convertView.findViewById(R.id.row_group_list);

            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        Subjects childItem = getChild(groupPos, childPos);
        if (childItem != null) {
            childViewHolder.mExpandableListView.setAdapter(new InfiniteListAdapter(mContext, getGroup(groupPos).getSubList()));
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupViewHolder {
        private TextView mTextView;
    }


    private class ChildViewHolder {
        private SecondLevelExpandableListView mExpandableListView;
    }
}
