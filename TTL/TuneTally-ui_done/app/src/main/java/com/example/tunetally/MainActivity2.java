package com.example.tunetally;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {
    private CardView LLMPage, explorePage, homePage, pastPage, unscramblePage;
    private List<WrappedItem> list1;
    private List<WrappedItem> list2 = new ArrayList<>();
    private List<String> temp;
    private ListView myWrappedView, myRecommendationView;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ma2", "onCreate: ");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mdatabsae = database.getReference();
        if (MainActivity.currentUser != null) {
            mdatabsae.child("users").child(MainActivity.currentUser.getUid()).child("access_token").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.e("a", "onDataChange: been here?");
                        UserInformation userInformation = new UserInformation(snapshot.getValue().toString());
                        mdatabsae.child("users").child(MainActivity.currentUser.getUid()).child("wrapped").child("long_term_artists").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d("take the data change", UserInformation.shortTermTracks.toString());
                                //todo implementing updating
                                EdgeToEdge.enable(MainActivity2.this);
                                setContentView(R.layout.activity_home);
                                initWidgets();
                                LLMPage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(), LLMActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                                homePage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                                pastPage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), PastSummariesActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                                unscramblePage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), UnscrambledActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                                reload();
                                if ((UserInformation.longTermArtists.isEmpty() || UserInformation.shortTermArtists.isEmpty() || UserInformation.midTermArtists.isEmpty() || UserInformation.shortTermTracks.isEmpty() || UserInformation.midTermTracks.isEmpty() || UserInformation.longTermTracks.isEmpty() || UserInformation.shortTermTracks.contains(null) || UserInformation.shortTermArtists.contains(null)) && MainActivity.currentUser != null) {


                                    if (!MainActivity.deletProcess) {
                                        String[] playList = new String[5];
                                        for (int i = 0; i < UserInformation.shortTermTracks.size(); i++) {
                                            playList[i] = UserInformation.shortTermTracks.get(i).getPreview_url();
                                        }
                                        if (playList[0] == null || playList[1] == null || playList[2] == null || playList[3] == null || playList[4] == null) {
                                            reload();
                                        } else {
                                            if (!MediaPlayerManager.existed()) {
                                                MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
                                                mediaPlayerManager.setPlaylist(playList);
                                                mediaPlayerManager.play();
                                            }
                                        }
                                    }
                                }
                                initData(); // Populate the list with data
                                setMyWrappedItemAdapter1();
                                setMyWrappedItemAdapter2();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void reload() {
        if (UserInformation.longTermArtists.size() == 0 && UserInformation.shortTermTracks.size() == 0 && MainActivity.currentUser != null) {
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finish();
            if (UserInformation.longTermArtists.size() <= 5 && !MainActivity.deletProcess) {
                startActivity(getIntent());
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_viewAccount) {
            Intent intent = new Intent(getApplicationContext(), ViewAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_updateEmail) { //todo: replace
            Intent intent = new Intent(getApplicationContext(), UpdateEmailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_logout) { //todo: replace
            logoutAccountAction();
            return true;
        } else if (item.getItemId() == R.id.menu_deleteAccount) { //todo: replace
            deleteAccountAction();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void initWidgets() {
        myWrappedView = findViewById(R.id.myWrappedView);
        myRecommendationView = findViewById(R.id.myRecommendationView);
        LLMPage = findViewById(R.id.LLMPageButton);
        homePage = findViewById(R.id.homePageButton);
        pastPage = findViewById(R.id.pastPageButton);
        unscramblePage = findViewById(R.id.unscramblePageButton);
    }

    private void initData() {
        // Initialize list and add sample data
        Log.d("ini data", UserInformation.shortTermTracks.toString());
        list1 = UserInformation.shortTermTracks;
//        temp = UserInformation.shortTermArtists;
//        for (int i = 0; i < temp.size(); i++) {
//            WrappedItem item = new WrappedItem("", temp.get(i), null, null);//todo: image one
//            list2.add(item);
//        }
        list2 = UserInformation.shortTermArtists;
    }

    private void setMyWrappedItemAdapter1() {
        WrappedItemAdapter adapter1 = new WrappedItemAdapter(getApplicationContext(), list1);
        myWrappedView.setAdapter(adapter1);
    }
    private void setMyWrappedItemAdapter2() {
        WrappedItemAdapter adapter2 = new WrappedItemAdapter(getApplicationContext(), list2);
        myRecommendationView.setAdapter(adapter2);
    }
    public void deleteAccountAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("title");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //todo: delete
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mdatabase = database.getReference();
                FirebaseUser user = MainActivity.currentUser;
                MainActivity.currentUser = null;
                MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
                if (mediaPlayerManager != null && MediaPlayerManager.existed()) {
                    mediaPlayerManager.stop();
                    MediaPlayerManager.initialize();
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                String userUid = user.getUid();
                intent.putExtra("delete uid", userUid);
                user.delete();
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void logoutAccountAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("title");
        builder.setMessage("Are you sure you want to log out your account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //todo: delete
                MainActivity.currentUser = null;
                MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
                if (mediaPlayerManager != null && MediaPlayerManager.existed()) {
                    mediaPlayerManager.stop();
                    MediaPlayerManager.initialize();
                }
                UserInformation.clear();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}