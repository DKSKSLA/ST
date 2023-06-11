package com.example.myapplication;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG="FBMessagingService";//작동확인용 TAG

    private String msg,title;
    @Override
    public void onNewToken(@NonNull String token) {

        super.onNewToken(token);
        //token을 서버로 전송
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title=remoteMessage.getNotification().getTitle();//TITLE받기
        msg=remoteMessage.getNotification().getBody();//BODY받기

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent contentintent = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);

        //수신한 메세지를 어떻게 보여줄지
        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)//제목
                        .setContentText(msg)//메세지내용
                        .setAutoCancel(true)//누르면자동으로취소
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVibrate(new long[]{1,1000});//1초동안 진동
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,nBuilder.build());

        nBuilder.setContentIntent(contentintent);
        //수신한 메시지를 처리



    }
}
