package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Calendar;

import io.paperdb.Paper;


public class TrainPage extends Utils {
    TextView submit;
    EditText answer;
    Exercise exercise = user.getNextExercise();
    long start_input_answer;
    int user_answer = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        submit = (TextView) findViewById(R.id.SubmitBtn);
        answer = (EditText) findViewById(R.id.InputEditText);
        updateView();
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainPage.this);
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.start_training));
        builder.setMessage(context.getResources().getString(R.string.start_training));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new CountDownTimer(SESSION_MILLI_DURATION, 1000) {
                    Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
                    public void onTick(long millisUntilFinished) {
                        TextView res = (TextView) findViewById(R.id.ResultTextView);
                        res.setText(String.format("%s %s", context.getResources().getString(R.string.seconds_remaining), millisUntilFinished / 1000));
                        res.setTextColor(Color.BLACK);
                        res.setTextSize(16);
                    }
                    public void onFinish() {
                        testDone();
                        // summary of exercises -- TODO
                    }
                }.start();
                showExercise(exercise);
                user.start_session_time = System.currentTimeMillis();
            }
        });
        UIUtil.showKeyboard(this,answer);
        builder.show();
        Button submitBtn = (Button) findViewById(R.id.SubmitBtn); // save the button for reference
        submitBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                user_answer =  Integer.parseInt(answer.getText().toString());
                handleAnswer();
            }
        }
        );
        answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    user_answer =  Integer.parseInt(answer.getText().toString());
                    handleAnswer();
                }
                UIUtil.showKeyboard(TrainPage.this,answer);
                return true;
            }
        });
    }

    public void handleAnswer(){
        exercise.time_answered = System.currentTimeMillis();
        if ((exercise.time_answered - exercise.time_displayed)/1000 > MAX_TIME_TO_ANSWER){
            handleOverTimeAnswer(false);
        }
        else{
            if (answer != null){
                int input_num;
                try{
                    input_num = Integer.parseInt(answer.getText().toString());
                    user_answer = input_num;
                    user.setAnswer(exercise, input_num);
                    if (input_num == (exercise.result())) { /*correct answer*/
                        toastAfterAnswer(true, true, exercise);
                    }
                    else {
                        toastAfterAnswer(false, true, exercise);
                    }
                    if (user.total_answers % 4 == 0) {
                        saveUser(user);
                    }
                    answer.setText("");
                    if(user.session_type == SEARCH_MODE){
                        testDone();
                    }
                    else{
                        exercise = user.getNextExercise();
                        showExercise(exercise);
                    }
                }
                catch(NumberFormatException ex){
                }
            }
        }
    }

    public void time_for_answer(View view){
        start_input_answer = System.currentTimeMillis();
        if ((start_input_answer - exercise.time_displayed)/1000 > 5){
            Toast.makeText(TrainPage.this, "Think faster..(5 seconds)", Toast.LENGTH_LONG).show();
            handleOverTimeAnswer(true);
        }
    }

    public void handleOverTimeAnswer(boolean no_answer){
        user.setAnswer(exercise, 0); // wrong answer
        if (user.total_answers % 4 == 0) {
            saveUser(user);
        }
        if (exercise.result() == user_answer && !no_answer){
            Toast.makeText(TrainPage.this, "Answer correct, but it took to much time..", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(TrainPage.this, "Try again..(faster)", Toast.LENGTH_SHORT).show();
        }
        answer.setText("");
        // exercise = user.getNextExercise(); // if we want to change exercise after over time
        showExercise(exercise);
    }

    public void showExercise(Exercise exercise){
        TextView show_exercise = (TextView) findViewById(R.id.ExerciseTextView);
        String temp_exercise = String.format("%s * %s", exercise.mul1, exercise.mul2);
        show_exercise.setText(temp_exercise);
        exercise.time_displayed = System.currentTimeMillis();
        user.start_exercise = exercise.time_displayed;
        user_answer = 0;
    }

    public void testDone(){
        DatabaseReference databaseUsers;
        user.session_done = true;
        user.last_day_of_session = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        saveUser(user);
        databaseUsers = FirebaseDatabase.getInstance().getReference("user");
        String id = databaseUsers.push().getKey();
        databaseUsers.child(id).setValue(user);
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainPage.this);
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.session_done));
        builder.setMessage(context.getResources().getString(R.string.finished_test_session_text));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveUser(user);
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
    public void onBackPressed() { // TODO - add toast for conformation if user wants to exit test - need to check
        Context context = LocaleHelper.setLocale(this, (String) Paper.book().read("language"));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        // TODO - switch to correct strings
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
                saveUser(user);
                finish();
            }
        });
        builder.show();
    }
}
