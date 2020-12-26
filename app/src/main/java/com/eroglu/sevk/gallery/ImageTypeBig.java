package com.eroglu.sevk.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.eroglu.sevk.BigPictureActivity;
import com.eroglu.sevk.MainActivity;
import com.eroglu.sevk.R;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@NonReusable
@Layout(R.layout.gallery_item_big)
public class ImageTypeBig {
    private String TAG = MainActivity.class.getSimpleName();
    @View(R.id.imageView)
    private ImageView imageView;
    private String mUlr;
    private Context mContext;
    private PlaceHolderView mPlaceHolderView;

    public ImageTypeBig(Context context, PlaceHolderView placeHolderView, String ulr) {
        mContext = context;
        mPlaceHolderView = placeHolderView;
        mUlr = ulr;
    }

    @Resolve
    private void onResolved() {
        Glide.with(mContext).load(mUlr).into(imageView);
    }

    @Click(R.id.imageView)
    public void onClick() {


        Intent intent = new Intent(mContext, BigPictureActivity.class);
        intent.putExtra("Path", this.mUlr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }

}
