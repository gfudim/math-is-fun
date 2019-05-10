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
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static com.example.guy.projectapp1.Utils.MULTI_MODE;
import static com.example.guy.projectapp1.Utils.SEARCH_MODE;
import static com.example.guy.projectapp1.Utils.SINGLE_MODE;
import static com.example.guy.projectapp1.Utils.user;
import static com.example.guy.projectapp1.Utils.id_for_user;

public class HomeFragment extends Fragment {
    Button startBtn;
    Boolean cleaned_exercises = false;
    private DatabaseReference reff;
    protected Boolean first_time = true;
    Exercise current_exercise = new Exercise(0,0,0);
    List<Integer> exercises_ids = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user.last_login = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        startBtn = view.findViewById(R.id.StartBtn);
        if(user.mode == SINGLE_MODE && (user.id_data_base == null || user.id_data_base == "")){
            user.id_data_base = String.format("%s",id_for_user);
            id_for_user++;
        }
        if (user.mode == MULTI_MODE){
            reff = FirebaseDatabase.getInstance().getReference().child("user").child(user.id_data_base);
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (first_time){
                        first_time = false;
                        List<String> keys = new ArrayList<>();
                        List<String> values = new ArrayList<>();
                        for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                            keys.add(keyNode.getKey());
                            values.add(keyNode.getValue().toString());
                            if(keyNode.getKey().equals("age")){
                                user.age = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("correct_answers")){
                                user.correct_answers = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("count_tests")){
                                user.count_tests = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("current_correct_tests_in_row")){
                                user.current_correct_tests_in_row = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("current_count_points_per_day")){
                                user.current_count_points_per_day = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("days_in_row")){
                                user.days_in_row = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("end_exercise")){
                                user.end_exercise = Long.parseLong(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("first_login")){
                                user.first_login = Long.parseLong(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("lang")){
                                user.lang = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("last_day_of_session")){
                                user.last_day_of_session = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("last_login")){
                                user.last_login = Long.parseLong(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("max_correct_tests_in_row")){
                                user.max_correct_tests_in_row = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("max_points_per_day")){
                                user.max_points_per_day = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("mode")){
                                user.mode = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("name")){
                                user.name = keyNode.getValue().toString();
                            }
                            if(keyNode.getKey().equals("search_exercises_done")){
                                user.search_exercises_done = Boolean.valueOf(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("session_done")){
                                user.session_done = Boolean.valueOf(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("session_type")){
                                user.session_type = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("start_exercise")){
                                user.start_exercise = Long.parseLong(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("start_page")){
                                user.start_page = Boolean.valueOf(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("start_session_time")){
                                user.start_session_time = Long.parseLong(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("tests_in_row")){
                                user.tests_in_row = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("total_answers")){
                                user.total_answers = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("wrong_answers")){
                                user.wrong_answers = Integer.parseInt(keyNode.getValue().toString());
                            }
                            if(keyNode.getKey().equals("current_exercises")){
                                for(DataSnapshot ds: keyNode.getChildren()){
                                    for(DataSnapshot ds2: ds.getChildren()){
                                        if (ds2.getKey().equals("count_correct_answers")){
                                            current_exercise.count_correct_answers = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds2.getKey().equals("count_wrong_answers")){
                                            current_exercise.count_wrong_answers = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds2.getKey().equals("displayed_today")){
                                            current_exercise.displayed_today = Boolean.valueOf(ds2.getValue().toString());
                                        }
                                        if (ds2.getKey().equals("exercise_id")){
                                            current_exercise.exercise_id = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds2.getKey().equals("mul1")){
                                            current_exercise.mul1 = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds2.getKey().equals("mul2")){
                                            current_exercise.mul2 = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds2.getKey().equals("time_answered")){
                                            current_exercise.time_answered = Long.parseLong(ds2.getValue().toString());
                                        }
                                        if (ds2.getKey().equals("time_displayed")){
                                            current_exercise.time_displayed = Long.parseLong(ds2.getValue().toString());
                                        }
                                    }
                                }
                                if(current_exercise.mul1 != 0 && !user.current_exercises.contains(current_exercise) && !exercises_ids.contains(current_exercise.exercise_id)){
                                    user.current_exercises.add(current_exercise);
                                    exercises_ids.add(current_exercise.exercise_id);
                                }
                            }
                            if(keyNode.getKey().equals("undefined_exercises")){
                                for(DataSnapshot ds: keyNode.getChildren()){
                                    for(DataSnapshot ds2: ds.getChildren()){
                                        if (ds.getKey().equals("count_correct_answers")){
                                            current_exercise.count_correct_answers = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds.getKey().equals("count_wrong_answers")){
                                            current_exercise.count_wrong_answers = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds.getKey().equals("displayed_today")){
                                            current_exercise.displayed_today = Boolean.valueOf(ds2.getValue().toString());
                                        }
                                        if (ds.getKey().equals("exercise_id")){
                                            current_exercise.exercise_id = Integer.parseInt(ds.getValue().toString());
                                        }
                                        if (ds.getKey().equals("mul1")){
                                            current_exercise.mul1 = Integer.parseInt(ds.getValue().toString());
                                        }
                                        if (ds.getKey().equals("mul2")){
                                            current_exercise.mul2 = Integer.parseInt(ds.getValue().toString());
                                        }
                                        if (ds.getKey().equals("time_answered")){
                                            current_exercise.time_answered = Long.parseLong(ds.getValue().toString());
                                        }
                                        if (ds.getKey().equals("time_displayed")){
                                            current_exercise.time_displayed = Long.parseLong(ds.getValue().toString());
                                        }
                                    }
                                }
                                if(current_exercise.mul1 != 0 && !user.undefined_exercises.contains(current_exercise) && !exercises_ids.contains(current_exercise.exercise_id)){
                                    user.undefined_exercises.add(current_exercise);
                                    exercises_ids.add(current_exercise.exercise_id);
                                }
                            }
                            if(keyNode.getKey().equals("unknown_exercises")){
                                for(DataSnapshot ds: keyNode.getChildren()){
                                    for(DataSnapshot ds2: ds.getChildren()){
                                        if (ds.getKey().equals("count_correct_answers")){
                                            current_exercise.count_correct_answers = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds.getKey().equals("count_wrong_answers")){
                                            current_exercise.count_wrong_answers = Integer.parseInt(ds2.getValue().toString());
                                        }
                                        if (ds.getKey().equals("displayed_today")){
                                            current_exercise.displayed_today = Boolean.valueOf(ds2.getValue().toString());
                                        }
                                        if (ds.getKey().equals("exercise_id")){
                                            current_exercise.exercise_id = Integer.parseInt(ds.getValue().toString());
                                        }
                                        if (ds.getKey().equals("mul1")){
                                            current_exercise.mul1 = Integer.parseInt(ds.getValue().toString());
                                        }
                                        if (ds.getKey().equals("mul2")){
                                            current_exercise.mul2 = Integer.parseInt(ds.getValue().toString());
                                        }
                                        if (ds.getKey().equals("time_answered")){
                                            current_exercise.time_answered = Long.parseLong(ds.getValue().toString());
                                        }
                                        if (ds.getKey().equals("time_displayed")){
                                            current_exercise.time_displayed = Long.parseLong(ds.getValue().toString());
                                        }
                                    }
                                }
                                if(current_exercise.mul1 != 0 && !user.unknown_exercises.contains(current_exercise) && !exercises_ids.contains(current_exercise.exercise_id)){
                                    user.unknown_exercises.add(current_exercise);
                                    exercises_ids.add(current_exercise.exercise_id);
                                }
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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
                        Log.e(TAG,"Starting Search Activity");
                        in = new Intent(getActivity(), SearchPage.class);
                    }
                    else{ //user.session_type = TRAIN_MODE;
                        Log.e(TAG,"Starting Training Activity");
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
