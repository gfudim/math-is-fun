package com.example.guy.projectapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import io.paperdb.Paper;

import static com.example.guy.projectapp1.Utils.ARABIC;
import static com.example.guy.projectapp1.Utils.ENGLISH;
import static com.example.guy.projectapp1.Utils.HEBREW;
import static com.example.guy.projectapp1.Utils.MULTI_MODE;
import static com.example.guy.projectapp1.Utils.RUSSIAN;
import static com.example.guy.projectapp1.Utils.SINGLE_MODE;
import static com.example.guy.projectapp1.Utils.user;

public class SettingsFragment extends Fragment {
    TextView name;
    TextView age;
    TextView reset;
    TextView save_details;
    TextView multi;
    TextView single;
    TextView sing_out;

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
        sing_out = fragment_view.findViewById(R.id.signoutBtn);
        user.start_page = true;
        updateView(fragment_view, (String) Paper.book().read("language"));
        final Button multiBtn = fragment_view.findViewById(R.id.multiModeBtn);
        final Button singleBtn = fragment_view.findViewById(R.id.singleModeBtn);
        final Button btn_sign_out = fragment_view.findViewById(R.id.signoutBtn);

       if (user.mode == SINGLE_MODE) {
            singleBtn.setEnabled(false);
            singleBtn.getCompoundDrawables()[1].setAlpha(128);
            btn_sign_out.setEnabled(false);
            btn_sign_out.getCompoundDrawables()[1].setAlpha(128);
            multiBtn.getCompoundDrawables()[1].setAlpha(255);
        }
        else{
            btn_sign_out.setEnabled(true);
            btn_sign_out.getCompoundDrawables()[1].setAlpha(255);
            multiBtn.setEnabled(false);
            multiBtn.getCompoundDrawables()[1].setAlpha(128);
            singleBtn.getCompoundDrawables()[1].setAlpha(255);
        }
        if (user.age > 0){
            age.setText(String.format("%s",user.age));
        }
        if (!user.name.equals("")){
            name.setText(user.name);
        }

        save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails((String) Paper.book().read("language"));
            }
        });

        singleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
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
                                        user = new User(SINGLE_MODE);
                                        ((MainActivity)getActivity()).saveUserToDevice(user);
                                        Intent intent = new Intent(getActivity(), LoadApp.class);
                                        intent.putExtra("new_connection",true);
                                        btn_sign_out.setEnabled(false);
                                        btn_sign_out.getCompoundDrawables()[1].setAlpha(128);
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

        multiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
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

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
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
                                        btn_sign_out.setEnabled(false);
                                        btn_sign_out.getCompoundDrawables()[1].setAlpha(128);
                                        user = new User(SINGLE_MODE);
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

        Button resetBtn = fragment_view.findViewById(R.id.resetBtn); // save the button for reference
        resetBtn.setOnClickListener(new View.OnClickListener() { // create a new event after pressing the button
            @Override
            public void onClick(View view) {
                Context context = LocaleHelper.setLocale(getActivity(), (String) Paper.book().read("language"));
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
                    }
                });
                builder.show();
            }
        });
        Button setEnglishBtn = fragment_view.findViewById(R.id.englishBtn);
        setEnglishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"en", ENGLISH);
            }
        });
        Button setHebrewBtn = fragment_view.findViewById(R.id.hebrewBtn);
        setHebrewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"iw", HEBREW);
            }
        });
        Button setArabicBtn = fragment_view.findViewById(R.id.arabicBtn);
        setArabicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"ar", ARABIC);
            }
        });
        Button setRussianBtn = fragment_view.findViewById(R.id.russianBtn);
        setRussianBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(fragment_view,"ru", RUSSIAN);
            }
        });
        return fragment_view;
    }
    private void updateLanguage(View view,String lang, int lang_for_user) {
        Paper.book().write("language", lang);
        updateView(view, (String)Paper.book().read("language"));
        user.lang = lang_for_user;
        ((MainActivity)getActivity()).saveUser(user);
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
        sing_out.setText(resources.getString(R.string.signout));
    }
    private void saveDetails(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        if (name != null){
            user.name = name.getText().toString();
            ((MainActivity)getActivity()).saveUser(user);
        }
        if (age != null){
            try{
                user.age = Integer.parseInt(age.getText().toString());
                ((MainActivity)getActivity()).saveUser(user);
            }
            catch(NumberFormatException ignored){
            }
        }
        Toast.makeText(getActivity(), resources.getString(R.string.saved_details), Toast.LENGTH_SHORT).show();
    }
}
