package com.tudorluca.sandbox.glide;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tudorluca.sandbox.R;

import care.smart.android.common.utils.glide.User;


public class GlideActivity extends AppCompatActivity {

    public static final String USER_PICTURE_URL = "http://tudorluca.ro/images/me.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);

        Glide.get(GlideActivity.this).clearMemory();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(GlideActivity.this).clearDiskCache();
            }
        });

        final ImageView picture1 = (ImageView) findViewById(R.id.glide_picture1);
        assert picture1 != null;

        final ImageView picture2 = (ImageView) findViewById(R.id.glide_picture2);
        assert picture2 != null;

        Glide.with(this)
                .load("http://tudorluca.ro/images/portfolio/mosaic/mosaic1.png")
                .into(picture1);

        final User user = new User(USER_PICTURE_URL);
        Glide.with(this)
                .load(user)
                .into(picture2);
    }
}
