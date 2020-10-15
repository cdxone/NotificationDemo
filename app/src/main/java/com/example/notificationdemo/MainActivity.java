package com.example.notificationdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CHANNEL_ID = "channel_id_01";
    private Context mContext;
    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        findViewById(R.id.btn_01).setOnClickListener(this);
        findViewById(R.id.btn_02).setOnClickListener(this);
        //显示通知
        findViewById(R.id.btn_show_not).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this,MyService.class));
            }
        });
        // 隐藏通知
        findViewById(R.id.btn_hide_not).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(R.string.app_name);
            }
        });
        //显示通知
        findViewById(R.id.btn_show_not2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationHelper.getInstance().show(mContext);
            }
        });
        findViewById(R.id.btn_update_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_01){
            showNotification();
        } else if (v.getId() == R.id.btn_02){
            builder.setContentText("Download complete").setProgress(0,0,false);
            notificationManager.notify(id, builder.build());
        }
    }

    private void showNotification() {
        Toast.makeText(mContext, "创建通知01", Toast.LENGTH_SHORT).show();
        //1、要在8.0系统上创建通知，必须要创建渠道
        createNotificationChannel();
        //设置通知的点击事件
        //2、Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, TestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //通知上添加操作按钮,
        Intent snoozeIntent = new Intent(this, MyBroadcastReceiver.class);
        snoozeIntent.setAction("action_test");
        snoozeIntent.putExtra("action_key", 0);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);
        //3、通知设置属性
        //设置点击事件
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("通知标题1")
                .setContentText("通知内容1")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //设置优先级
                .setContentIntent(pendingIntent)
                .setProgress(100,50,false)
                .addAction(R.drawable.ic_launcher_foreground, "自定义事件", snoozePendingIntent);
        //4、NotificationManager显示通知
        notificationManager = NotificationManagerCompat.from(this);
        //如果需要更新或者移除通知，则需要这个id
        id = R.string.app_name;
        notificationManager.notify(id, builder.build());
    }

    /**
     * 创建通知的渠道
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        // 26以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //通知的渠道名字 + 渠道的描述 + 渠道的优先级
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            //创建通知的渠道
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}