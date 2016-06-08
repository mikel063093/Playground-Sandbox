package com.tudorluca.sandbox.textinput;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.tudorluca.sandbox.R;
import com.tudorluca.sandbox.databinding.ActivityTextInputBinding;
import com.tudorluca.sandbox.util.UiUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class TextInputActivity extends AppCompatActivity implements TextInputContract.View {

    private ActivityTextInputBinding binding;
    private TextInputContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_text_input);

        presenter = new TextInputPresenter(new LookupUsername(), null);
        presenter.takeView(this);
    }

    @Override
    public Observable<String> getUsernameObservable() {
        return RxTextView.textChanges(binding.usernameInput)
                .skip(1) // We don't want to process the default empty value
                .sample(100, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .debounce(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .onBackpressureLatest()
                .map(CharSequence::toString);
    }


    @Override
    public void showProgressIndicator() {
        AnimatedVectorDrawable loading = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_loading);
        assert loading != null;
        loading.setBounds(0, 0, (int) UiUtils.dpToPx(this, 48), (int) UiUtils.dpToPx(this, 48));
        binding.usernameInput.setCompoundDrawablesRelative(null, null, loading.mutate(), null);
        loading.start();
    }

    @Override
    public void showUsernameAvailable(boolean isAvailable) {
        if (isAvailable) {
            AnimatedVectorDrawable status = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_loading_complete);
            assert status != null;
            status.setBounds(0, 0, (int) UiUtils.dpToPx(this, 48), (int) UiUtils.dpToPx(this, 48));
            binding.usernameInputLayout.setError(null);
            binding.usernameInput.setCompoundDrawablesRelative(null, null, status.mutate(), null);
            status.start();
        } else {
            binding.usernameInputLayout.setError("Username not available");
            binding.usernameInput.setCompoundDrawablesRelative(null, null, null, null);
        }
    }

    @Override
    public void clearUsernameStatus() {
        binding.usernameInput.setCompoundDrawablesRelative(null, null, null, null);
    }

    @Override
    public void clearUsernameErrors() {
        binding.usernameInputLayout.setError(null);
    }

    @Override
    public void showError(Throwable e) {
        Toast.makeText(this, "Aw snap!" + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dropView();
    }
}
