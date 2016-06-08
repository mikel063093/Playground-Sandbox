package com.tudorluca.sandbox.textinput;

import com.tudorluca.sandbox.util.AbsPresenter;

import rx.Observable;

/**
 * Created by tudor on 08/06/16.
 */
public class TextInputContract {

    interface View {

        Observable<String> getUsernameObservable();

        void clearUsernameErrors();

        void clearUsernameStatus();

        void showProgressIndicator();

        void showUsernameAvailable(boolean isAvailable);

        void showError(Throwable e);
    }

    interface Presenter extends AbsPresenter<View> {}
}
