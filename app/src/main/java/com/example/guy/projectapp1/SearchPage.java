package com.example.guy.projectapp1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class SearchPage extends Utils {
    TextView submit;
    EditText answer;
    private DatabaseReference databaseUsers;
    Exercise exercise = user.getNextExercise();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        submit = (TextView) findViewById(R.id.SubmitBtn);
        answer = (EditText) findViewById(R.id.InputEditText);
        updateView((String) Paper.book().read("language"));
        showExercise(exercise);
        user.start_session_time = System.currentTimeMillis();
        UIUtil.showKeyboard(this,answer);
        Button submitBtn = (Button)submit; // save the button for reference
        submitBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
             @Override
             public void onClick(View view) {
                 handleAnswer();
             }
         }
        );

        answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    handleAnswer();
                }
                UIUtil.showKeyboard(SearchPage.this,answer);
                return true;
            }
        });

        new CountDownTimer(SESSION_MILLI_DURATION, 1000) {
            Context context = LocaleHelper.setLocale(SearchPage.this, (String) Paper.book().read("language"));

            public void onTick(long millisUntilFinished) {
                TextView res = (TextView) findViewById(R.id.ResultTextView);
                res.setText(String.format("%s %s", context.getResources().getString(R.string.seconds_remaining), millisUntilFinished / 1000));
                res.setTextColor(Color.BLACK);
                res.setTextSize(16);
            }
            public void onFinish() {
                TextView res = (TextView) findViewById(R.id.ResultTextView);
                res.setText(context.getResources().getString(R.string.session_done));
                res.setTextSize(20);
                user.session_type = TRAIN_MODE;
                user.last_day_of_session = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                searchDoneToast();
                databaseUsers = FirebaseDatabase.getInstance().getReference("user");
                if (user.mode == MULTI_MODE){
                    databaseUsers.child(user.id_data_base).setValue(user);
                }
                else{
                    String id = databaseUsers.push().getKey();
                    databaseUsers.child(id).setValue(user);
                }
                // summary of exercises -- TODO
                finish();
            }
        }.start();
    }

    public void handleAnswer(){
        EditText firstNum = (EditText) findViewById(R.id.InputEditText);
        if (firstNum != null) {
            int input_num;
            try {
                input_num = Integer.parseInt(firstNum.getText().toString());
                user.setAnswer(exercise, input_num);
                if (input_num == (exercise.result())) { /*correct answer*/
                    toastAfterAnswer(true, false, exercise);
                }
                else {
                    toastAfterAnswer(false, false, exercise);
                }
                if (user.session_type == TRAIN_MODE){
                    searchDoneToast();
                    saveUser(user);
                    finish();
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

    public void searchDoneToast(){
        Toast.makeText(this, "Search Done!", Toast.LENGTH_SHORT).show();
    }

    public void showExercise(Exercise exercise) {
        TextView show_exercise = (TextView) findViewById(R.id.ExerciseTextView);
        String temp_exercise = String.format("%s * %s", exercise.mul1, exercise.mul2);
        show_exercise.setText(temp_exercise);
    }

    private void updateView(String language) {
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
