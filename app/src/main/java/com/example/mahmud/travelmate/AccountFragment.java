package com.example.mahmud.travelmate;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmud.travelmate.Interface.BackToEventFromAFListener;
import com.example.mahmud.travelmate.POJO.UserWithImg;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.http.Url;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private static final int REQUEST_GALLERY = 11;
    private Button mChangeImageBT;
    private DatabaseReference mUserRef;
    private StorageReference mImageStr;
    private ImageView mUserImg;
    private TextView mUserNameTV,mUserPhoneTV;
    private EditText mUserNameET,mUserPhoneET;
    private ImageButton mEditNameIB,mEditPhoneIB;
    private Button mUpdateInfo;
    private File mAppDir;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private File mImageFile;
    private String mUserName;
    private String mUserPhone;
    private String mUserImageName;
    private String mUserImageUrl;
    private BackToEventFromAFListener afListener;



    public AccountFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAppDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        afListener = (BackToEventFromAFListener) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mImageStr = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        mChangeImageBT = v.findViewById(R.id.change_image_bt_af);
        mUserImg = v.findViewById(R.id.user_img_af);
        mUserNameTV = v.findViewById(R.id.user_name_tv_af);
        mUserPhoneTV = v.findViewById(R.id.user_phone_tv_af);
        mUserNameET = v.findViewById(R.id.user_name_et_af);
        mUserPhoneET = v.findViewById(R.id.user_phone_et_af);
        mEditNameIB = v.findViewById(R.id.user_name_ib_af);
        mEditPhoneIB = v.findViewById(R.id.user_phone_ib_af);
        mUpdateInfo = v.findViewById(R.id.update_info_bt_af);

        Bundle bundle = getArguments();
        if (bundle != null){
            if (bundle.containsKey("userwithimg")){
                UserWithImg userWithImg = (UserWithImg) bundle.getSerializable("userwithimg");
                mUserName = userWithImg.getUser();
                mUserPhone = userWithImg.getPhone();
                mUserImageName = userWithImg.getImageName();
                mUserImageUrl = userWithImg.getImageUrl();
                if (mUserImageUrl.length() > 3){
                    Toast.makeText(getContext(), "length is greater than 3", Toast.LENGTH_SHORT).show();
                //if (URLUtil.isValidUrl(mUserImageUrl)){
                    File file = new File(mAppDir,mUserImageName);
                    if (!file.exists()){
                        Picasso.get().load(mUserImageUrl).into(mUserImg);
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    mUserImg.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(getContext(), "Length is smaller than 3", Toast.LENGTH_SHORT).show();
                    mUserImg.setImageResource(R.drawable.ic_account_black);
                }
                mUserNameTV.setText(mUserName);
                mUserNameET.setText(mUserName);
                mUserPhoneTV.setText(mUserPhone);
                mUserPhoneET.setText(mUserPhone);
            }
        }

        mEditNameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserNameTV.setVisibility(View.GONE);
                mUserNameET.setVisibility(View.VISIBLE);
            }
        });

        mEditPhoneIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserPhoneTV.setVisibility(View.GONE);
                mUserPhoneET.setVisibility(View.VISIBLE);
            }
        });

        mChangeImageBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_GALLERY);
            }
        });

        mUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName = mUserNameET.getText().toString();
                mUserPhone = mUserPhoneET.getText().toString();
                if (mUserName.isEmpty() || mUserPhone.isEmpty()){
                    Toast.makeText(getContext(), "Plz fill up all", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mImageFile != null){
                    Uri imageUri = Uri.fromFile(mImageFile);
                    mUserImageName = imageUri.getLastPathSegment();
                    final StorageReference photoRef = mImageStr.child("Images/"+mUserImageName);
                    UploadTask uploadTask = photoRef.putFile(imageUri);
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return photoRef.getDownloadUrl();
                        }
                    });
                    uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri downloadUri = task.getResult();
                            String dUrl = downloadUri.toString();
                            mUserRef.child(mUser.getUid()).removeValue();
                            UserWithImg userWithImg = new UserWithImg(mUserName,mUserPhone,mUserImageName,dUrl);
                            mUserRef.child(mUser.getUid()).setValue(userWithImg);
                            afListener.onUpdateInfo();

                        }
                    });
                } else {
                    mUserRef.child(mUser.getUid()).removeValue();
                    UserWithImg userWithImg = new UserWithImg(mUserName,mUserPhone,mUserImageName,mUserImageUrl);
                    mUserRef.child(mUser.getUid()).setValue(userWithImg);
                    afListener.onUpdateInfo();
                }

            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK){
            Uri fileUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(fileUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //BitmapFactory.decodeFile(picturePath,options);
            try {
                InputStream ip = new FileInputStream(picturePath);
                BitmapFactory.decodeStream(ip,null,options);
                //Toast.makeText(getContext(), "option "+options.outWidth+" "+options.outHeight, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = Math.min(options.outWidth/mUserImg.getWidth(),options.outHeight/mUserImg.getHeight());
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath,options);
            mUserImg.setImageBitmap(bitmap);
            mImageFile = new File(mAppDir,mUser.getEmail()+".jpg");
            try {
                FileOutputStream outputStream = new FileOutputStream(mImageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
