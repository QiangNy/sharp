package com.cydroid.tpicture.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cydroid.tpicture.R;
import com.cydroid.tpicture.animal.Pig;
import com.cydroid.tpicture.utils.DswLog;

import java.util.List;

/**
 * Created by qiang on 4/11/18.
 */
public class FireAdapter extends BaseAdapter {

    private static final String TAG = "FireAdapter";
    private List<Pig> pigList;
    private Context ctx;
    private LayoutInflater mInflater;

    public FireAdapter(List<Pig> pigList, Context ctx) {
        this.pigList = pigList;
        this.ctx = ctx;
        this.mInflater = LayoutInflater.from(ctx);

    }

    @Override
    public int getCount() {
        DswLog.d(TAG, "pigList.size()="+pigList.size());
        return pigList.size();
    }

    @Override
    public Object getItem(int i) {
        return pigList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Pig mPig = pigList.get(i);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.result_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.cameraid = (TextView) convertView.findViewById(R.id.item_cameraid);
            viewHolder.datail = (TextView) convertView.findViewById(R.id.item_detail);
            viewHolder.success = (TextView) convertView.findViewById(R.id.item_success);
            viewHolder.tresult = (TextView) convertView.findViewById(R.id.item_testresult);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cameraid.setText(mPig.getCameraID());
        viewHolder.datail.setText(mPig.getDegreetoString());
        if (mPig.getSuccess() != 0) {
            viewHolder.success.setTextColor(Color.RED);
        }
        viewHolder.success.setText(mPig.getSuccesstoString());
        viewHolder.tresult.setText(mPig.getTestResult());

        return convertView;
    }

    static class ViewHolder {
        private TextView cameraid;
        private TextView datail;
        private TextView success;
        private TextView tresult;
    }
}
