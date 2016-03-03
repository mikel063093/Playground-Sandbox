package com.tudorluca.sandbox.city.model;

import java.io.InputStream;

import rx.Observable;

/**
 * Created by tudor on 26/02/16.
 */
public interface CitiesInteractor {

    Observable<Void> seed(InputStream cityStream);

    Observable<City> getHomeTown();
}
