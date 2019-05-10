package com.example.guy.projectapp1;

class Exercise {
    int mul1;
    int mul2;
    int count_correct_answers;
    int count_wrong_answers;
    int exercise_id;
    long time_displayed; // for calculating the time for answers
    long time_answered;
    boolean displayed_today;

    Exercise(){} // for loading user from firebase

    Exercise(int i, int j, int id){
        mul1 = i;
        mul2 = j;
        count_correct_answers = 0;
        count_wrong_answers = 0;
        exercise_id = id;
        time_displayed = 0;
        time_answered = 0;
        displayed_today = false;
    }

    int result(){
        return mul1*mul2;
    }
    static Double variance(Exercise exercise1, Exercise exercise2){
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
}

