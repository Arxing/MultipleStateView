package org.arxing.multiplestateview_demo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ViewAnimator;

import org.arxing.multiplestateview.MultipleStateView;
import org.arxing.viewbinder.ViewBinder;


public class MainActivity extends Activity {
    @ViewBinder.Bind(R.id.next) private Button next;
    @ViewBinder.Bind(R.id.previous) private Button previous;
    @ViewBinder.Bind(R.id.add) private Button add;
    @ViewBinder.Bind(R.id.remove) private Button remove;
    @ViewBinder.Bind(R.id.layout) private LinearLayout layout;
    @ViewBinder.Bind(R.id.msv) private MultipleStateView multipleStateView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bindViews(this);

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

        add.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                layout.addView(LayoutInflater.from(MainActivity.this).inflate(R.layout.subview_test, null));
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (layout.getChildCount() < 1)
                    return;
                layout.removeViewAt(layout.getChildCount() - 1);
            }
        });

    }
}
