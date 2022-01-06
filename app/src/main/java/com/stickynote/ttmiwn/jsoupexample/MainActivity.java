package com.stickynote.ttmiwn.jsoupexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private AsyncWork asyncWork;
    private static String webValues;
    TextView textView;
    String url1 = "https://www.google.com/search?q=%E3%82%BB%E3%82%A4%E3%82%A6%E3%83%81&oq=%E3%82%BB%E3%82%A4%E3%82%A6%E3%83%81&aqs=chrome..69i57.1648j0j7&sourceid=chrome&ie=UTF-8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonStart = findViewById(R.id.button);
        textView = findViewById(R.id.textview);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
//                asyncWork = new AsyncWork();
//                asyncWork.execute();
//                textView.setText(webValues);
                Looper mainLooper = Looper.getMainLooper();
                Handler handler = HandlerCompat.createAsync(mainLooper);
                BackgroundJsoup bgj = new BackgroundJsoup(handler, url1);
                ExecutorService executorService  = Executors.newSingleThreadExecutor();
                executorService.submit(bgj);
            }
        });
        Button buttonHyouji = findViewById(R.id.button2);
        buttonHyouji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(webValues);
            }
        });
    }

    public static void setValues(String s) {

        webValues = s;

    }

    public class AsyncWork extends AsyncTask<Void, Void, String> {
        private String text;
//        String url1 = "https://www.google.com/search?q=%E3%82%BB%E3%82%A4%E3%82%A6%E3%83%81&oq=%E3%82%BB%E3%82%A4%E3%82%A6%E3%83%81&aqs=chrome..69i57.1648j0j7&sourceid=chrome&ie=UTF-8";

        @Override
        protected String doInBackground(Void... params) {
            text = "";
            Document doc;
            try {
                doc = Jsoup.connect(url1).get();
                text = doc.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String result) {
            MainActivity.setValues(text);
        }
    }
}