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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user.last_login = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        startBtn = view.findViewById(R.id.StartBtn);
        if(user.mode == SINGLE_MODE && (user.id_data_base == null || user.id_data_base == "")){
            user.id_data_base = String.format("%s",id_for_user);
            id_for_user++;
            Log.e(TAG,String.format("id_for_user %s",user.id_data_base));
        }
        if (user.mode == MULTI_MODE){//TODO - added a false to bypass the crash on database.getReference(user_path);
            reff = FirebaseDatabase.getInstance().getReference().child("user").child(user.id_data_base);
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int correct_answers = Integer.parseInt(dataSnapshot.child("correct_answers").getValue().toString());
                    int count_tests = Integer.parseInt(dataSnapshot.child("count_tests").getValue().toString());
                    int current_correct_tests_in_row = Integer.parseInt(dataSnapshot.child("current_correct_tests_in_row").getValue().toString());
                    int current_count_points_per_day = Integer.parseInt(dataSnapshot.child("current_count_points_per_day").getValue().toString());

                    user.correct_answers = correct_answers;
                    user.count_tests = count_tests;
                    user.current_correct_tests_in_row = current_correct_tests_in_row;
                    user.current_count_points_per_day = current_count_points_per_day;

                    // current exercises
                    // ex1
                    int count_correct_answers = Integer.parseInt(dataSnapshot.child("current_exercises").child("0").child("count_correct_answers").getValue().toString());
                    int count_wrong_answers = Integer.parseInt(dataSnapshot.child("current_exercises").child("0").child("count_wrong_answers").getValue().toString());
                    Boolean displayed_today = Boolean.valueOf(dataSnapshot.child("current_exercises").child("0").child("displayed_today").getValue().toString());
                    int exercise_id = Integer.parseInt(dataSnapshot.child("current_exercises").child("0").child("exercise_id").getValue().toString());
                    int mul1 = Integer.parseInt(dataSnapshot.child("current_exercises").child("0").child("mul1").getValue().toString());
                    int mul2 = Integer.parseInt(dataSnapshot.child("current_exercises").child("0").child("mul2").getValue().toString());
                    long time_answered = Long.parseLong(dataSnapshot.child("current_exercises").child("0").child("time_answered").getValue().toString());
                    long time_displayed = Long.parseLong(dataSnapshot.child("current_exercises").child("0").child("time_displayed").getValue().toString());
                    Exercise current_exercises_ex1 = new Exercise(mul1,mul2,exercise_id);
                    current_exercises_ex1.count_correct_answers = count_correct_answers;
                    current_exercises_ex1.count_wrong_answers = count_wrong_answers;
                    current_exercises_ex1.displayed_today = displayed_today;
                    current_exercises_ex1.time_answered = time_answered;
                    current_exercises_ex1.time_displayed = time_displayed;

                    // ex2
                    count_correct_answers = Integer.parseInt(dataSnapshot.child("current_exercises").child("1").child("count_correct_answers").getValue().toString());
                    count_wrong_answers = Integer.parseInt(dataSnapshot.child("current_exercises").child("1").child("count_wrong_answers").getValue().toString());
                    displayed_today = Boolean.valueOf(dataSnapshot.child("current_exercises").child("1").child("displayed_today").getValue().toString());
                    exercise_id = Integer.parseInt(dataSnapshot.child("current_exercises").child("1").child("exercise_id").getValue().toString());
                    mul1 = Integer.parseInt(dataSnapshot.child("current_exercises").child("1").child("mul1").getValue().toString());
                    mul2 = Integer.parseInt(dataSnapshot.child("current_exercises").child("1").child("mul2").getValue().toString());
                    time_answered = Long.parseLong(dataSnapshot.child("current_exercises").child("1").child("time_answered").getValue().toString());
                    time_displayed = Long.parseLong(dataSnapshot.child("current_exercises").child("1").child("time_displayed").getValue().toString());
                    Exercise current_exercises_ex2 = new Exercise(mul1,mul2,exercise_id);
                    current_exercises_ex2.count_correct_answers = count_correct_answers;
                    current_exercises_ex2.count_wrong_answers = count_wrong_answers;
                    current_exercises_ex2.displayed_today = displayed_today;
                    current_exercises_ex2.time_answered = time_answered;
                    current_exercises_ex2.time_displayed = time_displayed;

                    // ex3
                    count_correct_answers = Integer.parseInt(dataSnapshot.child("current_exercises").child("2").child("count_correct_answers").getValue().toString());
                    count_wrong_answers = Integer.parseInt(dataSnapshot.child("current_exercises").child("2").child("count_wrong_answers").getValue().toString());
                    displayed_today = Boolean.valueOf(dataSnapshot.child("current_exercises").child("2").child("displayed_today").getValue().toString());
                    exercise_id = Integer.parseInt(dataSnapshot.child("current_exercises").child("2").child("exercise_id").getValue().toString());
                    mul1 = Integer.parseInt(dataSnapshot.child("current_exercises").child("2").child("mul1").getValue().toString());
                    mul2 = Integer.parseInt(dataSnapshot.child("current_exercises").child("2").child("mul2").getValue().toString());
                    time_answered = Long.parseLong(dataSnapshot.child("current_exercises").child("2").child("time_answered").getValue().toString());
                    time_displayed = Long.parseLong(dataSnapshot.child("current_exercises").child("2").child("time_displayed").getValue().toString());
                    Exercise current_exercises_ex3 = new Exercise(mul1,mul2,exercise_id);
                    current_exercises_ex3.count_correct_answers = count_correct_answers;
                    current_exercises_ex3.count_wrong_answers = count_wrong_answers;
                    current_exercises_ex3.displayed_today = displayed_today;
                    current_exercises_ex3.time_answered = time_answered;
                    current_exercises_ex3.time_displayed = time_displayed;

                    // ex4
                    count_correct_answers = Integer.parseInt(dataSnapshot.child("current_exercises").child("3").child("count_correct_answers").getValue().toString());
                    count_wrong_answers = Integer.parseInt(dataSnapshot.child("current_exercises").child("3").child("count_wrong_answers").getValue().toString());
                    displayed_today = Boolean.valueOf(dataSnapshot.child("current_exercises").child("3").child("displayed_today").getValue().toString());
                    exercise_id = Integer.parseInt(dataSnapshot.child("current_exercises").child("3").child("exercise_id").getValue().toString());
                    mul1 = Integer.parseInt(dataSnapshot.child("current_exercises").child("3").child("mul1").getValue().toString());
                    mul2 = Integer.parseInt(dataSnapshot.child("current_exercises").child("3").child("mul2").getValue().toString());
                    time_answered = Long.parseLong(dataSnapshot.child("current_exercises").child("3").child("time_answered").getValue().toString());
                    time_displayed = Long.parseLong(dataSnapshot.child("current_exercises").child("3").child("time_displayed").getValue().toString());
                    Exercise current_exercises_ex4 = new Exercise(mul1,mul2,exercise_id);
                    current_exercises_ex4.count_correct_answers = count_correct_answers;
                    current_exercises_ex4.count_wrong_answers = count_wrong_answers;
                    current_exercises_ex4.displayed_today = displayed_today;
                    current_exercises_ex4.time_answered = time_answered;
                    current_exercises_ex4.time_displayed = time_displayed;

                    ArrayList <Exercise> temp_group = new ArrayList<>();
                    temp_group.add(current_exercises_ex1);
                    temp_group.add(current_exercises_ex2);
                    temp_group.add(current_exercises_ex3);
                    temp_group.add(current_exercises_ex4);
                    user.current_exercises = temp_group;

                    int days_in_row = Integer.parseInt(dataSnapshot.child("days_in_row").getValue().toString());
                    int lang = Integer.parseInt(dataSnapshot.child("lang").getValue().toString());
                    int last_day_of_session = Integer.parseInt(dataSnapshot.child("last_day_of_session").getValue().toString());
                    int max_correct_tests_in_row = Integer.parseInt(dataSnapshot.child("max_correct_tests_in_row").getValue().toString());
                    int max_points_per_day = Integer.parseInt(dataSnapshot.child("max_points_per_day").getValue().toString());
                    int mode = Integer.parseInt(dataSnapshot.child("mode").getValue().toString());
                    user.days_in_row = days_in_row;
                    user.lang = lang;
                    user.last_day_of_session = last_day_of_session;
                    user.max_correct_tests_in_row = max_correct_tests_in_row;
                    user.max_points_per_day = max_points_per_day;
                    user.mode = mode;
                    Boolean search_exercises_done = Boolean.valueOf(dataSnapshot.child("search_exercises_done").getValue().toString());
                    Boolean session_done = Boolean.valueOf(dataSnapshot.child("session_done").getValue().toString());
                    int session_type = Integer.parseInt(dataSnapshot.child("session_type").getValue().toString());
                    Boolean start_page = Boolean.valueOf(dataSnapshot.child("start_page").getValue().toString());
                    int tests_in_row = Integer.parseInt(dataSnapshot.child("tests_in_row").getValue().toString());
                    int total_answers = Integer.parseInt(dataSnapshot.child("total_answers").getValue().toString());
                    user.search_exercises_done = search_exercises_done;
                    user.session_done = session_done;
                    user.session_type = session_type;
                    user.start_page = start_page;
                    user.tests_in_row = tests_in_row;
                    user.total_answers = total_answers;
                    int wrong_answers = Integer.parseInt(dataSnapshot.child("wrong_answers").getValue().toString());
                    user.wrong_answers = wrong_answers;
                    Log.e(TAG,String.format("222222 ex2: %s",current_exercises_ex3.exercise_id));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        // changes the language
        updateView((String) Paper.book().read("language"));
        //
//        mAuth = FirebaseAuth.getInstance();
//        mFirebaseDatabse = FirebaseDatabase.getInstance();
//        myRef = mFirebaseDatabse.getReference();
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                updateUser(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

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
