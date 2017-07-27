package com.example.chopin.notepad;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by Chopin on 2017/7/10.
 */

public class Vedioview extends AppCompatActivity {
    public static final String EXRTA_PATH="path";
    private VideoView vv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vv=new VideoView(this);
        vv.setMediaController(new MediaController(this));
        setContentView(vv);

        String path=getIntent().getStringExtra(EXRTA_PATH);
        if(path!=null){
            vv.setVideoPath(path);
        }else{
            finish();
        }

    }
}
