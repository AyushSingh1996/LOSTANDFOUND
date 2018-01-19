package com.lostandfound.lostandfound;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    private ArrayList<String> mName;
    private ArrayList<String> mLocation;
    private ArrayList<String> mImages;

    private Context mContext;
    private static ClickListener clickListener;

    public CardViewAdapter(Context context, ArrayList<String> Name, ArrayList<String> Location, ArrayList<String> Images) {
        mName = Name;
        mContext = context;
        mLocation = Location;
        mImages = Images;


    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextViewName;
        public TextView mTextViewLocation;
        public ImageView mImageViewImage;


        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            v.setOnClickListener(this);

            mTextViewName = (TextView) v.findViewById(R.id.tvcardName);
            mTextViewLocation = (TextView) v.findViewById(R.id.tvcardLocation);
            mImageViewImage = (ImageView) v.findViewById(R.id.cardimageView);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);

        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        CardViewAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    @Override
    public CardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View

        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String location="\n"+"Location : "+mLocation.get(position);
        holder.mTextViewName.setText(mName.get(position));
        holder.mTextViewLocation.setText(location);


        String getUri = mImages.get(position);

        Uri myuri = Uri.parse(getUri);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(myuri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        Glide.with(mContext).load(myuri).override(300,500).fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageViewImage);

        // holder.mImageViewImage


        // Set a random height for TextView
        // holder.mTextView.getLayoutParams().height = getRandomIntInRange(250,75);
        // Set a random color for TextView background
        // holder.mTextView.setBackgroundColor(getRandomHSVColor());
    }

    @Override
    public int getItemCount() {
        return mName.size();
    }


}
