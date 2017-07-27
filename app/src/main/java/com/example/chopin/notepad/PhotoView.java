package com.example.chopin.notepad;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Chopin on 2017/7/10.
 */

public class PhotoView extends AppCompatActivity {
    public static  final String EXTRA_PATH ="path";

    ImageView iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv=new ImageView(this);
        setContentView(iv);

        String patn=getIntent().getStringExtra(EXTRA_PATH);
         if(patn!=null){
             iv.setImageURI(Uri.fromFile(new File(patn)));
         }else {
             finish();
         }

    }
}
