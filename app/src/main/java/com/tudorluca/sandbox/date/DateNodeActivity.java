package com.tudorluca.sandbox.date;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tudorluca.sandbox.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.realm.Realm;
import io.realm.RealmObject;

public class DateNodeActivity extends AppCompatActivity {

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_node);

        setupGson();

        try {
            InputStream stream = getStream();
            DateNode date = getDateFromStream(stream);
            stream.close();

            logDate("Before SAVE", date.getDate());

            stream = getStream();
            final Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            date = realm.createObjectFromJson(DateNode.class, stream);
            realm.commitTransaction();
            stream.close();

            logDate("After SAVE", date.getDate());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(DateNodeActivity.this, "Aw, Snap!", Toast.LENGTH_SHORT).show();
        }
    }

    private void logDate(final String tag, final String date) {
        Log.d(tag, date);
    }

    private void setupGson() {
        // GSON can parse the data.
        // Note there is a bug in GSON 2.5 that can cause it to StackOverflow when working with RealmObjects.
        // To work around this, use the ExclusionStrategy below or downgrade to 1.7.1
        // See more here: https://code.google.com/p/google-gson/issues/detail?id=440
        gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    private DateNode getDateFromStream(final InputStream stream) {
        JsonElement json = new JsonParser().parse(new InputStreamReader(stream));
        return gson.fromJson(json, DateNode.class);
    }

    private InputStream getStream() throws IOException {
        return getAssets().open("date.json");
    }
}
