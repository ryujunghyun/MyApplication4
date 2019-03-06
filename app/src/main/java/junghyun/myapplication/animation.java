package junghyun.myapplication;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class animation extends AppCompatActivity {
    ImageView bus1;

    int mScreenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        bus1=(ImageView) findViewById(R.id.bus);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;
        startTweenAnimation();
    }
    android.view.animation.Animation.AnimationListener animationListener = new android.view.animation.Animation.AnimationListener() {
        @Override
        public void onAnimationStart(android.view.animation.Animation animation) {

        }

        @Override
        public void onAnimationEnd(android.view.animation.Animation animation) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        @Override
        public void onAnimationRepeat(android.view.animation.Animation animation) {

        }
    };

    private void startTweenAnimation() {
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        bus1.startAnimation(animation);

        animation.setAnimationListener(animationListener);
    }
}