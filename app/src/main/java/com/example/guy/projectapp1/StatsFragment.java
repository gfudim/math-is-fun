package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import io.paperdb.Paper;

public class StatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats,container,false);
        SeekBar levelBar = (SeekBar)view.findViewById(R.id.levelBar);
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
                if(fromUser == true){
                    seekBar.setProgress(originalProgress);
                }
            }
        });
        connectButton(view,R.id.sevenDaysBtn,R.string.start_training,R.string.start_training);
        connectButton(view,R.id.fourteenDaysBtn,R.string.start_training,R.string.start_training);
        connectButton(view,R.id.thirtyDaysBtn,R.string.start_training,R.string.start_training);
        connectButton(view,R.id.p1500Btn,R.string.start_training,R.string.start_training);
        connectButton(view,R.id.p3000Btn,R.string.start_training,R.string.start_training);
        connectButton(view,R.id.p6000Btn,R.string.start_training,R.string.start_training);
        connectButton(view,R.id.c1Btn,R.string.start_training,R.string.start_training);
        connectButton(view,R.id.c5Btn,R.string.start_training,R.string.start_training);
        connectButton(view,R.id.c10Btn,R.string.start_training,R.string.start_training);
        return view;
    }
    private void connectButton(View view,int id_button,final int id_title,final int id_message){
        Button btn = (Button) view.findViewById(id_button);
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
