package com.landkid.said.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;

import com.landkid.said.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sds on 2017. 6. 7..
 */

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.ll_search_area) LinearLayout mLlSearchArea;

    int cx;
    int cy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        overridePendingTransition(0, 0);

        ButterKnife.bind(this);
        mLlSearchArea.setVisibility(View.INVISIBLE);
        cx = (int) getIntent().getFloatExtra("cx", 0);
        cy = (int) getIntent().getFloatExtra("cy", 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int finalRadius = Math.max(mLlSearchArea.getWidth(), mLlSearchArea.getHeight());

                Animator anim = ViewAnimationUtils.createCircularReveal(mLlSearchArea, cx, cy, 0, finalRadius);

                mLlSearchArea.setVisibility(View.VISIBLE);
                anim.start();
            }
        }, 300);
    }

    @Override
    public void onBackPressed() {
        int initialRadius = Math.max(mLlSearchArea.getWidth(), mLlSearchArea.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(mLlSearchArea, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLlSearchArea.setVisibility(View.INVISIBLE);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        anim.start();
    }
}
