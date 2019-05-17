package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import io.paperdb.Paper;

import static com.example.guy.projectapp1.Utils.user;

public class StatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats,container,false);
        SeekBar levelBar = view.findViewById(R.id.levelBar);
        levelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int originalProgress;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                originalProgress = seekBar.getProgress();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int arg1, boolean fromUser) {
                if(fromUser){
                    seekBar.setProgress(originalProgress);
                }
            }
        });

        connectDaysRow(view,R.id.sevenDaysBtn,7);
        connectDaysRow(view,R.id.fourteenDaysBtn,14);
        connectDaysRow(view,R.id.thirtyDaysBtn,30);
        connectMaxPoints(view,R.id.p2000Btn,2000);
        connectMaxPoints(view,R.id.p4000Btn,4000);
        connectMaxPoints(view,R.id.p6000Btn,6000);
        connectTestsRow(view,R.id.c1Btn,R.id.c1Frame,1);
        connectTestsRow(view,R.id.c3Btn,R.id.c3Frame,3);
        connectTestsRow(view,R.id.c5Btn,R.id.c5Frame,5);
        connectTrophy(view,R.id.trophyBtn,30,6000,5);
        return view;
    }

    private void connectDaysRow(View view, final int id_button, int threshold) {
        if(user.max_days_in_row>=threshold){
            connectButton(view,id_button,R.string.well_done,R.string.start_training);
            view.findViewById(id_button).setAlpha(1);
        }
        else{
            connectButton(view,id_button,R.string.more_work,R.string.keep_it_up);
        }
    }
    private void connectMaxPoints(View view, final int id_button, int threshold) {
        if(user.max_points_per_day>=threshold){
            connectButton(view,id_button,R.string.well_done,R.string.keep_it_up);
            view.findViewById(id_button).setAlpha(1);
        }
        else{
            connectButton(view,id_button,R.string.more_work,R.string.keep_it_up);
        }
    }
    private void connectTestsRow(View view, final int id_button,final int id_frame, int threshold) {
        if(user.max_correct_tests_in_row>=threshold){
            connectButton(view,id_button,R.string.well_done,R.string.keep_it_up);
            view.findViewById(id_frame).setAlpha(1);
        }
        else{
            connectButton(view,id_button,R.string.more_work,R.string.keep_it_up);
        }
    }
    private void connectTrophy(View view, final int id_button, int days_row_threshold, int max_points_thresold, int tests_row_threshold) {
        if(user.max_days_in_row>=days_row_threshold&&user.max_points_per_day>=max_points_thresold&&user.max_correct_tests_in_row>=tests_row_threshold){
            connectButton(view,id_button,R.string.well_done,R.string.great_work);
            view.findViewById(id_button).setAlpha(1);
        }
        else{
            connectButton(view,id_button,R.string.more_work,R.string.keep_it_up);
        }
    }
    private void connectButton(View view,int id_button,final int id_title,final int id_message){
        ImageButton btn = (ImageButton)view.findViewById(id_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExplainer(id_title,id_message);
            }
        });
    }
    private void createExplainer(int id_title,int id_message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
        builder.setTitle(context.getResources().getString(id_title));
        builder.setMessage(context.getResources().getString(id_message));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), null);
        builder.show();
    }
}
