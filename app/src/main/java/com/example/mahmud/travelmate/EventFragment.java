package com.example.mahmud.travelmate;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mahmud.travelmate.Adapter.RvAdapter;
import com.example.mahmud.travelmate.Interface.AddEventListener;
import com.example.mahmud.travelmate.Interface.AddEventOption;
import com.example.mahmud.travelmate.Interface.BackToEventFromAEF;
import com.example.mahmud.travelmate.Interface.EventDeleteListener;
import com.example.mahmud.travelmate.Interface.HandlingBackInEventListener;
import com.example.mahmud.travelmate.POJO.Event;
import com.example.mahmud.travelmate.POJO.TestEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.finishAffinity;


public class EventFragment extends Fragment{
    private LinearLayoutManager manager;
    private RecyclerView mEventRV;
    private ImageView mNothingImg;
    private Button mAddEventBT;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ArrayList<Event> events = new ArrayList<>();
    //private ArrayList<TestEvent> events = new ArrayList<>();
    private Context context;
    private RvAdapter rvAdapter;
    private DatabaseReference mEventUserRef;
    private AddEventOption addEventOption;
    private HandlingBackInEventListener backInEventListener;
    private BackToEventFromAEF backAction;
    private FloatingActionButton mAddEventFAB;
    private AddEventListener addEventListener;

    public EventFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        addEventOption = (AddEventOption) context;
        backInEventListener = (HandlingBackInEventListener) context;
        backAction = (BackToEventFromAEF) context;
        addEventListener = (AddEventListener) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mEventUserRef = FirebaseDatabase.getInstance().getReference().child("Events").child(mUser.getUid());


        mEventUserRef.addValueEventListener(new ValueEventListener() {
        //mUserIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    Event event = new Event();
                    event = d.getValue(Event.class);
                    events.add(event);

                }
                if (events.size() == 0){
                    mNothingImg.setVisibility(View.VISIBLE);
                    mAddEventBT.setVisibility(View.VISIBLE);
                }
                if (events.size() ==1){
                    mNothingImg.setVisibility(View.GONE);
                    mAddEventBT.setVisibility(View.GONE);
                }
                mEventRV.setAdapter(rvAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        View v = inflater.inflate(R.layout.fragment_event, container, false);
        mEventRV = v.findViewById(R.id.event_rv_ef);
        mNothingImg = v.findViewById(R.id.nothing_img_ef);
        mAddEventBT = v.findViewById(R.id.add_event_bt_ef);
        mAddEventFAB = v.findViewById(R.id.fab_add_event);
        mEventRV.setLayoutManager(manager);
        TestEvent event = new TestEvent("1","qwweq","23131",12);
        rvAdapter = new RvAdapter(context,events);
        mAddEventBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventOption.onAddEvent();
            }
        });


        mAddEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventListener.onClickAdd();
            }
        });

        //handling back press-----------------------------------------------------------------------
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                    backInEventListener.eventInBackListener();
                }
                return true;
            }
        });


        //backAction.backAction();

        return v;
    }


}
