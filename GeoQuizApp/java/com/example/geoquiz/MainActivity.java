package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX="index";
    private static final String KEY_CORRECT_COUNT_ANSWERS="correct_answers";
    private static final String KEY_IS_BUTTON_PRESSED="is_button_pressed";
    private static final String KEY_IS_CHEATER="is_cheater";
    private static final String KEY_AR_IS_QUES_SHOWN="array_is_ques_shown";
    private static final String KEY_CHEATS_ATTEMPS="cheats_attempt";
    private static final String EXTRA_CHEATS_ATTEMPTS="com.example.qeoquiz.cheats_attemps";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private boolean mIsButtonPressed = false;

    private int mCheatsAttempt = 3;
    private int mCorrectAnswers = 0;
    private boolean mIsCheater = true;
    private TextView mQuestionTextView;
    private TextView mCorrectAnswersView;
    private TextView mShowCheatsAttempts;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_Moscow, true),
    };

    private int mCurrentIndex = 0;

    private boolean mIsQuestionsShown[] = new boolean[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCorrectAnswers = savedInstanceState.getInt(KEY_CORRECT_COUNT_ANSWERS, 0);
            mIsButtonPressed = savedInstanceState.getBoolean(KEY_IS_BUTTON_PRESSED, false);
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
            mIsQuestionsShown = savedInstanceState.getBooleanArray(KEY_AR_IS_QUES_SHOWN);
            mCheatsAttempt = savedInstanceState.getInt(KEY_CHEATS_ATTEMPS);
            for(int i = 0;i<mQuestionBank.length;i++){
                if(mIsQuestionsShown[i]){
                    mQuestionBank[i].wasShown();
                }
            }
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);//связываем контроллер с хмд
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mCorrectAnswersView = (TextView) findViewById(R.id.correct_count);
        mCorrectAnswersView.setText(String.valueOf(mCorrectAnswers));

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                mIsButtonPressed = true;
                //mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                //updateQuestion();
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(false);
                //mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mFalseButton.setEnabled(false);
                mTrueButton.setEnabled(false);
                mIsButtonPressed = true;
                //updateQuestion();
            }
        });

        mNextButton = (Button)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsButtonPressed=false;
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater=false;
                updateQuestion();

                mTrueButton.setEnabled(!mQuestionBank[mCurrentIndex].isQuestionShown());
                mFalseButton.setEnabled(!mQuestionBank[mCurrentIndex].isQuestionShown());
            }
        });

        updateQuestion();

        mTrueButton.setEnabled(!mQuestionBank[mCurrentIndex].isQuestionShown());
        mFalseButton.setEnabled(!mQuestionBank[mCurrentIndex].isQuestionShown());

        mTrueButton.setEnabled(!mIsButtonPressed);
        mFalseButton.setEnabled(!mIsButtonPressed);

        mPrevButton = (Button)findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = mCurrentIndex - 1;
                if(mCurrentIndex < 0) mCurrentIndex = mQuestionBank.length - 1;
                updateQuestion();

                mTrueButton.setEnabled(!mQuestionBank[mCurrentIndex].isQuestionShown());
                mFalseButton.setEnabled(!mQuestionBank[mCurrentIndex].isQuestionShown());
            }

        });

        mShowCheatsAttempts = (TextView) findViewById(R.id.cheats_attempts);
        mShowCheatsAttempts.setText("Attempts: " + mCheatsAttempt);

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            //Start Cheat Activity
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue, mCheatsAttempt);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mCheatsAttempt = CheatActivity.getCheatsAttempts(data);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart(Bundle) called");
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mCheatsAttempt <= 0)
            mCheatButton.setEnabled(false);
        mShowCheatsAttempts.setText("Attempts: " + mCheatsAttempt);
        Log.d(TAG, "onResume(Bundle) called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause(Bundle) called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(KEY_INDEX, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        Log.i(KEY_CORRECT_COUNT_ANSWERS, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_CORRECT_COUNT_ANSWERS, mCorrectAnswers);
        Log.i(KEY_IS_BUTTON_PRESSED, "onSaveInstanceState");
        savedInstanceState.putBoolean(KEY_IS_BUTTON_PRESSED, mIsButtonPressed);
        Log.i(KEY_IS_CHEATER, "onSaveInstanceState");
        savedInstanceState.putBoolean(KEY_IS_CHEATER, mIsCheater);
        Log.i(KEY_AR_IS_QUES_SHOWN,"onSaveInstanceState");
        boolean mQuestionsWasShown[] = new boolean[mQuestionBank.length];
        for(int i = 0;i<mQuestionBank.length;i++){
            mQuestionsWasShown[i] = mQuestionBank[i].isQuestionShown();
        }
        savedInstanceState.putBooleanArray(KEY_AR_IS_QUES_SHOWN, mQuestionsWasShown);
        savedInstanceState.putInt(KEY_CHEATS_ATTEMPS,mCheatsAttempt);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop(Bundle) called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy(Bundle) called");
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResid();//получаем реузльтат ответа
        mQuestionTextView.setText(question);//устанавливаем текст вопроса
        mFalseButton.setEnabled(true);
        mTrueButton.setEnabled(true);
    }
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        mQuestionBank[mCurrentIndex].wasShown();
        int messageResId = 0;
        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                updateCorrectAnswers();
            } else messageResId = R.string.incorrect_toast;
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
    private void updateCorrectAnswers(){
        mCorrectAnswers++;
        mCorrectAnswersView.setText(String.valueOf(mCorrectAnswers));
    }

}
