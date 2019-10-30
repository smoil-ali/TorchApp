package com.free.torchapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.free.torchapp.MainActivity.flash_light_status;

public class NotificationReceiever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MainActivity.MY_TORCH)){
            if (flash_light_status){
                MainActivity.flash_light_off();
                Toast.makeText(context,"we are here",Toast.LENGTH_SHORT).show();
            }else {
                MainActivity.flash_light_oN();
            }
        }
    }
}
