package com.example.singgihsaputro.samplerxbindinglogin;

import android.support.annotation.BoolRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainActivity extends AppCompatActivity {
    TextInputEditText tie_username;
    TextInputEditText tie_password;
    TextInputLayout til_username;
    TextInputLayout til_password;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tie_username = (TextInputEditText) findViewById(R.id.et_username);
        tie_password = (TextInputEditText) findViewById(R.id.et_password);
        til_username = (TextInputLayout) findViewById(R.id.til_username);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        btn_login = (Button) findViewById(R.id.btn_login);

        Observable<CharSequence> o1 = RxTextView.textChanges(tie_username).debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
        Observable<CharSequence> o2 = RxTextView.textChanges(tie_password).debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());

        btn_login.setEnabled(false);
        Subscription subcription =
                Observable.combineLatest(o1, o2, new Func2<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence username, CharSequence password) {
                        boolean usernameValid = username.length() > 6;
                        boolean passValid = password.length() > 6;
                        return usernameValid && passValid;
                    }
                }).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            btn_login.setEnabled(true);
                        } else {
                            btn_login.setEnabled(false);
                        }

                    }
                });

        Subscription subcription_username =
                o1.subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence input) {
                        if(input.length()>6) {
                            til_username.setError(null);
                        }else {
                            til_username.setError("Username minimal 7 karakter");
                        }
                    }
                });

        Subscription subcription_password =
                o2.subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence input) {
                        if(input.length()>6) {
                            til_password.setError(null);
                        }else {
                            til_password.setError("Password minimal 7 karakter");
                        }
                    }
                });
    }

    public void doLogin(View v) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
        boolean passValid = tie_password.getText().toString().matches(pattern);
        if(passValid) {
            til_password.setError(null);
            Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
        }else {
            til_password.setError("Password berisi alfabet dan angka");
        }
    }
}
