package com.example.tunetally;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.concurrent.Task;

public class UserInformation {

    public static boolean initiated = false;

    public static String userName;

    public static List<WrappedItem> shortTermTracks = new ArrayList<>();

    public static List<WrappedItem> midTermTracks = new ArrayList<>();

    public static List<WrappedItem> longTermTracks = new ArrayList<>();

    public static List<WrappedItem> shortTermArtists = new ArrayList<>();

    public static List<WrappedItem> midTermArtists = new ArrayList<>();

    public static List<WrappedItem> longTermArtists = new ArrayList<>();

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static DatabaseReference mdatabase = database.getReference().child("users").child(MainActivity.currentUser.getUid()).child("wrapped");

    public UserInformation(String token) {
        if (!initiated) {
            retrievingUserProfile(token);
            initiated = true;
        }
    }

    public static void clear() {
        initiated = false;

        userName = "";

        shortTermTracks = new ArrayList<>();

        midTermTracks = new ArrayList<>();

        longTermTracks = new ArrayList<>();

        shortTermArtists = new ArrayList<>();

        midTermArtists = new ArrayList<>();

        longTermArtists = new ArrayList<>();
    }


    public void retrievingUserProfile(String token) {
        Call mCall;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    userName = jsonObject.getString("display_name");
                    retrievingShortTermTrack(token);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });
    }
    public static void retrievingShortTermTrack(String token) {


        if (shortTermArtists.size() == 0) {
            Call mCall;
            OkHttpClient mOkHttpClient = new OkHttpClient();
            // Create a request to get the user profile
            final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=5")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();


            mCall = mOkHttpClient.newCall(request);

            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("HTTP", "Failed to fetch data: " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("shortterm response", jsonObject.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("items");
                        int jsonArrSize = jsonArray.length();
                        for (int i = 0; i < jsonArrSize; i++) {
                            JSONObject trackJson = jsonArray.getJSONObject(i);
                            //track name
                            String trackName = trackJson.getString("name");
                            //artist name
                            JSONArray artistsJsonArray = trackJson.getJSONArray("artists");
                            String artistName = "";
                            for (int j = 0; j < artistsJsonArray.length(); j++) {
                                JSONObject artist = artistsJsonArray.getJSONObject(j);
                                if (j == artistsJsonArray.length() - 1) {
                                    artistName = artistName + artist.getString("name");
                                } else {
                                    artistName = artistName + artist.getString("name") + ", ";
                                }
                            }
                            //image url
                            JSONArray imageUrlsJson = trackJson.getJSONObject("album").getJSONArray("images");
                            String imageUrl = imageUrlsJson.getJSONObject(0).getString("url");
                            String preview_url = trackJson.getString("preview_url");
                            //creating track
                            WrappedItem track = new WrappedItem(trackName, artistName, imageUrl, preview_url);
                            Log.e("shortterm", "onResponse: been here" + track.toString());
                            shortTermTracks.add(track);
                        }
                        shortTermTracks = shortTermTracks.subList(0, 5);
                        mdatabase.child("short_term_tracks").setValue(shortTermTracks.toString());
                        retrievingMediumTermTrack(token);
                    } catch (JSONException e) {
                        Log.d("JSON", "Failed to parse data fail to parse short term data: " + e);
                    }
                }
            });
        }
    }
    public static void retrievingMediumTermTrack(String token) {
        Log.d("medium track", "medium track thing ");

        Call mCall;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=5")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        mCall = mOkHttpClient.newCall(request);


        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    int jsonArrSize = jsonArray.length();
                    for (int i = 0; i < jsonArrSize; i++) {
                        JSONObject trackJson = jsonArray.getJSONObject(i);
                        //track name
                        String trackName = trackJson.getString("name");
                        //artist name
                        JSONArray artistsJsonArray = trackJson.getJSONArray("artists");
                        String artistName = "";
                        for (int j = 0; j < artistsJsonArray.length(); j++) {
                            JSONObject artist = artistsJsonArray.getJSONObject(j);
                            if (j == artistsJsonArray.length() - 1) {
                                artistName = artistName + artist.getString("name");
                            } else {
                                artistName = artistName + artist.getString("name") + ", ";
                            }
                        }
                        //image url
                        JSONArray imageUrlsJson = trackJson.getJSONObject("album").getJSONArray("images");
                        String imageUrl = imageUrlsJson.getJSONObject(0).getString("url");
                        String preview_url = trackJson.getString("preview_url");
                        //creating track
                        WrappedItem track = new WrappedItem(trackName, artistName, imageUrl, preview_url);
                        midTermTracks.add(track);
                    }
                    midTermTracks = midTermTracks.subList(0,5);
                    mdatabase.child("medium_term_tracks").setValue(midTermTracks);
                    retrievingLongTermTrack(token);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                Log.d("track inside", shortTermTracks.toString());
            }
        });
    }
    public static void retrievingLongTermTrack(String token) {


        Call mCall;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=long_term&limit=5")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    int jsonArrSize = jsonArray.length();
                    for (int i = 0; i < jsonArrSize; i++) {
                        JSONObject trackJson = jsonArray.getJSONObject(i);
                        //track name
                        String trackName = trackJson.getString("name");
                        //artist name
                        JSONArray artistsJsonArray = trackJson.getJSONArray("artists");
                        String artistName = "";
                        for (int j = 0; j < artistsJsonArray.length(); j++) {
                            JSONObject artist = artistsJsonArray.getJSONObject(j);
                            if (j == artistsJsonArray.length() - 1) {
                                artistName = artistName + artist.getString("name");
                            } else {
                                artistName = artistName + artist.getString("name") + ", ";
                            }
                        }
                        //image url
                        JSONArray imageUrlsJson = trackJson.getJSONObject("album").getJSONArray("images");
                        String imageUrl = imageUrlsJson.getJSONObject(0).getString("url");
                        String preview_url = trackJson.getString("preview_url");
                        //creating track
                        WrappedItem track = new WrappedItem(trackName, artistName, imageUrl, preview_url);
                        longTermTracks.add(track);
                    }
                    longTermTracks = longTermTracks.subList(0,5);
                    mdatabase.child("long_term_tracks").setValue(longTermTracks);
                    retrievingShortTermArtists(token);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                Log.d("track inside", shortTermTracks.toString());
            }
        });
    }


    public static void retrievingShortTermArtists(String token) {

//

        Call mCall;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=short_term&limit=5")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.d("artist response", jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    int jsonArrSize = jsonArray.length();
                    for (int i = 0; i < jsonArrSize; i++) {
//                        shortTermArtists.add(jsonArray.getJSONObject(i).getString("name"));
                        String name = jsonArray.getJSONObject(i).getString("name");
                        String imageUrl = jsonArray.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url");
                        shortTermArtists.add(new WrappedItem(" ", name, imageUrl, null));

                    }
                    shortTermArtists = shortTermArtists.subList(0,5);
                    mdatabase.child("short_term_artists").setValue(shortTermArtists);
                    retrievingMediumTermArtists(token);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                Log.d("track inside", shortTermTracks.toString());
            }
        });
    }

    public static void retrievingMediumTermArtists(String token) {
//        midTermArtists = midTermArtists.subList(0,4);

        Call mCall;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=medium_term&limit=5")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    int jsonArrSize = jsonArray.length();
                    for (int i = 0; i < jsonArrSize; i++) {
                        String name = jsonArray.getJSONObject(i).getString("name");
                        String imageUrl = jsonArray.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url");
                        midTermArtists.add(new WrappedItem(" ", name, imageUrl, null));
                    }
                    midTermArtists = midTermArtists.subList(0,5);
                    mdatabase.child("medium_term_artists").setValue(midTermArtists);
                    retrievingLongTermArtists(token);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                Log.d("track inside", shortTermTracks.toString());
            }
        });
    }

    public static void retrievingLongTermArtists(String token) {
//        longTermArtists = longTermArtists.subList(0,4);

        Call mCall;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=long_term&limit=5")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    int jsonArrSize = jsonArray.length();
                    for (int i = 0; i < jsonArrSize; i++) {
                        String name = jsonArray.getJSONObject(i).getString("name");
                        String imageUrl = jsonArray.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url");
                        longTermArtists.add(new WrappedItem(" ", name, imageUrl, null));
                    }
                    longTermArtists = longTermArtists.subList(0,5);
                    mdatabase.child("long_term_artists").setValue(longTermArtists);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });
    }
}
