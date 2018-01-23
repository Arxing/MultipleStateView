package org.arxing.multiplestateview_demo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ViewAnimator;

import org.arxing.multiplestateview.MultipleStateView;


public class MainActivity extends Activity {
    private Button next;
    private Button previous;
    private MultipleStateView multipleStateView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);

        multipleStateView = (MultipleStateView) findViewById(R.id.msv);

        next.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                multipleStateView.turnNextChildWithoutAnimation();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                multipleStateView.turnPreviousChildWithoutAnimation();
            }
        });

    }
}
