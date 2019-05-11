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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import io.paperdb.Paper;

import static com.example.guy.projectapp1.Utils.MULTI_MODE;
import static com.example.guy.projectapp1.Utils.SEARCH_MODE;
import static com.example.guy.projectapp1.Utils.SINGLE_MODE;
import static com.example.guy.projectapp1.Utils.simpleDateFormat;
import static com.example.guy.projectapp1.Utils.user;
import static com.example.guy.projectapp1.Utils.id_for_user;

public class HomeFragment extends Fragment {
    Button startBtn;
    Boolean cleaned_exercises = false;
    private DatabaseReference reff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        startBtn = view.findViewById(R.id.StartBtn);
        if(user.mode == SINGLE_MODE && (user.id_data_base == null || user.id_data_base.equals(""))){
            user.id_data_base = String.format("%s",id_for_user);
            id_for_user++;
        }
        if (user.mode == MULTI_MODE){
            reff = FirebaseDatabase.getInstance().getReference().child("user").child(user.id_data_base);
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user == null){
                        user = new User(MULTI_MODE);
                        FirebaseUser user_loggin = FirebaseAuth.getInstance().getCurrentUser();
                        assert user_loggin != null;
                        user.id_data_base = user_loggin.getUid();
                        user.email = user_loggin.getEmail();
                        user.exerciseGroupWithMaxVar();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        user.last_login = simpleDateFormat.format(Calendar.getInstance().getTime());
        // changes the language
        updateView((String) Paper.book().read("language"));

        //A button to start a new session
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                if((user.session_done || user.last_day_of_session == (Calendar.getInstance().get(Calendar.DAY_OF_WEEK))) && false){ // TODO - remove last false condition(only for debug)
                    // TODO - if the user doesn't connect for a week, his problem - don't care now...
                    Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
                    Toast.makeText(getActivity(), String.format("%s", context.getResources().getString(R.string.training_over_today)), Toast.LENGTH_LONG).show();
                    if (cleaned_exercises){
                        uncheckDisplayedExercises();
                    }
                }
                else{
                    // new day - new session
                    user.session_done = false;
                    user.current_count_points_per_day = 0;
                    if(user.session_type == SEARCH_MODE){
                        user.search_exercises_done = false;
                        cleaned_exercises = false;
                        user.exerciseGroupWithMaxVar();
                        in = new Intent(getActivity(), SearchPage.class);
                    }
                    else{ //user.session_type = TRAIN_MODE;
                        in = new Intent(getActivity(), TrainPage.class);
                    }
                    startActivity(in);
                }
            }
        });
        return view;
    }

    public void uncheckDisplayedExercises(){
        int i;
        for (i=0;i<user.current_exercises.size(); i++){
            user.current_exercises.get(i).displayed_today = false;
        }
        for (i=0;i<user.known_exercises.size(); i++){
            user.known_exercises.get(i).displayed_today = false;
        }
        for (i=0;i<user.undefined_exercises.size(); i++){
            user.undefined_exercises.get(i).displayed_today = false;
        }
        for (i=0;i<user.unknown_exercises.size(); i++){
            user.unknown_exercises.get(i).displayed_today = false;
        }
        cleaned_exercises = true;
    }

    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        startBtn.setText(resources.getString(R.string.start));
    }
}
