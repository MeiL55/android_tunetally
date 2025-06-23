package com.example.tunetally;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.tunetally.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnscrambledActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private CardView LLMPage, explorePage, homePage, pastPage, unscramblePage;
    private ActivityMainBinding binding;
    //private List<String> wordsList = new ArrayList<>();
    private String[] wordsList = new String[5];
    private String currentWord;
    private List<String> visited = new ArrayList<>();
    private boolean isGameOver = false;
    private static final int MAX_NO_OF_WORDS = 5;
    private static final int SCORE_INCREASE = 10;

    private int score=0;
    private int word=1;

    private int totalWord=5;
    private TextView scoreView;
    private TextView countTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 5; i++) {
            wordsList[i] = UserInformation.shortTermArtists.get(i).getArtistName();
        }
        setContentView(R.layout.activity_unscrambled);
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
        TextView scrambledWord=findViewById(R.id.textView_unscrambled_word);
        Random random = new Random();
        int randomIndex = random.nextInt(wordsList.length);
        String randomWord = wordsList[randomIndex];
        currentWord=randomWord;
        visited.add(currentWord);
        scrambledWord.setText(randomWord);
        String leon=scrambledWord.getText().toString();
        char[] leon2=leon.toCharArray();
        shuffleArray(leon2);
        scrambledWord.setText(leon2,0,leon2.length);


        scoreView=findViewById(R.id.score);
        updateScore();

        countTextView = findViewById(R.id.word_count);

        TextInputEditText textInputLayout = findViewById(R.id.text_input_edit_text);
        String playerWord = textInputLayout.getText().toString();

        Button submitButton = findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerWord = textInputLayout.getText().toString();
                if(word < totalWord) {
                    if (isUserWordCorrect(playerWord)) {
                        score += SCORE_INCREASE;
                        updateScore();
                        textInputLayout.setText("");
                        displayShuffleNextWord(scrambledWord);
                        word++;
                        updateCount();
                    } else {
                        textInputLayout.setText("");
                        Toast.makeText(UnscrambledActivity.this, "Incorrect word!", Toast.LENGTH_SHORT).show();
                        displayShuffleNextWord(scrambledWord);
                        word++;
                        updateCount();
                    }
                }
                else {
                    showGameEndsDialog();
                    reinitializeData(scrambledWord);
                }
            }
        });
        Button skipButton = findViewById(R.id.skip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(word < totalWord) {
                    displayShuffleNextWord(scrambledWord);
                    word++;
                    updateCount();
                }
                else {
                    showGameEndsDialog();
                    reinitializeData(scrambledWord);
                }
            }
        });
    }
    private void shuffleArray(char[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    private void displayShuffleNextWord(TextView t) {
        Random random = new Random();
        int randomIndex = random.nextInt(wordsList.length);
        updateRandom(randomIndex);
        char[] tr = currentWord.toCharArray();
        shuffleArray(tr);
        String res = new String(tr);
        t.setText(res);
    }
    private void updateRandom(int num) {
        Random rand = new Random();
        int randomIndex = num;

        while (randomIndex < wordsList.length - 1 && visited.contains(wordsList[randomIndex])) {
            randomIndex++;
        }

        currentWord = wordsList[randomIndex];
        visited.add(currentWord);
    }
    private boolean isUserWordCorrect(String playerWord) {
        if (playerWord.equalsIgnoreCase(currentWord)) {
            score += SCORE_INCREASE;
            updateScore();
            return true;
        }
        return false;
    }
    private void updateScore() {
        String scoreText = "Score: " + score;
        scoreView.setText(scoreText);
    }
    private void updateCount(){
        String countText = word + " of " + totalWord + " words";
        countTextView.setText(countText);
    }
    private void showGameEndsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Game ends")
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }
    public void reinitializeData(TextView t) {
        score=0;
        updateScore();
        currentWord=null;
        word=1;
        updateCount();
        displayShuffleNextWord(t);
        visited.clear();
    }
    private void initWidgets() {
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