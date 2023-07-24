package eradev.bitcoin.mining.data.remote;

import java.util.Locale;

import eradev.bitcoin.mining.data.remote.models.ConfigAppModel;
import eradev.bitcoin.mining.data.remote.models.QuestsModel;
import eradev.bitcoin.mining.data.remote.models.StartScreenModel;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.models.UnityAdsModel;
import eradev.bitcoin.mining.data.remote.models.Users;
import eradev.bitcoin.mining.utils.Credentials;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    //Конфиг рекламы
    @GET("configs/v1/ads-{default}.json")
    Call<UnityAdsModel> ads(@Path("default") String language);

    //Конфиг заданий
    @GET("configs/v1/tasks-{default}.json")
    Call<QuestsModel> quests(@Path("default") String language);

    //Конфиг стартового экрана
    @GET("configs/v1/startscreen-{default}.json")
    Call<StartScreenModel> startScreen(@Path("default") String language);

    //Конфиг приложения
    @GET("configs/v1/app-{default}.json")
    Call<ConfigAppModel> configApp(@Path("default") String language);

    //Добавление пользователя по email и паролю
    @FormUrlEncoded
    @POST("insert_user.php")
    Call<StatusMessage> insertUser(@Field("email") String email, @Field("password") String password);

    //Добавление пользователя по email через google
    @FormUrlEncoded
    @POST("insert_user_email_only.php")
    Call<StatusMessage> insertUserOnlyEmail(@Field("email") String email);

    //Получение пользователя с помощью email и пароля
    @FormUrlEncoded
    @POST("get_user.php")
    Call<Users> getUser(@Field("email") String email, @Field("password") String password);

    //Получение пользователя с помощью email через Google
    @FormUrlEncoded
    @POST("get_user_google.php")
    Call<Users> getUserGoogle(@Field("email") String email);

    //Старт майнинга
    @FormUrlEncoded
    @POST("start_minig.php")
    Call<StatusMessage> startMining(@Field("email") String email);

    //Буст
    @FormUrlEncoded
    @POST("update_boost.php")
    Call<StatusMessage> sendBoost(@Field("email") String email);

    //Увеличение баланса пользователя
    @FormUrlEncoded
    @POST("update_value_mining.php")
    Call<StatusMessage> sendBalance(@Field("email") String email, @Field("value") int value);
}
