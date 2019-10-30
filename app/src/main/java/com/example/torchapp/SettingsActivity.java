package com.free.torchapp;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.free.torchapp.MainActivity.sharedPreferences;

public class SettingsActivity extends AppCompatActivity {

    CheckBox status_bar,sound,light_off,light_on;
    Button save_btn;
    public static final String mypreference="mypref";
    public static final String bar="Status_bar";
    public static final String noise="Sound";
    public static final String flash_off="Flash_off";
    public static final String flash_on="Flash_on";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        status_bar=findViewById(R.id.status_bar_box);
        sound=findViewById(R.id.sound_box);
        light_off=findViewById(R.id.torch_off_box);
        light_on=findViewById(R.id.torch_on_box);
        save_btn=findViewById(R.id.save);


        if(sharedPreferences.contains(bar)){
            status_bar.setChecked(true);
        } if (sharedPreferences.contains(noise)){
            sound.setChecked(true);
        } if (sharedPreferences.contains(flash_off)){
            light_off.setChecked(true);
        } if(sharedPreferences.contains(flash_on)){
            light_on.setChecked(true);
        }

        final MediaPlayer mp=MediaPlayer.create(this,R.raw.sound);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedPreferences.contains(noise)){
                    mp.start();
                }
                save();
                Toast.makeText(SettingsActivity.this, "saved", Toast.LENGTH_SHORT).show();
            }
        });




    }

    public void save(){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        if (status_bar.isChecked()){
            editor.putString(bar,"notify");
        }
        if (sound.isChecked()){
            editor.putString(noise,"Sound");
        }
        if (light_off.isChecked()){
            editor.putString(flash_off,"off");
        }
        if (light_on.isChecked()){
            editor.putString(flash_on,"on");
        }

        editor.commit();
    }


}