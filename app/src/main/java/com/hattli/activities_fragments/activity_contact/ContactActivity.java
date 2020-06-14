package com.hattli.activities_fragments.activity_contact;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hattli.R;
import com.hattli.databinding.ActivityContactBinding;
import com.hattli.interfaces.Listeners;
import com.hattli.language.Language;

import java.util.Locale;

import io.paperdb.Paper;

public class ContactActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityContactBinding binding;
    private String lang;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact);
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
