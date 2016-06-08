package com.tudorluca.sandbox.textinput;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.tudorluca.sandbox.util.Utils.isEmpty;

/**
 * Created by tudor on 08/06/16.
 */
public class TextInputPresenter implements TextInputContract.Presenter {

    @NonNull
    private final LookupUsername lookupUsername;
    @Nullable
    private final String currentUsername;
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();
    private TextInputContract.View view;

    public TextInputPresenter(@NonNull LookupUsername lookupUsername,
                              @Nullable String currentUsername) {
        this.lookupUsername = lookupUsername;
        this.currentUsername = currentUsername;
    }

    @Override
    public void takeView(TextInputContract.View view) {
        if (view == null) {
            throw new NullPointerException("new view must not be null");
        }
        this.view = view;

        final Subscription s = processUsername()
                .doOnNext(u -> view.showProgressIndicator())
                .switchMap(u -> lookupUsername.exec(u).subscribeOn(Schedulers.io()))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .materialize()
                .subscribe(notification -> {
                    if (notification.isOnError()) {
                        view.showError(notification.getThrowable());
                    } else {
                        view.showUsernameAvailable(notification.getValue());
                    }
                });
        compositeSubscription.add(s);
    }

    @Override
    public void dropView() {
        this.view = null;
        compositeSubscription.clear();
    }

    private Observable<String> processUsername() {
        return view.getUsernameObservable()
                .map(String::trim)
                .map(username -> {
                    if (isEmpty(username) && isEmpty(currentUsername)) {
                        view.clearUsernameStatus();
                    }
                    if (username != null && username.equals(currentUsername)) {
                        view.showUsernameAvailable(true);
                    }
                    view.clearUsernameErrors();
                    return username;
                })
                .filter(username -> !isEmpty(username) && !username.equals(currentUsername));
    }
}
