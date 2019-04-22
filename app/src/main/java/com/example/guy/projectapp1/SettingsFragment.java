package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static com.example.guy.projectapp1.Utils.ARABIC;
import static com.example.guy.projectapp1.Utils.ENGLISH;
import static com.example.guy.projectapp1.Utils.HEBREW;
import static com.example.guy.projectapp1.Utils.RUSSIAN;
import static com.example.guy.projectapp1.Utils.SINGLE_MODE;
import static com.example.guy.projectapp1.Utils.user;

public class SettingsFragment extends Fragment {
    TextView name;
    TextView age;
    TextView reset;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragment_view = inflater.inflate(R.layout.fragment_settings, container, false);
        user.start_page = true;
        updateView(fragment_view,(String)Paper.book().read("language"));
        Button multiBtn = (Button) fragment_view.findViewById(R.id.multiModeBtn);
        Button singleBtn = (Button) fragment_view.findViewById(R.id.singleModeBtn);
        if (user.mode == SINGLE_MODE){
            singleBtn.setEnabled(false);
        }
        else{
            multiBtn.setEnabled(false);
        }
        Button resetBtn = (Button) fragment_view.findViewById(R.id.resetBtn); // save the button for reference
        resetBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                saveDetails(view);
                Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        ((MainActivity)getActivity()).saveUser(user);
                    }
                });
                builder.show();
            }
        });
        Button setEnglishBtn = (Button) fragment_view.findViewById(R.id.englishBtn);
        setEnglishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"en", ENGLISH);
            }
        });
        Button setHebrewBtn = (Button) fragment_view.findViewById(R.id.hebrewBtn);
        setHebrewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"iw", HEBREW);
            }
        });
        Button setArabicBtn = (Button) fragment_view.findViewById(R.id.arabicBtn);
        setArabicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"ar", ARABIC);
            }
        });
        Button setRussianBtn = (Button) fragment_view.findViewById(R.id.russianBtn);
        setRussianBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"ru", RUSSIAN);
            }
        });

        return fragment_view;
    }
    private void updateLanguage(View view,String lang, int lang_for_user) {
        Paper.book().write("language", lang);
        updateView(view, (String)Paper.book().read("language"));
        user.lang = lang_for_user;
        ((MainActivity)getActivity()).saveUser(user);
    }
    private void updateView(View view,String language) {
        name = (TextView) view.findViewById(R.id.nameEditText);
        age = (TextView) view.findViewById(R.id.ageEditText);
        reset = (TextView) view.findViewById(R.id.resetBtn);
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        name.setHint(resources.getString(R.string.Name));
        age.setHint(resources.getString(R.string.Age));
        reset.setText(resources.getString(R.string.reset_history));
    }
    private void saveDetails(View view) {
        Context context = LocaleHelper.setLocale(getActivity(), (String)Paper.book().read("language"));
        name = (TextView) view.findViewById(R.id.nameEditText);
        age = (TextView) view.findViewById(R.id.ageEditText);
        if (name != null){
            String user_name = name.getText().toString();
            user.name = user_name;
            ((MainActivity)getActivity()).saveUser(user);
        }
        if (age != null){
            try{
                int user_age = Integer.parseInt(age.getText().toString());
                user.age = user_age;
                ((MainActivity)getActivity()).saveUser(user);
            }
            catch(NumberFormatException ex){
            }
        }
    }

}
