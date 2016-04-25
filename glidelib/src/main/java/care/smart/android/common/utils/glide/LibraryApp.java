package care.smart.android.common.utils.glide;

import android.app.Application;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tudor on 19/04/16.
 */
public class LibraryApp extends Application {

    private static LibraryApp instance;

    public static LibraryApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    private OkHttpClient client;

    public OkHttpClient getClient() {
        if (client == null) {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.networkInterceptors().add(funHeadersInterceptor());
            builder.networkInterceptors().add(new StethoInterceptor());

            client = builder.build();
        }

        return client;
    }

    private Interceptor funHeadersInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();

                final Request authorized = original.newBuilder()
                        .addHeader("Woot", "asta-i tati!")
                        .build();

                return chain.proceed(authorized);
            }
        };
    }
}
