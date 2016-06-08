package com.tudorluca.sandbox.okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tudorluca.sandbox.R;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static rx.Single.create;

public class OkHttpTLS2Activity extends AppCompatActivity {

    private static final String AUTH = "https://auth.smart.care/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http_tls2);

        final String username = "username@mail.com";
        final String password = "password";

        login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    Toast.makeText(OkHttpTLS2Activity.this, "Logged in!", Toast.LENGTH_LONG).show();
                    try {
                        Log.d("LOGIN", responseBody.string());
                    } catch (IOException ignored) {
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    Toast.makeText(OkHttpTLS2Activity.this, "Aw, snap!", Toast.LENGTH_LONG).show();
                    Log.e("WOPS", throwable.getMessage());
                });
    }

    private Observable<ResponseBody> login(String username, String password) {
        return create(
                (Single.OnSubscribe<Map<String, String>>) singleSubscriber ->
                        singleSubscriber.onSuccess(UserAuthModel.withUsernameAndPassword(username, password).toFieldMap()))
                .flatMapObservable(authMap -> {
                    final AuthApi api = provideAuthApi(provideOkHttpClient(), HttpUrl.parse(AUTH));
                    return api.login(authMap);
                });
    }

    private AuthApi provideAuthApi(OkHttpClient okHttpClient, HttpUrl url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(AuthApi.class);
    }

    private OkHttpClient provideOkHttpClient() {
        final ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_1)
                .build();

        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        return builder.build();
    }

    public interface AuthApi {

        @FormUrlEncoded
        @POST("connect/token")
        Observable<ResponseBody> login(@FieldMap Map<String, String> authMap);
    }
}
