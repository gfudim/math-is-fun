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
import android.widget.TextView;

import io.paperdb.Paper;

import static com.example.guy.projectapp1.Utils.user;

public class StatsFragment extends Fragment {

    int button_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats,container,false);
        TextView levelText=view.findViewById(R.id.levelText);
        levelText.setText(String.format("%s",user.getLevel()));
        SeekBar levelBar = view.findViewById(R.id.levelBar);
        levelBar.setProgress(user.getLevel());
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
            connectButton(view,id_button,R.string.well_done,false);
            view.findViewById(id_button).setAlpha(1);
        }
        else{
            connectButton(view,id_button,R.string.more_work,true);
        }
    }
    private void connectMaxPoints(View view, final int id_button, int threshold) {
        if(user.max_points_per_day>=threshold){
            connectButton(view,id_button,R.string.well_done,false);
            view.findViewById(id_button).setAlpha(1);
        }
        else{
            connectButton(view,id_button,R.string.more_work,true);
        }
    }
    private void connectTestsRow(View view, final int id_button,final int id_frame, int threshold) {
        if(user.max_correct_tests_in_row>=threshold){
            connectButton(view,id_button,R.string.well_done,false);
            view.findViewById(id_frame).setAlpha(1);
        }
        else{
            connectButton(view,id_button,R.string.more_work,true);
        }
    }
    private void connectTrophy(View view, final int id_button, int days_row_threshold, int max_points_thresold, int tests_row_threshold) {
        if(user.max_days_in_row>=days_row_threshold&&user.max_points_per_day>=max_points_thresold&&user.max_correct_tests_in_row>=tests_row_threshold){
            connectButton(view,id_button,R.string.well_done,false);
            view.findViewById(id_button).setAlpha(1);
        }
        else{
            connectButton(view,id_button,R.string.more_work,true);
        }
    }
    private void connectButton(View view, final int id_button, final int id_title, final Boolean no_prize){
        ImageButton btn = (ImageButton)view.findViewById(id_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_id = id_button;
                createExplainer(id_title,no_prize);
            }
        });
    }
    private void createExplainer(int id_title,Boolean no_prize){
        int trophy_value_missing=0;
        int msg_for_trphy=0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
        String body_msg = context.getResources().getString(R.string.great_work);
        builder.setTitle(context.getResources().getString(id_title));
        if (no_prize){
            if (button_id == R.id.trophyBtn){
                body_msg = context.getResources().getString(R.string.get_big_trophy);
            }
            else{
                switch (button_id) {
                    case R.id.sevenDaysBtn:
                        trophy_value_missing = 7-user.max_days_in_row;
                        msg_for_trphy = R.string.more_days_for_prize;
                        break;
                    case R.id.fourteenDaysBtn:
                        trophy_value_missing = 14-user.max_days_in_row;
                        msg_for_trphy = R.string.more_days_for_prize;
                        break;
                    case R.id.thirtyDaysBtn:
                        trophy_value_missing = 30-user.max_days_in_row;
                        msg_for_trphy = R.string.more_days_for_prize;
                        break;
                    case R.id.p2000Btn:
                        trophy_value_missing = 2000-user.max_points_per_day;
                        msg_for_trphy = R.string.more_points_for_prize;
                        break;
                    case R.id.p4000Btn:
                        trophy_value_missing = 4000-user.max_points_per_day;
                        msg_for_trphy = R.string.more_points_for_prize;
                        break;
                    case R.id.p6000Btn:
                        trophy_value_missing = 6000-user.max_points_per_day;
                        msg_for_trphy = R.string.more_points_for_prize;
                        break;
                    case R.id.c1Btn:
                        trophy_value_missing = 1-user.max_correct_tests_in_row;
                        msg_for_trphy = R.string.more_tests_for_prize;
                        break;
                    case R.id.c3Btn:
                        trophy_value_missing = 3-user.max_correct_tests_in_row;
                        msg_for_trphy = R.string.more_tests_for_prize;
                        break;
                    case R.id.c5Btn:
                        trophy_value_missing = 5-user.max_correct_tests_in_row;
                        msg_for_trphy = R.string.more_tests_for_prize;
                        break;
                }
                body_msg = String.format("%s ", trophy_value_missing) +context.getResources().getString(msg_for_trphy);
            }
        }
        builder.setMessage(body_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), null);
        builder.show();
    }
}
