package ro.tudorluca.realm.sandbox;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

/**
 * Created by tudor on 26/02/16.
 */
public class CitiesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        // Clear the realm from last time
        Realm.deleteRealm(realmConfiguration);

        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
            @Override
            public void handleError(Throwable e) {
                super.handleError(e);
                e.printStackTrace();
            }
        });
    }
}
