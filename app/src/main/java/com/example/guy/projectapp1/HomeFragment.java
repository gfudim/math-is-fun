package com.example.guy.projectapp1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static com.example.guy.projectapp1.Utils.SEARCH_MODE;
import static com.example.guy.projectapp1.Utils.TRAIN_MODE;
import static com.example.guy.projectapp1.Utils.user;

public class HomeFragment extends Fragment {
    TextView start;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user.last_login = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        // changes the language
        // TODO - maybe change the logic
        start = (TextView) view.findViewById(R.id.StartBtn);
        updateView((String) Paper.book().read("language"));
        //

        Button startBtn = (Button) view.findViewById(R.id.StartBtn);
        //A button to start a new session
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                if(user.session_type == SEARCH_MODE){
                    Log.e(TAG,"Starting Search Activity");
                    in = new Intent(getActivity(), SearchPage.class);
                }
                else{ //user.session_type = TRAIN_MODE;
                    Log.e(TAG,"Starting Training Activity");
                    in = new Intent(getActivity(), TrainPage.class);
                }
                startActivity(in);
            }
        });
        return view;
    }
    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        start.setText(resources.getString(R.string.start));
    }
}
