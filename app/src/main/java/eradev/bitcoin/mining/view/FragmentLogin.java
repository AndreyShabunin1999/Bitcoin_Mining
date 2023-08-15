package eradev.bitcoin.mining.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import eradev.bitcoin.mining.CallbackFragment;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.UserEntity;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.models.User;
import eradev.bitcoin.mining.data.remote.models.Users;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentLogin extends Fragment {

    Button btnLogIn;
    EditText etEmail, etPassword;
    TextView tvSignInGoogle, tvSignUp;
    CallbackFragment callbackFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Инициализация элементов интерфейса
        init(view);

        //Обработка нажатия текста "Sign Up"
        tvSignUp.setOnClickListener(v -> {
            if(callbackFragment != null){
                callbackFragment.changeFragment();
            }
        });

        tvSignInGoogle.setOnClickListener(v -> {
            if(etEmail.getText().toString().isEmpty()) {
               // Toast.makeText(getContext(), R.string.text_error_empty_email, Toast.LENGTH_SHORT).show();
            } else {
                if(validEmail(etEmail.getText().toString().trim())){
                    ApiService promoMinerApi = Servicey.getPromoMinerApi();
                    Call<Users> call = promoMinerApi.getUserGoogle(etEmail.getText().toString().trim());

                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                            //Если код от сервера в диапазоне от 200-300
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                //Проверка на существование пользователя на сервере
                                if(response.body().getSuccess() == 1){
                                    User user = response.body().getUsers().get(0);
                                    updateUserInBD(user, "");
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                } else {
                                //    Toast.makeText(getContext(), R.string.text_not_found_user, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                               // Toast.makeText(getContext(), R.string.text_error_auth, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                           // Toast.makeText(getContext(), R.string.text_error_auth, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btnLogIn.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
            v.startAnimation(scale);
            if(etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
              //  Toast.makeText(getContext(), R.string.text_error_empty_email, Toast.LENGTH_SHORT).show();
            } else {
                if (validEmail(etEmail.getText().toString().trim())) {
                    ApiService promoMinerApi = Servicey.getPromoMinerApi();
                    Call<Users> call = promoMinerApi.getUser(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                            //Если код от сервера в диапазоне от 200-300
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                //Проверка на существование пользователя на сервере
                                if(response.body().getSuccess() == 1){
                                    User user = response.body().getUsers().get(0);
                                    updateUserInBD(user, etPassword.getText().toString().trim());
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    requireActivity().finish();
                                } else {
                             //       Toast.makeText(getContext(), R.string.text_not_found_user, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                            //    Toast.makeText(getContext(), R.string.text_error_auth, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                       //     Toast.makeText(getContext(), R.string.text_error_auth, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return view;
    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(!(pattern.matcher(email).matches())){
            return false;
        } else {
            return true;
        }
    }

    //Функция обновления данных о Пользователе
    private void updateUserInBD(User user, String password){
        BitcoinMiningDB db = App.getInstance().getDatabase();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> handler.post(() -> db.userDAO().insert(new UserEntity(user, password))));
    }

    public void setCallbackFragment(CallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }

    private void init(View view) {
        btnLogIn = view.findViewById(R.id.btn_log_in);
        etEmail = view.findViewById(R.id.emailLoginEditText);
        etPassword = view.findViewById(R.id.passwordEditText);
        tvSignUp = view.findViewById(R.id.tv_sign_up);
        tvSignInGoogle = view.findViewById(R.id.tv_sing_in_google);
    }
}