package com.example.guy.projectapp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginPage extends Utils {
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        signInButton = (SignInButton)findViewById(R.id.signInBtn);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, 101)
//                finish();
//            }
//        });
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 101) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//            }
//        }
//    }
//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            startActivity(new Intent(getApplicationContext(), LoadApp.class));
//                            Toast.makeText(getApplicationContext(),"User has signed in", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getApplicationContext(),"User has Failed to signed in", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

        Button switch_to_single_user_btn = (Button) findViewById(R.id.switchToSingleUserBtn);
        switch_to_single_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.mode = SINGLE_MODE;
                saveUser(user);
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                intent.putExtra("new_connection",true);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginPage.this, FirstAppPage.class));
        finish();
    }
}