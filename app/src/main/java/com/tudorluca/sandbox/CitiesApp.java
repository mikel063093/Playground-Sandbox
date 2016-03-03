package com.tudorluca.sandbox;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.facebook.stetho.InspectorModulesProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.facebook.stetho.rhino.JsRuntimeReplFactoryBuilder;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Scriptable;

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

        final Handler handler = new Handler(Looper.getMainLooper());

        final ActivityCallback activityCallback = new ActivityCallback();
        registerActivityLifecycleCallbacks(activityCallback);

        final Context context = this;
        Stetho.initialize(Stetho.newInitializerBuilder(context)
                .enableWebKitInspector(new InspectorModulesProvider() {
                    @Override
                    public Iterable<ChromeDevtoolsDomain> get() {
                        return new Stetho.DefaultInspectorModulesBuilder(context).runtimeRepl(
                                new JsRuntimeReplFactoryBuilder(context)
                                        .addVariable("$h", handler)
                                        .addFunction("$a", new BaseFunction() {
                                            @Override
                                            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                                                return activityCallback.getActivity();
                                            }
                                        })
                                        .build()
                        ).finish();
                    }
                })
                .build()
        );
    }


}