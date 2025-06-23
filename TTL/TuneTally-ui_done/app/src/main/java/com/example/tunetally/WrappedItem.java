package com.example.tunetally;

import androidx.annotation.NonNull;

public class WrappedItem{
    private String trackName;

    private String artistName;

    private String imageUrl;

    private String preview_url;

    public WrappedItem(String trackName, String artistName, String imageUrl, String trackId) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.preview_url = trackId;
    }

    public WrappedItem(String artistName, String imageUrl) {
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    public WrappedItem() {

    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPreview_url(){
        return preview_url;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return trackName + "--" +artistName;
    }
}
