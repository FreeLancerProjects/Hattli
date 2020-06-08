package com.hattli.activities_fragments.activity_language;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hattli.R;
import com.hattli.activities_fragments.activity_login.LoginActivity;
import com.hattli.databinding.ActivityLanguageBinding;
import com.hattli.language.Language;
import com.hattli.preferences.Preferences;

import io.paperdb.Paper;

public class LanguageActivity extends AppCompatActivity {
    private ActivityLanguageBinding binding;
    private String lang;
    private String selected_language="ar";
    private Preferences preferences;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = Preferences.getInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language);
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);





        binding.arrowLeft.setOnClickListener(v -> {
            selected_language ="ar";
            binding.tvLanguage.setText("العربية");
            binding.arrowLeft.setVisibility(View.INVISIBLE);
            binding.arrowRight.setVisibility(View.VISIBLE);

        });

        binding.arrowRight.setOnClickListener(v -> {

            selected_language ="en";
            binding.tvLanguage.setText("English");
            binding.arrowLeft.setVisibility(View.VISIBLE);
            binding.arrowRight.setVisibility(View.INVISIBLE);

        });

        binding.btnNext.setOnClickListener(v -> {
            changeLanguage(selected_language);

        });


    }

    private void changeLanguage(String selected_language) {
        Paper.book().write("lang", selected_language);
        Language.setNewLocale(this, selected_language);
        preferences.setIsLanguageSelected(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
