package com.tudorluca.sandbox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalField;
import org.threeten.bp.temporal.WeekFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class SortDatesActivity extends AppCompatActivity {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_dates);
        Timber.tag("SortDates");

        final List<String> stringDates = someDates();

        final List<LocalDate> dates = parseDates(stringDates);

        final LocalDate today = getToday(dates);
        final List<LocalDate> thisWeek = getDatesThisWeek(dates);
        final List<LocalDate> thisMonth = getDatesThisMonth(dates);

        Timber.d("ALL");
        logDates(dates);

        Timber.d("TODAY");
        Timber.d(FORMATTER.format(today));
        Timber.d("WEEK");
        logDates(thisWeek);
        Timber.d("MONTH");
        logDates(thisMonth);
    }

    @Nullable
    private LocalDate getToday(List<LocalDate> dates) {
        final LocalDate today = LocalDate.now();
        for (LocalDate date : dates) {
            if (today.equals(date)) {
                return date;
            }
        }

        return null;
    }

    private List<LocalDate> getDatesThisWeek(List<LocalDate> dates) {
        final TemporalField dayOfWeek = WeekFields.of(Locale.getDefault()).dayOfWeek();
        final LocalDate start = LocalDate.now().with(dayOfWeek, 1);
        final LocalDate end = start.plusDays(6);

        return getDatesBetween(dates, start, end);
    }

    private List<LocalDate> getDatesThisMonth(List<LocalDate> dates) {
        final LocalDate now = LocalDate.now();
        final LocalDate start = now.withDayOfMonth(1);
        final LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        return getDatesBetween(dates, start, end);
    }

    private List<LocalDate> getDatesBetween(List<LocalDate> dates, LocalDate start, LocalDate end) {
        final List<LocalDate> datesInInterval = new ArrayList<>();

        for (LocalDate date : dates) {
            if (start.equals(date) || end.equals(date) || (date.isAfter(start) && date.isBefore(end))) {
                datesInInterval.add(date);
            }
        }

        return datesInInterval;
    }

    private List<LocalDate> parseDates(List<String> stringDates) {
        final List<LocalDate> dates = new ArrayList<>(stringDates.size());
        for (String stringDate : stringDates) {
            dates.add(LocalDate.parse(stringDate, FORMATTER));
        }

        return dates;
    }

    private List<String> someDates() {
        final List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now());
        dates.add(LocalDate.now().plusDays(1));
        dates.add(LocalDate.now().plusDays(2));
        dates.add(LocalDate.now().plusDays(3));
        dates.add(LocalDate.now().plusDays(8));
        dates.add(LocalDate.now().plusDays(9));
        dates.add(LocalDate.now().plusDays(13));
        dates.add(LocalDate.now().plusMonths(1));
        dates.add(LocalDate.now().plusMonths(2));
        dates.add(LocalDate.now().plusMonths(3));

        return Observable.from(dates)
                .map(new Func1<LocalDate, String>() {
                    @Override
                    public String call(LocalDate localDate) {
                        return FORMATTER.format(localDate);
                    }
                })
                .toList()
                .toBlocking()
                .first();
    }

    private void logDates(List<LocalDate> dates) {
        Observable.from(dates)
                .map(new Func1<LocalDate, String>() {
                    @Override
                    public String call(LocalDate localDate) {
                        return FORMATTER.format(localDate);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String date) {
                        Timber.d(date);
                    }
                })
                .toBlocking()
                .subscribe();

    }

}
