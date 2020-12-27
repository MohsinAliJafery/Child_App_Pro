package com.survey.hujuhj.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA3YVJ-Gw:APA91bH3dv6N_RUhcXsA7j56GuE-QTGuAjWDbhmKc6qxbH3O-IHeCKhLzOhUlc0bpfnUfTR1Kq_r0sKLSJfBRnuiRMXwIGKhIaM9O2tOEAH0PeieiXrNrqR0qOwdrhycTh86n-lQ8Ez2" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

