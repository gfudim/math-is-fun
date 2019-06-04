package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Calendar;

import io.paperdb.Paper;


public class TrainPage extends Utils {
    TextView submit;
    EditText answer;
    Exercise exercise = user.getNextExercise();
    long start_input_answer;
    long current_milli_train_timer;
    int user_answer = 0;
    Boolean builder_flag = false;
    CountDownTimer train_counter;
    MediaPlayer exercise_media = new MediaPlayer();
    MediaPlayer exercise_repeat_media = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        submit = findViewById(R.id.SubmitBtn);
        answer = findViewById(R.id.InputEditText);
        updateView();
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainPage.this);
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.start_training));
        builder.setMessage(context.getResources().getString(R.string.start_training));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                train_counter = OurCountDownTimer(SESSION_MILLI_DURATION);
                train_counter.start();
                showExercise(exercise);
                user.start_session_time = simpleDateFormat.format(Calendar.getInstance().getTime());
            }
        });
        UIUtil.showKeyboard(this,answer);
        builder.show();
        Button submitBtn = findViewById(R.id.SubmitBtn); // save the button for reference
        submitBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                try{
                    user_answer =  Integer.parseInt(answer.getText().toString());
                    handleAnswer();
                }catch(NumberFormatException ignored){ // if clicked submit without input
                }
            }
        }
        );
        answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    try{
                        user_answer =  Integer.parseInt(answer.getText().toString());
                        handleAnswer();
                    }
                    catch (NumberFormatException ignored){
                    }
                }
                UIUtil.showKeyboard(TrainPage.this,answer);
                return true;
            }
        });
    }

    public void handleAnswer(){
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        exercise.time_answered = System.currentTimeMillis();
        if ((exercise.time_answered - exercise.time_displayed)/1000 > MAX_TIME_TO_ANSWER){
            handleOverTimeAnswer(false);
        }
        else{
            try{
                user.setAnswer(exercise, user_answer);
                if (user.index % 4 == 0){
                    if (!user.testing_done){
                        timerPause();
                        AlertDialog.Builder builder = new AlertDialog.Builder(TrainPage.this);
                        builder.setTitle(context.getResources().getString(R.string.well_done));
                        builder.setMessage(context.getResources().getString(R.string.let_try_again));
                        builder.setCancelable(false);
                        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                timerResume();
                                exercise.time_displayed = System.currentTimeMillis();
                                builder_flag = false;
                                exercise = user.getNextExercise();
                                showExercise(exercise);
                            }
                        });
                        TextView show_exercise = findViewById(R.id.ExerciseTextView);
                        show_exercise.setText("-");
                        builder.show();
                        builder_flag = true;
                    }
                }
                saveUser(user);
                if (user.testing_done){
                    if (user_answer == (exercise.result())) { /*correct answer*/
                        toastAfterAnswer(true, true, exercise);
                    }
                    else {
                        toastAfterAnswer(false, true, exercise);
                    }
                }
                if(!user.session_done){ //backup -> && user.current_exercises.size() != 0
                    if (user.total_answers % 4 == 0) {
                        saveUser(user);
                    }
                    answer.setText("");
                    if (!builder_flag) {
                        exercise = user.getNextExercise();
                        showExercise(exercise);
                    }
                }
            }
            catch(NumberFormatException ignored){
            }
        }
        UIUtil.showKeyboard(this,answer);
    }

    public void time_for_answer_train(View view){
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        start_input_answer = System.currentTimeMillis();
        if ((start_input_answer - exercise.time_displayed)/1000 > 5){
            Toast.makeText(TrainPage.this, context.getResources().getString(R.string.start_answer), Toast.LENGTH_SHORT).show();
            handleOverTimeAnswer(true);
        }
    }

    public void handleOverTimeAnswer(boolean no_answer){
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        user.setAnswer(exercise, 0); // wrong answer
        if (user.total_answers % 4 == 0) {
            saveUser(user);
        }
        if (exercise.result() == user_answer && !no_answer){
            Toast.makeText(TrainPage.this, context.getResources().getString(R.string.correct_but_slow_answer), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(TrainPage.this, context.getResources().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
        answer.setText("");
        // exercise = user.getNextExercise(); // if we want to change exercise after over time
        showExercise(exercise);
    }

    public void showExercise(Exercise exercise){
        TextView show_exercise = findViewById(R.id.ExerciseTextView);
        String temp_exercise = String.format("%s x %s = ", exercise.mul1, exercise.mul2);
        if (user.testing_done){
            exercise_repeat_media = MediaPlayer.create(this, Utils.repeat_exercisesID[user.lang][exercise.exercise_id]);
        }
        show_exercise.setText(temp_exercise);
        show_exercise.setTextColor(getResources().getColor(R.color.colorAccent));
        if (Utils.resID[user.lang].length > 0){
            exercise_media = MediaPlayer.create(this, Utils.resID[user.lang][exercise.exercise_id]);
        }
        exercise_media.start();
        exercise.time_displayed = System.currentTimeMillis();
        user_answer = 0;
    }

    public void testDoneToast(){
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainPage.this);
        builder.setTitle(context.getResources().getString(R.string.session_done));
        String message="";
        for(int i=0;i<user.current_exercises.size();i++){
            String exercise = user.current_exercises.get(i).toString();
            String result = String.format(": %s/100",(int)(user.current_exercises.get(i).getScore()*100));
            Log.e("testDoneToast",exercise);
            Log.e("testDoneToast",result);
            message=message.concat(String.format("%s) %s\t%s\n",i+1,exercise, result));
        }
        String points = String.format("%s", user.current_count_points_per_day);
        message=message.concat(context.getResources().getString(R.string.points_won).concat(points));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(TrainPage.this,MainActivity.class));
                finish();
            }
        });
        builder.show();
    }
    private void updateView() {
        Context context = LocaleHelper.setLocale(this, (String) Paper.book().read("language"));
        Resources resources = context.getResources();
        submit.setText(resources.getString(R.string.submit));
        answer.setHint(resources.getString(R.string.answer));
    }

    @Override
    public void onBackPressed() {
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(context.getString(R.string.exit_session));
        builder.setMessage(context.getString(R.string.exit_session_warning));
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                user.end_session_time = simpleDateFormat.format(Calendar.getInstance().getTime());
                saveUser(user);
                train_counter.cancel();
                startActivity(new Intent(TrainPage.this,MainActivity.class));
                finish();
            }
        });
        builder.show();
    }

    public void timerPause() {
        train_counter.cancel();
    }
    private void timerResume() {
        train_counter = OurCountDownTimer(current_milli_train_timer);
        train_counter.start();
    }

    public CountDownTimer OurCountDownTimer(long session_duration){
        return new CountDownTimer(session_duration,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
                current_milli_train_timer = millisUntilFinished;
                TextView res = findViewById(R.id.ResultTextView);
                res.setText(String.format("%s %s", context.getResources().getString(R.string.seconds_remaining), millisUntilFinished / 1000));
                res.setTextColor(getResources().getColor(R.color.colorAccent));
                res.setTextSize(24);
                if (user.session_done) {
                    testDoneToast();
                    user.setEndSession();
                    saveUser(user);
                    train_counter.cancel();
                }
            }

            @Override
            public void onFinish() {
                testDoneToast();
                user.setEndSession();
                saveUser(user);
            }
        };
    }
}

