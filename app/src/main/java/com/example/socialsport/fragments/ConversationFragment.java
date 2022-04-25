package com.example.socialsport.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.socialsport.entities.Message;
import com.example.socialsport.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

public class ConversationFragment extends Fragment {

    private String name;

    private ArrayList<String> message;
    private FragmentConversationBinding binding;
    private User user, organiser;
    private String uuidOrga;
    private String idConv, currentUidUser;
    private SimpleDateFormat formatter;
    private Date today;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConversationBinding.inflate(inflater);
        View view = binding.getRoot();
        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(2,true);
        user = ((PrincipalPageActivity) requireActivity()).getUser();
        currentUidUser = ((PrincipalPageActivity) requireActivity()).getUidUser();
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        today = new Date();

        //Recuperation des infos
        Bundle bundle = getArguments();
        organiser= (User) bundle.getSerializable("organiser");
        uuidOrga = bundle.getString("uidorganiser");
        Log.d("DEBUG000","uuidOrga : " + uuidOrga);
        Log.d("DEBUG000","UserId(me) : " + currentUidUser);

        if (uuidOrga != null) {
            retriveIdConv();
            Handler handler = new Handler();
            handler.postDelayed(() -> displayConvFromActivity(), 200);
        }else{
            idConv=bundle.getString("idConv");
            displayConvFromMessageFragment();
        }


        //Quand on ouvre une conv a parti de l'activité

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
                            addConvForUser(binding.etMessage.getText().toString(),formatter.format(today),currentUidUser);
                            binding.etMessage.setText("");
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }


    //Permet d'ajouter la conv dans la bdd si elle n'existe pas
    private void addConvForUser(String msg, String date, String sender) {
        Message message = new Message(msg, date, sender);
        ArrayList<Message> a = new ArrayList<>();
        a.add(message);

        DatabaseReference conv = FirebaseDatabase.getInstance().getReference().child("conversation");
        idConv = conv.push().getKey();
        conv.child(idConv).child("messages").setValue(a);
        ArrayList<String> particiapnt = new ArrayList<>();

        particiapnt.add(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        particiapnt.add(uuidOrga);
        conv.child(idConv).child("participants").setValue(particiapnt);

        addConvInMessageFragment(idConv);
        Log.d("DEBUG000",((PrincipalPageActivity) requireActivity()).getIdconv().toString());

        FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("conversation").setValue(((PrincipalPageActivity) requireActivity()).getIdconv());

        ArrayList<String> listConv = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrga).child("conversation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String data = snapshot.getValue(String.class);
                    listConv.add(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            };
        });
        Handler handler = new Handler();
        handler.postDelayed(() ->FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("conversation").setValue(listConv)
        , 200);


    }

    //Ajoute l'id de la conv dans principal page
    private void addConvInMessageFragment(String idConv){
        ((PrincipalPageActivity) requireActivity()).getIdconv().add(idConv);
    }

    //Permet de retrouver l'id de la conv commune si elle existe
    private void retriveIdConv(){
        HashSet<String> idConvs = new HashSet();
        final String[] realIdConv = {""};
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUidUser).child("conversation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("DEBUG000",snapshot.getValue(String.class));
                    idConvs.add(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrga).child("conversation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("DEBUG000","SET : " +idConvs.toString());

                    Log.d("DEBUG000",snapshot.getValue(String.class));
                    if (idConvs.add(snapshot.getValue().toString()) == false) {
                        Log.d("DEBUG000","FALSE  " +snapshot.getValue().toString());
                        realIdConv[0] = snapshot.getValue().toString();
                    }else{
                        idConvs.add(snapshot.getValue(String.class));
                    }
                }
                Log.d("DEBUG000","SET : " +idConvs.toString());

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        Handler handler = new Handler();
        handler.postDelayed(() -> idConv=realIdConv[0], 100);

    }

    private void displayConvFromMessageFragment(){
        FirebaseDatabase.getInstance().getReference().child("conversation").child(idConv).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    if (!data.equals(currentUidUser)) {
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
    }

    private void displayConvFromActivity(){
        //Cas ou l'on passe par l'activité
        if (organiser != null && uuidOrga!=null) {
            binding.name.setText(organiser.getName());
            if (organiser.getImage() != null) {
                byte[] bytes = Base64.decode(organiser.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.image.setImageBitmap(bitmap);
            }

            if(idConv!=""){
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