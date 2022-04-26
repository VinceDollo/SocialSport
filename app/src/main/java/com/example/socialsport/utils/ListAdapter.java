package com.example.socialsport.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class ListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<String> idConv;
    private final int[] images;
    private String currentUser;

    public ListAdapter(Context context, ArrayList<String> idConv, int[] images, String currentUser) {
        this.context = context;
        this.idConv = idConv;
        this.images = images;
        this.currentUser=currentUser;
    }

    @Override
    public int getCount() {
        return idConv.size();
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

        String firstKey = (String) idConv.get(position);
        Log.d("DEBUG123",  idConv.toString());

        final String[] name = {""};
        final String[] image = {""};

        FirebaseDatabase.getInstance().getReference().child("conversation").child(firstKey).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String data = snapshot.getValue(String.class);
                    if(!data.equals(currentUser)){
                        FirebaseDatabase.getInstance().getReference().child("users").child(data).child("name").get().addOnCompleteListener(task ->{
                            name[0] = task.getResult().getValue().toString();
                            viewHolder.txtName.setText(name[0]);

                        }) ;
                        FirebaseDatabase.getInstance().getReference().child("users").child(data).child("image").get().addOnCompleteListener(task ->{
                            if(task.getResult().getValue()!=null){
                                image[0] = task.getResult().getValue().toString();
                                byte[] bytes = Base64.decode(image[0] , Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                viewHolder.icon.setImageBitmap(bitmap);
                            }
                        }) ;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            };

        });
       /* ArrayList<String> array = nameMessage.get(firstKey);

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
        }*/
       // viewHolder.txtName.setText(firstKey);
       // viewHolder.txtMessage.setText(msg);
      //  viewHolder.txtTime.setText(time);
      //  viewHolder.txtDate.setText(date);
     //   viewHolder.icon.setImageResource(images[position]);

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
