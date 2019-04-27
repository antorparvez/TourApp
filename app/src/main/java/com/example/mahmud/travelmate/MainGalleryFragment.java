package com.example.mahmud.travelmate;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mahmud.travelmate.Adapter.MainGalleryRvAdapter;
import com.example.mahmud.travelmate.POJO.Event;
import com.example.mahmud.travelmate.POJO.PictureData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MainGalleryFragment extends Fragment {
    private RecyclerView mRV;
    private DatabaseReference mGalleryUserRef;
    private DatabaseReference mEventsUserRef;
    private StorageReference mImagesStr;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ArrayList<PictureData> pictureDataArrayList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Context mContext;
    MainGalleryRvAdapter rvAdapter;
    private HashMap<String, ArrayList<String>> mAllEventImages = new HashMap<>();
    private HashMap<String, ArrayList<PictureData>> mAllEventImageDataList = new HashMap<>();
    private ArrayList<String> mEventKey = new ArrayList<>();
    private ArrayList<String> mEventNameList = new ArrayList<>();
    private String mEventname;
    private Event mEvent;


    public MainGalleryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mEventsUserRef = FirebaseDatabase.getInstance().getReference().child("Events").child("Users");
        mGalleryUserRef = FirebaseDatabase.getInstance().getReference().child("Gallery").child(mUser.getUid());
        Log.e("--------------------","MainGalleryFragment onCreate called");
        mGalleryUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    String s = d.getKey();
                    //Toast.makeText(mContext, mEventname, Toast.LENGTH_SHORT).show();
                    mEventKey.add(s);
                    ArrayList<PictureData> pictureDataList = new ArrayList<>();
                    for (DataSnapshot dInside : d.getChildren()){
                        PictureData pictureData = dInside.getValue(PictureData.class);
                        pictureDataList.add(pictureData);
                    }
                    mAllEventImageDataList.put(s,pictureDataList);
                }
                mEventsUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()){
                            mEvent = d.getValue(Event.class);
                            if (mEventKey.contains(mEvent.getId())){
                                mEventNameList.add(mEvent.getEventName());
                            }
                        }
                        //Toast.makeText(mContext, "Size is "+mEventNameList.size(), Toast.LENGTH_SHORT).show();
                        //Log.e("------------------","in 0 : "+mEventNameList.get(0)
                        //+"in 1 : "+mEventNameList.get(1));
                        //if (mAllEventImageDataList.size()> 0){
                            rvAdapter = new MainGalleryRvAdapter(mContext,mEventNameList,mAllEventImageDataList);
                            mRV.setAdapter(rvAdapter);
                        //} else {
                        //    Toast.makeText(mContext, "Nothing to display", Toast.LENGTH_SHORT).show();
                        //}
                        
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Toast.makeText(mContext, "Size is "+mAllEventImageDataList.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mEventsUserRef = FirebaseDatabase.getInstance().getReference().child("Events").child(mUser.getUid());
        /*mEventsUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEventNames.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    Event event = d.getValue(Event.class);
                    mEventNames.add(event.getEventName());
                }
                mRV.setAdapter(rvAdapter);
                Toast.makeText(mContext, "Size is "+mEventNames.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        mRV = v.findViewById(R.id.rv_gallery_main_mgf);
        manager = new LinearLayoutManager(mContext);
        mRV.setLayoutManager(manager);

        /*mGalleryUserRef = FirebaseDatabase.getInstance().getReference().child("Gallery").child(mUser.getUid());
        mEventsUserRef = FirebaseDatabase.getInstance().getReference().child("Events").child(mUser.getUid());*/
        return v;
    }

}
