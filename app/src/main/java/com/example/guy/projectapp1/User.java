package com.example.guy.projectapp1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Collections;

import static com.example.guy.projectapp1.Utils.MAX_NUMBER;
import static com.example.guy.projectapp1.Utils.SEARCH_MODE;
import static com.example.guy.projectapp1.Utils.TRAIN_MODE;
import static com.example.guy.projectapp1.Utils.day_format;
import static com.example.guy.projectapp1.Utils.optional_exercises;
import static com.example.guy.projectapp1.Utils.NUM_OF_EXERCISES_IN_SESSION;
import static com.example.guy.projectapp1.Utils.MAX_TIME_TO_ANSWER;
import static com.example.guy.projectapp1.Utils.simpleDateFormat;


class User{

    int correct_answers;
    int wrong_answers;
    int total_answers;
    int mode; //single or multi
    int lang;
    int age;
    int session_type;
    int max_correct_tests_in_row;
    int current_correct_tests_in_row;
    int count_tests;
    int current_count_points_per_day;
    int max_points_per_day;
    int total_points;
    int index;
    String last_day_of_session;
    int days_in_row;
    int max_days_in_row;
    String first_login;
    String last_login;
    String start_session_time;
    String end_session_time;
    boolean session_done;
    boolean start_page;  //true if last page was the main menu - for the "back" option
    boolean search_exercises_done;
    boolean training_done;
    boolean testing_done;
    boolean fullscore;
    String name;
    String id_data_base;
    String email;
    ArrayList<Exercise> known_exercises;
    ArrayList<Exercise> unknown_exercises;
    ArrayList<Exercise> undefined_exercises;
    ArrayList<Exercise> current_exercises;
    private boolean new_prize;
    private boolean sevenDays;
    private boolean fourteenDays;
    private boolean thirtyDays;
    private boolean p2000;
    private boolean p4000;
    private boolean p6000;
    private boolean c1;
    private boolean c3;
    private boolean c5;

    User(){} //for loading user from firebase

    User(int user_mode){
        this.mode = user_mode;
        this.lang = Utils.DEFAULT_LANG;
        this.session_type = SEARCH_MODE;
        this.first_login = simpleDateFormat.format(Calendar.getInstance().getTime());
        this.last_login = simpleDateFormat.format(Calendar.getInstance().getTime());
        this.index = 0;
        this.session_done = false;
        this.name = "";
        this.email = "";
        this.id_data_base = "";
        this.age = 0;
        this.start_page = true;
        this.last_day_of_session = "None";
        this.search_exercises_done = false;

        init();
    }

    private void init(){
        int id = 0;
        this.correct_answers = 0;
        this.wrong_answers = 0;
        this.total_answers = 0;
        this.max_correct_tests_in_row = 3;
        this.current_correct_tests_in_row = 0;
        this.count_tests = 0;
        this.current_count_points_per_day = 0;
        this.max_points_per_day = 0;
        this.total_points = 0;
        this.days_in_row = 0;
        this.max_days_in_row = 0;
        this.fullscore = false;
        this.training_done = false;
        this.testing_done = false;
        this.new_prize=false;
        this.known_exercises = new ArrayList<>();
        this.unknown_exercises = new ArrayList<>();
        this.undefined_exercises = new ArrayList<>();
        this.current_exercises = new ArrayList<>();
        for (int i = 2; i <= MAX_NUMBER; i++){
            for( int j = i; j <= MAX_NUMBER; j++){
                Exercise exercise = new Exercise(i, j, id);
                this.undefined_exercises.add(exercise);
                id++;
            }
        }
        this.initPrizes();
    }

    private void initPrizes() {
        this.sevenDays=false;
        this.fourteenDays=false;
        this.thirtyDays=false;
        this.p2000=false;
        this.p4000=false;
        this.p6000=false;
        this.c1=false;
        this.c3=false;
        this.c5=false;
    }

    void resetHistory(){
        init();
    }

