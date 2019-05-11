package com.example.guy.projectapp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


public class LoginPage extends Utils {
    private static final int REQUEST_CODE = 8448;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button switch_to_single_user_btn = findViewById(R.id.switchToSingleUserBtn);
        switch_to_single_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new User(SINGLE_MODE);
                saveUserToDevice(user);
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                intent.putExtra("new_connection",true);
                startActivity(intent);
            }
        });

        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        Button btn_sign_in = findViewById(R.id.signInBtn);
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.mode = MULTI_MODE;
                showSignInOptions();
            }
        });
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.ThemeForFirebase)
                .build(), REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                FirebaseUser user_loggin = FirebaseAuth.getInstance().getCurrentUser();
                assert user_loggin != null;
                user.id_data_base = user_loggin.getUid();
                user.email = user_loggin.getEmail();
                saveUserToDevice(user);
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                intent.putExtra("new_connection",true);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginPage.this, FirstAppPage.class));
        finish();
    }
}