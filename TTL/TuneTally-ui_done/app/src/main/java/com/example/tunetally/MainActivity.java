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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    FirebaseDatabase database;

    DatabaseReference mdatabase;

    private boolean signInState = true;
    public static boolean deletProcess = false;
    public static FirebaseUser currentUser;

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

        if (getIntent().getStringExtra("delete uid") != null) {
            mdatabase.child("users").child(getIntent().getStringExtra("delete uid")).removeValue();
            UserInformation.clear();
            UserInformation.initiated = false;
            deletProcess = true;
        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
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
                            currentUser = user;
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
                            currentUser = user;
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

    private void updateUI(FirebaseUser user) {
        if (deletProcess) {
            deletProcess = false;
        }
        if(user != null) {
            mdatabase.child("users").child(user.getUid()).child("refresh_token").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (currentUser != null) {
                            Log.i("information for page change", "go to main page");
                            getTokenThroughRefreshToken();
                            //todo go to main page
                            Log.d("updateUI", "change to maina2");
                            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                            intent.putExtra("from", "MainActivity");
                            startActivity(intent);
                        }
                    } else {
                        Log.d("on spotify auth" , "invoked ");
                            Intent intent = new Intent(getApplicationContext(), SpotifyAuthActivity.class);
                            intent.putExtra("uid", currentUser.getUid());
                            startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void getTokenThroughRefreshToken() {
        mdatabase.child("users").child(currentUser.getUid()).child("refresh_token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String mRefreshToken = snapshot.getValue().toString();
                Call mCall;
                OkHttpClient mOkHttpClient = new OkHttpClient();
                // Create a request to get the user profile
                String encodedCredentials = Base64.getEncoder().encodeToString((SpotifyAuthActivity.CLIENT_ID + ":" + SpotifyAuthActivity.CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));

                RequestBody requestBody = new FormBody.Builder()
                        .add("grant_type", "refresh_token")
                        .add("refresh_token", mRefreshToken)
                        .build();

                final Request request = new Request.Builder()
                        .url("https://accounts.spotify.com/api/token")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("Authorization", "Basic " + encodedCredentials)
                        .post(requestBody)
                        .build();

                mCall = mOkHttpClient.newCall(request);

                mCall.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("HTTP", "Failed to fetch data: " + e);
                        Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            final JSONObject jsonObject = new JSONObject(response.body().string());
                            mdatabase.child("users").child(currentUser.getUid()).child("access_token").setValue(jsonObject.getString("access_token"));
                            Log.e("response", jsonObject.toString() );
                        } catch (JSONException e) {
                            Log.d("JSON", "Failed to parse data: " + e);
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    protected void onDestroy() {
        super.onDestroy();
    }
}