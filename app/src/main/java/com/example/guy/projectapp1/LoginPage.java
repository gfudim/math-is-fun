package com.example.guy.projectapp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class LoginPage extends Utils {
    private static final int REQUEST_CODE = 8448;
    private DatabaseReference reff;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button switch_to_single_user_btn = findViewById(R.id.switchToSingleUserBtn);
        switch_to_single_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int temp_lang = user.lang;
                user = new User(SINGLE_MODE);
                //user.lang = temp_lang;
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
            if(true || resultCode == RESULT_OK){
                FirebaseUser user_loggin = FirebaseAuth.getInstance().getCurrentUser();
                assert user_loggin != null;
                user.email = user_loggin.getEmail();
                user.id_data_base = user.email.replace(".","");
                reff = FirebaseDatabase.getInstance().getReference().child("user");
                reff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(user.id_data_base.replace(".",""))){
                            user = new User(MULTI_MODE);
                            FirebaseUser user_loggin = FirebaseAuth.getInstance().getCurrentUser();
                            assert user_loggin != null;
                            user.email = user_loggin.getEmail();
                            user.id_data_base = user.email.replace(".","");;
                        }
                        else{
                            user = dataSnapshot.child(user.id_data_base.replace(".","")).getValue(User.class);
                        }
                        user.last_login = simpleDateFormat.format(Calendar.getInstance().getTime());
                        if (user.known_exercises == null){
                            user.known_exercises = new ArrayList<>();
                        }
                        if (user.unknown_exercises == null){
                            user.unknown_exercises = new ArrayList<>();
                        }
                        if (user.undefined_exercises == null){
                            user.undefined_exercises = new ArrayList<>();
                        }
                        if (user.current_exercises == null){
                            user.current_exercises = new ArrayList<>();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
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