    Exercise getNextExercise(){
        Random rand = new Random();
        Exercise exercise;
        int counter = 0;
        if (this.session_type == Utils.TRAIN_MODE) {
            return this.current_exercises.get(this.index % this.current_exercises.size());
        }
        else if(this.session_type == SEARCH_MODE){
            if (rand.nextInt(100) < 25 || this.search_exercises_done){  //from group A
                if (this.known_exercises.size() > 0) {
                    exercise = this.known_exercises.get(rand.nextInt(this.known_exercises.size()));
                    if (!exercise.displayed_today){
                        return exercise;
                    }
                }
            }
            if (this.current_exercises.size() > 0){
                exercise = this.current_exercises.get(rand.nextInt(this.current_exercises.size()));
                if (this.undefined_exercises.size() > 0){
                    while (checkExerciseInGroup(this.unknown_exercises, exercise) && counter < 16){ // show user only exercises from group C in search mode
                        exercise = this.current_exercises.get(rand.nextInt(this.current_exercises.size()));
                        counter++;
                    }
                    return exercise;
                }
                else if (this.known_exercises != null && this.known_exercises.size() > 0) {
                    return this.known_exercises.get(rand.nextInt(this.known_exercises.size()));
                }
                else if(this.unknown_exercises != null){
                    return this.unknown_exercises.get(rand.nextInt(this.unknown_exercises.size())); // backup if everyone is in B
                }
            }
        }
        return null;
    }

    void setAnswer(Exercise exercise, int answer){
        int user_answer_time;
        exercise.displayed_today = true;
        exercise.time_answered = System.currentTimeMillis();
        if (exercise.result() == answer && ((exercise.time_answered - exercise.time_displayed)/1000 <= MAX_TIME_TO_ANSWER)){
            exercise.count_correct_answers++;
            exercise.session_count_correct++;
            this.correct_answers++;

            user_answer_time = (int)(((exercise.time_answered - exercise.time_displayed)/1000));
            int points=calculatePoints(user_answer_time);
            this.current_count_points_per_day += points;
            this.total_points+=points;
            if(this.current_count_points_per_day>this.max_points_per_day){
                this.max_points_per_day=this.current_count_points_per_day;
            }
        }
        else{
            exercise.count_wrong_answers++;
            exercise.session_count_wrong++;
            this.wrong_answers++;
            exercise.count_correct_answers = 0; //wrong answerText or too much time
        }
        this.total_answers = this.correct_answers + this.wrong_answers;

        if(this.session_type == Utils.TRAIN_MODE){
            setGroupTrainMode(exercise);
        }
        else if(this.session_type == SEARCH_MODE){
            setGroupSearchMode(exercise);
            // check if need to remove exercise from C
            if (checkAllExercisesInGroup(unknown_exercises,current_exercises)){
                this.search_exercises_done = true;
            }
            else{
                for (int i=0; i < current_exercises.size(); i++){
                    if (checkExerciseInGroup(known_exercises,current_exercises.get(i))){
                        this.exerciseGroupWithMaxVar();
                        break;
                    }
                }
            }
        }
        this.index++;
        if (this.session_type == TRAIN_MODE){
            if (this.index % 4 == 0){
                Collections.shuffle(this.current_exercises);
            }
        }
    }

    private int calculatePoints(int user_answer_time) {
        return (int)(100*(Math.pow(0.95, Math.max(0,user_answer_time-Utils.PERFECT_TIME_FOR_ANSWER))));
    }

    private void setGroupTrainMode(Exercise exercise) {
        int i;
        if (exercise.count_wrong_answers == 1){
            this.resetExercises();
            if (!this.testing_done) {
                this.testing_done = true;
            }
        }
        else {
            if (!this.testing_done){
                for (i=0; i < current_exercises.size(); i++){
                    if(current_exercises.get(i).count_correct_answers < 3){
                        return;
                    }
                }
                this.testing_done = true;
                this.fullscore = true;
                this.training_done = true;
                this.session_done=true;//TODO - we need to continue to search mode
            }
        }
    }

    private void resetExercises() {
        for (int i=0; i< current_exercises.size(); i++){
            current_exercises.get(i).count_wrong_answers = 0;
            current_exercises.get(i).count_correct_answers = 0;
        }
    }

    private void setGroupSearchMode(Exercise exercise) {
        if(checkExerciseInGroup(known_exercises,exercise)){
            if(exercise.count_wrong_answers == 1){
                moveExercise(known_exercises, undefined_exercises, exercise);
            }
        }
        else if(checkExerciseInGroup(undefined_exercises,exercise)){
            if(exercise.count_wrong_answers == 1){
                moveExercise(undefined_exercises, unknown_exercises, exercise);
            }
            else if(exercise.count_correct_answers == 1){
                exercise.displayed_today = true;
            }
            else if(exercise.count_correct_answers == 2){
                moveExercise(undefined_exercises, known_exercises, exercise);
            }
        }
    }

