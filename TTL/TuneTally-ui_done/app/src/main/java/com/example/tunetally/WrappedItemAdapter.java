package com.example.tunetally;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.util.List;

public class WrappedItemAdapter extends ArrayAdapter<WrappedItem> {
    private TextView trackTV, artistTV;

    public WrappedItemAdapter(@NonNull Context context, List<WrappedItem> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wrapped_item, parent, false);
        }

        // Get the item at the current position
        WrappedItem item = getItem(position);

        // Set data to views
        trackTV = convertView.findViewById(R.id.track_name);
        artistTV = convertView.findViewById(R.id.artist);
        new DownloadImageTask((ImageView) convertView.findViewById(R.id.scrapedImage))
                .execute(item.getImageUrl());
        trackTV.setText(item.getTrackName());

        artistTV.setText(item.getArtistName());
        return convertView;
    }
}
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}