package eradev.bitcoin.mining.view;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;

public class ConclusionFragment extends Fragment {

    private ClipboardManager clipboardManager;

    SharedPreferences sharedPreferences;

    BitcoinMiningDB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conclusion, container, false);

        db = App.getInstance().getDatabase();
        sharedPreferences = requireContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);
        //инициализация глобальный переменных
        clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        Button btnCopy = view.findViewById(R.id.btn_copy_dep_code);
        ImageView imgShareMV = view.findViewById(R.id.img_share_mixer_wallet);
        TextView textValueUSTD = view.findViewById(R.id.text_balance_trans);
        EditText etDepozitCode = view.findViewById(R.id.et_deposit_code);
        etDepozitCode.setText(sharedPreferences.getString("promocode",""));

        //Обработка нажатия на иконку для скачивания Mixer Wallet
        imgShareMV.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.share_mixer_wallet)))));

        //Обработка нажатия на кнопку "Скопировать"
        btnCopy.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(requireActivity(), R.anim.scale);
            v.startAnimation(scale);
            copyDepCode();
        });

        //Отображение диалогового окна с депозитным кодом
        showDialog();

        Double valueUSTD = this.getArguments().getInt("balance", 0) / this.getArguments().getDouble("courseToUSTD", 0);
        textValueUSTD.setText(getResources().getString(R.string.text_value_ustd, valueUSTD));

        return view;
    }

    //Создание диалогового окна о невозможности вывести средства
    private void showDialog(){
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_depozit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnCopyDepCode = dialog.findViewById(R.id.btn_save_depozit_code);
        TextView textDepozitCode = dialog.findViewById(R.id.text_depozit_code);
        textDepozitCode.setText(sharedPreferences.getString("promocode",""));
        ImageView imgCross = dialog.findViewById(R.id.img_cross);
        //Обработка нажатия на кнопку "Сохранить"
        btnCopyDepCode.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
            v.startAnimation(scale);
            copyDepCode();
            dialog.cancel();
        });
        //Обработка нажатия на крестик (закрытие диалогового окна)
        imgCross.setOnClickListener(v -> dialog.cancel());
        dialog.show();
    }

    private void copyDepCode(){
        ClipData clipData = ClipData.newPlainText("promocode", sharedPreferences.getString("promocode", ""));
        clipboardManager.setPrimaryClip(clipData);
    }
}