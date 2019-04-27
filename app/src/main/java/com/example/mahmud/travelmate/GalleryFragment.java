package com.example.mahmud.travelmate;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mahmud.travelmate.Adapter.GalleryRvAdapter;
import com.example.mahmud.travelmate.Interface.ImageOnClickListener;
import com.example.mahmud.travelmate.POJO.PictureData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class GalleryFragment extends Fragment implements ImageOnClickListener {
    private RecyclerView rv;
    private GridLayoutManager mGridLayoutManager;
    private Context mContext;
    private ArrayList<PictureData> mPictureDatas = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mGalleryUserEventRef;
    private String mEventId;
    private GalleryRvAdapter rvAdapter;
    private File mAppPath;
    public GalleryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mAppPath = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGalleryUserEventRef =FirebaseDatabase.getInstance().getReference().child("Gallery").child(mUser.getUid()).child(mEventId);
        mGalleryUserEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPictureDatas.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    PictureData pictureData = new PictureData();
                    pictureData = d.getValue(PictureData.class);
                    mPictureDatas.add(pictureData);
                }
                rv.setAdapter(rvAdapter);
                Log.e("----------------","PictureDatas size : "+mPictureDatas.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        rv = view.findViewById(R.id.rv_gf);
        mGridLayoutManager = new GridLayoutManager(mContext,4);
        rv.setLayoutManager(mGridLayoutManager);
        rvAdapter = new GalleryRvAdapter(mContext,mPictureDatas,mAppPath,this);
        rv.setAdapter(rvAdapter);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("Event Id")){
            mEventId = bundle.getString("Event Id");
        }



        return view;
    }

    @Override
    public void onImageDelete(PictureData pictureData) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("Images/"+pictureData.getName()+".jpg");
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
        mGalleryUserEventRef.child(pictureData.getId()).removeValue();
        File thumb = new File(mAppPath+"/thumb/t"+pictureData.getName()+".jpg");
        thumb.delete();
        File main = new File(mAppPath,pictureData.getName()+".jpg");
        main.delete();
    }

    @Override
    public void onImageEdit(final PictureData pictureData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View v = LayoutInflater.from(mContext).inflate(R.layout.single_edit_layout,null);
        final EditText ET = v.findViewById(R.id.single_et_gf);
        ET.setText(pictureData.getName());
        builder.setView(v);
        builder.setNegativeButton("cancel",null).setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File thumb = new File(mAppPath+"/thumb/t"+pictureData.getName()+".jpg");
                File main = new File(mAppPath,pictureData.getName()+".jpg");
                pictureData.setName(ET.getText().toString());
                mGalleryUserEventRef.child(pictureData.getId()).removeValue();
                mGalleryUserEventRef.child(pictureData.getId()).setValue(pictureData);
                thumb.renameTo(new File(mAppPath + "/thumb/t" + pictureData.getName() + ".jpg"));
                main.renameTo(new File(mAppPath + "/" + pictureData.getName() + ".jpg"));
                Toast.makeText(mContext, "Renamed", Toast.LENGTH_SHORT).show();

            }
        });
        builder.show();
    }
}
