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
 * Created by mario on 2017/2/7.
 */

public class MyListAdapter extends BaseAdapter {
    List<TechnicianInfo> list = new ArrayList<TechnicianInfo>();
    private static SparseBooleanArray isSelected;/**用SparseBooleanArray来代替map**/
    Context context;
    HolderView holderView = null;


    CheckedAllListener mListener;

    public void setCheckedAllListener(CheckedAllListener listener) {
        mListener = listener;
    }


    public MyListAdapter(List<TechnicianInfo> list, Context context) {
        this.context = context;
        this.list = list;
        isSelected = new SparseBooleanArray();
        initData();
    }

    /**
     * initial data
     */
    private void initData()
    {
        for (int i = 0; i < list.size(); i++) {

            getIsSelected().put(i, false);

        }
    }


    public static SparseBooleanArray getIsSelected()
    {
        return isSelected;
    }

    public static void setIsSelected(SparseBooleanArray isSelected) {
        MyListAdapter.isSelected = isSelected;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public TechnicianInfo getItem(int position) {
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
            holderView = new HolderView();
            //get the source file
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item, parent, false);
            holderView.cb_button = (CheckBox)view.findViewById(R.id.cb_button);
            holderView.tv_name = (TextView)view.findViewById(R.id.tv_name);
            view.setTag(holderView);

        }
        else {
            holderView = (HolderView)view.getTag();
        }

        final TechnicianInfo item = getItem(position);
        if (item != null) {
            holderView.tv_name.setText(item.getFirstName());                            //set Text for the checkbox;
            holderView.cb_button.setChecked(isSelected.get(position));

        }
        /**
         * handle the onclick of each checkbox
         */
        holderView.cb_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                //						holderView.cb_button.toggle();
                if (buttonView.isPressed()) {

                    isSelected.put(position,isChecked);
                    //监听回调，是否改变全选按钮的状态
                    mListener.CheckAll(isSelected);
                }
            }
        });
        return view;
    }


    class HolderView
    {
        private CheckBox cb_button;
        private TextView tv_name;
    }

    /**
     * 当所有CheckBox全选时回调
     * @author Administrator
     *
     */
    public interface CheckedAllListener
    {

        void CheckAll(SparseBooleanArray checkall);

    }

}
