package com.globatrides.customer.utils.api.service;

import com.globatrides.customer.json.CheckStatusTransRequest;
import com.globatrides.customer.json.CheckStatusTransResponse;
import com.globatrides.customer.json.DetailRequestJson;
import com.globatrides.customer.json.DetailTransResponseJson;
import com.globatrides.customer.json.GetNearRideCarRequestJson;
import com.globatrides.customer.json.GetNearRideCarResponseJson;
import com.globatrides.customer.json.ItemRequestJson;
import com.globatrides.customer.json.LocationDriverRequest;
import com.globatrides.customer.json.LocationDriverResponse;
import com.globatrides.customer.json.RideCarRequestJson;
import com.globatrides.customer.json.RideCarResponseJson;
import com.globatrides.customer.json.SendRequestJson;
import com.globatrides.customer.json.SendResponseJson;
import com.globatrides.customer.json.fcm.CancelBookRequestJson;
import com.globatrides.customer.json.fcm.CancelBookResponseJson;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ourdevelops Team on 10/17/2019.
 */

public interface BookService {

    @POST("customerapi/list_ride")
    Call<GetNearRideCarResponseJson> getNearRide(@Body GetNearRideCarRequestJson param);

    @POST("customerapi/list_car")
    Call<GetNearRideCarResponseJson> getNearCar(@Body GetNearRideCarRequestJson param);

    @POST("customerapi/request_transaksi")
    Call<RideCarResponseJson> requestTransaksi(@Body RideCarRequestJson param);

    @POST("customerapi/inserttransaksimerchant")
    Call<RideCarResponseJson> requestTransaksiMerchant(@Body ItemRequestJson param);

    @POST("customerapi/request_transaksi_send")
    Call<SendResponseJson> requestTransaksisend(@Body SendRequestJson param);

    @POST("customerapi/check_status_transaksi")
    Call<CheckStatusTransResponse> checkStatusTransaksi(@Body CheckStatusTransRequest param);

    @POST("customerapi/user_cancel")
    Call<CancelBookResponseJson> cancelOrder(@Body CancelBookRequestJson param);

    @POST("customerapi/liat_lokasi_driver")
    Call<LocationDriverResponse> liatLokasiDriver(@Body LocationDriverRequest param);

    @POST("customerapi/detail_transaksi")
    Call<DetailTransResponseJson> detailtrans(@Body DetailRequestJson param);


}
