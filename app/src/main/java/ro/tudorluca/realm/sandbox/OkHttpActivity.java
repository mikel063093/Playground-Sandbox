package ro.tudorluca.realm.sandbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpActivity extends AppCompatActivity {

    private OkHttpClient cookieJarClient;
    private OkHttpClient interceptorClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);

        interceptorClient = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();

                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", "cookie-name=cookie-value")
                                .build();

                        return chain.proceed(authorized);
                    }
                })
                .build();

        cookieJarClient = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new StethoInterceptor())
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return new ArrayList<Cookie>() {{
                            add(createNonPersistentCookie());
                        }};
                    }
                })
                .build();

        final Button interceptorButton = (Button) findViewById(R.id.interceptor_button);
        interceptorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCall(interceptorClient);
            }
        });

        final Button cookieJarButton = (Button) findViewById(R.id.cookie_jar_button);
        cookieJarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCall(cookieJarClient);
            }
        });
    }

    private void newCall(final OkHttpClient client) {
        final Request request = new Request.Builder()
                .url("https://publicobject.com/helloworld.txt")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("Ok", response.body().string());
            }
        });
    }

    public static Cookie createNonPersistentCookie() {
        return new Cookie.Builder()
                .domain("publicobject.com")
                .path("/")
                .name("cookie-name")
                .value("cookie-value")
                .httpOnly()
                .secure()
                .build();
    }
}
