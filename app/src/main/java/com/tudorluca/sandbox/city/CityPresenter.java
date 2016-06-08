package com.tudorluca.sandbox.city;


import android.util.Log;

import com.tudorluca.sandbox.city.model.CitiesInteractor;
import com.tudorluca.sandbox.city.model.City;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tudor on 26/02/16.
 */
public class CityPresenter implements CityContract.Presenter {
    private final CitiesInteractor interactor;
    private CityContract.View view;
    private CompositeSubscription compositeSubscription;

    public CityPresenter(CitiesInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(CityContract.View view) {
        this.view = view;
    }

    @Override
    public void onInitialize() {
        compositeSubscription = new CompositeSubscription();

        final Subscription subscription = interactor.getHomeTown()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> view.showProgressIndicator())
                .subscribe(
                        city -> {
                            view.hideProgressIndicator();
                            view.bind(city);
                        },
                        throwable -> {
                            view.hideProgressIndicator();
                            view.showAwSnapError();
                        }
                );
        compositeSubscription.add(subscription);

        Subscription subscription1 = interactor.getFunkCities(true, true).subscribe(cities -> {
            for (City city : cities) {
                Log.d("CITIES", "All: " + cities.size() + " " + City.printString(city));
            }
        });
        compositeSubscription.add(subscription1);

        Subscription subscription2 = interactor.getFunkCities(true, false)
                .subscribe(cities -> {
                    for (City city : cities) {
                        Log.d("CITIES", "Bellow: " + cities.size() + " " + City.printString(city));
                    }
                });
        compositeSubscription.add(subscription2);

        Subscription subscription3 = interactor.getFunkCities(false, true)
                .subscribe(cities -> {
                    for (City city : cities) {
                        Log.d("CITIES", "Above: " + cities.size() + " " + City.printString(city));
                    }
                });
        compositeSubscription.add(subscription3);
    }

    @Override
    public void onDestroy() {
        compositeSubscription.unsubscribe();
    }
}
