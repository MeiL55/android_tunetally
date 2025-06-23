package com.example.tunetally;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    FirebaseDatabase database;

    DatabaseReference mdatabase;

    private boolean signInState = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START initialize_auth]
        // Initialize Firebase Auth
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        database = FirebaseDatabase.getInstance();
        mdatabase = database.getReference();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView pageState = (TextView) findViewById(R.id.PageStateHint);
        TextView signText = (TextView) findViewById(R.id.SignText);
        EditText email = (EditText) findViewById(R.id.EmailAdressText);
        EditText password = (EditText) findViewById(R.id.PasswordText);
        Button createBtn = (Button) findViewById(R.id.Create);
        pageState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: The textview");
                signInState = !signInState;
                if(pageState.getText().equals(getString(R.string.signInHint))) {
                    pageState.setText(R.string.signUpHint);
                    createBtn.setText(R.string.signUp);
                    signText.setText(R.string.signUp);
                } else {
                    pageState.setText(R.string.signInHint);
                    createBtn.setText(R.string.signIn);
                    signText.setText(R.string.signIn);
                }
            }
        });
        if(currentUser != null){
            reload();
        }
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signInState) {
                    if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                        if (password.getText().toString().length() >= 6) {
                            signIn(email.getText().toString(), password.getText().toString());
                        } else {
                            Toast.makeText(MainActivity.this, "Password must have six characters or more",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Must enter email and password to create an account",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                        if (password.getText().toString().length() >= 6) {
                            createAccount(email.getText().toString(), password.getText().toString());
                        } else {
                            Toast.makeText(MainActivity.this, "Password must have six characters or more",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Must enter email and password to create an account",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(user.getUid(), user.getEmail().toString());
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "fail to sign up",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void writeNewUser(String uid, String email) {
        mdatabase.child("users").child(uid).child("Email").setValue(email);
    }
    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Sign in successful",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "fail to sign in",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Intent intent = new Intent(this, SpotifyAuthActivity.class);
            intent.putExtra("uid", user.getUid());
            startActivity(intent);
        }
    }
    protected void onDestroy() {
        super.onDestroy();
    }
}