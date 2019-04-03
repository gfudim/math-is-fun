package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.paperdb.Paper;

public class SettingsPage extends Utils {

    TextView name;
    TextView age;
    TextView reset;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        user.start_page = true;
        updateView((String)Paper.book().read("language"));
        Button multiBtn2 = (Button) findViewById(R.id.multiBtn2);
        Button singleBtn2 = (Button) findViewById(R.id.singleBtn2);
        if (user.mode == SINGLE_MODE){
            singleBtn2.setEnabled(false);
        }
        else{
            multiBtn2.setEnabled(false);
        }
        Button homeBtn = (Button) findViewById(R.id.settings_home_btn); // save the button for reference
        homeBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                saveDetails();
                startActivity(new Intent(SettingsPage.this, MainMenuPage.class));
            }
        });

        Button statsBtn = (Button) findViewById(R.id.settings_stats_btn);
        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
                startActivity(new Intent(SettingsPage.this, StatsPage.class));
            }
        });

        Button resetBtn = (Button) findViewById(R.id.resetBtn); // save the button for reference
        resetBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                saveDetails();
                Context context = LocaleHelper.setLocale(SettingsPage.this, (String)Paper.book().read("language"));
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsPage.this);
                builder.setCancelable(true);
                builder.setTitle(context.getString(R.string.reset_notice));
                builder.setMessage(context.getString(R.string.reset_warning));
                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.resetHistory();
                        saveUser(user);
                    }
                });
                builder.show();
            }
        });
        Button setEnglishBtn = (Button) findViewById(R.id.english_btn);
        setEnglishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage("en", ENGLISH);
            }
        });
        Button setHebrewBtn = (Button) findViewById(R.id.hebrew_btn);
        setHebrewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage("iw", HEBREW);
            }
        });
        Button setArabicBtn = (Button) findViewById(R.id.arabic_btn);
        setArabicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage("ar", ARABIC);
            }
        });
        Button setRussianBtn = (Button) findViewById(R.id.russian_btn);
        setRussianBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage("ru", RUSSIAN);
            }
        });
    }

    private void updateLanguage(String lang, int lang_for_user) {
        Paper.book().write("language", lang);
        updateView((String)Paper.book().read("language"));
        user.lang = lang_for_user;
        saveUser(user);
    }

    private void saveDetails() {
        Context context = LocaleHelper.setLocale(this, (String)Paper.book().read("language"));
        name = (TextView) findViewById(R.id.NameEditText);
        age = (TextView) findViewById(R.id.AgeEditText);
        if (name != null){
            String user_name = name.getText().toString();
            user.name = user_name;
            saveUser(user);
        }
        if (age != null){
            try{
                int user_age = Integer.parseInt(age.getText().toString());
                user.age = user_age;
                saveUser(user);
            }
            catch(NumberFormatException ex){
            }
        }
    }

    private void updateView(String language) {
        name = (TextView) findViewById(R.id.NameEditText);
        age = (TextView) findViewById(R.id.AgeEditText);
        reset = (TextView) findViewById(R.id.resetBtn);
        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();
        name.setHint(resources.getString(R.string.Name));
        age.setHint(resources.getString(R.string.Age));
        reset.setText(resources.getString(R.string.reset_history));
    }

    @Override
    public void onBackPressed() {
        if(user.start_page){
            startActivity(new Intent(SettingsPage.this, MainMenuPage.class));
        }
        else{
            startActivity(new Intent(SettingsPage.this, StatsPage.class));
        }
        finish();
    }
}
