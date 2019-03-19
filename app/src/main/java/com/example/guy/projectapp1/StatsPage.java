package com.example.guy.projectapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StatsPage extends Utils {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        user.start_page = false;
        Button homeBtn = (Button) findViewById(R.id.stats_home_btn); // save the button for reference
        homeBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatsPage.this, MainMenuPage.class));
            }
        });

        Button settingsBtn = (Button) findViewById(R.id.stats_setting_btn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatsPage.this, SettingsPage.class));
            }
        });
    }
}
