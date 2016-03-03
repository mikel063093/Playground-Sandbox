package com.tudorluca.sandbox.city;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.tudorluca.sandbox.R;
import com.tudorluca.sandbox.city.model.CitiesInteractorImplementation;
import com.tudorluca.sandbox.city.model.City;


public class CityActivity extends AppCompatActivity implements CityContract.View {

    private ProgressDialog dialog;
    private TextView cityTextView;
    private CityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        cityTextView = (TextView) findViewById(R.id.city_text_view);

        presenter = new CityPresenter(new CitiesInteractorImplementation());
        presenter.setView(this);
        presenter.onInitialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void bind(@Nullable City city) {
        if (city == null) {
            cityTextView.setText(R.string.no_city);
            return;
        }

        final String description = city.getName() + " has " + city.getVotes() + " votes!";
        cityTextView.setText(description);
    }

    @Override
    public void hideProgressIndicator() {
        dialog.dismiss();
    }

    @Override
    public void showProgressIndicator() {
        dialog = ProgressDialog.show(this, "Please wait", "Loading your hometown", true, false);
        dialog.show();
    }

    @Override
    public void showAwSnapError() {
        Toast.makeText(this, "Aw, Snap!", Toast.LENGTH_SHORT).show();
    }
}
