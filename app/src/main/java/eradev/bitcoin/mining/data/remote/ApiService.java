package eradev.bitcoin.mining.data.remote;

import java.util.Locale;

import eradev.bitcoin.mining.data.remote.models.ConfigAppModel;
import eradev.bitcoin.mining.data.remote.models.DepositCodes;
import eradev.bitcoin.mining.data.remote.models.QuestsModel;
import eradev.bitcoin.mining.data.remote.models.Referals;
import eradev.bitcoin.mining.data.remote.models.StartScreenModel;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.models.StatusMessageDepozit;
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
    @GET("eradev/btc/configs/v1/ads-{default}.json")
    Call<UnityAdsModel> ads(@Path("default") String language);

    //Конфиг заданий
    @GET("eradev/btc/configs/v1/tasks-{default}.json")
    Call<QuestsModel> quests(@Path("default") String language);

    //Конфиг стартового экрана
    @GET("eradev/btc/configs/v1/startscreen-{default}.json")
    Call<StartScreenModel> startScreen(@Path("default") String language);

    //Конфиг приложения
    @GET("eradev/btc/configs/v1/app-{default}.json")
    Call<ConfigAppModel> configApp(@Path("default") String language);

    //Добавление пользователя по email и паролю
    @FormUrlEncoded
    @POST("eradev/btc/insert_user.php")
    Call<StatusMessage> insertUser(@Field("email") String email, @Field("password") String password);

    //Добавление пользователя по email через google
    @FormUrlEncoded
    @POST("eradev/btc/insert_user_email_only.php")
    Call<StatusMessage> insertUserOnlyEmail(@Field("email") String email);

    //Получение пользователя с помощью email и пароля
    @FormUrlEncoded
    @POST("eradev/btc/get_user.php")
    Call<Users> getUser(@Field("email") String email, @Field("password") String password);

    //Получение пользователя с помощью email через Google
    @FormUrlEncoded
    @POST("eradev/btc/get_user_google.php")
    Call<Users> getUserGoogle(@Field("email") String email);

    //Старт майнинга
    @FormUrlEncoded
    @POST("eradev/btc/start_minig.php")
    Call<StatusMessage> startMining(@Field("email") String email);

    //Буст
    @FormUrlEncoded
    @POST("eradev/btc/update_boost.php")
    Call<StatusMessage> sendBoost(@Field("email") String email);

    //Увеличение баланса пользователя
    @FormUrlEncoded
    @POST("eradev/btc/update_value_mining.php")
    Call<StatusMessage> sendBalance(@Field("email") String email, @Field("value") int value);

    //Установка времени использования ежедневного бонуса
    @FormUrlEncoded
    @POST("eradev/btc/update_daily.php")
    Call<StatusMessage> sendDailyBonus(@Field("email") String email);

    //Получение данных о рефералах
    @FormUrlEncoded
    @POST("eradev/btc/update_tasks.php")
    Call<StatusMessage> sendTasks(@Field("email") String email, @Field("tasks") String tasks);

    //Выполнение задания
    @FormUrlEncoded
    @POST("eradev/btc/get_referals_list.php")
    Call<Referals> getReferals(@Field("ref_code") String ref_cod);

    //Получение списка депосит кодов
    @FormUrlEncoded
    @POST("motherwallet/get_user_promcodes.php")
    Call<DepositCodes> getDepCodes(@Field("email") String email);

    //Создание депосит кода
    @FormUrlEncoded
    @POST("motherwallet/create_promocode_mobile.php")
    Call<StatusMessageDepozit> createDepCodes(@Field("email") String email, @Field("value") Double value, @Field("sname") String sname, @Field("source") String source);
}
