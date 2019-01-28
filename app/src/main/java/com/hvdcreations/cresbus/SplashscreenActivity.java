package com.hvdcreations.cresbus;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

public class SplashscreenActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        constraintLayout = findViewById(R.id.parent);
        progressBar = findViewById(R.id.progressBar);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.mytrans);
        constraintLayout.startAnimation(animation);
        progressBar.startAnimation(animation);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashscreenActivity.this,MapsActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 4000);

    }
}
