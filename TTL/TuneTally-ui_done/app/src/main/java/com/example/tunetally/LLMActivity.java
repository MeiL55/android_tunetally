package com.example.tunetally;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LLMActivity extends AppCompatActivity {
    private com.example.tunetally.openAI_API api;
    private TextView inputText;
    private CardView LLMPage, homePage, pastPage, unscramblePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llmactivity);

        api = new com.example.tunetally.openAI_API("Initial system message for the chat");

        final TextView requestInput = findViewById(R.id.inputText);
        final CardView submitButton = findViewById(R.id.submitButton);
        final TextView responseView = findViewById(R.id.resultText);
        responseView.setMovementMethod(new ScrollingMovementMethod());
        initWidgets();
        submitButton.setOnClickListener(v -> {
            String question = generatePrompt(UserInformation.longTermArtists);
            //requestInput.setText(question);
            api.getCompletionRequest(question, new com.example.tunetally.openAI_API.LLMResponse() {
                @Override
                public void receive(String response) {
                    Log.e("response", "receive: " + response);
                    runOnUiThread(() -> {
                        responseView.setText(response);

                    });
                }
            });
            Toast.makeText(LLMActivity.this, "Generating...",
                    Toast.LENGTH_SHORT).show();
        });
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
        LLMPage = findViewById(R.id.LLMPageButton);
        homePage = findViewById(R.id.homePageButton);
        pastPage = findViewById(R.id.pastPageButton);
        unscramblePage = findViewById(R.id.unscramblePageButton);
    }
    private String generatePrompt(List<WrappedItem> artistList) {
        String res = "";
        for (int i = 0; i < artistList.size(); i++) {
            String part = artistList.get(i).getArtistName();
            if (i == artistList.size() - 1) {
                res += part;
            } else {
                res += part + ",";
            }
        }
        return String.format("Here is a list of my favorite artists: . Using the list of artists " +
                "I have given you above, describe how someone who listens to this kind of music " +
                "tends to behave. How would the person who listens to this music act? " +
                "How would they dress? How would they think? Give me a response limited to " +
                "200 words.", res);
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