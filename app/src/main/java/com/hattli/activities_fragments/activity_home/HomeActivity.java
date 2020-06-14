package com.hattli.activities_fragments.activity_home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hattli.R;
import com.hattli.activities_fragments.activity_about_app.AboutAppActivity;
import com.hattli.activities_fragments.activity_cart.CartActivity;
import com.hattli.activities_fragments.activity_home.fragments.Fragment_Main;
import com.hattli.activities_fragments.activity_home.fragments.Fragment_More;
import com.hattli.activities_fragments.activity_home.fragments.Fragment_Profile;
import com.hattli.activities_fragments.activity_home.fragments.Fragment_Store;
import com.hattli.activities_fragments.activity_login.LoginActivity;
import com.hattli.activities_fragments.activity_notification.NotificationActivity;
import com.hattli.activities_fragments.activity_order.OrderActivity;
import com.hattli.databinding.ActivityHomeBinding;
import com.hattli.interfaces.Listeners;
import com.hattli.language.Language;
import com.hattli.models.NotFireModel;
import com.hattli.models.UserModel;
import com.hattli.preferences.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements Listeners.SettingActions,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, LocationListener {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private Fragment_Main fragment_main;
    private Fragment_Store fragment_store;
    private Fragment_Profile fragment_profile;
    private Fragment_More fragment_more;
    private UserModel userModel;
    private String lang;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String gps_perm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 22;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();


    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        setSelectedNavItem(0);
        binding.flSearch.setOnClickListener(view -> {

           /* Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent,100);*/

        });


        binding.flNotification.setOnClickListener(view -> {

            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
            /*if (userModel != null) {
                readNotificationCount();
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);

            } else {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            }*/

        });

        binding.flOrder.setOnClickListener(view -> {

            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);

            /*if (userModel != null) {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);

            } else {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            }*/

        });


        binding.flCart.setOnClickListener(view -> {

            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        binding.llHome.setOnClickListener(v -> setSelectedNavItem(0));
        binding.llStore.setOnClickListener(v -> setSelectedNavItem(1));
        binding.llProfile.setOnClickListener(v -> setSelectedNavItem(2));
        binding.llMore.setOnClickListener(v -> setSelectedNavItem(3));

        if (userModel != null) {
            EventBus.getDefault().register(this);
            getNotificationCount();
            updateTokenFireBase();

        }

        CheckPermission();


    }




    private void setSelectedNavItem(int pos) {

        switch (pos) {
            case 0:
                binding.llHome.setBackgroundResource(R.color.white);
                binding.llStore.setBackgroundResource(R.color.colorPrimary);
                binding.llProfile.setBackgroundResource(R.color.colorPrimary);
                binding.llMore.setBackgroundResource(R.color.colorPrimary);

                binding.iconHome.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary));
                binding.iconStore.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconProfile.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconMore.setColorFilter(ContextCompat.getColor(this,R.color.white));

                binding.tvHome.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                binding.tvStore.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvProfile.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvMore.setTextColor(ContextCompat.getColor(this,R.color.white));
                displayFragmentMain();
                break;
            case 1:


                binding.llHome.setBackgroundResource(R.color.colorPrimary);
                binding.llStore.setBackgroundResource(R.color.white);
                binding.llProfile.setBackgroundResource(R.color.colorPrimary);
                binding.llMore.setBackgroundResource(R.color.colorPrimary);

                binding.iconHome.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconStore.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary));
                binding.iconProfile.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconMore.setColorFilter(ContextCompat.getColor(this,R.color.white));

                binding.tvHome.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvStore.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                binding.tvProfile.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvMore.setTextColor(ContextCompat.getColor(this,R.color.white));
                displayFragmentStore();


                break;
            case 2:
                binding.llHome.setBackgroundResource(R.color.colorPrimary);
                binding.llStore.setBackgroundResource(R.color.colorPrimary);
                binding.llProfile.setBackgroundResource(R.color.white);
                binding.llMore.setBackgroundResource(R.color.colorPrimary);

                binding.iconHome.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconStore.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconProfile.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary));
                binding.iconMore.setColorFilter(ContextCompat.getColor(this,R.color.white));

                binding.tvHome.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvStore.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvProfile.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                binding.tvMore.setTextColor(ContextCompat.getColor(this,R.color.white));
                displayFragmentProfile();
                break;
            case 3:
                binding.llHome.setBackgroundResource(R.color.colorPrimary);
                binding.llStore.setBackgroundResource(R.color.colorPrimary);
                binding.llProfile.setBackgroundResource(R.color.colorPrimary);
                binding.llMore.setBackgroundResource(R.color.white);

                binding.iconHome.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconStore.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconProfile.setColorFilter(ContextCompat.getColor(this,R.color.white));
                binding.iconMore.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary));

                binding.tvHome.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvStore.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvProfile.setTextColor(ContextCompat.getColor(this,R.color.white));
                binding.tvMore.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                displayFragmentMore();
                break;

        }
    }


    private void getNotificationCount() {
      /*  Api.getService(Tags.base_url)
                .getUnreadNotificationCount(userModel.getUser().getToken())
                .enqueue(new Callback<NotificationCount>() {
                    @Override
                    public void onResponse(Call<NotificationCount> call, Response<NotificationCount> response) {
                        if (response.isSuccessful()) {
                            Log.e("count",response.body().getCount()+"__");
                            binding.setNotCount(response.body().getCount());
                        } else {
                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationCount> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });*/
    }

    private void readNotificationCount() {
       /* Api.getService(Tags.base_url)
                .readNotification(userModel.getUser().getToken())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            binding.setNotCount(0);
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });*/
    }


    public void displayFragmentMain() {
        try {
            if (fragment_main == null) {
                fragment_main = Fragment_Main.newInstance();
            }



            if (fragment_store != null && fragment_store.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_store).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }

            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_main.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_main).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_main, "fragment_main").addToBackStack("fragment_main").commit();

            }
            binding.setTitle(getString(R.string.home));
        } catch (Exception e) {
        }

    }

    public void displayFragmentStore() {
        try {


            fragment_store = Fragment_Store.newInstance();




            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }

            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }
            if (fragment_store.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_store).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_store, "fragment_store").addToBackStack("fragment_store").commit();

            }
            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            binding.setTitle(getString(R.string.stores));
        } catch (Exception e) {
        }

    }

    public void displayFragmentProfile() {

        try {
            if (fragment_profile == null) {
                fragment_profile = Fragment_Profile.newInstance();
            }

            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }

            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_store != null && fragment_store.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_store).commit();
            }

            if (fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_profile).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_profile, "fragment_profile").addToBackStack("fragment_profile").commit();

            }
            binding.setTitle(getString(R.string.profile));

        } catch (Exception e) {
        }
    }

    public void displayFragmentMore() {

        try {
            if (fragment_more == null) {
                fragment_more = Fragment_More.newInstance();
            }


            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_store != null && fragment_store.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_store).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }

            if (fragment_more.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_more).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_more, "fragment_more").addToBackStack("fragment_more").commit();

            }
            binding.setTitle(getString(R.string.profile));

        } catch (Exception e) {
        }
    }


    @Override
    public void terms() {
        Intent intent = new Intent(this, AboutAppActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    @Override
    public void aboutApp() {
        Intent intent = new Intent(this, AboutAppActivity.class);
        intent.putExtra("type", 2);
        startActivity(intent);
    }

    @Override
    public void contactUs() {
        /*Intent intent = new Intent(this, ContactUsActivity.class);
        startActivity(intent);*/

    }


    private void updateTokenFireBase() {

       /* FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                token = task.getResult().getToken();

                try {

                    Api.getService(Tags.base_url)
                            .updateToken(userModel.getUser().getToken(), token, "android")
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("token", "updated successfully");
                                    } else {
                                        try {

                                            Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {

                                        if (t.getMessage() != null) {
                                            Log.e("errorToken2", t.getMessage());
                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                            });
                } catch (Exception e) {


                }

            }
        });*/
    }

    @Override
    public void logout() {
        if (userModel != null) {


         /*   ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.show();


            FirebaseInstanceId.getInstance()
                    .getInstanceId().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    token = task.getResult().getToken();

                    Api.getService(Tags.base_url)
                            .logout("Bearer " + userModel.getUser().getToken(), token)
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    dialog.dismiss();
                                    if (response.isSuccessful()) {
                                        Log.e("dd", "ddd");
                                        preferences.clear(HomeActivity.this);
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        if (manager != null) {
                                            manager.cancel(Tags.not_tag, Tags.not_id);
                                        }
                                        navigateToSignInActivity();


                                    } else {
                                        dialog.dismiss();
                                        try {
                                            Log.e("error", response.code() + "__" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (response.code() == 500) {
                                            Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {
                                        dialog.dismiss();
                                        if (t.getMessage() != null) {
                                            Log.e("error", t.getMessage() + "__");

                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("Error", e.getMessage() + "__");
                                    }
                                }
                            });

                }
            });


        } else {
            navigateToSignInActivity();
        }*/

        }

    }

    @Override
    public void rateApp() {

    }

    @Override
    public void arLang() {

    }

    @Override
    public void enLang() {

    }

    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 1050);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNotifications(NotFireModel notFireModel) {
        if (userModel != null) {
            getNotificationCount();

        }
    }




    private void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    private void initGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void CheckPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, gps_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{gps_perm}, loc_req);
        } else {

            initGoogleApiClient();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode==1255&&resultCode==RESULT_OK){
            Log.e("tt","tt");
            startLocationUpdate();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == loc_req)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initGoogleApiClient();
            }else
            {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initLocationRequest()
    {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());

        result.setResultCallback(result1 -> {

            Status status = result1.getStatus();
            switch (status.getStatusCode())
            {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(HomeActivity.this,1255);
                    }catch (Exception e)
                    {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e("not available","not available");
                    break;
            }
        });

    }
    @SuppressLint("MissingPermission")
    private void startLocationUpdate()
    {
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
     initLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null){
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        listenToLocationUpdate(location);
        if (googleApiClient!=null){
            googleApiClient.disconnect();
        }
        if (locationCallback!=null){
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        }
    }

    private void listenToLocationUpdate(Location location) {
        if (fragment_main!=null&&fragment_main.isAdded()){
            fragment_main.setLocationData(location);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment_main != null && fragment_main.isAdded() && fragment_main.isVisible()) {
            finish();

        } else {
            setSelectedNavItem(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationCallback!=null)
        {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        }
        if (googleApiClient!=null)
        {
            googleApiClient.disconnect();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
