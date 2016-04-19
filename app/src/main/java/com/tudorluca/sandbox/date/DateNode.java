package com.tudorluca.sandbox.date;

import io.realm.RealmObject;

/**
 * Created by tudor on 14/04/16.
 */
public class DateNode extends RealmObject {

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
