package com.example.mahmud.travelmate.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmud.travelmate.POJO.PictureData;
import com.example.mahmud.travelmate.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MainGalleryRvAdapter extends RecyclerView.Adapter<MainGalleryRvAdapter.GalleryEventNameVH>{
    private Context mContext;
    private ArrayList<String> mEventNames = new ArrayList<>();
    private ArrayList<String> mEventKeys = new ArrayList<>();
    private HashMap<String ,ArrayList<PictureData>> mAllEventPictureDataList = new HashMap<>();

    private ArrayList<ArrayList<PictureData>> mPictureDataList = new ArrayList<ArrayList<PictureData>>();

    //private String mUid;
    private DatabaseReference mEventUserRef;

    public MainGalleryRvAdapter(Context mContext, ArrayList<String> mEventNames,
                                HashMap<String, ArrayList<PictureData>> mAllEventPictureDataList) {
        this.mContext = mContext;
        this.mEventNames.addAll(mEventNames);
        this.mAllEventPictureDataList = mAllEventPictureDataList;
        this.mEventKeys.addAll(mAllEventPictureDataList.keySet());
        //Toast.makeText(mContext, "size is Total"+mAllEventPictureDataList.size(), Toast.LENGTH_SHORT).show();


    }

    @NonNull
    @Override
    public GalleryEventNameVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.single_main_gallery_event_item,viewGroup,false);
        return new GalleryEventNameVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryEventNameVH galleryEventNameVH, int i) {
        galleryEventNameVH.eventNameTV.setText(mEventNames.get(i));
        SingleGalleryRvAdpater innerRvAdapter = new SingleGalleryRvAdpater(mContext,mAllEventPictureDataList.get(mEventKeys.get(i)), i);
        GridLayoutManager gManager = new GridLayoutManager(mContext,3);
        galleryEventNameVH.rv.setLayoutManager(gManager);
        galleryEventNameVH.rv.setAdapter(innerRvAdapter);


    }

    @Override
    public int getItemCount() {
        return mEventNames.size();
    }

    class GalleryEventNameVH extends RecyclerView.ViewHolder{
        private TextView eventNameTV;
        private RecyclerView rv;
        public GalleryEventNameVH(@NonNull View itemView) {
            super(itemView);
            rv = itemView.findViewById(R.id.rv_event_smgei);
            eventNameTV = itemView.findViewById(R.id.single_event_name_tv_mgf);
        }
    }
}
