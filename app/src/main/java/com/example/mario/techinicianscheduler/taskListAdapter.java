package com.example.mario.techinicianscheduler;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * An customized adapter to make the task list checkable.
 * Used when choose the task.
 */

public class TaskListAdapter extends BaseAdapter {
    List<Task> list = new ArrayList<Task>();
    private static SparseBooleanArray isSelected;
    Context context;
    TaskListAdapter.HolderView holderView = null;


    TaskListAdapter.CheckedAllListener mListener;

    public void setCheckedAllListener(TaskListAdapter.CheckedAllListener listener) {
        mListener = listener;
    }


    public TaskListAdapter(List<Task> list, Context context) {
        this.context = context;
        this.list = list;
        isSelected = new SparseBooleanArray();
        initData();
    }

    /**
     * initial data
     */
    private void initData() {
        for (int i = 0; i < list.size(); i++) {

            getIsSelected().put(i, false);

        }
    }


    public static SparseBooleanArray getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(SparseBooleanArray isSelected) {
        TaskListAdapter.isSelected = isSelected;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Task getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        if (view == null) {
            holderView = new TaskListAdapter.HolderView();
            //get the source file
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_task, parent, false);
            holderView.cb_task = (CheckBox) view.findViewById(R.id.cb_task);
            holderView.task_name = (TextView) view.findViewById(R.id.task_name);
            holderView.task_skill = (TextView) view.findViewById(R.id.task_skill);
            holderView.task_duration = (TextView) view.findViewById(R.id.task_duration);

            view.setTag(holderView);

        } else {
            holderView = (TaskListAdapter.HolderView) view.getTag();
        }

        final Task item = getItem(position);
        if (item != null) {
            holderView.task_name.setText(item.getName());                           //set Text for the checkbox;
            holderView.task_skill.setText("Skill Requirement:" + item.getSkillRequirement());
            holderView.task_duration.setText(item.getDuration() + "");
            holderView.cb_task.setChecked(isSelected.get(position));

        }
        /**
         * handle the onclick of each checkbox
         */
        holderView.cb_task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                //						holderView.cb_button.toggle();
                if (buttonView.isPressed()) {
                    isSelected.put(position, isChecked);
                    mListener.CheckAll(isSelected);
                }
            }
        });
        return view;
    }


    class HolderView {
        private CheckBox cb_task;
        private TextView task_name;
        private TextView task_duration;
        private TextView task_skill;
    }


    public interface CheckedAllListener {

        void CheckAll(SparseBooleanArray checkall);

    }
}
