package dev.rezaur.vhoot.fragment;

import dev.rezaur.vhoot.notification.Sender;
import dev.rezaur.vhoot.notification.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA3zbsbVI:APA91bGXlmXVQT4y7YHO36uqgdoqIHRbIM6VRO4Dd5sND4SGUEpSDMFE1SoqsZtzWiwwiuhiyUzeQ87wbEm8sOEwQ_GHWwCgxTWPIr6GSi-ZsITwGzLSmv-gkzK8pgJbTOCVxyYmNfNU"
            }
    )

    @POST("fcm/send")
    Call<UserResponse> sendNotification(@Body Sender body);
}