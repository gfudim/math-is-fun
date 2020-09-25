package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import static com.example.guy.projectapp1.Utils.ARABIC;
import static com.example.guy.projectapp1.Utils.ENGLISH;
import static com.example.guy.projectapp1.Utils.HEBREW;
import static com.example.guy.projectapp1.Utils.MULTI_MODE;
import static com.example.guy.projectapp1.Utils.RUSSIAN;
import static com.example.guy.projectapp1.Utils.SINGLE_MODE;
import static com.example.guy.projectapp1.Utils.getLanguage;
import static com.example.guy.projectapp1.Utils.user;


public class SettingsFragment extends Fragment {
    TextView name;
    TextView age;
    TextView reset;
    TextView save_details;
    TextView multi;
    TextView single;
    TextView sign_out;
    Button setEnglishBtn;
    Button setHebrewBtn;
    Button setArabicBtn;
    Button setRussianBtn;

    private DatabaseReference databaseUsers;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragment_view = inflater.inflate(R.layout.fragment_settings, container, false);
        age = fragment_view.findViewById(R.id.ageEditText);
        name = fragment_view.findViewById(R.id.nameEditText);
        reset = fragment_view.findViewById(R.id.resetBtn);
        save_details = fragment_view.findViewById(R.id.saveDetailsBtn);
        multi = fragment_view.findViewById(R.id.multiModeBtn);
        single = fragment_view.findViewById(R.id.singleModeBtn);
        sign_out = fragment_view.findViewById(R.id.signoutBtn);
        user.start_page = true;

       if (user.mode == SINGLE_MODE) {
           setDisable(single);
           setDisable(sign_out);
           setEnable(multi);
        }
        else{
           setEnable(single);
           setEnable(sign_out);
           setDisable(multi);
        }
        if (user.age > 0){
            age.setText(String.format("%s",user.age));
        }
        if (user.name !=null && !user.name.equals("")){
            name.setText(user.name);
        }

        save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails(getLanguage());
            }
        });

        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = LocaleHelper.setLocale(getActivity(), getLanguage());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle(context.getString(R.string.signout_notice));
                builder.setMessage(context.getString(R.string.signout_warning));
                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AuthUI.getInstance()
                                .signOut(getActivity())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        databaseUsers = FirebaseDatabase.getInstance().getReference("user");
                                        databaseUsers.child(user.id_data_base).setValue(user);
                                        //int temp_lang = user.lang;
                                        user = new User(SINGLE_MODE);
                                        //user.lang = temp_lang;
                                        ((MainActivity)getActivity()).saveUserToDevice(user);
                                        Intent intent = new Intent(getActivity(), LoadApp.class);
                                        intent.putExtra("new_connection",true);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.show();
            }
        });

        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = LocaleHelper.setLocale(getActivity(), getLanguage());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle(context.getString(R.string.signout_notice));
                builder.setMessage(context.getString(R.string.signout_warning));
                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.mode = MULTI_MODE;
                        startActivity(new Intent(getActivity(), LoginPage.class));
                    }
                });
                builder.show();
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = LocaleHelper.setLocale(getActivity(), getLanguage());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle(context.getString(R.string.signout_notice));
                builder.setMessage(context.getString(R.string.signout_warning));
                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AuthUI.getInstance()
                                .signOut(getActivity())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        databaseUsers = FirebaseDatabase.getInstance().getReference("user");
                                        databaseUsers.child(user.id_data_base.replace(".","")).setValue(user);
                                        //int temp_lang=user.lang;
                                        user = new User(SINGLE_MODE);
                                        //user.lang=temp_lang;
                                        ((MainActivity)getActivity()).saveUserToDevice(user);
                                        startActivity(new Intent(getActivity(), LoginPage.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.show();
            }
        });


        reset.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                Context context = LocaleHelper.setLocale(getActivity(), getLanguage());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle(context.getString(R.string.reset_notice));
                builder.setMessage(context.getString(R.string.reset_warning));
                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.resetHistory();
                        ((MainActivity)getActivity()).saveUser(user);
                        ((MainActivity)getActivity()).saveUserToDevice(user);
                    }
                });
                builder.show();
            }
        });
        setEnglishBtn = fragment_view.findViewById(R.id.englishBtn);
        setHebrewBtn = fragment_view.findViewById(R.id.hebrewBtn);
        setArabicBtn = fragment_view.findViewById(R.id.arabicBtn);
        setRussianBtn = fragment_view.findViewById(R.id.russianBtn);
        setEnglishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"en", ENGLISH);
            }
        });
        setHebrewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"iw", HEBREW);
            }
        });
        setArabicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"ar", ARABIC);
            }
        });
        setRussianBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"ru", RUSSIAN);
            }
        });
        updateView(fragment_view, getLanguage());
        return fragment_view;
    }
    private void updateLanguage(View view,String lang, int lang_for_user) {
        user.lang = lang_for_user;
        updateView(view, getLanguage());
        ((MainActivity)getActivity()).saveUser(user);
        ((MainActivity)getActivity()).saveUserToDevice(user);
    }
    private void setDisable(TextView textView){
        textView.setAlpha(0.5f);
        textView.setEnabled(false);
        textView.getCompoundDrawables()[1].setAlpha(128);
    }
    private void setEnable(TextView textView){
        textView.setAlpha(1f);
        textView.setEnabled(true);
        textView.getCompoundDrawables()[1].setAlpha(255);
    }
    private void setEnableAllLanguages(){
        setEnable(setEnglishBtn);
        setEnable(setHebrewBtn);
        setEnable(setArabicBtn);
        setEnable(setRussianBtn);
    }
    private void updateView(View view,String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        name.setHint(resources.getString(R.string.Name));
        age.setHint(resources.getString(R.string.Age));
        reset.setText(resources.getString(R.string.reset_history));
        save_details.setText(resources.getString(R.string.save_details));
        multi.setText(resources.getString(R.string.multi_mode));
        single.setText(resources.getString(R.string.single_mode));
        sign_out.setText(resources.getString(R.string.signout));
        setEnableAllLanguages();
        switch (user.lang){
            case ENGLISH:
                setDisable(setEnglishBtn);
                break;
            case HEBREW:
                setDisable(setHebrewBtn);
                break;
            case ARABIC:
                setDisable(setArabicBtn);
                break;
            case RUSSIAN:
                setDisable(setRussianBtn);
                break;
        }
    }
    private void saveDetails(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        if (name != null){
            user.name = name.getText().toString();
            ((MainActivity)getActivity()).saveUser(user);
            ((MainActivity)getActivity()).saveUserToDevice(user);
        }
        if (age != null){
            try{
                user.age = Integer.parseInt(age.getText().toString());
                ((MainActivity)getActivity()).saveUser(user);
                ((MainActivity)getActivity()).saveUserToDevice(user);
            }
            catch(NumberFormatException ignored){
            }
        }
        Toast.makeText(getActivity(), resources.getString(R.string.saved_details), Toast.LENGTH_SHORT).show();
    }
}
