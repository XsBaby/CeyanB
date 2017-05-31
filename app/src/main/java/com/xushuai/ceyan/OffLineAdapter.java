package com.xushuai.ceyan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class OffLineAdapter extends BaseAdapter {
    private List<Beans.AppBean> list;
    private Context context;

    public OffLineAdapter(List<Beans.AppBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.lv_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.off_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(list.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        TextView tv;
        CheckBox cb;
    }
}