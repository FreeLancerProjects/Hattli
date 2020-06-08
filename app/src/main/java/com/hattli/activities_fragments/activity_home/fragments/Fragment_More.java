package com.hattli.activities_fragments.activity_home.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hattli.R;
import com.hattli.activities_fragments.activity_about_app.AboutAppActivity;
import com.hattli.activities_fragments.activity_home.HomeActivity;
import com.hattli.databinding.FragmentMoreBinding;
import com.hattli.interfaces.Listeners;
import com.hattli.models.UserModel;
import com.hattli.preferences.Preferences;

import io.paperdb.BuildConfig;
import io.paperdb.Paper;

public class Fragment_More extends Fragment implements Listeners.SettingActions{

    private HomeActivity activity;
    private FragmentMoreBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;

    public static Fragment_More newInstance() {

        return new Fragment_More();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        if (lang.equals("ar"))
        {
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_primary);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_second);
            binding.btnAr.setTextColor(ContextCompat.getColor(activity,R.color.white));
            binding.btnEn.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimary));

        }else {
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_second);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_primary);
            binding.btnAr.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimary));
            binding.btnEn.setTextColor(ContextCompat.getColor(activity,R.color.white));
        }
        binding.setLang(lang);
        binding.setAction(this);
        binding.tvVersion.setText(String.format("%s%s","Version : ", BuildConfig.VERSION_NAME) );
    }



    @Override
    public void terms() {
        Intent intent = new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type",1);
        startActivity(intent);
    }

    @Override
    public void aboutApp() {
        Intent intent = new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type",2);
        startActivity(intent);
    }

    @Override
    public void contactUs() {

    }



    @Override
    public void logout() {
        activity.logout();
    }

    @Override
    public void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+activity.getPackageName())));
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+activity.getPackageName())));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(activity, "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void arLang() {

        if (!lang.equals("ar"))
        {
            activity.refreshActivity("ar");

            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_primary);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_second);
            binding.btnAr.setTextColor(ContextCompat.getColor(activity,R.color.white));
            binding.btnEn.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        }
    }

    @Override
    public void enLang() {
        if (!lang.equals("en"))
        {
            activity.refreshActivity("en");
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_second);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_primary);
            binding.btnAr.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimary));
            binding.btnEn.setTextColor(ContextCompat.getColor(activity,R.color.white));
        }
    }


}
