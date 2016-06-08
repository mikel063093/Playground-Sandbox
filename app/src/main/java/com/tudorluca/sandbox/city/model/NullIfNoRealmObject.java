package com.tudorluca.sandbox.city.model;

import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by tudor on 26/02/16.
 */
public class NullIfNoRealmObject<RlmObject extends RealmObject> implements Observable.Transformer<RealmResults<RlmObject>, RlmObject> {

    @Override
    public Observable<RlmObject> call(Observable<RealmResults<RlmObject>> observable) {
        return observable
                .filter(rs -> rs != null && rs.isLoaded())
                .map(rs -> {
                    if (rs.size() > 0) {
                        return rs.first();
                    }
                    return null;
                });
    }
}

