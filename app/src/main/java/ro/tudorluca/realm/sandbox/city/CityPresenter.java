package ro.tudorluca.realm.sandbox.city;

import ro.tudorluca.realm.sandbox.model.CitiesInteractor;
import ro.tudorluca.realm.sandbox.model.City;
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
    }

    @Override
    public void onDestroy() {
        compositeSubscription.unsubscribe();
    }
}
