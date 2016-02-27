package ro.tudorluca.realm.sandbox.model;

import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by tudor on 26/02/16.
 */
public class NullIfNoRealmObject<RlmObject extends RealmObject> implements Observable.Transformer<RealmResults<RlmObject>, RlmObject> {

    @Override
    public Observable<RlmObject> call(Observable<RealmResults<RlmObject>> observable) {
        return observable
                .filter(new Func1<RealmResults<RlmObject>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<RlmObject> rs) {
                        return rs != null && rs.isLoaded();
                    }
                })
                .map(new Func1<RealmResults<RlmObject>, RlmObject>() {
                    @Override
                    public RlmObject call(RealmResults<RlmObject> rs) {
                        if (rs.size() > 0) {
                            return rs.first();
                        }
                        return null;
                    }
                });
    }
}

