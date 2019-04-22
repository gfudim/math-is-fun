package com.example.guy.projectapp1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.paperdb.Paper;

public class MainActivity extends Utils {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //This if statement was added to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
        // Creating new Paper book
        Intent intent = getIntent(); // gets the previously created intent
        Boolean new_connection = intent.getBooleanExtra("new_connection", false);
        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null){
            Paper.book().write("language", "en");
        }
        if(user.name != "" && new_connection){
            showWelcomeMsg();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_stats:
                            selectedFragment = new StatsFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
    private void showWelcomeMsg() {
        Toast toast;
        Context context;
        context = LocaleHelper.setLocale(this, (String) Paper.book().read("language"));
        if(user.lang > 0){
            toast = Toast.makeText(this, user.name + " " + context.getResources().getString(R.string.hello), Toast.LENGTH_LONG);
        }
        else{
            toast = Toast.makeText(this, context.getResources().getString(R.string.hello) + " " + user.name, Toast.LENGTH_LONG);
        }
        toast.setGravity(Gravity.CENTER, 0, -200);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(25);
        messageTextView.setTextColor(Color.BLUE);
        toast.show();
    }

}
