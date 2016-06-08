package com.tudorluca.sandbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import com.tudorluca.sandbox.city.CityActivity;
import com.tudorluca.sandbox.city.model.CitiesInteractor;
import com.tudorluca.sandbox.city.model.CitiesInteractorImplementation;
import com.tudorluca.sandbox.okhttp.OkHttpTLS2Activity;
import com.tudorluca.sandbox.textinput.TextInputActivity;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements ProviderInstaller.ProviderInstallListener {

    private ProgressBar loadingProgressBar;
    private Button goHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProviderInstaller.installIfNeededAsync(this, this);

        loadingProgressBar = (ProgressBar) findViewById(R.id.loading_progress);

        goHome = (Button) findViewById(R.id.go_home_button);
        if (goHome != null) {
            goHome.setOnClickListener(v -> startActivity(new Intent(this, CityActivity.class)));
        }

        final Button seed = (Button) findViewById(R.id.seed_button);
        if (seed != null) {
            seed.setOnClickListener(v -> seed());
        }

        final Button tls = (Button) findViewById(R.id.tls_button);
        if (tls != null) {
            tls.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OkHttpTLS2Activity.class)));
        }

        final Button rx = (Button) findViewById(R.id.rx_button);
        if (rx != null) {
            rx.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TextInputActivity.class)));
        }
    }

    @Override
    public void onProviderInstalled() {
        Toast.makeText(this, "SSL Provider installed", Toast.LENGTH_LONG).show();
    }

    private static final int ERROR_DIALOG_REQUEST_CODE = 1;

    @Override
    public void onProviderInstallFailed(int errorCode, Intent intent) {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        if (googleApi.isUserResolvableError(errorCode)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            googleApi.getErrorDialog(MainActivity.this, errorCode, ERROR_DIALOG_REQUEST_CODE, dialog -> {
                // The user chose not to take the recovery action
                onProviderInstallerNotAvailable();
            });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }
    }

    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.

        Toast.makeText(this, "WOOOOW! Security problems!", Toast.LENGTH_LONG).show();
    }

    private void seed() {
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
                    .doOnSubscribe(() -> {
                        goHome.setEnabled(false);
                        loadingProgressBar.setVisibility(View.VISIBLE);
                    })
                    .doOnTerminate(() -> loadingProgressBar.setVisibility(View.INVISIBLE))
                    .subscribe(
                            seeded -> {
                                goHome.setEnabled(true);
                            },
                            throwable -> {
                                Toast.makeText(MainActivity.this, "Aw, Snap!", Toast.LENGTH_SHORT).show();
                            }
                    );

        } catch (IOException ignored) {
            Toast.makeText(MainActivity.this, "Aw, Snap!", Toast.LENGTH_SHORT).show();
        }
    }
}
