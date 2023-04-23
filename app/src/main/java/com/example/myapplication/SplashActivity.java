package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

// 스플래시 화면
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Main Thread 로 메시지를 전달하기 위한 Worker Thread
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // delayMillis 밀리초 뒤에 실행
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500); // 2.5초 후에 실행
    }
    @Override
    protected void onPause() { // 다른 Activity가 활성화됐을 때 호출
        super.onPause();
        finish();
    }
}
