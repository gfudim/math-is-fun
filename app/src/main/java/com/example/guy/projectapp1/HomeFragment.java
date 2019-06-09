package com.example.guy.projectapp1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import io.paperdb.Paper;

import static com.example.guy.projectapp1.Utils.SEARCH_MODE;
import static com.example.guy.projectapp1.Utils.SINGLE_MODE;
import static com.example.guy.projectapp1.Utils.simpleDateFormat;
import static com.example.guy.projectapp1.Utils.user;

public class HomeFragment extends Fragment {
    Button startBtn;
    private DatabaseReference reff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        startBtn = view.findViewById(R.id.StartBtn);
        if(user.mode == SINGLE_MODE && (user.id_data_base == null || user.id_data_base.equals(""))){
            user.last_login = simpleDateFormat.format(Calendar.getInstance().getTime());
            ((MainActivity)getActivity()).saveUserToDevice(user);
            ((MainActivity)getActivity()).saveUser(user);
        }
        // changes the language
        updateView((String) Paper.book().read("language"));
        if(user.hadSessionToday()){
            ((View)startBtn).setAlpha(.5f);
        }
        else {
            ((View)startBtn).setAlpha(1f);
        }
        //A button to start a new session
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                if(user.hadSessionToday()){
                    Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
                    Toast.makeText(getActivity(), String.format("%s", context.getResources().getString(R.string.training_over_today)), Toast.LENGTH_LONG).show();
                    ((View)startBtn).setAlpha(.5f);
                    ((MainActivity)getActivity()).saveUserToDevice(user);
                    ((MainActivity)getActivity()).saveUser(user);
                }
                else{
                    user.setStartSession();
                    if(user.session_type == SEARCH_MODE){
                        in = new Intent(getActivity(), SearchPage.class);
                    }
                    else{ //user.session_type = TRAIN_MODE;
                        in = new Intent(getActivity(), TrainPage.class);
                    }
                    startActivity(in);
                    getActivity().finish();
                }
            }
        });
        return view;
    }

    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        startBtn.setText(resources.getString(R.string.start));
    }
}
