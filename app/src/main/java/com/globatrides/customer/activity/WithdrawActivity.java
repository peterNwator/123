package com.globatrides.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globatrides.customer.R;
import com.globatrides.customer.constants.BaseApp;
import com.globatrides.customer.constants.Constant;
import com.globatrides.customer.item.BankItem;
import com.globatrides.customer.json.BankResponseJson;
import com.globatrides.customer.json.ResponseJson;
import com.globatrides.customer.json.WithdrawRequestJson;
import com.globatrides.customer.json.fcm.FCMMessage;
import com.globatrides.customer.models.Notif;
import com.globatrides.customer.models.User;
import com.globatrides.customer.utils.SettingPreference;
import com.globatrides.customer.utils.Utility;
import com.globatrides.customer.utils.api.FCMHelper;
import com.globatrides.customer.utils.api.ServiceGenerator;
import com.globatrides.customer.utils.api.service.UserService;

import java.io.IOException;
import java.util.Objects;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    EditText amount, bank, accnumber, nama;
    Button submit;
    TextView notif;
    ImageView backbtn, images;
    RelativeLayout rlnotif, rlprogress;
    String disableback, type, nominal;
    SettingPreference sp;
    RecyclerView petunjuk;
    LinearLayout llpentunjuk;
    BankItem bankItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        disableback = "false";
        amount = findViewById(R.id.amount);
        bank = findViewById(R.id.bank);
        accnumber = findViewById(R.id.accnumber);
        backbtn = findViewById(R.id.back_btn);
        submit = findViewById(R.id.submit);
        rlnotif = findViewById(R.id.rlnotif);
        notif = findViewById(R.id.textnotif);
        rlprogress = findViewById(R.id.rlprogress);
        nama = findViewById(R.id.namanumber);
        images = findViewById(R.id.imagebackground);
        sp = new SettingPreference(this);
        llpentunjuk = findViewById(R.id.llpentunjuk);
        petunjuk = findViewById(R.id.petunjuk);
        sp = new SettingPreference(this);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if (Objects.equals(type, "topup")) {
            images.setImageResource(R.drawable.atm);
            images.setScaleType(ImageView.ScaleType.FIT_XY);
            nominal = intent.getStringExtra("nominal");

            Utility.currencyTXT(amount, Objects.requireNonNull(nominal), WithdrawActivity.this);
            petunjuk.setHasFixedSize(true);
            petunjuk.setNestedScrollingEnabled(false);
            petunjuk.setLayoutManager(new GridLayoutManager(this, 1));
            getpetunjuk();
        }

        amount.addTextChangedListener(Utility.currencyTW(amount, this));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User userLogin = BaseApp.getInstance(WithdrawActivity.this).getLoginUser();
                if (type.equals("withdraw")) {
                    if (amount.getText().toString().isEmpty()) {
                        notif(getString(R.string.amount_toast));
                    } else if (Long.parseLong(amount.getText()
                            .toString()
                            .replace(".", "")
                            .replace(",", "")
                            .replace(sp.getSetting()[0], "")) > userLogin.getWalletSaldo()) {
                        notif(getString(R.string.noteniugh_balance));
                    } else if (bank.getText().toString().isEmpty()) {
                        notif(getString(R.string.bank_toast));
                    } else if (accnumber.getText().toString().isEmpty()) {
                        notif(getString(R.string.accnumber_toast));
                    } else {
                        submit();
                    }
                } else {
                    if (amount.getText().toString().isEmpty()) {
                        notif(getString(R.string.amount_toast));
                    } else if (bank.getText().toString().isEmpty()) {
                        notif(getString(R.string.bank_toast));
                    } else if (accnumber.getText().toString().isEmpty()) {
                        notif(getString(R.string.accnumber_toast));
                    } else {
                        submit();
                    }
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submit() {
        progressshow();
        final User user = BaseApp.getInstance(this).getLoginUser();
        WithdrawRequestJson request = new WithdrawRequestJson();
        request.setId(user.getId());
        request.setBank(bank.getText().toString());
        request.setName(nama.getText().toString());
        if (sp.getSetting()[19].equals("2")) {
            request.setAmount(amount.getText().toString().replace(".", "").replace(sp.getSetting()[0], ""));
        } else {
            request.setAmount(amount.getText().toString().replace(".", "").replace(sp.getSetting()[0], "")+"00");
        }
        request.setCard(accnumber.getText().toString());
        request.setNotelepon(user.getNoTelepon());
        request.setEmail(user.getEmail());
        request.setType(type);

        UserService service = ServiceGenerator.createService(UserService.class, user.getNoTelepon(), user.getPassword());
        service.withdraw(request).enqueue(new Callback<ResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<ResponseJson> call, @NonNull Response<ResponseJson> response) {
                progresshide();
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                        Notif notif = new Notif();
                        if (type.equals("withdraw")) {
                            notif.title = "Withdraw";
                            notif.message = "Withdrawal requests have been successful, we will send a notification after we have sent funds to your account";
                        } else {
                            notif.title = "Topup";
                            notif.message = "Topup requests have been successful, we will send a notification after we confirm";
                        }
                        sendNotif(user.getToken(), notif);

                    } else {
                        notif(getString(R.string.accdata_error));
                    }
                } else {
                    notif(getString(R.string.error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseJson> call, @NonNull Throwable t) {
                progresshide();
                t.printStackTrace();
                notif(getString(R.string.error));
            }
        });
    }

    public void onBackPressed() {
        if (!disableback.equals("true")) {
            finish();
        }
    }

    public void notif(String text) {
        rlnotif.setVisibility(View.VISIBLE);
        notif.setText(text);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                rlnotif.setVisibility(View.GONE);
            }
        }, 3000);
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
        disableback = "true";
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
        disableback = "false";
    }

    private void sendNotif(final String regIDTujuan, final Notif notif) {

        final FCMMessage message = new FCMMessage();
        message.setTo(regIDTujuan);
        message.setData(notif);

        FCMHelper.sendMessage(Constant.FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void getpetunjuk() {
        User user = BaseApp.getInstance(this).getLoginUser();
        WithdrawRequestJson request = new WithdrawRequestJson();

        UserService service = ServiceGenerator.createService(UserService.class, user.getNoTelepon(), user.getPassword());
        service.listbank(request).enqueue(new Callback<BankResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<BankResponseJson> call, @NonNull Response<BankResponseJson> response) {
                if (response.isSuccessful()) {
                    llpentunjuk.setVisibility(View.VISIBLE);
                    bankItem = new BankItem(WithdrawActivity.this, Objects.requireNonNull(response.body()).getData(), R.layout.item_bank);
                    petunjuk.setAdapter(bankItem);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankResponseJson> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
