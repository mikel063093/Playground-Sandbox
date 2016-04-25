package com.tudorluca.sandbox.city.model;

import java.io.InputStream;
import java.util.List;

import rx.Observable;

/**
 * Created by tudor on 26/02/16.
 */
public interface CitiesInteractor {

    Observable<Void> seed(InputStream cityStream);

    Observable<List<City>> getFunkCities(boolean bellow, boolean above);

    Observable<City> getHomeTown();
}
