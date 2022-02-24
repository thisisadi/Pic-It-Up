package com.example.photoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    Animation animSwing,animSlide;
    ImageView img,text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        img = findViewById(R.id.logo);
        text = findViewById(R.id.splash_text);
        animSwing = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.swing);
        animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        img.startAnimation(animSwing);
        text.startAnimation(animSlide);
        checkAnimation();

    }
    protected void checkAnimation() {
        animSwing.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}