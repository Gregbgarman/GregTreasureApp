package com.mobile.treasureapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.R;

public class SplashScreenActivity extends AppCompatActivity {       //simply shows splash screen for 2 seconds

    private ImageView ivAppLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShowSplashScreen();
    }

    public void ShowSplashScreen(){
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NiceBlue));

        ivAppLogo=findViewById(R.id.ivAppLogo);
        Glide.with(this).load(R.drawable.trashcantransparent).into(ivAppLogo);

        Handler myhandler = new Handler();
        myhandler.postDelayed(new Runnable() {      //user will only be in this activity for 2 seconds before being
            @Override                               //sent to Login
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
                finish();
            }

        }, 2000);


    }
}