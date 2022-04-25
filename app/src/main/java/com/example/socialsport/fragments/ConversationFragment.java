package com.example.socialsport.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentConversationBinding;
import com.example.socialsport.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConversationFragment extends Fragment {

    private String name;

    private ArrayList<String> message;
    private FragmentConversationBinding binding;
    private User user, organiser;
    private String uuidOrga;
    private Boolean isConvExist = false;
    private String idConv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConversationBinding.inflate(inflater);
        View view = binding.getRoot();
        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(2,true);
        user = ((PrincipalPageActivity) requireActivity()).getUser();
        String currentUidUser = ((PrincipalPageActivity) requireActivity()).getUidUser();


        //Recuperation des infos
        Bundle bundle = getArguments();
        organiser= (User) bundle.getSerializable("organiser");
        uuidOrga = bundle.getString("uidorganiser");
        idConv = bundle.getString("idConv");


        //Quand on ouvre une conv a parti de l'activité
        if (organiser != null && uuidOrga!=null) {
            binding.name.setText(organiser.getName());
            if (organiser.getImage() != null) {
                byte[] bytes = Base64.decode(organiser.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.image.setImageBitmap(bitmap);
            }
        }

        //Quand on ouvre une conv a parti du menu message
        else if(idConv!=null){
            FirebaseDatabase.getInstance().getReference().child("conversation").child(idConv).child("participants").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String data = snapshot.getValue(String.class);
                        if (!data.equals(currentUidUser)) {
                        /*
                        User a = Utils.getUserFromDatabase(data);
                        viewHolder.txtName.setText(a.getName());
                        if(a.getImage()!=null){
                            byte[] bytes = Base64.decode(a.getImage() , Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            viewHolder.icon.setImageBitmap(bitmap);
                        }*/

                            FirebaseDatabase.getInstance().getReference().child("users").child(data).child("name").get().addOnCompleteListener(task -> {
                                binding.name.setText(task.getResult().getValue().toString());

                            });
                            FirebaseDatabase.getInstance().getReference().child("users").child(data).child("image").get().addOnCompleteListener(task -> {
                                if (task.getResult().getValue() != null) {
                                    String image = task.getResult().getValue().toString();
                                    byte[] bytes = Base64.decode(image, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    binding.image.setImageBitmap(bitmap);
                                }
                            });

                            FirebaseDatabase.getInstance().getReference().child("conversation").child(idConv).child("messages").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot a : snapshot.getChildren()) {
                                        Log.d("DEBUG456",a.getValue().toString());
                                        TextView textView = new TextView(getContext());
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(0, 0, 0, 20);
                                        textView.setText(a.getValue().toString());
                                        textView.setTextSize(15);
                                        textView.setPadding(30, 20, 30, 20);
                                        textView.setLayoutParams(params);
                                        isConvExist=true;
                                        binding.llConversation.addView(textView);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }

                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Log.d("debug123","NULL");
        }




/*
        for (String msg : message) {
            String[] a = msg.split("//");
            messages = a[0];
            sender = a[2];
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 20);
            textView.setText(messages);
            textView.setTextSize(15);
            textView.setPadding(30, 20, 30, 20);
            if (sender.equals("false")) {
                params.gravity = Gravity.START;
                textView.setTextColor(textView.getContext().getColor(R.color.black));
                textView.setBackgroundResource(R.drawable.tv_received_message);
            } else {
                params.gravity = Gravity.END;
                textView.setBackgroundResource(R.drawable.tv_send_message);
                textView.setTextColor(textView.getContext().getColor(R.color.white));
                textView.setLayoutParams(params);
            }
            textView.setLayoutParams(params);

            binding.llConversation.addView(textView);*/
      //  }
       // binding.name.setText(name);

        setListener();

        return view;
    }

    private void setListener(){
        binding.etMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (binding.etMessage.getRight() - binding.etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Toast.makeText(getActivity(),"YES", Toast.LENGTH_SHORT).show();
                        if(!binding.etMessage.getText().toString().trim().isEmpty()){
                            TextView textView = new TextView(getContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, 20);
                            textView.setText(binding.etMessage.getText().toString());
                            textView.setTextSize(15);
                            textView.setPadding(30, 20, 30, 20);
                            params.gravity = Gravity.END;
                            textView.setBackgroundResource(R.drawable.tv_send_message);
                            textView.setTextColor(textView.getContext().getColor(R.color.white));
                            textView.setLayoutParams(params);
                            binding.llConversation.addView(textView);
                            ArrayList<String> a = new ArrayList<>();
                            a.add(binding.etMessage.getText().toString());
                            addConvForUser(a);
                            binding.etMessage.setText("");
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }


    private void addConvForUser(ArrayList<String> msg){
        if(isConvExist){
            FirebaseDatabase.getInstance().getReference().child("conversation").child(idConv).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }else {
            DatabaseReference conv = FirebaseDatabase.getInstance().getReference().child("conversation");
            String mGroupId = conv.push().getKey();
            conv.child(mGroupId).child("messages").setValue(msg);
            ArrayList<String> particiapnt = new ArrayList<>();
            particiapnt.add(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            particiapnt.add(uuidOrga);
            conv.child(mGroupId).child("participants").setValue(particiapnt);
            ArrayList<String> tab = new ArrayList<>();
            tab.add(mGroupId);
            FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("conversation").setValue(tab);
            FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrga).child("conversation").setValue(tab);
            addConvInMessageFragment(mGroupId);
        }
    }

    private void addConvInMessageFragment(String idConv){
        ((PrincipalPageActivity) requireActivity()).getIdconv().add(idConv);
    }

    private void AddMessage(String messages){
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        textView.setText(messages);
        textView.setTextSize(15);
        textView.setPadding(30, 20, 30, 20);
   /*     if (sender.equals("false")) {
            params.gravity = Gravity.START;
            textView.setTextColor(textView.getContext().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.tv_received_message);
        } else {
            params.gravity = Gravity.END;
            textView.setBackgroundResource(R.drawable.tv_send_message);
            textView.setTextColor(textView.getContext().getColor(R.color.white));
            textView.setLayoutParams(params);
        }*/
        textView.setLayoutParams(params);

        binding.llConversation.addView(textView);
    }
}