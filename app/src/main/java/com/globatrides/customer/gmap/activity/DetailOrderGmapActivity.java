package com.globatrides.customer.gmap.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.globatrides.customer.R;
import com.globatrides.customer.activity.MainActivity;
import com.globatrides.customer.activity.ProgressActivity;
import com.globatrides.customer.activity.TopupSaldoActivity;
import com.globatrides.customer.constants.BaseApp;
import com.globatrides.customer.constants.Constant;
import com.globatrides.customer.item.ItemItem;
import com.globatrides.customer.json.CheckStatusTransRequest;
import com.globatrides.customer.json.CheckStatusTransResponse;
import com.globatrides.customer.json.GetNearRideCarRequestJson;
import com.globatrides.customer.json.GetNearRideCarResponseJson;
import com.globatrides.customer.json.ItemRequestJson;
import com.globatrides.customer.json.PromoRequestJson;
import com.globatrides.customer.json.PromoResponseJson;
import com.globatrides.customer.json.RideCarResponseJson;
import com.globatrides.customer.json.fcm.DriverRequest;
import com.globatrides.customer.json.fcm.DriverResponse;
import com.globatrides.customer.json.fcm.FCMMessage;
import com.globatrides.customer.models.DriverModel;
import com.globatrides.customer.models.ItemModel;
import com.globatrides.customer.models.PesananMerchant;
import com.globatrides.customer.models.ServiceModel;
import com.globatrides.customer.models.TransModel;
import com.globatrides.customer.models.User;
import com.globatrides.customer.utils.SettingPreference;
import com.globatrides.customer.utils.Utility;
import com.globatrides.customer.utils.api.FCMHelper;
import com.globatrides.customer.utils.api.MapDirectionAPI;
import com.globatrides.customer.utils.api.ServiceGenerator;
import com.globatrides.customer.utils.api.service.BookService;
import com.globatrides.customer.utils.api.service.UserService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.globatrides.customer.json.fcm.FCMType.ORDER;

public class DetailOrderGmapActivity extends AppCompatActivity implements ItemItem.OnCalculatePrice {

    TextView location, orderprice, deliveryfee, diskon, total, diskontext, topuptext, textnotif, saldotext;
    Button order;
    RecyclerView rvmerchantnear;
    LinearLayout llcheckedcash, llcheckedwallet, llbtn;
    ImageButton checkedcash, checkedwallet;
    RelativeLayout rlprogress;
    Thread thread;
    boolean threadRun = true;
    TransModel transaksi;
    private DriverRequest request;
    TextView cashpayment, walletpayment;
    private double jarak;
    private long price, promocode;
    public static final String FITUR_KEY = "FiturKey";
    String alamat, biayaminimum, getbiaya, biayaakhir, biayadistance;
    double lat, lon, merlat, merlon, distance;
    private ServiceModel designedFitur;
    private final int DESTINATION_ID = 1;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;

    private FastItemAdapter<ItemItem> itemAdapter;
    private Realm realm;
    ImageView backbtn;
    private List<DriverModel> driverAvailable;
    private long foodCostLong = 0, maksimum;
    private long deliveryCostLong = 0;
    private String saldoWallet, checkedpaywallet, checkedpaycash, idresto, alamatresto, namamerchant, back;
    int service;
    SettingPreference sp;
    RelativeLayout rlnotif;

    EditText promokode;
    String home, layanan, description, icon;

    Button btnpromo;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        promocode = 0;
        realm = Realm.getDefaultInstance();
        rvmerchantnear = findViewById(R.id.merchantnear);
        location = findViewById(R.id.pickUpText);
        orderprice = findViewById(R.id.orderprice);
        llcheckedcash = findViewById(R.id.llcheckedcash);
        llcheckedwallet = findViewById(R.id.llcheckedwallet);
        cashpayment = findViewById(R.id.cashPayment);
        walletpayment = findViewById(R.id.walletpayment);
        deliveryfee = findViewById(R.id.cost);
        checkedcash = findViewById(R.id.checkedcash);
        checkedwallet = findViewById(R.id.checkedwallet);
        total = findViewById(R.id.price);
        diskon = findViewById(R.id.diskon);
        backbtn = findViewById(R.id.back_btn);
        diskontext = findViewById(R.id.ketsaldo);
        topuptext = findViewById(R.id.topUp);
        order = findViewById(R.id.order);
        rlprogress = findViewById(R.id.rlprogress);
        textnotif = findViewById(R.id.textnotif);
        rlnotif = findViewById(R.id.rlnotif);
        saldotext = findViewById(R.id.balance);
        promokode = findViewById(R.id.promocode);
        btnpromo = findViewById(R.id.btnpromo);
        back = "0";
        llbtn = findViewById(R.id.llbtn);

