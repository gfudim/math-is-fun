package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.paperdb.Paper;


public class TrainPage extends Utils {
    TextView submit;
    TextView answer;
    Exercise exercise = user.getNextExercise();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        submit = (TextView) findViewById(R.id.SubmitBtn);
        answer = (TextView) findViewById(R.id.InputEditText);
        updateView();
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainPage.this);
        Context context = LocaleHelper.setLocale(TrainPage.this, (String) Paper.book().read("language"));
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.start_training));
        builder.setMessage(context.getResources().getString(R.string.about_to_start_training));
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
                        TextView res = (TextView) findViewById(R.id.ResultTextView);
                        user.session_done = true;
                        res.setText(context.getResources().getString(R.string.session_done));
                        res.setTextSize(20);
                        // summary of exercises -- TODO
                        startActivity(new Intent(TrainPage.this, MainMenuPage.class));
                        finish();
                    }
                }.start();
                showExercise(exercise);
                user.start_session_time = System.currentTimeMillis();
            }
        });
        builder.show();
        Button submitBtn = (Button) findViewById(R.id.SubmitBtn); // save the button for reference
        submitBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                EditText firstNum = (EditText) findViewById(R.id.InputEditText);
                if (firstNum != null){
                    int input_num;
                    try{
                        input_num = Integer.parseInt(firstNum.getText().toString());
                        user.setAnswer(exercise, input_num);
                        if (input_num == (exercise.result())) { /*correct answer*/
                            toastAfterAnswer(true, true, exercise);
                        }
                        else {
                            toastAfterAnswer(false, true, exercise);
                        }
                        if (user.total_answers % 4 == 0) {
                            saveUser(user);
                            String temp = String.format("%s", user.current_count_points_per_day);
                            Toast.makeText(TrainPage.this, temp, Toast.LENGTH_SHORT).show();
                        }
                        firstNum.setText("");
                        exercise = user.getNextExercise();
                        showExercise(exercise);
                        if(user.current_exercises.size() == 0){
                            testDoneToast();
                            user.session_type = SEARCH_MODE;
                            saveUser(user);
                            startActivity(new Intent(TrainPage.this, MainMenuPage.class));
                            finish();
                        }
                    }
                    catch(NumberFormatException ex){
                    }
                }
            }
        }
        );
    }

    public void showExercise(Exercise exercise){
        TextView show_exercise = (TextView) findViewById(R.id.ExerciseTextView);
        String temp_exercise = String.format("%s * %s", exercise.mul1, exercise.mul2);
        show_exercise.setText(temp_exercise);
        user.start_exercise = System.currentTimeMillis();
    }

    public void testDoneToast(){
        Toast.makeText(this, R.string.finished_test_session_text, Toast.LENGTH_SHORT).show();
    }
    private void updateView() {
        Context context = LocaleHelper.setLocale(this, (String) Paper.book().read("language"));
        Resources resources = context.getResources();
        submit.setText(resources.getString(R.string.submit));
        answer.setHint(resources.getString(R.string.answer));
    }

    @Override
    public void onBackPressed() { // TODO - add toast for conformation if user wants to exit test
        saveUser(user);
        startActivity(new Intent(TrainPage.this, MainMenuPage.class));
        finish();
    }
}
