package com.example.guy.projectapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class LoadApp extends Utils {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProgressBar spinner;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_app);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

       loadUser();
       if (user.mode == -1){
           startActivity(new Intent(LoadApp.this, FirstAppPage.class));
       }
       else{
           if (user.mode == MULTI_MODE){ // load login page
               startActivity(new Intent(LoadApp.this, LoginPage.class));
           }
           else{ // load FirstApp page
               Intent intent = new Intent(LoadApp.this, MainMenuPage.class);
               intent.putExtra("new_connection",true);
               startActivity(intent);
           }
       }
        finish();
    }
}


