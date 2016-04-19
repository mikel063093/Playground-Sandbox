package ro.tudorluca.sandbox.library;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created by tudor on 19/04/16.
 */
public class GlideSetup implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        final OkHttpClient client = LibraryApp.getInstance().getClient();
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
        glide.register(User.class, InputStream.class, new UserModelLoader.Factory());
    }


    private static class UserModelLoader extends BaseGlideUrlLoader<User> {

        public static class Factory implements ModelLoaderFactory<User, InputStream> {

            @Override
            public ModelLoader<User, InputStream> build(Context context, GenericLoaderFactory factories) {
                return new UserModelLoader(context);
            }

            @Override
            public void teardown() {

            }
        }

        public UserModelLoader(Context context) {
            super(context);
        }

        @Override
        protected String getUrl(User model, int width, int height) {
            return model.avatarUrl;
        }
    }
}
