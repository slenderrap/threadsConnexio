package com.oriol.threads;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ExecutorService ex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ex = Executors.newSingleThreadExecutor();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Conexio conexio = new Conexio(MainActivity.this);
                ex.execute(conexio);
            }});





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ex.shutdown();
    }
}

class Conexio implements Runnable{
    Context context;
    private String resultat;
    public Conexio(Context context) {
        this.context=context;
    }

    @Override
    public void run() {

        HttpsURLConnection con = null;
        int duration = Toast.LENGTH_SHORT;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://api.myip.com");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            int code = con.getResponseCode();
            if (code == HttpsURLConnection.HTTP_OK){
                InputStream in = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String text = "";
                while ((text = br.readLine())!=null){
                    sb.append(text);
                }
            }
            String txt = "Conexio creada";


        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (con != null) {
                con.disconnect();
            }
        }
        resultat =  sb.toString();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(resultat);
                    String ip = json.getString("ip");
                    String ciutat = json.getString("country");
                    String text = "IP: " + ip+", ciutat: "+ciutat;
                    Toast.makeText(context,text,duration).show();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


}

    }
