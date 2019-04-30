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
import android.widget.Toast;
//import android.widget.Toast;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
//import static com.example.guy.projectapp1.Utils.MULTI_MODE;
import static com.example.guy.projectapp1.Utils.SEARCH_MODE;
import static com.example.guy.projectapp1.Utils.TRAIN_MODE;
import static com.example.guy.projectapp1.Utils.user;

public class HomeFragment extends Fragment {
    TextView start;
//    private FirebaseDatabase mFirebaseDatabse;
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    private DatabaseReference myRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user.last_login = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        // changes the language
        start = (TextView) view.findViewById(R.id.StartBtn);
        updateView((String) Paper.book().read("language"));
        // TODO - need to switch the user.session_done flag to false at a new day
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

        Button startBtn = (Button) view.findViewById(R.id.StartBtn);
        //A button to start a new session
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                if(user.session_done && user.last_day_of_session == (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) && false){ // TODO - remove last false condition(only for debug)
                    // TODO - if the user doesn't connect for a week, his problem - don't care now...
                    Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
                    Toast.makeText(getActivity(), String.format("%s", context.getResources().getString(R.string.training_over_today)), Toast.LENGTH_LONG).show();
                }
                else{
                    if(user.session_type == SEARCH_MODE){
                        Log.e(TAG,"Starting Search Activity");
                        user.session_done = false;
                        in = new Intent(getActivity(), SearchPage.class);
                    }
                    else{ //user.session_type = TRAIN_MODE;
                        Log.e(TAG,"Starting Training Activity");
                        user.last_day_of_session = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
                        in = new Intent(getActivity(), TrainPage.class);
                    }
                    startActivity(in);
                }
            }
        });
        return view;
    }
//
//    private void updateUser(DataSnapshot dataSnapshot) {
//        for(DataSnapshot ds: dataSnapshot.getChildren()){
//            User temp_user = new User(MULTI_MODE);
//            temp_user.setCorrectAnswers(ds.child(user.id_data_base).getValue(User.class).correct_answers);
//            temp_user.setWrongAnswers(ds.child(user.id_data_base).getValue(User.class).wrong_answers);
//            temp_user.setTotalAnswers(ds.child(user.id_data_base).getValue(User.class).total_answers);
//            temp_user.setMode(ds.child(user.id_data_base).getValue(User.class).mode);
//            temp_user.setLang(ds.child(user.id_data_base).getValue(User.class).lang);
//            temp_user.setAge(ds.child(user.id_data_base).getValue(User.class).age);
//            temp_user.setSessionType(ds.child(user.id_data_base).getValue(User.class).session_type);
//            temp_user.setMaxCorrectTestsInRow(ds.child(user.id_data_base).getValue(User.class).max_correct_tests_in_row);
//            temp_user.setCurrentCorrectTestsInRow(ds.child(user.id_data_base).getValue(User.class).current_correct_tests_in_row);
//            temp_user.setCountTests(ds.child(user.id_data_base).getValue(User.class).count_tests);
//            temp_user.setCurrentCountPointsPerDay(ds.child(user.id_data_base).getValue(User.class).current_count_points_per_day);
//            temp_user.setMaxPointsPerDay(ds.child(user.id_data_base).getValue(User.class).max_points_per_day);
//            temp_user.setFirstLogin(ds.child(user.id_data_base).getValue(User.class).first_login);
//            temp_user.setLastLogin(ds.child(user.id_data_base).getValue(User.class).last_login);
//            temp_user.setStartSessionTime(ds.child(user.id_data_base).getValue(User.class).start_session_time);
//            temp_user.setStartExercise(ds.child(user.id_data_base).getValue(User.class).start_exercise);
//            temp_user.setEndExercise(ds.child(user.id_data_base).getValue(User.class).end_exercise);
//            temp_user.setSessionDone(ds.child(user.id_data_base).getValue(User.class).session_done);
//            temp_user.setStartPage(ds.child(user.id_data_base).getValue(User.class).start_page);
//            temp_user.setName(ds.child(user.id_data_base).getValue(User.class).name);
//            temp_user.setName(ds.child(user.id_data_base).getValue(User.class).name);
//            temp_user.setIdDataBase(ds.child(user.id_data_base).getValue(User.class).id_data_base);
//            temp_user.setKnownExercises(ds.child(user.id_data_base).getValue(User.class).known_exercises);
//            temp_user.setUnknownExercises(ds.child(user.id_data_base).getValue(User.class).unknown_exercises);
//            temp_user.setUndefinedExercises(ds.child(user.id_data_base).getValue(User.class).undefined_exercises);
//            temp_user.setCurrentExercises(ds.child(user.id_data_base).getValue(User.class).current_exercises);
//            // user = temp_user;
//            Toast.makeText(getActivity(),String.format("total_answers%s", temp_user.total_answers),Toast.LENGTH_LONG).show(); // TODO - remove (only for debug)
//        }
//    }
//
    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        start.setText(resources.getString(R.string.start));
    }
}