    private Boolean checkExerciseInGroup(ArrayList<Exercise> group, Exercise exercise){
        Exercise temp_exercise;
        if (group == null){
            return false;
        }
        for(int i=0; i<group.size();i++){
            temp_exercise = group.get(i);
            if(temp_exercise.mul1 == exercise.mul1 && temp_exercise.mul2 == exercise.mul2){
                return true;
            }
        }
        return false;
    }

    private Boolean checkAllExercisesInGroup(ArrayList<Exercise> group, ArrayList<Exercise> exercises){
        if (group == null){
            return false;
        }
        int count_exercises = 0;
        for(int i=0; i<exercises.size();i++) {
            for (int j = 0; j < group.size(); j++) {
                if (group.get(j) == exercises.get(i)) {
                    count_exercises++;
                    break;
                }
            }
        }
        return count_exercises == exercises.size();
    }

    private void removeExerciseFromGroup(ArrayList<Exercise> group, Exercise exercise){
        Exercise temp_exercise;
        for(int i=0; i<group.size();i++){
            temp_exercise = group.get(i);
            if(temp_exercise.mul1 == exercise.mul1 && temp_exercise.mul2 == exercise.mul2){
                group.remove(i);
                break;
            }
        }
    }

    private void moveExercise(ArrayList<Exercise> src, ArrayList<Exercise> dst, Exercise exercise){
        exercise.count_correct_answers = 0;
        exercise.count_wrong_answers = 0;
        dst.add(exercise);
        removeExerciseFromGroup(src,exercise);
    }

    public void exerciseGroupWithMaxVar(){
        Random rand = new Random();
        Double current_var;
        Exercise exercise;
        Double max_var = Double.NEGATIVE_INFINITY;
        ArrayList <Exercise> candidates = new ArrayList<>();
        candidates.addAll(undefined_exercises);
        candidates.addAll(unknown_exercises);
        for (int i=0; i< optional_exercises; i++){
            ArrayList <Exercise> temp_group = new ArrayList<>();
            if(candidates.size()<=NUM_OF_EXERCISES_IN_SESSION){
                temp_group=candidates;
            }
            else{
                while (temp_group.size()<NUM_OF_EXERCISES_IN_SESSION){
                    exercise = candidates.get(rand.nextInt(candidates.size()));
                    if (!checkExerciseInGroup(temp_group, exercise)){
                        temp_group.add(exercise);
                    }
                }
            }
            current_var = calculateGroupVar(temp_group);
            if (current_var > max_var){
                max_var = current_var;
                this.current_exercises = temp_group; // here we init the current_exercises
            }
        }
    }

    private static Double calculateGroupVar(ArrayList<Exercise> current_exercises){
        Double sum_var = 0.0;
        for (int i=0; i< current_exercises.size(); i++){
            for(int j=i+1; j < current_exercises.size(); j++){
                sum_var += Exercise.variance(current_exercises.get(i), current_exercises.get(j));
            }
        }
        return sum_var;
    }
    protected boolean hadSessionToday(){
        if((this.session_done || this.last_day_of_session.equals(day_format.format(Calendar.getInstance().getTime())))&&false){
            // TODO - remove last false condition(only for debug)
            return true;
        }
        else{
            return false;
        }
    }
    public void uncheckDisplayedExercises(){
        int i;
        for (i=0;i<this.current_exercises.size(); i++){
            this.current_exercises.get(i).displayed_today = false;
        }
        for (i=0;i<this.known_exercises.size(); i++){
            this.known_exercises.get(i).displayed_today = false;
        }
        for (i=0;i<this.undefined_exercises.size(); i++){
            this.undefined_exercises.get(i).displayed_today = false;
        }
        for (i=0;i<this.unknown_exercises.size(); i++){
            this.unknown_exercises.get(i).displayed_today = false;
        }
    }
    public void setStartSession() {
        this.uncheckDisplayedExercises();
        this.resetSessionCounters();
        this.session_done =false;
        this.index = 0;
        this.current_count_points_per_day = 0;

        if(this.session_type == SEARCH_MODE){
            this.search_exercises_done = false;
            this.exerciseGroupWithMaxVar();
        }
        else{ //user.session_type = TRAIN_MODE;
            this.testing_done = false;
        }
    }

