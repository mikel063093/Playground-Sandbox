package ro.tudorluca.realm.sandbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ro.tudorluca.realm.sandbox.city.CityActivity;
import ro.tudorluca.realm.sandbox.model.CitiesInteractor;
import ro.tudorluca.realm.sandbox.model.CitiesInteractorImplementation;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button goHome = (Button) findViewById(R.id.go_to_hometown);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, CityActivity.class);
                startActivity(intent);
            }
        });

        final ProgressBar loadingProgressBar = (ProgressBar) findViewById(R.id.loading_progress);

        final Button seed = (Button) findViewById(R.id.seed_button);
        seed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    final Realm realm = Realm.getDefaultInstance();
                    final RealmConfiguration realmConfiguration = realm.getConfiguration();
                    realm.close();
                    Realm.deleteRealm(realmConfiguration);

                    final CitiesInteractor interactor = new CitiesInteractorImplementation();
                    InputStream stream = getAssets().open("cities.json");

                    interactor.seed(stream)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(new Action0() {
                                @Override
                                public void call() {
                                    goHome.setEnabled(false);
                                    loadingProgressBar.setVisibility(View.VISIBLE);
                                }
                            })
                            .finallyDo(new Action0() {
                                @Override
                                public void call() {
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                }
                            })
                            .subscribe(
                                    new Action1<Void>() {
                                        @Override
                                        public void call(Void seeded) {
                                            goHome.setEnabled(true);
                                        }
                                    },
                                    new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Toast.makeText(MainActivity.this, "Aw, Snap!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );

                } catch (IOException ignored) {
                    Toast.makeText(MainActivity.this, "Aw, Snap!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button okHttp = (Button) findViewById(R.id.ok_button);
        okHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, OkHttpActivity.class);
                startActivity(intent);
            }
        });
    }
}
