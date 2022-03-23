package com.example.socialsport;

import android.annotation.SuppressLint;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListAdapter extends BaseAdapter {
    Context context;
    private final Map<String, ArrayList<String>> nameMessage;
    private final int[] images;

    public ListAdapter(Context context, Map<String, ArrayList<String>> nameMessage, int[] images) {
        this.context = context;
        this.nameMessage = nameMessage;
        this.images = images;
    }

    @Override
    public int getCount() {
        return nameMessage.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_message, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.tv_name1);
            viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.tv_message);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.sender = (ImageView) convertView.findViewById(R.id.sender);
            viewHolder.txtTime = convertView.findViewById(R.id.time);
            viewHolder.txtDate = convertView.findViewById(R.id.date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Object firstKey = nameMessage.keySet().toArray()[position];

        ArrayList<String> array = nameMessage.get(firstKey);

        assert array != null;
        String[] array1 = array.get(array.size() - 1).split("//");
        String msg = array1[0];
        String[] array2 = array1[1].split(" ");
        String date = array2[0];
        String time = array2[1];
        String sender = array1[2];


        Log.d("SENDER", "" + sender);
        if (sender.equals("true")) {
            viewHolder.sender.setImageResource(R.drawable.img_message_send);
        } else {
            viewHolder.sender.setImageResource(R.drawable.img_message_received_2);
        }
        viewHolder.txtName.setText(firstKey.toString());
        viewHolder.txtMessage.setText(msg);
        viewHolder.txtTime.setText(time);
        viewHolder.txtDate.setText(date);
        viewHolder.icon.setImageResource(images[position]);

        return convertView;
    }


    private static class ViewHolder {
        TextView txtName;
        TextView txtMessage;
        TextView txtTime;
        TextView txtDate;

        ImageView icon;
        ImageView sender;
    }

}
