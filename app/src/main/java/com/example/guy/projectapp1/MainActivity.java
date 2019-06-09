package com.example.guy.projectapp1;

import android.app.AlertDialog;
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

        // if it's the first time, print a welcome message
//        Boolean new_connection = intent.getBooleanExtra("new_connection", false);
//        if(new_connection){
//            showWelcomeMsg();
//        }
        if(user.gotPrize()){
            showPrizeMsg();
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

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
    private void showWelcomeMsg() {
        Toast toast;
        Context context = LocaleHelper.setLocale(this, getLanguage());
        toast = Toast.makeText(this, context.getResources().getString(R.string.hello), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, -250);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(25);
        messageTextView.setTextColor(Color.BLUE);
        toast.show();
    }
    private void showPrizeMsg() {
        Context context = LocaleHelper.setLocale(MainActivity.this, getLanguage());
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(context.getResources().getString(R.string.congratulations));
        String message=context.getResources().getString(R.string.you_got_a_prize);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), null);
        builder.show();
    }
}