        driverAvailable = new ArrayList<>();
        service = 0;
        topuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopupSaldoActivity.class));
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailOrderGmapActivity.this, PicklocationGmapActivity.class);
                intent.putExtra(PicklocationGmapActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
                startActivityForResult(intent, PicklocationGmapActivity.LOCATION_PICKER_ID);
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sp = new SettingPreference(this);
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lon = intent.getDoubleExtra("lon", 0);
        merlat = intent.getDoubleExtra("merlat", 0);
        merlon = intent.getDoubleExtra("merlon", 0);
        distance = intent.getDoubleExtra("distance", 0);
        alamatresto = intent.getStringExtra("alamatresto");
        idresto = intent.getStringExtra("idresto");
        namamerchant = intent.getStringExtra("namamerchant");
        service = intent.getIntExtra(FITUR_KEY, -1);
        if (service != -1)
            designedFitur = realm.where(ServiceModel.class).equalTo("idFitur", service).findFirst();
        home = Objects.requireNonNull(designedFitur).getHome();
        layanan = designedFitur.getFitur();
        description = designedFitur.getKeterangan();
        icon = designedFitur.getIcon();

        LatLng latlang = new LatLng(lat, lon);

        requestAddress(latlang);

        RealmResults<ServiceModel> fiturs = realm.where(ServiceModel.class).findAll();

        for (ServiceModel service : fiturs) {
            Log.e("ID_FITUR", service.getIdFitur() + " " + service.getFitur() + " " + service.getBiayaAkhir());
        }
        getbiaya = String.valueOf(designedFitur.getBiaya());
        biayaminimum = String.valueOf(designedFitur.getBiaya_minimum());
        biayaakhir = String.valueOf(designedFitur.getBiayaAkhir());
        maksimum = Long.parseLong(designedFitur.getMaksimumdist());

        diskontext.setText("Discount " + designedFitur.getDiskon() + " with Wallet");
        total.setText("wait");
        deliveryfee.setText("wait");
        Utility.currencyTXT(diskon, String.valueOf(promocode), DetailOrderGmapActivity.this);
        User userLogin = BaseApp.getInstance(this).getLoginUser();
        saldoWallet = String.valueOf(userLogin.getWalletSaldo());

        pickUpLatLang = new LatLng(merlat, merlon);
        destinationLatLang = new LatLng(lat, lon);

        btnpromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                } catch (Exception ignored) {

                }
                if (promokode.getText().toString().isEmpty()) {
                    notif("Promo code cant be empty!");
                } else {
                    promokodedata();
                }
            }
        });

        itemAdapter = new FastItemAdapter<>();
        itemAdapter.notifyDataSetChanged();
        itemAdapter.withSelectable(true);
        itemAdapter.withItemEvent(new ClickEventHook<ItemItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ItemItem.ViewHolder) {
                    return ((ItemItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<ItemItem> fastAdapter, ItemItem item) {
                //sheetlist(position);
            }
        });
        rvmerchantnear.setLayoutManager(new LinearLayoutManager(this));
        rvmerchantnear.setAdapter(itemAdapter);
        updateEstimatedItemCost();
        loadItem();
    }

    private void requestAddress(LatLng latlang) {
        if (latlang != null) {
            MapDirectionAPI.getAddress(latlang, this).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull final okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String json = Objects.requireNonNull(response.body()).string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject Jobject = new JSONObject(json);
                                    JSONArray Jarray = Jobject.getJSONArray("results");
                                    JSONObject userdata = Jarray.getJSONObject(0);
                                    alamat = userdata.getString("formatted_address");
                                    location.setText(alamat);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void notif(String text) {
        rlnotif.setVisibility(View.VISIBLE);
        textnotif.setText(text);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                rlnotif.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private boolean readyToOrder() {
        if (destinationLatLang == null) {
            Toast.makeText(this, "Please select your location.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (total.getText().toString().isEmpty() || total.getText().toString().equals("wait")) {
            Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
            return false;
        }

        List<PesananMerchant> existingFood = realm.copyFromRealm(realm.where(PesananMerchant.class).findAll());

        int quantity = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
        }

        if (quantity == 0) {
            Toast.makeText(this, "Please order at least 1 item.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (jarak == -99.0) {
            Toast.makeText(this, "Please wait a moment...", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PicklocationGmapActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String addressset = data.getStringExtra(PicklocationGmapActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(PicklocationGmapActivity.LOCATION_LATLNG);
                location.setText(addressset);
                destinationLatLang = new LatLng(Objects.requireNonNull(latLng).latitude, latLng.longitude);
                if (pickUpLatLang != null) {
                    MapDirectionAPI.getDirection(pickUpLatLang, destinationLatLang, this).enqueue(updateRouteCallback);
                }
            }
        }
    }

    private void loadItem() {
        List<ItemModel> makananList = realm.copyFromRealm(realm.where(ItemModel.class).findAll());
        List<PesananMerchant> pesananFoods = realm.copyFromRealm(realm.where(PesananMerchant.class).findAll());
        itemAdapter.clear();
        for (PesananMerchant pesanan : pesananFoods) {
            ItemItem makananItem = new ItemItem(this, this);
            for (ItemModel makanan : makananList) {
                if (makanan.getId_item() == pesanan.getIdItem()) {
                    makananItem.quantity = pesanan.getQty();
                    makananItem.id = makanan.getId_item();
                    makananItem.namaMenu = makanan.getNama_item();
                    makananItem.deskripsiMenu = makanan.getDeskripsi_item();
                    makananItem.photo = makanan.getFoto_item();
                    makananItem.price = Long.parseLong(makanan.getHarga_item());
                    makananItem.promo = makanan.getStatus_promo();
                    if (makanan.getHarga_promo().isEmpty()) {
                        makananItem.hargapromo = 0;
                    } else {
                        makananItem.hargapromo = Long.parseLong(makanan.getHarga_promo());
                    }
                    makananItem.note = pesanan.getCatatan();

                    break;
                }
            }

            itemAdapter.add(makananItem);
        }

        itemAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void promokodedata() {
        btnpromo.setEnabled(false);
        btnpromo.setText("Wait...");
        final User user = BaseApp.getInstance(this).getLoginUser();
        PromoRequestJson request = new PromoRequestJson();
        request.setFitur(String.valueOf(service));
        request.setCode(promokode.getText().toString());

        UserService service = ServiceGenerator.createService(UserService.class, user.getNoTelepon(), user.getPassword());
        service.promocode(request).enqueue(new Callback<PromoResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<PromoResponseJson> call, @NonNull Response<PromoResponseJson> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        final long finalBiayaTotalpay = foodCostLong + price;
                        btnpromo.setEnabled(true);
                        btnpromo.setText("Use");
                        if (response.body().getType().equals("persen")) {
                            promocode = (Long.parseLong(response.body().getNominal()) * finalBiayaTotalpay) / 100;
                        } else {
                            promocode = Long.parseLong(response.body().getNominal());
                        }
                        updateDistance();
                    } else {
                        notif("promo code not available!");
                        btnpromo.setEnabled(true);
                        btnpromo.setText("Use");
                        promocode = 0;
                        updateDistance();
                    }
                } else {
                    notif("error!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PromoResponseJson> call, @NonNull Throwable t) {
                t.printStackTrace();
                notif("error");
            }
        });
    }

    @Override
    public void calculatePrice() {
        updateEstimatedItemCost();
    }

    String time;

    private void updateEstimatedItemCost() {
        List<PesananMerchant> existingFood = realm.copyFromRealm(realm.where(PesananMerchant.class).findAll());
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            cost += existingFood.get(p).getTotalHarga();
        }
        foodCostLong = cost;
        Utility.currencyTXT(orderprice, String.valueOf(foodCostLong), this);
        MapDirectionAPI.getDirection(pickUpLatLang, destinationLatLang, this).enqueue(updateRouteCallback);
    }

    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

        }

        @Override
        public void onResponse(@NonNull okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = Objects.requireNonNull(response.body()).string();
                final long distancetext = MapDirectionAPI.getDistance(DetailOrderGmapActivity.this, json);
                time = MapDirectionAPI.getTimeDistance(DetailOrderGmapActivity.this, json);
                if (distance >= 0) {
                    DetailOrderGmapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String format = String.format(Locale.US, "%.0f", ((float) (distancetext)) / 1000f);
                            long dist = Long.parseLong(format);
                            promocode = 0;
                            promokode.setText("");
                            distance = ((float) (distancetext)) / 1000f;
                            updateDistance();
                            if (dist < maksimum) {
                                order.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (readyToOrder()) {
                                            sendOrder();
                                            back = "1";
                                        }
                                    }
                                });
                            } else {
                                notif("destination too far away!");
                                order.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (readyToOrder()) {
                                            notif("destination too far away!");
                                            back = "0";
                                        }
                                    }
                                });

                            }

                        }
                    });
                }
            }
        }
    };
    double km;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (back.equals("1")) {
            Intent intent = new Intent(DetailOrderGmapActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    private void updateDistance() {

        checkedpaycash = "1";
        checkedpaywallet = "0";
        Log.e("CHECKEDWALLET", checkedpaywallet);
        checkedcash.setSelected(true);
        checkedwallet.setSelected(false);
        cashpayment.setTextColor(getResources().getColor(R.color.colorgradient));
        walletpayment.setTextColor(getResources().getColor(R.color.gray));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkedcash.setBackgroundTintList(getResources().getColorStateList(R.color.colorgradient));
            checkedwallet.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
        }
        km = distance;


        this.jarak = km;

        String cost = String.valueOf(biayaminimum);
        long biayaTotal = (long) (Double.parseDouble(getbiaya) * km);
        if (biayaTotal < Double.parseDouble(biayaminimum)) {
            this.price = Long.parseLong(biayaminimum);
            biayaTotal = Long.parseLong(biayaminimum);
            biayadistance = cost;
        } else {
            biayadistance = getbiaya;
        }
        this.price = biayaTotal;

        deliveryCostLong = biayaTotal;
        Log.e("distance", String.valueOf(deliveryCostLong));

        Utility.currencyTXT(deliveryfee, String.valueOf(deliveryCostLong), this);
        final long finalBiayaTotalpay = foodCostLong + price;

        Utility.currencyTXT(total, String.valueOf(finalBiayaTotalpay - promocode), DetailOrderGmapActivity.this);
        Utility.currencyTXT(diskon, String.valueOf(promocode), DetailOrderGmapActivity.this);
        long saldokini = Long.parseLong(saldoWallet);
        if (saldokini < ((foodCostLong + price) - (finalBiayaTotalpay * Double.parseDouble(biayaakhir)))) {
            llcheckedcash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.currencyTXT(total, String.valueOf(finalBiayaTotalpay - promocode), DetailOrderGmapActivity.this);
                    Utility.currencyTXT(diskon, String.valueOf(promocode), DetailOrderGmapActivity.this);
                    checkedcash.setSelected(true);
                    checkedwallet.setSelected(false);
                    checkedpaycash = "1";
                    checkedpaywallet = "0";
                    Log.e("CHECKEDWALLET", checkedpaywallet);
                    cashpayment.setTextColor(getResources().getColor(R.color.colorgradient));
                    walletpayment.setTextColor(getResources().getColor(R.color.gray));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkedcash.setBackgroundTintList(getResources().getColorStateList(R.color.colorgradient));
                        checkedwallet.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                    }
                }
            });
            llcheckedwallet.setEnabled(false);
        } else {
            llcheckedwallet.setEnabled(true);
            llcheckedcash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.currencyTXT(total, String.valueOf(finalBiayaTotalpay - promocode), DetailOrderGmapActivity.this);
                    Utility.currencyTXT(diskon, String.valueOf(promocode), DetailOrderGmapActivity.this);
                    checkedcash.setSelected(true);
                    checkedwallet.setSelected(false);
                    checkedpaycash = "1";
                    checkedpaywallet = "0";
                    Log.e("CHECKEDWALLET", checkedpaywallet);
                    cashpayment.setTextColor(getResources().getColor(R.color.colorgradient));
                    walletpayment.setTextColor(getResources().getColor(R.color.gray));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkedcash.setBackgroundTintList(getResources().getColorStateList(R.color.colorgradient));
                        checkedwallet.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                    }
                }
            });

            llcheckedwallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long diskonwallet = (long) ((finalBiayaTotalpay * Double.parseDouble(biayaakhir)) + promocode);
                    Log.e("distance", String.valueOf((foodCostLong + price) - (finalBiayaTotalpay * Double.parseDouble(biayaakhir))));
                    String totalwallet = String.valueOf(diskonwallet);
                    Utility.currencyTXT(diskon, totalwallet, DetailOrderGmapActivity.this);
                    Utility.currencyTXT(total, String.valueOf(finalBiayaTotalpay - diskonwallet), DetailOrderGmapActivity.this);
                    checkedcash.setSelected(false);
                    checkedwallet.setSelected(true);
                    checkedpaycash = "0";
                    checkedpaywallet = "1";
                    Log.e("CHECKEDWALLET", checkedpaywallet);
                    walletpayment.setTextColor(getResources().getColor(R.color.colorgradient));
                    cashpayment.setTextColor(getResources().getColor(R.color.gray));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkedwallet.setBackgroundTintList(getResources().getColorStateList(R.color.colorgradient));
                        checkedcash.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                    }
                }
            });
        }
    }

    private void sendOrder() {
        List<PesananMerchant> existingItem = realm.copyFromRealm(realm.where(PesananMerchant.class).findAll());

        for (PesananMerchant pesanan : existingItem) {
            if (pesanan.getCatatan() == null || pesanan.getCatatan().trim().equals(""))
                pesanan.setCatatan("");
        }

        ItemRequestJson param = new ItemRequestJson();
        User userLogin = BaseApp.getInstance(this).getLoginUser();
        param.setIdPelanggan(userLogin.getId());
        param.setOrderFitur(String.valueOf(service));
        param.setStartLatitude(destinationLatLang.latitude);
        param.setStartLongitude(destinationLatLang.longitude);
        param.setEndLatitude(merlat);
        param.setEndLongitude(merlon);
        param.setAlamatTujuan(location.getText().toString());
        param.setAlamatAsal(alamatresto);
        param.setJarak(jarak);
        param.setEstimasi(time);
        param.setHarga(deliveryCostLong);
        if (checkedpaycash.equals("1")) {
            param.setPakaiWallet(0);
            param.setKreditpromo(String.valueOf(promocode));
        } else {
            param.setPakaiWallet(1);
            param.setKreditpromo(String.valueOf((foodCostLong + price) * Double.parseDouble(biayaakhir) + promocode));
        }
        param.setIdResto(idresto);
        param.setTotalBiayaBelanja(foodCostLong);
        param.setCatatan("");
        param.setPesanan(existingItem);

        Log.e("Bookingdata", ServiceGenerator.gson.toJson(param));

        fetchNearDriver(param);
    }

    private void fetchNearDriver(final ItemRequestJson paramdata) {
        rlprogress.setVisibility(View.VISIBLE);
        if (destinationLatLang != null) {
            User loginUser = BaseApp.getInstance(this).getLoginUser();

            BookService services = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(merlat);
            param.setLongitude(merlon);
            param.setFitur(String.valueOf(service));

            services.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                @Override
                public void onResponse(@NonNull Call<GetNearRideCarResponseJson> call, @NonNull Response<GetNearRideCarResponseJson> response) {
                    if (response.isSuccessful()) {
                        driverAvailable = Objects.requireNonNull(response.body()).getData();
                        if (driverAvailable.isEmpty()) {
                            finish();
                            Toast.makeText(DetailOrderGmapActivity.this, "no driver arround you!", Toast.LENGTH_SHORT).show();
                        } else {
                            sendRequestTransaksi(paramdata, driverAvailable);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<GetNearRideCarResponseJson> call, @NonNull Throwable t) {

                }
            });

        }
    }

    private void buildDriverRequest(RideCarResponseJson response) {
        transaksi = response.getData().get(0);
        Log.e("wallet", String.valueOf(transaksi.isPakaiWallet()));
        User loginUser = BaseApp.getInstance(this).getLoginUser();
        if (request == null) {
            request = new DriverRequest();
            request.setIdTransaksi(transaksi.getId());
            request.setIdPelanggan(transaksi.getIdPelanggan());
            request.setRegIdPelanggan(loginUser.getToken());
            request.setOrderFitur(home);
            request.setStartLatitude(transaksi.getStartLatitude());
            request.setStartLongitude(transaksi.getStartLongitude());
            request.setEndLatitude(transaksi.getEndLatitude());
            request.setEndLongitude(transaksi.getEndLongitude());
            request.setJarak(transaksi.getJarak());
            request.setHarga(transaksi.getHarga() + foodCostLong);
            request.setWaktuOrder(transaksi.getWaktuOrder());
            request.setAlamatAsal(transaksi.getAlamatAsal());
            request.setAlamatTujuan(transaksi.getAlamatTujuan());
            request.setKodePromo(transaksi.getKodePromo());
            request.setKreditPromo(transaksi.getKreditPromo());
            request.setPakaiWallet(String.valueOf(transaksi.isPakaiWallet()));
            request.setEstimasi(namamerchant);
            request.setLayanan(layanan);
            request.setLayanandesc(description);
            request.setIcon(icon);
            request.setBiaya(String.valueOf(foodCostLong));
            request.setTokenmerchant(transaksi.getToken_merchant());
            request.setIdtransmerchant(transaksi.getIdtransmerchant());
            request.setDistance(String.valueOf(deliveryCostLong));


            String namaLengkap = String.format("%s", loginUser.getFullnama());
            request.setNamaPelanggan(namaLengkap);
            request.setTelepon(loginUser.getNoTelepon());
            request.setType(ORDER);
        }
    }

    private void sendRequestTransaksi(ItemRequestJson param, final List<DriverModel> driverList) {
        User loginUser = BaseApp.getInstance(this).getLoginUser();
        final BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        service.requestTransaksiMerchant(param).enqueue(new Callback<RideCarResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<RideCarResponseJson> call, @NonNull Response<RideCarResponseJson> response) {
                if (response.isSuccessful()) {
                    buildDriverRequest(Objects.requireNonNull(response.body()));
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < driverList.size(); i++) {
                                fcmBroadcast(i, driverList);
                            }

                            try {
                                Thread.sleep(30000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (threadRun) {
                                CheckStatusTransRequest param = new CheckStatusTransRequest();
                                param.setIdTransaksi(transaksi.getId());
                                service.checkStatusTransaksi(param).enqueue(new Callback<CheckStatusTransResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<CheckStatusTransResponse> call, @NonNull Response<CheckStatusTransResponse> response) {
                                        if (response.isSuccessful()) {
                                            CheckStatusTransResponse checkStatus = response.body();
                                            if (!Objects.requireNonNull(checkStatus).isStatus()) {
                                                notif("Driver not found!");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        notif("Driver not found!");
                                                    }
                                                });

                                                new Handler().postDelayed(new Runnable() {
                                                    public void run() {
                                                        finish();
                                                    }
                                                }, 3000);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<CheckStatusTransResponse> call, @NonNull Throwable t) {
                                        notif("Driver not found!");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                notif("Driver not found!");
                                            }
                                        });

                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                finish();
                                            }
                                        }, 3000);

                                    }
                                });
                            }

                        }
                    });
                    thread.start();


                }
            }

            @Override
            public void onFailure(@NonNull Call<RideCarResponseJson> call, @NonNull Throwable t) {
                t.printStackTrace();
                notif("Your account has a problem, please contact customer service!");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 3000);
            }
        });
    }

    private void fcmBroadcast(int index, List<DriverModel> driverList) {
        DriverModel driverToSend = driverList.get(index);
        request.setTime_accept(new Date().getTime() + "");
        final FCMMessage message = new FCMMessage();
        message.setTo(driverToSend.getRegId());
        message.setData(request);

        Log.e("REQUEST TO DRIVER", message.getData().toString());

        FCMHelper.sendMessage(Constant.FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
                Log.e("REQUEST TO DRIVER", message.getData().toString());
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final DriverResponse response) {
        Log.e("DRIVER RESPONSE (W)", response.getResponse() + " " + response.getId() + " " + response.getIdTransaksi());
        if (response.getResponse().equalsIgnoreCase(DriverResponse.ACCEPT) || response.getResponse().equals("3") || response.getResponse().equals("4")) {
            runOnUiThread(new Runnable() {
                public void run() {
                    threadRun = false;
                    for (DriverModel cDriver : driverAvailable) {
                        if (cDriver.getId().equals(response.getId())) {
                            Intent intent = new Intent(DetailOrderGmapActivity.this, ProgressActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("driver_id", cDriver.getId());
                            intent.putExtra("transaction_id", request.getIdTransaksi());
                            intent.putExtra("response", "2");
                            intent.putExtra("complete", "1");
                            startActivity(intent);
                            DriverResponse response = new DriverResponse();
                            response.setId("");
                            response.setIdTransaksi("");
                            response.setResponse("");
                            EventBus.getDefault().postSticky(response);
                            finish();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User userLogin = BaseApp.getInstance(this).getLoginUser();
        saldoWallet = String.valueOf(userLogin.getWalletSaldo());
        Utility.currencyTXT(saldotext, saldoWallet, this);
    }


}
