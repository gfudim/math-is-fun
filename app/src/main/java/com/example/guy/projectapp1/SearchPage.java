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

public class SearchPage extends Utils {
    TextView submit;
    TextView dont_know;
    EditText answer;
    Exercise exercise = user.getNextExercise();
    long start_input_answer;
    int user_answer = 0;
    CountDownTimer search_counter;
    MediaPlayer exercise_media = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        submit = findViewById(R.id.SubmitBtn);
        dont_know=findViewById(R.id.DontKnowBtn);
        answer = findViewById(R.id.InputEditText);
        updateView();
        UIUtil.showKeyboard(this,answer);
        Button submitBtn = (Button)submit; // save the button for reference
        Button dontKnowBtn=(Button)dont_know;
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
        dontKnowBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                user_answer =  -1;
                handleAnswer();
            }
        });

        answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                   try {
                       user_answer = Integer.parseInt(answer.getText().toString());
                       handleAnswer();
                   }
                   catch (NumberFormatException ignored){}
                }
                UIUtil.showKeyboard(SearchPage.this,answer);
                return true;
            }
        });

        search_counter = new CountDownTimer(SESSION_MILLI_DURATION, 1000) {
            Context context = LocaleHelper.setLocale(SearchPage.this, (String) Paper.book().read("language"));
            public void onTick(long millisUntilFinished) {
                TextView res = findViewById(R.id.ResultTextView);
                res.setText(String.format("%s %s", context.getResources().getString(R.string.seconds_remaining), millisUntilFinished / 1000));
                res.setTextColor(getResources().getColor(R.color.colorAccent));
                res.setTextSize(24);
            }
            public void onFinish() {
                user.setEndSession();
                saveUser(user);
                searchDoneToast();
            }
        }.start();
        showExercise(exercise);
        user.start_session_time = simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    public void handleAnswer(){
        exercise.time_answered = System.currentTimeMillis();
        if ((exercise.time_answered - exercise.time_displayed)/1000 > MAX_TIME_TO_ANSWER){
            handleOverTimeAnswer(false);
        }
        else{
            try {
                 user.setAnswer(exercise, user_answer);
                if (user_answer == (exercise.result())) { /*correct answer*/
                    toastAfterAnswer(true, false, exercise);
                }
                else {
                    toastAfterAnswer(false, false, exercise);
                }
                if (!user.session_done){
                    if (user.total_answers % 4 == 0) {
                        saveUser(user);
                    }
                    answer.setText("");
                    exercise = user.getNextExercise();
                    showExercise(exercise);
                }
            }
            catch (NumberFormatException ignored) {
            }
        }
        UIUtil.showKeyboard(this,answer);
    }

    public void time_for_answer_search(View view){
        Context context = LocaleHelper.setLocale(SearchPage.this, (String) Paper.book().read("language"));
        start_input_answer = System.currentTimeMillis();
        UIUtil.showKeyboard(SearchPage.this,answer);
        if ((start_input_answer - exercise.time_displayed)/1000 > 5){
            Toast.makeText(SearchPage.this, context.getResources().getString(R.string.start_answer), Toast.LENGTH_SHORT).show();
            handleOverTimeAnswer(true);
        }
    }

    public void handleOverTimeAnswer(boolean no_answer){
        Context context = LocaleHelper.setLocale(SearchPage.this, (String) Paper.book().read("language"));
        user.setAnswer(exercise, 0); // wrong answer
        if (user.total_answers % 4 == 0) {
            saveUser(user);
        }
        if (exercise.result() == user_answer && !no_answer){
            Toast.makeText(SearchPage.this, context.getResources().getString(R.string.correct_but_slow_answer), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(SearchPage.this, context.getResources().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
        answer.setText("");
        // exercise = user.getNextExercise(); // if we want to change exercise after over time
        showExercise(exercise);
    }

    public void searchDoneToast(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchPage.this);
        Context context = LocaleHelper.setLocale(SearchPage.this, (String) Paper.book().read("language"));
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.session_done));
        builder.setMessage(String.format("You just won %s points!", user.current_count_points_per_day)); // TODO - change string
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(SearchPage.this,MainActivity.class));
                finish();
            }
        });
        builder.show();
    }

    public void showExercise(Exercise exercise) {
        TextView show_exercise = findViewById(R.id.ExerciseTextView);
        String temp_exercise = String.format("%s x %s = ", exercise.mul1, exercise.mul2);
        show_exercise.setText(temp_exercise);
        show_exercise.setTextColor(getResources().getColor(R.color.colorAccent));
        if (Utils.resID[user.lang].length > 0){
            exercise_media = MediaPlayer.create(this, Utils.resID[user.lang][exercise.exercise_id]);
        }
        exercise_media.start();
        exercise.time_displayed = System.currentTimeMillis();
        user_answer = 0;
    }

    private void updateView() {
        Context context = LocaleHelper.setLocale(this, (String) Paper.book().read("language"));
        Resources resources = context.getResources();
        submit.setText(resources.getString(R.string.submit));
        dont_know.setText(resources.getString(R.string.dont_know));
        answer.setHint(resources.getString(R.string.answer));
    }

    @Override
    public void onBackPressed() {
        Context context = LocaleHelper.setLocale(this, (String) Paper.book().read("language"));
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
                search_counter.cancel();
                startActivity(new Intent(SearchPage.this,MainActivity.class));
                finish();
            }
        });
        builder.show();
    }
}
