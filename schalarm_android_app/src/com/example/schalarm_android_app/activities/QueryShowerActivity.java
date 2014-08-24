package com.example.schalarm_android_app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.schalarm_android_app.R;
import com.example.schalarm_android_app.alarm.AlarmTask;
import com.example.schalarm_android_app.alarm.AlarmTaskService;
import com.example.schalarm_android_app.utils.InjectorApplication;
import com.github.mikhailerofeev.scholarm.api.entities.Question;
import com.github.mikhailerofeev.scholarm.api.services.QuestionsService;

import java.util.*;

/**
 * Created by FFX20413 on 23.08.2014.
 */
public class QueryShowerActivity extends Activity {
    private static Set<String> tags = new HashSet<String>() {{
        add("programming");
    }};

    QuestionsService questionsService;
    AlarmTaskService alarmTaskService;

    private TextView questionText;
    private LinearLayout answersLayout;

    private int rightAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rightAnswers = 0;
        setContentView(R.layout.qa);
        questionText = (TextView) findViewById(R.id.question_text);
        answersLayout = (LinearLayout) findViewById(R.id.answers_list);
        questionsService = InjectorApplication.get(QuestionsService.class);
        alarmTaskService = InjectorApplication.get(AlarmTaskService.class);
        Question question1 = getNextQuestion();
        setNewQuestion(question1, checkCanQuitAdnStopAlarm());
    }

    private boolean checkCanQuitAdnStopAlarm() {
        AlarmTask alarmTask = alarmTaskService.geAlarmTask();
        int minimalRightAnswers = 3;
        boolean canQuite = rightAnswers >= minimalRightAnswers;
        if (alarmTask != null && alarmTask.isActive()) {
            if (canQuite) {                
                alarmTaskService.shutdownTask(); //todo create next day task
            }
            return canQuite;
        } else {
            return true;
        }
    }

    private Random random = new Random();

    private Question getNextQuestion() {
        List<Question> questions = questionsService.getQuestions(tags);
        int index = random.nextInt(questions.size());
        return questions.get(index);
    }

    private void setNewQuestion(final Question question, boolean canQuit) {
        questionText.setText(question.getQuestionText());
        answersLayout.removeAllViews();

        if (question.getRightAnswers().size() == 1) {
            setOneAnswerButtons(question);
        }
        if (canQuit) {
            Button closeBtn = new Button(this.getApplicationContext());
            closeBtn.setText("leave");
            answersLayout.addView(closeBtn);
        }
    }

    private void setOneAnswerButtons(Question question) {
        ActionBar.LayoutParams fillParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final Character rightAnswer = question.getRightAnswers().iterator().next();
        final Map<Character, Button> answersBtns = new HashMap<>();
        for (Map.Entry<Character, String> key2answer : question.getVariants().entrySet()) {
            final Button answerBtn = new Button(this.getApplicationContext());
            answersBtns.put(key2answer.getKey(), answerBtn);
            answerBtn.setText(key2answer.getValue());
            answerBtn.setTextSize(14);
            answerBtn.setLayoutParams(fillParams);
            answerBtn.setTag(key2answer.getKey());
            answerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button btn = (Button) v;
                    Character answer = (Character) btn.getTag();
                    boolean correct = rightAnswer.equals(answer);
                    if (correct) {
                        QueryShowerActivity.this.rightAnswers++;
                        btn.setBackgroundColor(Color.GREEN);
                    } else {
                        btn.setBackgroundColor(Color.RED);
                        answersBtns.get(rightAnswer).setBackgroundColor(Color.GREEN);
                    }
                    blockButtons(answersBtns.values());
                    final Handler handler = new Handler();
                    final boolean canQuit = checkCanQuitAdnStopAlarm();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setNewQuestion(getNextQuestion(), canQuit);
                        }
                    }, 2000);

                }
            });
            answersLayout.addView(answerBtn);
        }
    }

    private void blockButtons(Collection<Button> values) {
        for (Button value : values) {
            value.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (checkCanQuitAdnStopAlarm()) {
            super.onBackPressed();
        }
    }
}
