package com.example.socialsport.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.socialsport.ListAdapter;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MessageFragment extends Fragment {
    private DatabaseReference mDatabase;
    private ListView lv_chat;


    //TODO changer avec image de bdd
    private int[] images = {R.drawable.img_football,R.drawable.img_football,R.drawable.img_football,R.drawable.img_football,R.drawable.img_football};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(2,true);

/*
        lldisplay_mesgs = view.findViewById(R.id.llmessagedisplay);
        llreponse = view.findViewById(R.id.llmessagereponse);
        lllist_mesgs = view.findViewById(R.id.lllistmessgs);*/

        lv_chat = view.findViewById(R.id.lv_chat);


        ListAdapter lAdapter = new ListAdapter(getContext(), ((PrincipalPageActivity) requireActivity()).getMap(), images);

        lv_chat.setAdapter(lAdapter);



        lv_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object firstKey = ((PrincipalPageActivity) requireActivity()).getMap().keySet().toArray()[i];
                Bundle bundle = new Bundle();
                bundle.putString("name", (String) firstKey);
                bundle.putStringArrayList("message",((PrincipalPageActivity) requireActivity()).getMap().get(firstKey));
                Fragment newF = new ConversationFragment();
                newF.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }



                                    /*
                                        TextView tv = new TextView(MessageFragment.this.getContext());
                                        String message = snapshot.child("message").getValue().toString();
                                        String date = snapshot.child("date").getValue().toString();
                                        String sender = snapshot.child("sender").getValue().toString();*/
                        /*
                        Button btn = new Button(MessageFragment.this.getContext());
                        String contact =snapshot.getKey().toString();
                        btn.setText(contact);
                        btn.setBackgroundColor(Color.rgb(255,127,80));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        btn.setLayoutParams(params);

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                lllist_mesgs.removeAllViews();
                                LinearLayout.LayoutParams paramsll = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        300
                                );
                                lllist_mesgs.setLayoutParams(paramsll);
                                lllist_mesgs.setVisibility(View.VISIBLE);
                                mDatabase.child("chat").child("YB1RV3E1hxU0sOGRxzC0n3QhdNH2").child(contact).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Log.e("firebase", "Error getting data", task.getException());
                                        }
                                        else {
                                            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                                TextView tv = new TextView(MessageFragment.this.getContext());
                                                String message = snapshot.child("message").getValue().toString();
                                                String date = snapshot.child("date").getValue().toString();
                                                String sender = snapshot.child("sender").getValue().toString();
                                                if (sender.equals("true")) {
                                                    tv.setText("<- "+message+" envoyé le : "+date);
                                                }
                                                else{
                                                    tv.setText("-> "+message+" recu le : "+date);
                                                }
                                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                                );
                                                tv.setLayoutParams(params);
                                                lllist_mesgs.addView(tv);
                                            }
                                            llreponse.setVisibility(View.VISIBLE);
                                            llreponse.removeAllViews();
                                            llreponse.setLayoutParams(paramsll);
                                            EditText et = new EditText(MessageFragment.this.getContext());
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            );
                                            llreponse.addView(et);
                                            et.setLayoutParams(params);
                                            Button btnsend = new Button(MessageFragment.this.getContext());
                                            btnsend.setBackgroundColor(Color.rgb(50,205,50));
                                            btnsend.setText("SEND");
                                            btnsend.setLayoutParams(params);
                                            btnsend.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    String msg = et.getText().toString();
                                                    if(!msg.isEmpty()){
                                                        Toast.makeText(MessageFragment.this.getContext(), "Message sent", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Toast.makeText(MessageFragment.this.getContext(), "Error message is empty", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            llreponse.addView(btnsend);
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

    }*/
}