    private void resetSessionCounters() {
        for(int i=0;i<this.current_exercises.size();i++){
            this.current_exercises.get(i).session_count_correct=0;
            this.current_exercises.get(i).session_count_wrong=0;
        }
    }

    public void setEndSession() {
        this.session_done=true;
        //trophies
        this.checkDaysInRow();
        if(this.session_type==TRAIN_MODE) {
            this.checkTest();
            for (int i=0; i< current_exercises.size(); i++){
                current_exercises.get(i).count_trained++;
                if(this.training_done){
                    moveExercise(unknown_exercises,known_exercises, current_exercises.get(i));
                }
            }
        }
        //end trophies
        this.checkIfGotNewPrize();
        this.last_day_of_session=day_format.format(Calendar.getInstance().getTime());
        this.end_session_time=simpleDateFormat.format(Calendar.getInstance().getTime());
        this.switchMode();

    }

    private void checkIfGotNewPrize() {
        if(checkPrize(this.max_days_in_row,7)){
            if(!this.sevenDays){
                this.sevenDays=true;
                this.new_prize=true;
            }
        }
        if(checkPrize(this.max_days_in_row,14)){
            if(!this.fourteenDays){
                this.fourteenDays=true;
                this.new_prize=true;
            }
        }
        if(checkPrize(this.max_days_in_row,30)){
            if(!this.thirtyDays){
                this.thirtyDays=true;
                this.new_prize=true;
            }
        }
        if(checkPrize(this.max_points_per_day,2000)){
            if(!this.p2000){
                this.p2000=true;
                this.new_prize=true;
            }
        }
        if(checkPrize(this.max_points_per_day,4000)){
            if(!this.p4000){
                this.p4000=true;
                this.new_prize=true;
            }
        }
        if(checkPrize(this.max_points_per_day,6000)){
            if(!this.p6000){
                this.p6000=true;
                this.new_prize=true;
            }
        }
        if(checkPrize(this.max_correct_tests_in_row,1)){
            if(!this.c1){
                this.c1=true;
                this.new_prize=true;
            }
        }
        if(checkPrize(this.max_correct_tests_in_row,3)){
            if(!this.c3){
                this.c3=true;
                this.new_prize=true;
            }
        }
        if(checkPrize(this.max_correct_tests_in_row,5)){
            if(!this.c5){
                this.c5=true;
                this.new_prize=true;
            }
        }
    }
    private boolean checkPrize(int current,int threshold){
        return current>=threshold;
    }

    private void checkTest() {
        //checks if the test got a full score
        if(this.checkFullScore()){
            this.current_correct_tests_in_row+=1;
            if(this.current_correct_tests_in_row>this.max_correct_tests_in_row){
                this.max_correct_tests_in_row=this.current_correct_tests_in_row;
            }
        }
        else{
            this.current_correct_tests_in_row=0;
        }
    }

    private boolean checkFullScore() {
        //checks if all the answers are correct for the current test
        return this.fullscore;
    }

    private void checkDaysInRow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DATE,-1);
        Date yesterday = calendar.getTime();
        if(day_format.format(yesterday).equals(this.last_day_of_session)){
            //the user had a session yesterday and today
            this.days_in_row+=1;
            if(this.days_in_row>this.max_days_in_row){
                this.max_days_in_row=this.days_in_row;
            }
        }
        else{
            // the user didn't have a session yesterday
            this.days_in_row=0;
        }
    }

    private void switchMode() {//switches the user mode - TRAIN MODE <-> SEARCH MODE
        if(this.session_type==TRAIN_MODE&&this.training_done){
            this.session_type=SEARCH_MODE;
        }
        else if(this.session_type==SEARCH_MODE&&this.search_exercises_done){
            this.session_type=TRAIN_MODE;
            this.resetExercises();
        }
    }

    public boolean gotPrize() {
        if(this.new_prize){
            this.new_prize=false;
            return true;
        }
        return false;
    }
    public int getLevel(){
        if(this.total_points>=1){
            int current=(int) Math.sqrt(this.total_points/100.0);
            if(current>=100){
                return 100;
            }
            else{
                return current;
            }
        }
        return 0;
    }
}