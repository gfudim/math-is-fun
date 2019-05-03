package com.example.guy.projectapp1;

import java.util.ArrayList;
import java.util.Random;

import static com.example.guy.projectapp1.Utils.MAX_NUMBER;
import static com.example.guy.projectapp1.Utils.optional_exercises;
import static com.example.guy.projectapp1.Utils.user;
import static com.example.guy.projectapp1.Utils.NUM_OF_EXERCISES_IN_SESSION;
import static com.example.guy.projectapp1.Utils.MAX_TIME_TO_ANSWER;

//
//    public void setCorrectAnswers(int correct_answers) {
//        this.correct_answers = correct_answers;
//    }
//
//    public void setWrongAnswers(int wrong_answers) {
//        this.wrong_answers = wrong_answers;
//    }
//
//    public void setTotalAnswers(int total_answers) {
//        this.total_answers = total_answers;
//    }
//
//    public void setMode(int mode) {
//        this.mode = mode;
//    }
//
//    public void setLang(int lang) {
//        this.lang = lang;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }
//
//    public void setSessionType(int session_type) {
//        this.session_type = session_type;
//    }
//
//    public void setMaxCorrectTestsInRow(int max_correct_tests_in_row) {
//        this.max_correct_tests_in_row = max_correct_tests_in_row;
//    }
//
//    public void setCurrentCorrectTestsInRow(int current_correct_tests_in_row) {
//        this.current_correct_tests_in_row = current_correct_tests_in_row;
//    }
//
//    public void setCountTests(int count_tests) {
//        this.count_tests = count_tests;
//    }
//
//    public void setCurrentCountPointsPerDay(int current_count_points_per_day) {
//        this.current_count_points_per_day = current_count_points_per_day;
//    }
//
//    public void setMaxPointsPerDay(int max_points_per_day) {
//        this.max_points_per_day = max_points_per_day;
//    }
//
//    public void setFirstLogin(long first_login) {
//        this.first_login = first_login;
//    }
//
//    public void setLastLogin(long last_login) {
//        this.last_login = last_login;
//    }
//
//    public void setStartSessionTime(long start_session_time) {
//        this.start_session_time = start_session_time;
//    }
//
//    public void setStartExercise(long start_exercise) {
//        this.start_exercise = start_exercise;
//    }
//
//    public void setEndExercise(long end_exercise) {
//        this.end_exercise = end_exercise;
//    }
//
//    public void setSessionDone(boolean session_done) {
//        this.session_done = session_done;
//    }
//
//    public void setStartPage(boolean start_page) {
//        this.start_page = start_page;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setIdDataBase(String id_data_base) {
//        this.id_data_base = id_data_base;
//    }
//
//    public void setKnownExercises(ArrayList<Exercise> known_exercises) {
//        this.known_exercises = known_exercises;
//    }
//
//    public void setUnknownExercises(ArrayList<Exercise> unknown_exercises) {
//        this.unknown_exercises = unknown_exercises;
//    }
//
//    public void setUndefinedExercises(ArrayList<Exercise> undefined_exercises) {
//        this.undefined_exercises = undefined_exercises;
//    }
//
//    public void setCurrentExercises(ArrayList<Exercise> current_exercises) {
//        this.current_exercises = current_exercises;
//    }
//
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
    int last_day_of_session;
    long first_login;
    long last_login;
    long start_session_time;
    long start_exercise;
    long end_exercise;
    boolean session_done;
    boolean start_page;  //true if last page was the main menu - for the "back" option
    boolean search_exercises_done;
    String name;
    String id_data_base;
    ArrayList<Exercise> known_exercises;
    ArrayList<Exercise> unknown_exercises;
    ArrayList<Exercise> undefined_exercises;
    ArrayList<Exercise> current_exercises;

    User(int user_mode){
        this.mode = user_mode;
        this.lang = Utils.DEFAULT_LANG;
        this.session_type = Utils.SEARCH_MODE;
        this.first_login = System.currentTimeMillis();
        this.last_login = first_login;
        this.session_done = false;
        this.name = "";
        this.id_data_base = "";
        this.age = 0;
        this.start_page = true;
        this.last_day_of_session = 0;
        this.search_exercises_done = false;
        init();
    }

    // User() {}

    private void init(){
        this.correct_answers = 0;
        this.wrong_answers = 0;
        this.total_answers = 0;
        this.max_correct_tests_in_row = 0;
        this.current_correct_tests_in_row = 0;
        this.count_tests = 0;
        this.current_count_points_per_day = 0;
        this.max_points_per_day = 0;
        this.known_exercises = new ArrayList<>();
        this.unknown_exercises = new ArrayList<>();
        this.undefined_exercises = new ArrayList<>();
        this.current_exercises = new ArrayList<>();
        for (int i = 2; i <= MAX_NUMBER; i++){
            for( int j = i; j<= MAX_NUMBER; j++){
                Exercise exercise = new Exercise(i, j);
                this.undefined_exercises.add(exercise);
            }
        }
        this.exerciseGroupWithMaxVar();
    }

    void resetHistory(){
        init();
    }

    Exercise getNextExercise(){
        Random rand = new Random();
        Exercise exercise;
        if (this.session_type == Utils.TRAIN_MODE) {
            if (current_exercises.size() > 0){
                return current_exercises.get(rand.nextInt(current_exercises.size()));
            }
        }
        else if(this.session_type == Utils.SEARCH_MODE){
            if (rand.nextInt(100) < 25 || user.search_exercises_done){  //from, group A
                if (known_exercises.size() > 0) {
                    return known_exercises.get(rand.nextInt(known_exercises.size()));
                }
            }
            if (current_exercises.size() > 0){
                exercise = current_exercises.get(rand.nextInt(current_exercises.size()));
                if (user.undefined_exercises.size() > 0){
                    while (checkExerciseInGroup(user.unknown_exercises, exercise)){ // show user only exercises from group C in search mode
                        exercise = current_exercises.get(rand.nextInt(current_exercises.size()));
                    }
                }
                else if (known_exercises.size() > 0) {
                    exercise = known_exercises.get(rand.nextInt(known_exercises.size()));
                }
                else{
                    exercise = unknown_exercises.get(rand.nextInt(unknown_exercises.size())); // backup if everyone is in B
                }
                return exercise;
            }
        }
        return null;
    }

    void setAnswer(Exercise exercise, int answer){
        int user_answer_time;
        exercise.time_answered = System.currentTimeMillis();
        user.end_exercise = exercise.time_answered;
        if (exercise.result() == answer && ((exercise.time_answered - exercise.time_displayed)/1000 <= MAX_TIME_TO_ANSWER)){
            exercise.count_correct_answers++;
            user.correct_answers++;
        }
        else{
            exercise.count_wrong_answers++;
            user.wrong_answers++;
        }
        user.total_answers = user.correct_answers + user.wrong_answers;
        if(this.session_type == Utils.TRAIN_MODE){
            if (exercise.result() == answer && ((exercise.time_answered - exercise.time_displayed)/1000 <= MAX_TIME_TO_ANSWER)){
                user_answer_time = (int)(((exercise.time_answered - exercise.time_displayed)/1000));
                user.current_count_points_per_day += calculatePoints(user_answer_time);
            }
            setGroupTrainMode(exercise);
        }
        else if(this.session_type == Utils.SEARCH_MODE){
            setGroupSearchMode(exercise);
            // check if need to remove exercise from C
            if (checkAllExercisesInGroup(unknown_exercises,current_exercises)){
                user.search_exercises_done = true;
            }
            for (int i=0; i<current_exercises.size(); i++){
                if (checkExerciseInGroup(known_exercises,current_exercises.get(i))){
                    this.exerciseGroupWithMaxVar();
                    break;
                }
            }
        }
    }

    private int calculatePoints(int user_answer_time) {
        return (int)(100*(Math.pow(0.95, Math.max(0,user_answer_time-Utils.PERFECT_TIME_FOR_ANSWER))));
    }

    private void setGroupTrainMode(Exercise exercise) {
        int i;
        if (exercise.count_wrong_answers == 1){
            for (i=0; i< current_exercises.size(); i++){
               current_exercises.get(i).count_wrong_answers=0;
               current_exercises.get(i).count_correct_answers=0;
            }
        }
        else {
            for (i=0; i< current_exercises.size(); i++){
                if(current_exercises.get(i).count_correct_answers<3){
                    return;
                }
            }
            // test done!
            for (i=0; i< current_exercises.size(); i++){
                moveExercise(current_exercises,known_exercises, current_exercises.get(i));
            }
            this.session_type = Utils.SEARCH_MODE;
            this.session_done = true;
            this.exerciseGroupWithMaxVar();
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
        for(int i=0; i<group.size();i++){
            temp_exercise = group.get(i);
            if(temp_exercise.mul1 == exercise.mul1 && temp_exercise.mul2 == exercise.mul2){
                return true;
            }
        }
        return false;
    }

    private Boolean checkAllExercisesInGroup(ArrayList<Exercise> group, ArrayList<Exercise> exercises){
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

    private void exerciseGroupWithMaxVar(){
        Random rand = new Random();
        Double current_var;
        Double max_var = Double.NEGATIVE_INFINITY;
        ArrayList <Exercise> candidates = new ArrayList<>();
        candidates.addAll(undefined_exercises);
        candidates.addAll(unknown_exercises);
        for (int i=0; i< optional_exercises; i++){
            ArrayList <Exercise> temp_group = new ArrayList<>();
            for (int j=0; j< NUM_OF_EXERCISES_IN_SESSION; j++){
                temp_group.add(candidates.get(rand.nextInt(candidates.size())));
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
//    protected void setUserInformation(User user){
//
//    }
}