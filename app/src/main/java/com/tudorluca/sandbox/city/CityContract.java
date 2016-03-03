package com.tudorluca.sandbox.city;

import android.support.annotation.Nullable;

import com.tudorluca.sandbox.city.model.City;


/**
 * Created by tudor on 26/02/16.
 */
public interface CityContract {

    interface View {

        void bind(@Nullable City city);

        void showProgressIndicator();

        void hideProgressIndicator();

        void showAwSnapError();
    }

    interface Presenter {

        void setView(View view);

        void onInitialize();

        void onDestroy();
    }
}
