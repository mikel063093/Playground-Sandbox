package com.tudorluca.sandbox.util;

public interface AbsPresenter<View> {

    void takeView(View view);

    void dropView();
}