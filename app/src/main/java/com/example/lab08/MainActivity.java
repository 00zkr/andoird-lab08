package com.example.lab08;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView txtStatus;
    private ProgressBar progressBar;
    private ImageView image;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);
        progressBar = findViewById(R.id.progressBar);
        image = findViewById(R.id.img);
        Button btnLoadThread = findViewById(R.id.btnLoadThread);
        Button btnCalcAsync = findViewById(R.id.btnCalcAsync);
        Button btnToast = findViewById(R.id.btnToast);

        mainHandler = new Handler(Looper.getMainLooper());

        btnLoadThread.setOnClickListener(v -> loadImageWithThread());
        btnCalcAsync.setOnClickListener(v -> new HeavyCalcTask().execute());
        btnToast.setOnClickListener(v ->
                Toast.makeText(this, "UI is still responsive", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadImageWithThread() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        txtStatus.setText("Status: loading image with Thread...");

        new Thread(() -> {
            for (int progress = 0; progress <= 100; progress += 25) {
                int currentProgress = progress;
                mainHandler.post(() -> progressBar.setProgress(currentProgress));
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) {
                }
            }

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            mainHandler.post(() -> {
                image.setImageBitmap(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
                txtStatus.setText("Status: image loaded from a worker thread");
            });
        }).start();
    }

    private class HeavyCalcTask extends AsyncTask<Void, Integer, Long> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            txtStatus.setText("Status: heavy calculation running...");
        }

        @Override
        protected Long doInBackground(Void... voids) {
            long result = 0;
            for (int i = 1; i <= 100; i++) {
                for (int k = 0; k < 180000; k++) {
                    result += (long) (i * k) % 7;
                }
                publishProgress(i);
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            progressBar.setVisibility(View.INVISIBLE);
            txtStatus.setText("Status: calculation finished, result = " + result);
        }
    }
}
