package com.example.mahmud.travelmate.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmud.travelmate.Interface.FullScreenImageListener;
import com.example.mahmud.travelmate.POJO.PictureData;
import com.example.mahmud.travelmate.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleGalleryRvAdpater extends RecyclerView.Adapter<SingleGalleryRvAdpater.SingleGalleryVH>{
    private Context mContext;
    private int mParentPos;
    private List<Target> targets = new ArrayList<>();
    private ArrayList<PictureData> mPictureDataList = new ArrayList<>();
    private FullScreenImageListener fullScreenImageListener;

    public SingleGalleryRvAdpater(Context mContext, ArrayList<PictureData> mPictureDataList, int mParentPos) {
        this.mContext = mContext;
        this.mPictureDataList = mPictureDataList;
        this.mParentPos = mParentPos;
        fullScreenImageListener = (FullScreenImageListener) mContext;
        //Toast.makeText(mContext, "Size in single is "+mPictureDataList.size(), Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public SingleGalleryVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.single_image_view,viewGroup,false);
        return new SingleGalleryVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SingleGalleryVH singleGalleryVH, final int i) {
        singleGalleryVH.nameTV.setText(mPictureDataList.get(i).getName());
        final File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        final File imageFile = new File(dir,mPictureDataList.get(i).getName()+".jpg");
        File thumbDir = new File(dir+"/thumb/");
        if (!thumbDir.exists()){
            thumbDir.mkdir();
        }
        final File thumbImageFile = new File(thumbDir,"t"+mPictureDataList.get(i).getName()+".jpg");
        if (!imageFile.exists()){
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        FileOutputStream outputStream = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                        outputStream.flush();
                        outputStream.close();
                        Toast.makeText(mContext, "image saved", Toast.LENGTH_SHORT).show();
                        FileOutputStream outputStreamthumb = new FileOutputStream(thumbImageFile);
                        Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap,300,300,false);
                        thumbBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStreamthumb);
                        Toast.makeText(mContext, "thumb saved", Toast.LENGTH_SHORT).show();
                        singleGalleryVH.imageIMG.setImageBitmap(thumbBitmap);
                        outputStreamthumb.flush();
                        outputStreamthumb.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            targets.add(target);
            Toast.makeText(mContext, "ImageFile not exist", Toast.LENGTH_SHORT).show();
            //Toast.makeText(mContext, "imageFile "+imageFile.getPath() , Toast.LENGTH_SHORT).show();
            //Toast.makeText(mContext, "thumbimage "+thumbImageFile.getPath(), Toast.LENGTH_SHORT).show();
            //Picasso.get().load(mPictureDataList.get(i).getURL()).into(singleGalleryVH.imageIMG);
            Picasso.get().load(mPictureDataList.get(i).getURL()).into(target);
        } else {
            Picasso.get().load(Uri.parse("file://"+thumbImageFile.getPath())).into(singleGalleryVH.imageIMG);
        }

        singleGalleryVH.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked on "+mParentPos+" "+i, Toast.LENGTH_SHORT).show();
                //fullScreenImageListener.onFullScreenImageView(dir+"/"+mPictureDataList.get(i).getName()+".jpg");
                fullScreenImageListener.onFullScreenImageViewFromMG(dir+"/"+mPictureDataList.get(i).getName()+".jpg");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPictureDataList.size();
    }

    class SingleGalleryVH extends RecyclerView.ViewHolder{
        private TextView nameTV;
        private ImageView imageIMG;
        public SingleGalleryVH(@NonNull View itemView) {
            super(itemView);
            imageIMG = itemView.findViewById(R.id.img_single_view_gf);
            nameTV = itemView.findViewById(R.id.img_name_single_view_gf);
        }
    }
}
