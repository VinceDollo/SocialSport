package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
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
    private String uuidOrganiser;
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
        formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        today = new Date();

        //Get info from overview fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            organiser = (User) bundle.getSerializable("organiser");
            uuidOrganiser = bundle.getString("uidOrganiser");

            if (uuidOrganiser != null) {
                findIdConvInCommon();
                Handler handler = new Handler();
                handler.postDelayed(this::displayConvFromActivity, 200);
            } else {
                //If you go through message fragment
                idConv = bundle.getString("idConv");
                displayConvFromMessageFragment();
            }
        }

        setListener();

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        binding.etMessage.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.etMessage.getRight() - binding.etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    String lastMessage = Objects.requireNonNull(binding.etMessage.getText()).toString();
                    if (!lastMessage.trim().isEmpty()) {
                        if (!"".equals(idConv)) {
                            ArrayList<Message> messages = getMessagesList();
                            addMessageView(lastMessage);
                            Handler handler = new Handler();
                            handler.postDelayed(() -> addMessageInMessagesList(messages, new Message(lastMessage, formatter.format(today), currentUidUser)), 1000);
                        } else {
                            addConvForUser(lastMessage, formatter.format(today), currentUidUser);
                            addMessageView(lastMessage);
                        }
                        binding.etMessage.setText("");
                    }
                    return true;
                }
            }
            return false;
        });
    }

    //Add a new message in messages list
    private void addMessageInMessagesList(ArrayList<Message> messages, Message message) {
        messages.add(message);
        Log.d(TAG, messages.toString());
        FirebaseDatabase.getInstance().getReference().child("conversations").child(idConv).child("messages").setValue(messages);
    }

    private ArrayList<Message> getMessagesList() {
        ArrayList<Message> messages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("conversations").child(idConv).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String message = snapshot1.child("message").getValue(String.class);
                    String senderId = snapshot1.child("idSender").getValue(String.class);
                    String date = snapshot1.child("date").getValue(String.class);

                    Message msg = new Message(message, date, senderId);
                    messages.add(msg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
        return messages;
    }

    private void addMessageView(String message) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        textView.setText(message);
        textView.setTextSize(15);
        textView.setPadding(30, 20, 30, 20);
        params.gravity = Gravity.END;
        textView.setBackgroundResource(R.drawable.tv_send_message);
        textView.setTextColor(textView.getContext().getColor(R.color.white));
        textView.setLayoutParams(params);
        binding.llConversation.addView(textView);
    }

    //Add conversation in database if it doesn't exist
    private void addConvForUser(String msg, String date, String sender) {
        Message message = new Message(msg, date, sender);
        ArrayList<Message> a = new ArrayList<>();
        a.add(message);

        DatabaseReference conv = FirebaseDatabase.getInstance().getReference().child("conversations");
        idConv = conv.push().getKey();
        assert idConv != null;
        conv.child(idConv).child("messages").setValue(a);
        ArrayList<String> participant = new ArrayList<>();

        participant.add(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        participant.add(uuidOrganiser);
        conv.child(idConv).child("participants").setValue(participant);

        addConvInMessageFragment(idConv);

        FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("conversations").setValue(((PrincipalPageActivity) requireActivity()).getIdConv());

        ArrayList<String> listConv = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrganiser).child("conversations").addValueEventListener(new ValueEventListener() {
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
        handler.postDelayed(() -> addNewConversationInConversationsList(listConv), 1000);
    }

    //Add a conversation id in users table
    private void addNewConversationInConversationsList(ArrayList<String> conversationsList) {
        conversationsList.add(idConv);
        FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrganiser).child("conversations").setValue(conversationsList);
    }

    //Add conversation id in principal activity
    private void addConvInMessageFragment(String idConv) {
        ((PrincipalPageActivity) requireActivity()).getIdConv().add(idConv);
    }

    //Find conversation id in common if it exists
    private void findIdConvInCommon() {
        HashSet<String> idConversations = new HashSet<>();
        final String[] realIdConv = {""};
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUidUser).child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    idConversations.add(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").child(uuidOrganiser).child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!idConversations.add(Objects.requireNonNull(snapshot.getValue()).toString())) {
                        realIdConv[0] = snapshot.getValue(String.class);
                    } else {
                        idConversations.add(snapshot.getValue(String.class));
                    }
                }
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
        FirebaseDatabase.getInstance().getReference().child("conversations").child(idConv).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    if (data != null && !data.equals(currentUidUser)) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(data).child("name").get().addOnCompleteListener(task -> binding.name.setText(Objects.requireNonNull(task.getResult().getValue()).toString()));
                        FirebaseDatabase.getInstance().getReference().child("users").child(data).child("image").get().addOnCompleteListener(task -> {
                            if (task.getResult().getValue() != null) {
                                String image = task.getResult().getValue().toString();
                                byte[] bytes = Base64.decode(image, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                binding.image.setImageBitmap(bitmap);
                            } else
                                binding.image.setImageResource(R.drawable.img_person);
                        });

                        displayMessagesInConversation();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void displayMessagesInConversation() {
        FirebaseDatabase.getInstance().getReference().child("conversations").child(idConv).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.llConversation.removeAllViews();
                for (DataSnapshot a : snapshot.getChildren()) {

                    String message = Objects.requireNonNull(a.child("message").getValue()).toString();
                    String sender = Objects.requireNonNull(a.child("idSender").getValue()).toString();

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

    private void displayConvFromActivity() {
        //If you come through the activity
        if (organiser != null) {
            binding.name.setText(organiser.getName());
            if (organiser.getImage() != null) {
                byte[] bytes = Base64.decode(organiser.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.image.setImageBitmap(bitmap);
            }

            if (!"".equals(idConv)) {
                displayMessagesInConversation();
            }
        }
    }

}