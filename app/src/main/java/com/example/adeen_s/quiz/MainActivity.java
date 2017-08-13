package com.example.adeen_s.quiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    static CountDownTimer timer;
    final String OPENTDB_REQUEST_URL = "https://opentdb.com/api.php";
    public String JSONResponse = null;
    int counter = 50;
    int correctAnswered = 0;
    String answer;
    TextView timerView;
    TextView questionTextView;
    TextView statsView;
    TextView option1, option2, option3, option4;
    Toast toastMessage;
    ArrayList<Question> questionList = new ArrayList<Question>();
    boolean isTimerRunning = false;
    Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("ONCREATE", "Inside onCreate()");
        super.onCreate(null);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        timerView = (TextView) findViewById(R.id.timer);
        questionTextView = (TextView) findViewById(R.id.question);
        statsView = (TextView) findViewById(R.id.stats);
        option1 = (TextView) findViewById(R.id.option1);
        option2 = (TextView) findViewById(R.id.option2);
        option3 = (TextView) findViewById(R.id.option3);
        option4 = (TextView) findViewById(R.id.option4);
        TextView emptyView = (TextView) findViewById(R.id.empty);

        timerView.setVisibility(View.INVISIBLE);
        questionTextView.setVisibility(View.INVISIBLE);
        statsView.setVisibility(View.INVISIBLE);
        option1.setVisibility(View.INVISIBLE);
        option2.setVisibility(View.INVISIBLE);
        option3.setVisibility(View.INVISIBLE);
        option4.setVisibility(View.INVISIBLE);

        timer = new CountDownTimer(600000L, 1000L) {
            @Override
            public void onTick(long millisecondsLeft) {
                isTimerRunning = true;
                String string = ("" + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisecondsLeft),
                        TimeUnit.MILLISECONDS.toSeconds(millisecondsLeft) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsLeft))));
                timerView.setText(string);
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                showDialog();
            }
        };

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.INVISIBLE);
            emptyView = (TextView) findViewById(R.id.empty);
            emptyView.setText("No Internet Connection");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    public void generateQuestion() {
        if (!isTimerRunning) {
            timer.start();
        }
        if (counter > 0) {
            Question question = questionList.get(--counter);
            answer = question.getAnswer();
            answer = Jsoup.parse(answer).text();

            String questionText = question.getQuestion();
            questionText = Jsoup.parse(questionText).text();


            ArrayList<String> arrayList = new ArrayList<String>(asList(answer, question.getOption1(), question.getOption2(), question.getOption3()));
            Collections.shuffle(arrayList, new Random(System.currentTimeMillis()));
            questionTextView.setText(questionText);

            String op1, op2, op3, op4;
            op1 = Jsoup.parse(arrayList.get(0)).text();
            op2 = Jsoup.parse(arrayList.get(1)).text();
            op3 = Jsoup.parse(arrayList.get(2)).text();
            op4 = Jsoup.parse(arrayList.get(3)).text();

            statsView.setText((50 - counter) + "/50");

            option1.setText(op1);
            option1.setTag(op1);
            option2.setText(op2);
            option2.setTag(op2);
            option3.setText(op3);
            option3.setTag(op3);
            option4.setText(op4);
            option4.setTag(op4);
            Log.i("GENERATE QUESTIONS", "Answer == " + answer);
        } else {
            showDialog();
        }

    }

    public void optionSelected(View view) {
        if (toastMessage != null) {
            toastMessage.cancel();
        }
        if (view.getTag().toString().equals(answer)) {
            correctAnswered++;
            toastMessage = Toast.makeText(context, " Correct ! ", Toast.LENGTH_SHORT);
        } else {
            toastMessage = Toast.makeText(context, "Correct Answer is " + answer, Toast.LENGTH_SHORT);
        }
        toastMessage.show();
        generateQuestion();
    }

    public void showDialog() {
        String message;

        if (isTimerRunning) {
            timer.cancel();
            message = "Score : " + correctAnswered + "out of 50" + "\n\nPlay Again ?";
        } else {
            message = "Time Over ! \nYou failed to answer all questions" + "\n\nPlay Again ?";
        }
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("Game Over")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getLoaderManager().destroyLoader(0);
                        recreate();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        onDestroy();
                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTimerRunning) {
            timer.cancel();
        }
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(OPENTDB_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("amount", "50");
        uriBuilder.appendQueryParameter("type", "multiple");

        return new QuestionLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String jsonResponse) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.INVISIBLE);
        JSONResponse = jsonResponse;
        questionList = QueryUtils.extractQuestions(JSONResponse);

        timerView.setVisibility(View.VISIBLE);
        questionTextView.setVisibility(View.VISIBLE);
        statsView.setVisibility(View.VISIBLE);
        option1.setVisibility(View.VISIBLE);
        option2.setVisibility(View.VISIBLE);
        option3.setVisibility(View.VISIBLE);
        option4.setVisibility(View.VISIBLE);

        generateQuestion();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        Log.v("myLogs", "onLoaderReset");
    }
}
