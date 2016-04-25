package com.tudorluca.sandbox.city;


import android.util.Log;

import com.tudorluca.sandbox.city.model.CitiesInteractor;
import com.tudorluca.sandbox.city.model.City;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
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
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        view.showProgressIndicator();
                    }
                })
                .subscribe(
                        new Action1<City>() {
                            @Override
                            public void call(City city) {
                                view.hideProgressIndicator();
                                view.bind(city);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                view.hideProgressIndicator();
                                view.showAwSnapError();
                            }
                        }
                );
        compositeSubscription.add(subscription);

        Subscription subscription1 = interactor.getFunkCities(true, true).subscribe(new Action1<List<City>>() {
            @Override
            public void call(List<City> cities) {
                for (City city : cities) {
                    Log.d("CITIES", "All: " + cities.size() + " " + City.printString(city));
                }
            }
        });
        compositeSubscription.add(subscription1);

        Subscription subscription2 = interactor.getFunkCities(true, false)
                .subscribe(new Action1<List<City>>() {
                    @Override
                    public void call(List<City> cities) {
                        for (City city : cities) {
                            Log.d("CITIES", "Bellow: " + cities.size() + " " + City.printString(city));
                        }
                    }
                });
        compositeSubscription.add(subscription2);

        Subscription subscription3 = interactor.getFunkCities(false, true)
                .subscribe(new Action1<List<City>>() {
                    @Override
                    public void call(List<City> cities) {
                        for (City city : cities) {
                            Log.d("CITIES", "Above: " + cities.size() + " " + City.printString(city));
                        }
                    }
                });
        compositeSubscription.add(subscription3);
    }

    @Override
    public void onDestroy() {
        compositeSubscription.unsubscribe();
    }
}
