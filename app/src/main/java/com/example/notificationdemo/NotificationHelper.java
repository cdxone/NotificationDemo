package com.example.notificationdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.widget.RemoteViews;

public class NotificationHelper {

    private String PAUSE_EVENT = "";

    private static NotificationHelper mInstance = new NotificationHelper();

    public static NotificationHelper getInstance(){
        return mInstance;
    }

    public void show(Context context){
        // 创建通知的通道
        createNotifyChannel(context,"hdl");
        // 创建通知
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = getNotify(context, PAUSE_EVENT, "小虎隊", true,
                50, SystemClock.elapsedRealtime());
        // 让通知不消失
        notify.flags = Notification.FLAG_NO_CLEAR;
        notifyMgr.notify(R.string.app_name, notify);
    }

    private Notification getNotify(Context ctx, String event, String song, boolean isPlay, int progress, long time) {
        Intent pIntent = new Intent(event);
        PendingIntent nIntent = PendingIntent.getBroadcast(ctx, R.string.app_name, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建RemoteViews
        RemoteViews notify_music = new RemoteViews(ctx.getPackageName(), R.layout.notify_music);
        notify_music.setTextViewText(R.id.tv_info, song+"正在播放");
        notify_music.setProgressBar(R.id.pb_play, 100, progress, false);
        notify_music.setOnClickPendingIntent(R.id.btn_play, nIntent);
        // 构建PendintIntent
        Intent intent = new Intent(ctx, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Notification
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(ctx,"hdl");
        } else {
            builder = new Notification.Builder(ctx);
        }
        builder.setContentIntent(contentIntent)
                .setContent(notify_music)
                .setTicker("xxxxxx")
                .setSmallIcon(R.drawable.ic_launcher_foreground);//小图标，必须提供
        Notification notify = builder.build();
        return notify;
    }

    /**
     * //创建一个默认重要性的通知渠道
     * @param ctx 上下文
     * @param channelld 渠道Id
     */
    public void createNotifyChannel(Context ctx, String channelld){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // 1、创建通知渠道对象，并且设置参数。
            NotificationChannel channel  = new NotificationChannel(channelld, "Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null); // 设置推送通知之时的铃声。null 表示静音推送
            channel.enableLights(true); //设置在桌面图标右上角展示小红点
            channel.setLightColor(Color.RED); // 设置小红点的颜色
            channel.setShowBadge(true); //在长按桌面图标时显示该渠道的通知
            // 2、将通知渠道设置到NotificationManager中。
            //从系统服务中获取通知管理器
            NotificationManager notifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            //创建指定的通知渠道
            notifyMgr.createNotificationChannel(channel);
        }
    }
}
