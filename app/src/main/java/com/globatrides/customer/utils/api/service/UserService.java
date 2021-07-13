package com.globatrides.customer.utils.api.service;

import com.globatrides.customer.json.AllMerchantByNearResponseJson;
import com.globatrides.customer.json.AllMerchantbyCatRequestJson;
import com.globatrides.customer.json.AllTransResponseJson;
import com.globatrides.customer.json.BankResponseJson;
import com.globatrides.customer.json.NewsDetailRequestJson;
import com.globatrides.customer.json.NewsDetailResponseJson;
import com.globatrides.customer.json.ChangePassRequestJson;
import com.globatrides.customer.json.DetailRequestJson;
import com.globatrides.customer.json.EditprofileRequestJson;
import com.globatrides.customer.json.GetAllMerchantbyCatRequestJson;
import com.globatrides.customer.json.GetServiceResponseJson;
import com.globatrides.customer.json.GetHomeRequestJson;
import com.globatrides.customer.json.GetHomeResponseJson;
import com.globatrides.customer.json.GetMerchantbyCatRequestJson;
import com.globatrides.customer.json.LoginRequestJson;
import com.globatrides.customer.json.LoginResponseJson;
import com.globatrides.customer.json.MerchantByCatResponseJson;
import com.globatrides.customer.json.MerchantByIdResponseJson;
import com.globatrides.customer.json.MerchantByNearResponseJson;
import com.globatrides.customer.json.MerchantbyIdRequestJson;
import com.globatrides.customer.json.PrivacyRequestJson;
import com.globatrides.customer.json.PrivacyResponseJson;
import com.globatrides.customer.json.PromoRequestJson;
import com.globatrides.customer.json.PromoResponseJson;
import com.globatrides.customer.json.RateRequestJson;
import com.globatrides.customer.json.RateResponseJson;
import com.globatrides.customer.json.RegisterRequestJson;
import com.globatrides.customer.json.RegisterResponseJson;
import com.globatrides.customer.json.ResponseJson;
import com.globatrides.customer.json.SearchMerchantbyCatRequestJson;
import com.globatrides.customer.json.StripeRequestJson;
import com.globatrides.customer.json.TopupRequestJson;
import com.globatrides.customer.json.TopupResponseJson;
import com.globatrides.customer.json.WalletRequestJson;
import com.globatrides.customer.json.WalletResponseJson;
import com.globatrides.customer.json.WithdrawRequestJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Ourdevelops Team on 10/13/2019.
 */

public interface UserService {

    @POST("customerapi/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("customerapi/kodepromo")
    Call<PromoResponseJson> promocode(@Body PromoRequestJson param);

    @POST("customerapi/listkodepromo")
    Call<PromoResponseJson> listpromocode(@Body PromoRequestJson param);

    @POST("customerapi/list_bank")
    Call<BankResponseJson> listbank(@Body WithdrawRequestJson param);

    @POST("customerapi/changepass")
    Call<LoginResponseJson> changepass(@Body ChangePassRequestJson param);

    @POST("customerapi/register_user")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @GET("customerapi/detail_fitur")
    Call<GetServiceResponseJson> getFitur();

    @POST("customerapi/forgot")
    Call<LoginResponseJson> forgot(@Body LoginRequestJson param);

    @POST("customerapi/privacy")
    Call<PrivacyResponseJson> privacy(@Body PrivacyRequestJson param);

    @POST("customerapi/home")
    Call<GetHomeResponseJson> home(@Body GetHomeRequestJson param);

    @POST("customerapi/topupstripe")
    Call<TopupResponseJson> topup(@Body TopupRequestJson param);

    @POST("customerapi/stripeaction")
    Call<ResponseJson> actionstripe(@Body StripeRequestJson param);

    @POST("customerapi/intentstripe")
    Call<ResponseJson> intentstripe(@Body StripeRequestJson param);

    @POST("customerapi/withdraw")
    Call<ResponseJson> withdraw(@Body WithdrawRequestJson param);

    @POST("customerapi/topuppaypal")
    Call<ResponseJson> topuppaypal(@Body WithdrawRequestJson param);

    @POST("customerapi/rate_driver")
    Call<RateResponseJson> rateDriver(@Body RateRequestJson param);

    @POST("customerapi/edit_profile")
    Call<RegisterResponseJson> editProfile(@Body EditprofileRequestJson param);

    @POST("customerapi/wallet")
    Call<WalletResponseJson> wallet(@Body WalletRequestJson param);

    @POST("customerapi/history_progress")
    Call<AllTransResponseJson> history(@Body DetailRequestJson param);

    @POST("customerapi/detail_berita")
    Call<NewsDetailResponseJson> beritadetail(@Body NewsDetailRequestJson param);

    @POST("customerapi/all_berita")
    Call<NewsDetailResponseJson> allberita(@Body NewsDetailRequestJson param);

    @POST("customerapi/merchantbykategoripromo")
    Call<MerchantByCatResponseJson> getmerchanbycat(@Body GetMerchantbyCatRequestJson param);

    @POST("customerapi/merchantbykategori")
    Call<MerchantByNearResponseJson> getmerchanbynear(@Body GetMerchantbyCatRequestJson param);

    @POST("customerapi/allmerchantbykategori")
    Call<AllMerchantByNearResponseJson> getallmerchanbynear(@Body GetAllMerchantbyCatRequestJson param);

    @POST("customerapi/itembykategori")
    Call<MerchantByIdResponseJson> getitembycat(@Body GetAllMerchantbyCatRequestJson param);

    @POST("customerapi/searchmerchant")
    Call<AllMerchantByNearResponseJson> searchmerchant(@Body SearchMerchantbyCatRequestJson param);

    @POST("customerapi/allmerchant")
    Call<AllMerchantByNearResponseJson> allmerchant(@Body AllMerchantbyCatRequestJson param);

    @POST("customerapi/merchantbyid")
    Call<MerchantByIdResponseJson> merchantbyid(@Body MerchantbyIdRequestJson param);


}
