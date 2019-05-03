package com.example.guy.projectapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class FirstAppPage extends Utils {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_app_page);

        Button singleBtn = findViewById(R.id.singleBtn); // save the button for reference
        singleBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                newProfile(SINGLE_MODE);
            }
        });

        Button multiBtn = findViewById(R.id.multiBtn); // save the button for reference
        multiBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                newProfile(MULTI_MODE);
            }
        });
        }
    protected void newProfile(int state){
        user.mode = state;
        saveUser(user);
        startActivity(new Intent(FirstAppPage.this, LoadApp.class));
        finish();
        }
}
