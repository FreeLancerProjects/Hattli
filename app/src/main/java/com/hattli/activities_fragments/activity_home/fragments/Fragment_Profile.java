package com.hattli.activities_fragments.activity_home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hattli.R;
import com.hattli.activities_fragments.activity_home.HomeActivity;
import com.hattli.databinding.FragmentProfileBinding;
import com.hattli.models.UserModel;
import com.hattli.preferences.Preferences;

import io.paperdb.Paper;

public class Fragment_Profile extends Fragment {
    private HomeActivity activity;
    private FragmentProfileBinding binding;
    private String lang;

    private Preferences preferences;
    private UserModel userModel;

    public static Fragment_Profile newInstance() {
        return new Fragment_Profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
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
        binding.setLang(lang);
        /*
        binding.setUserModel(userModel);
        binding.setLang(lang);
        if (userModel.getUser().getLogo() != null && !userModel.getUser().getLogo().isEmpty() && !userModel.getUser().getLogo().equals("0")) {
            Picasso.get().load(Uri.parse(Tags.IMAGE_URL + userModel.getUser().getLogo())).fit().into(binding.image);

        }
        binding.btnUpdateProfile.setOnClickListener(v -> {
            activity.displayFragmentUpdateProfile();
        });*/

    }


}
