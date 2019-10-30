 package com.free.torchapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    public static final String MY_TORCH="com.example.torchapp.torch";
    NotificationManager manager;
    static SharedPreferences sharedPreferences;
    static ImageButton power_btn;
    ImageView window,torch;
    public static boolean flash_light_status=false;
    static CameraManager cameraManager;
    public static final int CAMERA_REQUEST=99;
    static String camera_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        power_btn=findViewById(R.id.power_btn);
        if (!check_permission()){
            request_permission();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);
            try {
                camera_id=cameraManager.getCameraIdList()[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        window=findViewById(R.id.window);
        torch=findViewById(R.id.torch);
        final MediaPlayer mp=MediaPlayer.create(this,R.raw.sound);
        manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPreferences=getSharedPreferences("mypref",Context.MODE_PRIVATE);
        final boolean has_camera_flash=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        power_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(sharedPreferences.contains(SettingsActivity.noise)){
                        mp.start();
                    }

                if (check_permission()){
                    if (has_camera_flash){
                        if (flash_light_status){
                            flash_light_off();
                        }else {
                            flash_light_oN();
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Flash is unavailable", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    request_permission();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sharedPreferences.contains(SettingsActivity.flash_off)){
            flash_light_off();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.contains(SettingsActivity.flash_on)){
            flash_light_oN();
        }
        if (sharedPreferences.contains(SettingsActivity.bar)){
            notification_view();
        }else {
            manager.cancel(0);
        }
        if (flash_light_status){
            power_btn.setBackgroundResource(R.drawable.borders2);
            power_btn.setImageResource(R.drawable.power_btn);
        }

    }

    public static void flash_light_oN() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(camera_id,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        power_btn.setBackgroundResource(R.drawable.borders2);
        power_btn.setImageResource(R.drawable.power_btn);
        flash_light_status=true;
    }

    public static void flash_light_off() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(camera_id,false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        power_btn.setBackgroundResource(R.drawable.borders);
        power_btn.setImageResource(R.drawable.ic_power_settings_new_black_24dp);
        flash_light_status=false ;
    }


    public boolean check_permission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    public void request_permission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Need Permission to access flashlight", Toast.LENGTH_SHORT).show();
                    request_permission();
                }
                break;
        }
    }

    public void notification_view(){

        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.activity_notifiaction_view);

        NotificationCompat.Builder builder=
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_settings_black_24dp)
                .setContentTitle("")
                .setContentText("")
                .setOngoing(true)
                .setCustomBigContentView(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notifiaction_intent=new Intent(this,Notifiaction_view.class);
        notifiaction_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notifiaction_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        setListener(remoteViews,this);
        manager.notify(0,builder.build());
    }

    private void setListener(RemoteViews remoteViews, MainActivity mainActivity) {
        Intent torch_open=new Intent(MY_TORCH);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,new Intent(mainActivity,MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.window,pendingIntent);
        PendingIntent pendingIntent2=PendingIntent.getBroadcast(this,0,torch_open,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.torch,pendingIntent2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(this,SettingsActivity.class));
                return true;
        }
        return true;
    }
}
