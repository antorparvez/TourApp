package com.example.mahmud.travelmate;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mahmud.travelmate.Interface.BackToMainGalleryFromPictureEvent;

public class PictureFragment extends Fragment{

    private ImageView mImageIMG;
    private String mImagePath;
    private Bundle bundle;
    private int code = 0;
    private Context mContext;
    private BackToMainGalleryFromPictureEvent toMainGalleryFromPictureEvent;
    public PictureFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        toMainGalleryFromPictureEvent = (BackToMainGalleryFromPictureEvent) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_picture, container, false);
        bundle = getArguments();
        if (bundle != null && bundle.containsKey("Image Path")){
            mImagePath = bundle.getString("Image Path");
            if (bundle.containsKey("code")){
                code = bundle.getInt("code",0);
            }
        }
        mImageIMG = v.findViewById(R.id.image_img_pf);
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        mImageIMG.setImageBitmap(bitmap);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && code == 101){
                    toMainGalleryFromPictureEvent.onBackPressedFromPictureEvent();
                    return true;
                }
                return false;
            }
        });

        return v;
    }

}
