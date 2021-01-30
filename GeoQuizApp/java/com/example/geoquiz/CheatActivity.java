package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.icu.util.VersionInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_TRUE="com.example.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN="com.example.qeoquiz.answer_shown";
    private static final String EXTRA_CHEATS_ATTEMPTS="com.example.qeoquiz.cheats_attempts";

    private static final String KEY_CHEAT="is_cheating";
    private static final String KEY_CHEAT_ATTEMPTS="cheat_attempt";

    private int mCheatsAttemp;
    private boolean mAnswerIsTrue;
    private boolean mIsCheater = false;

    private TextView mShowCheatsAttempts;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int cheatsAttempts){
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_CHEATS_ATTEMPTS, cheatsAttempts);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);

    }
    public static int getCheatsAttempts(Intent result){
        return result.getIntExtra(EXTRA_CHEATS_ATTEMPTS, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if(savedInstanceState != null){
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEAT, false);
            mCheatsAttemp = savedInstanceState.getInt(KEY_CHEAT_ATTEMPTS, 0);
        }

        mShowCheatsAttempts = (TextView) findViewById(R.id.view_version);

        mAnswerIsTrue=getIntent().getBooleanExtra(EXTRA_ANSWER_TRUE,false);
        mCheatsAttemp=getIntent().getIntExtra(EXTRA_CHEATS_ATTEMPTS, 0);

        mShowCheatsAttempts.setText("Attempts: " + mCheatsAttemp);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        if(mIsCheater) {
            setAnswerShownResult(mIsCheater,mCheatsAttemp);
            if(mAnswerIsTrue){
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }
        }

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                mIsCheater = true;
                mCheatsAttemp--;
                mShowCheatsAttempts.setText("Attempts: " + mCheatsAttemp);
                setAnswerShownResult(mIsCheater, mCheatsAttemp);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private void setAnswerShownResult(boolean isAnswerShown, int cheatsAttempt){
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_CHEATS_ATTEMPTS, mCheatsAttemp);
        setResult(RESULT_OK, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(KEY_CHEAT, "onSaveInstanceState");
        savedInstanceState.putBoolean(KEY_CHEAT, mIsCheater);
        savedInstanceState.putInt(KEY_CHEAT_ATTEMPTS, mCheatsAttemp);
    }
}
