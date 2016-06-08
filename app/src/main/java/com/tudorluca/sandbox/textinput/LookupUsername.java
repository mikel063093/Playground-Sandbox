package com.tudorluca.sandbox.textinput;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by tudor on 08/06/16.
 */
public class LookupUsername {

    private int flag = 0;

    public Observable<Boolean> exec(@NonNull String username) {
        return Observable.just(username)
                .delay(2, TimeUnit.SECONDS)
                .map(u -> {
                    flag++;
                    return flag % 2 == 1;
                });
    }
}
