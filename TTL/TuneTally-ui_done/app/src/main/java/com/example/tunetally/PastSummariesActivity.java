package com.example.tunetally;

import android.content.DialogInterface;
import android.content.Intent;
import java.util.List;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.media.MediaPlayer;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class PastSummariesActivity extends AppCompatActivity {
    private CardView LLMPage, explorePage, homePage, pastPage, unscramblePage;
    private List<WrappedItem> list1, list2;
    private ListView pastTrackView, pastArtistView;
    private CheckBox checkBox1;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mdatabsae = database.getReference();
        if (MainActivity.currentUser != null){
            mdatabsae.child("users").child(MainActivity.currentUser.getUid()).child("access_token").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e("a", "onDataChange: been here?");
                    UserInformation userInformation = new UserInformation(snapshot.getValue().toString());
                    mdatabsae.child("users").child(MainActivity.currentUser.getUid()).child("wrapped").child("long_term_artists").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("take the data change", UserInformation.shortTermTracks.toString());
                            //todo implementing updating
                            EdgeToEdge.enable(PastSummariesActivity.this);
                            setContentView(R.layout.activity_past_summary);
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
                            if (UserInformation.longTermArtists.size() == 0) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                homePage.performClick();
                            }
                            initData(); // Populate the list with data
                            setWrappedItemAdapter1();
                            setWrappedItemAdapter2();
                            String[] playList = new String[5];
                            for (int i = 0; i < 5; i++) {
                                playList[i] = UserInformation.shortTermTracks.get(i).getPreview_url();
                            }
                            if (!MediaPlayerManager.existed()) {
                                MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
                                mediaPlayerManager.setPlaylist(playList);
                                mediaPlayerManager.play();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
        pastTrackView = findViewById(R.id.pastTrackView1);
        pastArtistView = findViewById(R.id.pastArtistView1);
        checkBox1 = findViewById(R.id.checkbox1);
        LLMPage = findViewById(R.id.LLMPageButton);
        homePage = findViewById(R.id.homePageButton);
        pastPage = findViewById(R.id.pastPageButton);
        unscramblePage = findViewById(R.id.unscramblePageButton);
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform actions when the checkbox is clicked
                if (checkBox1.isChecked()) {
                    Intent intent = new Intent(getApplicationContext(), PastSummariesActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Log.d("CheckBox", "Checked");
                } else {
                    // Checkbox is unchecked
                    Log.d("CheckBox", "Unchecked");
                }
            }
        });
    }
    private void initData() {
        Log.d("ini data", UserInformation.shortTermTracks.toString());
        list1 = UserInformation.midTermTracks;
        list2 = UserInformation.midTermArtists;
    }
    private void setWrappedItemAdapter1() {
        WrappedItemAdapter adapter = new WrappedItemAdapter(getApplicationContext(), list1);
        pastTrackView.setAdapter(adapter);
    }
    private void setWrappedItemAdapter2() {
        WrappedItemAdapter adapter = new WrappedItemAdapter(getApplicationContext(), list2);
        pastArtistView.setAdapter(adapter);
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