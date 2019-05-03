package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Calendar;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;

public class SearchPage extends Utils {
    TextView submit;
    EditText answer;
    private DatabaseReference databaseUsers;
    Exercise exercise = user.getNextExercise();
    long start_input_answer;
    int user_answer = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        submit = findViewById(R.id.SubmitBtn);
        answer = findViewById(R.id.InputEditText);
        updateView();
        showExercise(exercise);
        user.start_session_time = System.currentTimeMillis();
        UIUtil.showKeyboard(this,answer);
        Button submitBtn = (Button)submit; // save the button for reference
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
//                Log.e(TAG,String.format("actionID : %s", actionId));
//
//                if (event != null){
//                    Log.e(TAG,String.format("code : %s", event.getKeyCode()));
//                }
//                if (event != null && event.getKeyCode() > 6 && event.getKeyCode() < 17){
//                    Toast.makeText(SearchPage.this, "!!!!USER PRESSED!!!!", Toast.LENGTH_LONG).show();
//                }
//                else
               if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    user_answer =  Integer.parseInt(answer.getText().toString());
                    handleAnswer();
                }
                UIUtil.showKeyboard(SearchPage.this,answer);
                return true;
            }
        });

        new CountDownTimer(SESSION_MILLI_DURATION, 1000) {
            Context context = LocaleHelper.setLocale(SearchPage.this, (String) Paper.book().read("language"));

            public void onTick(long millisUntilFinished) {
                TextView res = findViewById(R.id.ResultTextView);
                res.setText(String.format("%s %s", context.getResources().getString(R.string.seconds_remaining), millisUntilFinished / 1000));
                res.setTextColor(Color.BLACK);
                res.setTextSize(16);
            }
            public void onFinish() {
                searchDoneToast();
                // summary of exercises -- TODO
            }
        }.start();
    }
    public void time_for_answer(View view){
        UIUtil.showKeyboard(SearchPage.this,answer);
    }

    public void handleAnswer(){
        EditText firstNum = findViewById(R.id.InputEditText);
        exercise.time_answered = System.currentTimeMillis();
        if ((exercise.time_answered - exercise.time_displayed)/1000 > MAX_TIME_TO_ANSWER){
            handleOverTimeAnswer(false);
        }
        else{
            if (firstNum != null) {
                int input_num;
                try {
                    input_num = Integer.parseInt(answer.getText().toString());
                    user_answer = input_num;
                    user.setAnswer(exercise, input_num);
                    if (input_num == (exercise.result())) { /*correct answer*/
                        toastAfterAnswer(true, false, exercise);
                    }
                    else {
                        toastAfterAnswer(false, false, exercise);
                    }
                    if (user.session_type == TRAIN_MODE){
                        searchDoneToast();
                    }
                    else{
                        if (user.total_answers % 4 == 0) {
                            saveUser(user);
                        }
                        firstNum.setText("");
                        exercise = user.getNextExercise();
                        showExercise(exercise);
                    }

                } catch (NumberFormatException ex) {
                }
            }
        }
        UIUtil.showKeyboard(this,answer);
    }

    public void time_for_answer_search(View view){
        start_input_answer = System.currentTimeMillis();
        if ((start_input_answer - exercise.time_displayed)/1000 > 5){
            Toast.makeText(SearchPage.this, "Think faster..(5 seconds)", Toast.LENGTH_LONG).show();
            handleOverTimeAnswer(true);
        }
    }

    public void handleOverTimeAnswer(boolean no_answer){
        user.setAnswer(exercise, 0); // wrong answer
        if (user.total_answers % 4 == 0) {
            saveUser(user);
        }
        if (exercise.result() == user_answer && !no_answer){
            Toast.makeText(SearchPage.this, "Answer correct, but it took to much time..", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(SearchPage.this, "Try again..(faster)", Toast.LENGTH_SHORT).show();
        }
        answer.setText("");
        // exercise = user.getNextExercise(); // if we want to change exercise after over time
        showExercise(exercise);
    }

    public void searchDoneToast(){
        DatabaseReference databaseUsers;
        user.session_done = true;
        user.session_type = TRAIN_MODE;
        user.last_day_of_session = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        saveUser(user);
        databaseUsers = FirebaseDatabase.getInstance().getReference("user");
        if (user.mode == MULTI_MODE){
            databaseUsers.child(user.id_data_base).setValue(user);
        }
        else{
            String id = databaseUsers.push().getKey();
            databaseUsers.child(id).setValue(user);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchPage.this);
        Context context = LocaleHelper.setLocale(SearchPage.this, (String) Paper.book().read("language"));
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.session_done));
        builder.setMessage("Search Done!"); // TODO - change string
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

    public void showExercise(Exercise exercise) {
        TextView show_exercise = findViewById(R.id.ExerciseTextView);
        String temp_exercise = String.format("%s * %s", exercise.mul1, exercise.mul2);
        show_exercise.setText(temp_exercise);
        exercise.time_displayed = System.currentTimeMillis();
        user.start_exercise = exercise.time_displayed;
        user_answer = 0;
    }

    private void updateView() {
        Context context = LocaleHelper.setLocale(this, (String) Paper.book().read("language"));
        Resources resources = context.getResources();
        submit.setText(resources.getString(R.string.submit));
        answer.setHint(resources.getString(R.string.answer));
    }

    @Override
    public void onBackPressed() {
        saveUser(user);
        finish();
    }
}
