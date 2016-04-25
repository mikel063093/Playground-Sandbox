package com.tudorluca.sandbox.city.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.Subscriptions;

/**
 * Created by tudor on 26/02/16.
 */
public class CitiesInteractorImplementation implements CitiesInteractor {

    @Override
    public Observable<Void> seed(final InputStream cityStream) {
        return Observable.just(cityStream)
                .map(new Func1<InputStream, List<City>>() {
                    @Override
                    public List<City> call(InputStream inputStream) {
                        // GSON can parse the data.
                        // Note there is a bug in GSON 2.5 that can cause it to StackOverflow when working with RealmObjects.
                        // To work around this, use the ExclusionStrategy below or downgrade to 1.7.1
                        // See more here: https://code.google.com/p/google-gson/issues/detail?id=440
                        Gson gson = new GsonBuilder()
                                .setExclusionStrategies(new ExclusionStrategy() {
                                    @Override
                                    public boolean shouldSkipField(FieldAttributes f) {
                                        return f.getDeclaringClass().equals(RealmObject.class);
                                    }

                                    @Override
                                    public boolean shouldSkipClass(Class<?> clazz) {
                                        return false;
                                    }
                                })
                                .create();

                        JsonElement json = new JsonParser().parse(new InputStreamReader(cityStream));
                        return gson.fromJson(json, new TypeToken<List<City>>() {
                        }.getType());
                    }
                })
                .map(new Func1<List<City>, Void>() {
                    @Override
                    public Void call(final List<City> cities) {
                        final Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(cities);
                            }
                        });
                        realm.close();
                        return null;
                    }
                });
    }

    @Override
    public Observable<List<City>> getFunkCities(final boolean bellow, final boolean above) {
        Observable<List<City>> citiesList = null;

        if (bellow) {
            citiesList = getCitiesBelow30();
        }

        if (above) {
            if (citiesList == null) {
                citiesList = getCitiesAbove30();
            } else {
                citiesList = citiesList.withLatestFrom(getCitiesAbove30(),
                        new Func2<List<City>, List<City>, List<City>>() {
                            @Override
                            public List<City> call(List<City> cities, List<City> cities2) {
                                final List<City> allCities = new ArrayList<>(cities.size() + cities2.size());
                                allCities.addAll(cities);
                                allCities.addAll(cities2);
                                return allCities;
                            }
                        });
            }
        }

        if (citiesList == null) {
            return Observable.empty();
        }

        return citiesList;
    }

    private Observable<List<City>> getCitiesBelow30() {
        return Observable.defer(new Func0<Observable<List<City>>>() {
            @Override
            public Observable<List<City>> call() {
                return getManagedRealm()
                        .concatMap(new Func1<Realm, Observable<List<City>>>() {
                            @Override
                            public Observable<List<City>> call(Realm realm) {
                                return realm.where(City.class).lessThan("votes", 30).findAllAsync()
                                        .asObservable()
                                        .filter(new Func1<RealmResults<City>, Boolean>() {
                                            @Override
                                            public Boolean call(RealmResults<City> cities) {
                                                return cities.isLoaded();
                                            }
                                        })
                                        .map(new Func1<RealmResults<City>, List<City>>() {
                                            @Override
                                            public List<City> call(RealmResults<City> cities) {
                                                return cities;
                                            }
                                        });
                            }
                        });
            }
        });
    }

    private Observable<List<City>> getCitiesAbove30() {
        return Observable.defer(new Func0<Observable<List<City>>>() {
            @Override
            public Observable<List<City>> call() {
                return getManagedRealm()
                        .concatMap(new Func1<Realm, Observable<List<City>>>() {
                            @Override
                            public Observable<List<City>> call(Realm realm) {
                                return realm.where(City.class).greaterThan("votes", 30).findAllAsync()
                                        .asObservable()
                                        .filter(new Func1<RealmResults<City>, Boolean>() {
                                            @Override
                                            public Boolean call(RealmResults<City> cities) {
                                                return cities.isLoaded();
                                            }
                                        })
                                        .map(new Func1<RealmResults<City>, List<City>>() {
                                            @Override
                                            public List<City> call(RealmResults<City> cities) {
                                                return cities;
                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public Observable<City> getHomeTown() {
        return getManagedRealm()
                .concatMap(new Func1<Realm, Observable<City>>() {
                    @Override
                    public Observable<City> call(Realm realm) {
                        return realm.where(City.class).equalTo("name", "Cluj-Napoca").findAllAsync().asObservable()
                                .compose(new NullIfNoRealmObject<City>());
                    }
                });
    }

    private static Observable<Realm> getManagedRealm() {
        return Observable.create(new Observable.OnSubscribe<Realm>() {
            @Override
            public void call(final Subscriber<? super Realm> subscriber) {
                final Realm realm = Realm.getDefaultInstance();
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        realm.close();
                    }
                }));
                subscriber.onNext(realm);
            }
        });
    }
}
