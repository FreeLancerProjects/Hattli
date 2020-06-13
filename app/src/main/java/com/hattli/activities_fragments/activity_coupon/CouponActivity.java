package com.hattli.activities_fragments.activity_coupon;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.hattli.R;
import com.hattli.databinding.ActivityAboutAppBinding;
import com.hattli.databinding.ActivityCouponBinding;
import com.hattli.interfaces.Listeners;
import com.hattli.language.Language;

import java.util.Locale;

import io.paperdb.Paper;

public class CouponActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityCouponBinding binding;
    private String lang;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coupon);
        initView();
    }




    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);


    }



    @Override
    public void back() {
        finish();
    }

}
