package com.example.mahmud.travelmate.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmud.travelmate.GalleryFragment;
import com.example.mahmud.travelmate.Interface.FullScreenImageListener;
import com.example.mahmud.travelmate.Interface.ImageOnClickListener;
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

public class GalleryRvAdapter extends RecyclerView.Adapter<GalleryRvAdapter.ImageVH>{
    private Context mContext;
    private ArrayList<PictureData> mPictureDatas = new ArrayList<>();
    private File mAppPath;
    private ImageOnClickListener imageOnClickListener;
    private FullScreenImageListener fullScreenImageListener;
    private List<Target> targets = new ArrayList<>();

    public GalleryRvAdapter(Context mContext, ArrayList<PictureData> mPictureDatas, File mAppPath, GalleryFragment galleryFragment) {
        this.mContext = mContext;
        this.mPictureDatas = mPictureDatas;
        this.mAppPath = mAppPath;
        imageOnClickListener = galleryFragment;
        fullScreenImageListener = (FullScreenImageListener) mContext;
    }

    @NonNull
    @Override
    public ImageVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.single_image_view,viewGroup,false);
        return new ImageVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageVH imageVH, final int i) {
        File dir = new File(mAppPath+"/thumb/");
        if (!dir.exists()){
            dir.mkdir();
        }
        final File thumbImageFile = new File(dir,"t"+mPictureDatas.get(i).getName()+".jpg");
        final File imageFile = new File(mAppPath,mPictureDatas.get(i).getName()+".jpg");
        if (!thumbImageFile.exists()){

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
                        imageVH.imageIMG.setImageBitmap(thumbBitmap);
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

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            targets.add(target);
            Picasso.get().load(mPictureDatas.get(i).getURL()).into(target);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(mAppPath+"/thumb/t"+mPictureDatas.get(i).getName()+".jpg");
            imageVH.imageIMG.setImageBitmap(bitmap);
        }


        //imageVH.imageIMG.setImageResource(R.drawable.ic_logo);

        imageVH.imageIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreenImageListener.onFullScreenImageView(mAppPath+"/"+mPictureDatas.get(i).getName()+".jpg");
            }
        });
        imageVH.nameTV.setText(mPictureDatas.get(i).getName());
        imageVH.menuTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(mContext,v);
                menu.getMenuInflater().inflate(R.menu.menu_layout,menu.getMenu());
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_delete){
                            imageOnClickListener.onImageDelete(mPictureDatas.get(i));
                        }
                        if (item.getItemId() == R.id.menu_edit){
                            imageOnClickListener.onImageEdit(mPictureDatas.get(i));
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPictureDatas.size();
    }

    class ImageVH extends RecyclerView.ViewHolder{
        TextView nameTV,menuTV;
        ImageView imageIMG;

        public ImageVH(@NonNull View itemView) {
            super(itemView);
            imageIMG = itemView.findViewById(R.id.img_single_view_gf);
            nameTV = itemView.findViewById(R.id.img_name_single_view_gf);
            menuTV = itemView.findViewById(R.id.img_menu_single_view_gf);

        }
    }
}
