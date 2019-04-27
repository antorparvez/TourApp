package com.example.mahmud.travelmate;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mahmud.travelmate.Interface.BackFromTakePhotoFragment;
import com.example.mahmud.travelmate.POJO.PictureData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;


public class TakePhotoFragment extends Fragment {
    private static final int REQUEST_CAPTURE_CODE = 2;
    private Context mContext;
    private Button mTakeSnapshotBT,mBackBT;
    private ImageView mImageIMG;
    private File mImageFile;
    private File mAppDir;
    private String mImageBaseName;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mEventId;
    private GifImageView mStatusGIF;
    private ImageView mStatusIMG;
    private BackFromTakePhotoFragment backFromTakePhotoFragment;


    public TakePhotoFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mAppDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        backFromTakePhotoFragment = (BackFromTakePhotoFragment) context;

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);
        mTakeSnapshotBT = view.findViewById(R.id.take_snapshot_bt_tpf);
        mImageIMG = view.findViewById(R.id.image_img_tpf);
        mStatusGIF = view.findViewById(R.id.status_gif_tpf);
        mStatusIMG = view.findViewById(R.id.status_img_tpf);
        mBackBT = view.findViewById(R.id.back_bt_tpf);
        Bundle argument = getArguments();
        if (argument != null && argument.containsKey("Event Id")){
            mEventId = argument.getString("Event Id");

        }

        mBackBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFromTakePhotoFragment.backFromTakePhotoListener();
            }
        });



        mTakeSnapshotBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                    try {
                        mImageFile = createImageFile();
                        if (mImageFile != null){
                            Uri fileUri = FileProvider.getUriForFile(mContext,"com.example.mahmud.travelmate",mImageFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                            startActivityForResult(intent,REQUEST_CAPTURE_CODE);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_CODE && resultCode == Activity.RESULT_OK){
            //File Uploading
            mStatusGIF.setVisibility(View.VISIBLE);
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            Uri imageUri = Uri.fromFile(mImageFile);
            final StorageReference photoRef = storageReference.child("Images/"+imageUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(imageUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                }
            });

            //When uploading finished
            uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    //Uri downloadUri = task.getResult();
                    Uri downloadUri = task.getResult();
                    String dUrl = downloadUri.toString();
                    //stop progressbar
                   // Log.e("-------------","userId : "+mUser.getUid());
                   // Log.e("-------------","eventId : "+mEventId);
                    DatabaseReference galleryUserEventRef = FirebaseDatabase.getInstance().getReference().child("Gallery").child(mUser.getUid()).child(mEventId);

                    String pictureDataKey = galleryUserEventRef.push().getKey();
                    String time = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a").format(new Date());
                    PictureData picture = new PictureData(pictureDataKey,mImageBaseName,time,dUrl);
                    galleryUserEventRef.child(pictureDataKey).setValue(picture);
                    mStatusGIF.setVisibility(View.GONE);
                    mStatusIMG.setVisibility(View.VISIBLE);
                    //Picasso.get().load(dUrl).into(mImageIMG);
                    createPicWithSet();
                }
            });
        }
    }

    private void createPicWithSet() {
        int targetW = mImageIMG.getWidth();
        int targetH = mImageIMG.getHeight();
        BitmapFactory.Options  options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFile.getAbsolutePath(),options);

        int picW = options.outWidth;
        int picH = options.outHeight;
        options.inJustDecodeBounds = false;
        options.inSampleSize = Math.min(picW/targetW,picH/targetH);
        //creating bitmap for display
        Bitmap bitmap = BitmapFactory.decodeFile(mImageFile.getAbsolutePath(), options);

        //------------------------------------------------------------------------------------------------
        mImageIMG.setVisibility(View.VISIBLE);
        mBackBT.setVisibility(View.VISIBLE);
        mImageIMG.setImageBitmap(bitmap);
        //generating thumbnail of bitmap
        Bitmap thumpBitmap = Bitmap.createScaledBitmap(bitmap,300,300,false);
        //saving thumbnail
        //Checking directory
        File thumbDir = new File(mAppDir+"/thumb/");
        if (!thumbDir.exists()){
            thumbDir.mkdir();
        }
        File thumbFile = new File(thumbDir,"t"+mImageBaseName+".jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(thumbFile);
            thumpBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            mStatusGIF.setVisibility(View.GONE);
            mStatusIMG.setVisibility(View.VISIBLE);
            mBackBT.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
        mImageBaseName = "JPEG_"+timeStamp;
        //File file = File.createTempFile(mImageBaseName,".jpg",mAppDir);
        File file1 = new File(mAppDir,mImageBaseName+".jpg");
        FileOutputStream outputStream = new FileOutputStream(file1);
        outputStream.close();
        return file1;
    }

}
