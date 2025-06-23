package com.example.tunetally;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewAccountActivity extends AppCompatActivity {
    private CardView LLMPage, explorePage, homePage, pastPage, unscramblePage;
    private TextView vaInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_account);
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
    }
    private void initWidgets() {
        vaInformation = findViewById(R.id.vaInformation); //todo: changeTextView
        vaInformation.setText(UserInformation.userName);
        LLMPage = findViewById(R.id.LLMPageButton);
        homePage = findViewById(R.id.homePageButton);
        pastPage = findViewById(R.id.pastPageButton);
        unscramblePage = findViewById(R.id.unscramblePageButton);
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
            //startActivity(intent);
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