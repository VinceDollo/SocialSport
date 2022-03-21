package com.example.socialsport.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialsport.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    private LinearLayout lldisplay_mesgs;
    private DatabaseReference mDatabase;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        lldisplay_mesgs = view.findViewById(R.id.llmessagedisplay);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        mDatabase.child("chat").child("YB1RV3E1hxU0sOGRxzC0n3QhdNH2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Button btn = new Button(MessageFragment.this.getContext());
                        String contact =snapshot.getKey().toString();
                        btn.setText(contact);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        btn.setLayoutParams(params);

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDatabase.child("chat").child("YB1RV3E1hxU0sOGRxzC0n3QhdNH2").child(contact).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Log.e("firebase", "Error getting data", task.getException());
                                        }
                                        else {
                                            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                                TextView tv = new TextView(MessageFragment.this.getContext());
                                                tv.setText(snapshot.getValue().toString());
                                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                                );
                                                tv.setLayoutParams(params);
                                                lldisplay_mesgs.addView(tv);
                                            }
                                            Button btnrep = new Button(MessageFragment.this.getContext());
                                            btnrep.setText("Repondre à "+contact);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            );
                                            btnrep.setLayoutParams(params);
                                            btnrep.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    EditText et = new EditText(MessageFragment.this.getContext());
                                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                                    );
                                                    lldisplay_mesgs.addView(et);
                                                    et.setLayoutParams(params);
                                                    Button btnsend = new Button(MessageFragment.this.getContext());
                                                    btnsend.setText("SEND");
                                                    btnsend.setLayoutParams(params);
                                                    btnsend.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Toast.makeText(MessageFragment.this.getContext(), "Message sent", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    lldisplay_mesgs.addView(btnsend);
                                                }
                                            });
                                            lldisplay_mesgs.addView(btnrep);
                                        }
                                    }
                                });
                            }
                        });
                        lldisplay_mesgs.addView(btn);
                    }
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

    }
}