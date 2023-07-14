package eradev.bitcoin.mining.data.remote.request;

import eradev.bitcoin.mining.utils.Credentials;
import eradev.bitcoin.mining.data.remote.ApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Servicey {
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Credentials.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static ApiService promoMinerApi = retrofit.create(ApiService.class);

    public static ApiService getPromoMinerApi(){
        return promoMinerApi;
    }

}
