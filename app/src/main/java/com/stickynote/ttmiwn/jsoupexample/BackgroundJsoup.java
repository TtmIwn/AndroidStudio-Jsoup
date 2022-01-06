package com.stickynote.ttmiwn.jsoupexample;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.core.os.HandlerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundJsoup implements Runnable {

    private final Handler _handler;
    private final String _urlFull;

    String DEBUG_TAG;
    /**
     * コンストラクタ。
     * 非同期でお天気情報Web APIにアクセスするのに必要な情報を取得する。
     *
     * @param handler ハンドラオブジェクト。
     * @param urlFull お天気情報を取得するURL。
     */
    public BackgroundJsoup(Handler handler , String urlFull) {
        _handler = handler;
        _urlFull = urlFull;
    }

    @WorkerThread
    @Override
    public void run() {
        // HTTP接続を行うHttpURLConnectionオブジェクトを宣言。finallyで解放するためにtry外で宣言。
        HttpURLConnection con = null;

        // 天気情報サービスから取得したJSON文字列。天気情報が格納されている。
        String text = "";
        try {
            // URLオブジェクトを生成。
            URL url = new URL(_urlFull);
            // URLオブジェクトからHttpURLConnectionオブジェクトを取得。
            con = (HttpURLConnection) url.openConnection();
//            // 接続に使ってもよい時間を設定。
//            con.setConnectTimeout(1000);
//            // データ取得に使ってもよい時間。
//            con.setReadTimeout(1000);
            // HTTP接続メソッドをGETに設定。
            con.setRequestMethod("GET");
            // 接続。
            con.connect();
//             HttpURLConnectionオブジェクトからレスポンスデータを取得。
            final int statusCode = con.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                System.err.println("正常に接続できていません。statusCode:" + statusCode);
            }
            // Jsoupで対象URLの情報を取得する。
            Document doc;
            try {
                doc = Jsoup.connect(_urlFull).get();
                text = doc.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch(MalformedURLException ex) {
            Log.e(DEBUG_TAG, "URL変換失敗", ex);
        }
        // タイムアウトの場合の例外処理。
        catch(SocketTimeoutException ex) {
            Log.w(DEBUG_TAG, "通信タイムアウト", ex);
        }
        catch(IOException ex) {
            Log.e(DEBUG_TAG, "通信失敗", ex);
        }
        finally {
            // HttpURLConnectionオブジェクトがnullでないなら解放。
            if(con != null) {
                con.disconnect();
            }
        }
        MainActivity.setValues(text);
    }
}