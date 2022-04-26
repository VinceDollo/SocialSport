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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentConversationBinding;
import com.example.socialsport.entities.Message;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class ConversationFragment extends Fragment {

    private static final String TAG = ConversationFragment.class.getSimpleName();

    private FragmentConversationBinding binding;
    private User organiser;
    private String uuidOrga;
    private String idConv;
    private String currentUidUser;
    private SimpleDateFormat formatter;
    private Date today;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConversationBinding.inflate(inflater);
        view = binding.getRoot();
        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(2, true);

        currentUidUser = ((PrincipalPageActivity) requireActivity()).getUidUser();
        formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
        today = new Date();

        //Recuperation des infos quand tu es sur la page de l'activité
        Bundle bundle = getArguments();
        if (bundle != null) {
            organiser = (User) bundle.getSerializable("organiser");
            uuidOrga = bundle.getString("uidorganiser");

            if (uuidOrga != null) {
                retriveIdConv();//retrouver si une conv existe ou non
                Handler handler = new Handler();
                handler.postDelayed(this::displayConvFromActivity, 200);
            } else {
                //Si jamais tu passses par le fragment message
                idConv = bundle.getString("idConv");
                displayConvFromMessageFragment();
            }
        }

        setListener();

        return view;
    }

    private void setListener() {
        binding.etMessage.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.etMessage.getRight() - binding.etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (!binding.etMessage.getText().toString().trim().isEmpty()) {
                        if (idConv != "") {
                            ArrayList<Message> listMessage = new ArrayList<>();
                            FirebaseDatabase.getInstance().getReference().child("conversation").child(idConv).child("messages").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    listMessage.clear();
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        Log.d("Coucou", snapshot1.toString());
                                        String message = snapshot1.child("message").getValue(String.class);
                                        String senderId = snapshot1.child("idSender").getValue(String.class);
                                        String date = snapshot1.child("date").getValue(String.class);
                                        Log.d("DEBUG000", "Message : " + message);
                                        Log.d("DEBUG000", "Sender : " + senderId);
                                        Log.d("DEBUG000", "date : " + date);

                                        Message msg = new Message(message, date, senderId);
                                        listMessage.add(msg);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
                            Message newM = new Message(binding.etMessage.getText().toString(), formatter.format(today), currentUidUser);
                            binding.etMessage.setText("");
                            Handler handler = new Handler();
                            handler.postDelayed(() -> addData2(listMessage, newM), 1000);
                        } else {
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
                            addConvForUser(binding.etMessage.getText().toString(), formatter.format(today), currentUidUser);
                            binding.etMessage.setText("");
                        }
                    }
                    return true;
                }
            }
            return false;
        });
    }


    //Permet d'ajouter la conv dans la bdd si elle n'existe pas
    private void addConvForUser(String msg, String date, String sender) {
        Message message = new Message(msg, date, sender);
        ArrayList<Message> a = new ArrayList<>();
        a.add(message);

        DatabaseReference conv = FirebaseDatabase.getInstance().getReference().child("conversation");
        idConv = conv.push().getKey();
        assert idConv != null;
        conv.child(idConv).child("messages").setValue(a);
        ArrayList<String> participant = new ArrayList<>();

        participant.add(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        participant.add(uuidOrga);
        conv.child(idConv).child("participants").setValue(participant);

        addConvInMessageFragment(idConv);
        Log.d("DEBUG000", ((PrincipalPageActivity) requireActivity()).getIdconv().toString());

        FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("conversation").setValue(((PrincipalPageActivity) requireActivity()).getIdconv());

        ArrayList<String> listConv = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrga).child("conversation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listConv.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    listConv.add(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> addData(listConv), 1000);
    }

    //Pour mettre a jour les données id conv de la bdd
    private void addData(ArrayList<String> a) {
        a.add(idConv);
        FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrga).child("conversation").setValue(a);
    }

    //Pour mettre a jour les données id conv de la bdd
    private void addData2(ArrayList<Message> a, Message b) {
        a.add(b);
        FirebaseDatabase.getInstance().getReference().child("conversation").child(idConv).child("messages").setValue(a);
    }

    //Ajoute l'id de la conv dans principal page
    private void addConvInMessageFragment(String idConv) {
        ((PrincipalPageActivity) requireActivity()).getIdconv().add(idConv);
    }

    //Permet de retrouver l'id de la conv commune si elle existe
    private void retriveIdConv() {
        HashSet<String> idConvs = new HashSet<>();
        final String[] realIdConv = {""};
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUidUser).child("conversation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idConvs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("DEBUG000", snapshot.getValue(String.class));
                    idConvs.add(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }

        });

        FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrga).child("conversation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idConvs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("DEBUG000", "SET : " + idConvs);

                    Log.d("DEBUG000", snapshot.getValue(String.class));
                    if (!idConvs.add(Objects.requireNonNull(snapshot.getValue()).toString())) {
                        Log.d("DEBUG000", "FALSE  " + snapshot.getValue().toString());
                        realIdConv[0] = snapshot.getValue().toString();
                    } else {
                        idConvs.add(snapshot.getValue(String.class));
                    }
                }
                Log.d("DEBUG000", "SET : " + idConvs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(() -> idConv = realIdConv[0], 100);
    }

    private void displayConvFromMessageFragment() {
        FirebaseDatabase.getInstance().getReference().child("conversation").child(idConv).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    if (data != null && !data.equals(currentUidUser)) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(data).child("name").get().addOnCompleteListener(task -> {
                            binding.name.setText(Objects.requireNonNull(task.getResult().getValue()).toString());
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
                                binding.llConversation.removeAllViews();
                                for (DataSnapshot a : snapshot.getChildren()) {
                                    Log.d("DEBUG456 : ", Objects.requireNonNull(a.getValue()).toString());

                                    String message = Objects.requireNonNull(a.child("message").getValue()).toString();
                                    String sender = Objects.requireNonNull(a.child("idSender").getValue()).toString();
                                    String date = a.child("date").getValue().toString();

                                    Log.d("DEBUG456", message);
                                    TextView textView = new TextView(view.getContext());
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(0, 0, 0, 20);
                                    textView.setText(message);
                                    textView.setTextSize(15);
                                    textView.setPadding(30, 20, 30, 20);
                                    if (sender.equals(currentUidUser)) {
                                        params.gravity = Gravity.END;
                                        textView.setBackgroundResource(R.drawable.tv_send_message);
                                        textView.setTextColor(textView.getContext().getColor(R.color.white));
                                        textView.setLayoutParams(params);
                                    } else {
                                        params.gravity = Gravity.START;
                                        textView.setTextColor(textView.getContext().getColor(R.color.black));
                                        textView.setBackgroundResource(R.drawable.tv_received_message);
                                    }
                                    textView.setLayoutParams(params);
                                    binding.llConversation.addView(textView);
                                    Utils.hideKeyboard(view.getContext(), view);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void displayConvFromActivity() {
        //Cas ou l'on passe par l'activité
        if (organiser != null && uuidOrga != null) {
            binding.name.setText(organiser.getName());
            if (organiser.getImage() != null) {
                byte[] bytes = Base64.decode(organiser.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.image.setImageBitmap(bitmap);
            }

            if (!"".equals(idConv)) {
                FirebaseDatabase.getInstance().getReference().child("conversation").child(idConv).child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.llConversation.removeAllViews();
                        for (DataSnapshot a : snapshot.getChildren()) {
                            Log.d("DEBUG456", Objects.requireNonNull(a.getValue()).toString());
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
                        Log.e(TAG, error.getMessage());
                    }
                });
            }
        }
    }

}