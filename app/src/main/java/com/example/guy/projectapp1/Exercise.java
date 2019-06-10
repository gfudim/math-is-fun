package com.example.guy.projectapp1;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.example.guy.projectapp1.Utils.DROR_VAR;
import static com.example.guy.projectapp1.Utils.MAX_TIME_TO_ANSWER;
import static com.example.guy.projectapp1.Utils.RANDOM_VAR;

class Exercise {
    int mul1;
    int mul2;
    int count_correct_answers;
    int count_wrong_answers;
    int exercise_id;
    int count_trained;
    long time_displayed; // for calculating the time for answers
    long time_answered;
    long aggregated_time;
    int count_displayed;
    boolean displayed_today;
    int session_count_wrong;
    int session_count_correct;

    Exercise(){} // for loading user from firebase

    Exercise(int i, int j, int id){
        mul1 = i;
        mul2 = j;
        count_trained=0;
        count_correct_answers = 0;
        count_wrong_answers = 0;
        session_count_wrong=0;
        session_count_correct=0;
        exercise_id = id;
        time_displayed = 0;
        time_answered = 0;
        aggregated_time=0;
        count_displayed=0;
        displayed_today = false;
    }

    int result(){
        return mul1*mul2;
    }
    static Double variance(Exercise exercise1, Exercise exercise2){
        if(RANDOM_VAR){
            return randomVariance();
        }
        else if(DROR_VAR){
            return drorVariance(exercise1,exercise2);
        }
        else {
            return commonDigitsVariance(exercise1,exercise2);
        }
    }

    private static Double randomVariance() {
        Random rand = new Random();
        return rand.nextDouble();
    }

    private static Double drorVariance(Exercise exercise1, Exercise exercise2) {
        Set<String>hset1=new HashSet<>();
        Set<String>hset2=new HashSet<>();
        String string1= (exercise1.mul1)+ String.valueOf(exercise1.mul2) + (exercise1.result());
        String string2= (exercise2.mul1)+ String.valueOf(exercise2.mul2) + (exercise2.result());
        for(int i=0; i<string1.length();i++){
            hset1.add(String.valueOf(string1.charAt(i)));
        }
        for(int i=0; i<string2.length();i++){
            hset2.add(String.valueOf(string2.charAt(i)));
        }
        hset1.retainAll(hset2);
        int size=hset1.size();
        if(size<2){
            return (double)0;
        }
        else if(size==2){
            return -1.0;
        }
        else{// if(size>=3)
            return -3.0;
        }
    }

    static Double commonDigitsVariance(Exercise exercise1, Exercise exercise2){
        double count=0.0;
        String string1= (exercise1.mul1)+ String.valueOf(exercise1.mul2) + (exercise1.result());
        String string2= (exercise2.mul1)+ String.valueOf(exercise2.mul2) + (exercise2.result());
        for(int i=0; i<string1.length();i++){
            for(int j=0; j<string2.length();j++){
                if(string1.charAt(i)==string2.charAt(j)){
                    count++;
                }
            }
        }
        return count*(-1);
    }
    @NonNull
    @Override
    public String toString() {
        String result="";
        result=String.format("%s * %s = %s",mul1,mul2,this.result());
        return result;
    }

    public float getScore() {
        if(this.session_count_correct+this.session_count_wrong==0){
            return -1;
        }
        return this.session_count_correct/((float)(this.session_count_correct+this.session_count_wrong));
    }
    public void setIsInTime(int user_answer_time) {
        this.count_displayed+=1;
        this.aggregated_time+=user_answer_time;
    }
    public boolean answerIsInTime(int user_answer_time) {
        if(user_answer_time <= MAX_TIME_TO_ANSWER&&user_answer_time<=this.getTimeLimit()){
            return true;
        }
        return false;
    }

    private int averageTimeToAnswer(){
        if (this.count_displayed==0){
            return MAX_TIME_TO_ANSWER;
        }
        double average=(this.aggregated_time/(float)this.count_displayed);
        if(average<=3){
            return 3;
        }
        if (average>=10){
            return 10;
        }
        return (int)average;
    }
    private int getTimeLimit(){
        return (int)(this.averageTimeToAnswer()*1.5);
    }


}

