package com.example.guy.projectapp1;

import android.content.Context;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Random;


public class Utils extends AppCompatActivity {

    protected static final String FILE_NAME = "DATA_FILE.txt";
    public static boolean RANDOM_VAR=false;
    public static boolean DROR_VAR=true;
    public static int MAX_NUMBER = 9;
    public static int SINGLE_MODE = 0;
    public static int MULTI_MODE = 1;
    public static int DEFAULT_LANG = 0;
    protected static int SEARCH_MODE = 0;
    protected static int TRAIN_MODE = 1;
    protected static User user;
    protected static DatabaseReference reff;
    //protected static long SESSION_MILLI_DURATION = 30000;
    protected static long SESSION_MILLI_DURATION = 60000;
    protected static int optional_exercises = 1000;
    final protected static int ENGLISH = 0;
    final protected static int HEBREW = 1;
    final protected static int ARABIC = 2;
    final protected static int RUSSIAN = 3;
    protected static int PERFECT_TIME_FOR_ANSWER = 3;
    protected static int NUM_OF_EXERCISES_IN_SESSION = 4;
    protected static int MAX_TIME_TO_ANSWER = 10;

    final protected static int [][] resID = {{R.raw.ex2times2, R.raw.ex2times3, R.raw.ex2times4, R.raw.ex2times5, R.raw.ex2times6, R.raw.ex2times7, R.raw.ex2times8,R.raw.ex2times9,
            R.raw.ex3times3, R.raw.ex3times4, R.raw.ex3times5, R.raw.ex3times6, R.raw.ex3times7, R.raw.ex3times8, R.raw.ex3times9,
            R.raw.ex4times4, R.raw.ex4times5, R.raw.ex4times6, R.raw.ex4times7, R.raw.ex4times8, R.raw.ex4times9,
            R.raw.ex5times5,  R.raw.ex5times6,  R.raw.ex5times7, R.raw.ex5times8, R.raw.ex5times9,
            R.raw.ex6times6, R.raw.ex6times7, R.raw.ex6times8, R.raw.ex6times9,
            R.raw.ex7times7, R.raw.ex7times8, R.raw.ex7times9,
            R.raw.ex8times8, R.raw.ex8times9,
            R.raw.ex9times9},{},{},{}};

    final protected static int [][] repeat_exercisesID = {{R.raw.ex2times2, R.raw.ex2times3, R.raw.ex2times4, R.raw.ex2times5, R.raw.ex2times6, R.raw.ex2times7, R.raw.ex2times8,R.raw.ex2times9,
            R.raw.ex3times3, R.raw.ex3times4, R.raw.ex3times5, R.raw.ex3times6, R.raw.ex3times7, R.raw.ex3times8, R.raw.ex3times9,
            R.raw.ex4times4, R.raw.ex4times5, R.raw.ex4times6, R.raw.ex4times7, R.raw.ex4times8, R.raw.ex4times9,
            R.raw.ex5times5, R.raw.ex5times6,  R.raw.ex5times7, R.raw.ex5times8, R.raw.ex5times9,
            R.raw.ex6times6, R.raw.ex6times7, R.raw.ex6times8, R.raw.ex6times9,
            R.raw.ex7times7, R.raw.ex7times8, R.raw.ex7times9,
            R.raw.ex8times8, R.raw.ex8times9,
            R.raw.ex9times9},{},{},{}};

    protected static String pattern = "dd/MM/yyyy_HH:mm";
    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    protected static SimpleDateFormat day_format = new SimpleDateFormat("dd/MM/yyyy");

    public void saveUser(User current_user){
        reff = FirebaseDatabase.getInstance().getReference("user");
        if (current_user.mode == SINGLE_MODE && (current_user.id_data_base == null || current_user.id_data_base.equals(""))) {
            current_user.id_data_base =  String.format("%s", new Random().nextLong());
        }
        reff.child(current_user.id_data_base).setValue(current_user);
    }

    public void saveUserToDevice(User user){
        FileOutputStream fos;
        Gson gson = new Gson();
        String json = gson.toJson(user);
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(json.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadUser(){
        Gson gson = new Gson();
        FileInputStream fis;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }
            Type type = new TypeToken<User>(){}.getType();
            user = gson.fromJson(sb.toString(), type);

        } catch (FileNotFoundException e) {
            user = new User(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void toastAfterAnswer(Boolean good_answer, Boolean train_mode, Exercise exercise){
        Context context = LocaleHelper.setLocale(this, getLanguage());
        Toast toast;
        int color;
        if(good_answer){
            toast = Toast.makeText(this, context.getResources().getString(R.string.Good_job_text), Toast.LENGTH_SHORT);
            color = Color.GREEN;
        }
        else{
            color = Color.RED;
            if (train_mode){
                toast = Toast.makeText(this, context.getResources().getString(R.string.mistake_text), Toast.LENGTH_SHORT);
            }
            else{  //search
                toast = Toast.makeText(this, String.format("%s %s", context.getResources().getString(R.string.show_answer), exercise.mul1 * exercise.mul2), Toast.LENGTH_SHORT);
            }
        }
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(25);
        messageTextView.setTextColor(color);
        int yOffset=100;
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,yOffset);
        toast.show();
    }
    static protected String getLanguage(){
        switch (user.lang){
            case ENGLISH:
                return "en";
            case HEBREW:
                return "iw";
            case ARABIC:
                return "ar";
            case RUSSIAN:
                return "ru";
        }
        return "en";
    }
}


