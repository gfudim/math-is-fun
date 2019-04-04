package com.example.guy.projectapp1;

import java.util.Random;

public class Exercise {
    int mul1;
    int mul2;
    int count_correct_answers;
    int count_wrong_answers;
    boolean displayed_today;

    Exercise(int i, int j){
        mul1 = i;
        mul2 = j;
        count_correct_answers = 0;
        count_wrong_answers = 0;
        displayed_today = false;
    }
    public int result(){
        return mul1*mul2;
    }
    public static int variance (Exercise exercise1, Exercise exercise2){
        // TODO: change to real calculation
        int count=0;
        String string1= String.valueOf(exercise1.mul1)+String.valueOf(exercise1.mul2)+String.valueOf(exercise1.result());
        String string2= String.valueOf(exercise2.mul1)+String.valueOf(exercise2.mul2)+String.valueOf(exercise2.result());
        for(int i=0; i<string1.length();i++){
            for(int j=i; j<string2.length();j++){
                if(string1.charAt(i)==string2.charAt(j)){
                    count++;
                }
            }
        }
        return count*(-1);
    }
}

