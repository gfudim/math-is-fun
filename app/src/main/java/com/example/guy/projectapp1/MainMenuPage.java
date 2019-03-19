package com.example.guy.projectapp1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;

public class MainMenuPage extends Utils {

    TextView start;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Intent intent = getIntent(); // gets the previously created intent
        Boolean new_connection = intent.getBooleanExtra("new_connection", false);
        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null){
            Paper.book().write("language", "en");
        }
        start = (TextView) findViewById(R.id.StartBtn);
        updateView((String) Paper.book().read("language"));
        user.last_login = System.currentTimeMillis();
        if(user.name != "" && new_connection){
            showWelcomeMsg();
        }
        Button startBtn = (Button) findViewById(R.id.StartBtn); // save the button for reference
        startBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                user.session_type = TRAIN_MODE; // TODO - remove!
                if(user.session_type == SEARCH_MODE){
                    Log.e(TAG,"!!!!! TESTINGGN!!!!!!");
                    startActivity(new Intent(MainMenuPage.this, SearchPage.class));
                }
                else{ //user.session_type = TRAIN_MODE;
                    startActivity(new Intent(MainMenuPage.this, TrainPage.class));
                }
            }
        });

        Button settingsBtn = (Button) findViewById(R.id.main_settings_btn);
        settingsBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuPage.this, SettingsPage.class));
                finish();
            }
        });

        Button statsBtn = (Button) findViewById(R.id.main_stats_btn);
        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuPage.this, StatsPage.class));
            }
        });
    }

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

    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();
        start.setText(resources.getString(R.string.start));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
