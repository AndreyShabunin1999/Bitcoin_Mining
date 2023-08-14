package eradev.bitcoin.mining.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
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
import retrofit2.Retrofit;

public class FragmentRegister extends Fragment {

    Button btnSignUp;

    TextView tvSignWithGoogle, tvSingIn;

    EditText etEmail, etPassword, etConfirmPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //Инициализация элементов интерфейса
        init(view);

        //Обработка нажатия на textView "Sign In"
        tvSingIn.setOnClickListener(v -> requireActivity().onBackPressed());

        //Обработка нажатия на кнопку "Sign Up"
        btnSignUp.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
            v.startAnimation(scale);
            //Проверка на корректность полей графического интерфейса
            if(checkField()){
                ApiService promoMinerApi = Servicey.getPromoMinerApi();
                Call<StatusMessage> call = promoMinerApi.insertUser(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
                call.enqueue(new Callback<StatusMessage>() {
                    @Override
                    public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                        //Если код ответа от сервера в пределах 200-300
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            //Если регистрацмя прошла неудачно, то уведомить пользователя
                            if(response.body().getSuccess() == 0) {
                                //Toast.makeText(getContext(), R.string.text_error_registration, Toast.LENGTH_SHORT).show();
                            } else {
                                addUserBD(new UserEntity(etEmail.getText().toString().trim(), etPassword.getText().toString().trim()));
                              //  Toast.makeText(getContext(), R.string.text_success_registration, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), MainActivity.class));
                            }
                        } else {
                           // Toast.makeText(getContext(), R.string.text_error_registration, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {
                     //   Toast.makeText(getContext(), R.string.text_error_registration, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Обработка нажатия на текст "Sign Up With Google"
        tvSignWithGoogle.setOnClickListener(v -> {
            //если поле Email не заполнено
            if(etEmail.getText().toString().isEmpty()){
              //  Toast.makeText(getContext(), R.string.text_error_empty_email, Toast.LENGTH_SHORT).show();
            } else {
                if (validEmail(etEmail.getText().toString().trim())) {
                    ApiService promoMinerApi = Servicey.getPromoMinerApi();
                    Call<StatusMessage> call = promoMinerApi.insertUserOnlyEmail(etEmail.getText().toString().trim());
                    call.enqueue(new Callback<StatusMessage>() {
                        @Override
                        public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                            //Если код ответа от сервера в пределах 200-300
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                //Если регистрация прошла неудачно
                                if(response.body().getSuccess() == 0) {
                            //        Toast.makeText(getContext(), R.string.text_error_registration, Toast.LENGTH_SHORT).show();
                                } else {
                                    UserEntity user = new UserEntity(etEmail.getText().toString().trim(), "");
                                    addUserBD(user);
                                    updateUserDB(user);
                                }
                            } else {
                            //    Toast.makeText(getContext(), R.string.text_error_registration, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {
                        //    Toast.makeText(getContext(), R.string.text_error_registration, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return view;
    }

    public void updateUserDB(UserEntity user){
        ApiService apiService = Servicey.getPromoMinerApi();
        Call<Users> usersCall;
        //Способ регистрации пользователя (без password через gmail
        if(user.getPassword().isEmpty()){
            usersCall = apiService
                    .getUserGoogle(user.getEmail());
        } else {
            usersCall = apiService
                    .getUser(user.getEmail(), user.getPassword());
        }

        usersCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    //обновляем данные о пользователе в БД
                    updateUserDB(response.body().getUsers().get(0));
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {}
        });
    }

    private void updateUserDB(User user) {
        BitcoinMiningDB db = App.getInstance().getDatabase();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> handler.post(() -> db.userDAO().updateUser(0, user.getEmail(),
                user.getEntered_code(), user.getRef_code(), user.getRef_value(), user.getServer_time(),user.getValue(), user.getMining_is_started(), user.getTask(), user.getDaily(), user.getBoost())));
    }

    //Добавление пользователя в БД
    private void addUserBD(UserEntity userEntity){
        BitcoinMiningDB db = App.getInstance().getDatabase();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> handler.post(() -> db.userDAO().insert(userEntity)));
    }

    //Функция проверки Email
    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(!(pattern.matcher(email).matches())){
           // Toast.makeText(getContext(), R.string.text_error_email, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkField(){
        if(etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()
                || etConfirmPassword.getText().toString().isEmpty()){
         //   Toast.makeText(getContext(), R.string.text_empty_fields, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
               // Toast.makeText(getContext(), R.string.error_passwords, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return validEmail(etEmail.getText().toString().trim());
            }
        }
    }

    private void init(View view) {
        btnSignUp = view.findViewById(R.id.btn_sign_up);
        etEmail = view.findViewById(R.id.emailLoginEditTextReg);
        etPassword = view.findViewById(R.id.passwordEditTextReg);
        etConfirmPassword = view.findViewById(R.id.confirmPasswordEditTextReg);
        tvSingIn = view.findViewById(R.id.tv_sing_in);
        tvSignWithGoogle = view.findViewById(R.id.tv_sign_with_google);
    }
}