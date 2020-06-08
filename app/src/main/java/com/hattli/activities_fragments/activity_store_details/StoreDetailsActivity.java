package com.hattli.activities_fragments.activity_store_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.hattli.R;
import com.hattli.databinding.ActivityStoreDetailsBinding;
import com.hattli.interfaces.Listeners;
import com.hattli.language.Language;
import com.hattli.models.PlaceDetailsModel;
import com.hattli.models.PlaceModel;
import com.hattli.models.UserModel;
import com.hattli.remote.Api;
import com.hattli.share.Common;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreDetailsActivity extends AppCompatActivity implements Listeners.BackListener, OnMapReadyCallback {
    private ActivityStoreDetailsBinding binding;
    private String lang;
    private PlaceModel placeModel;
    private UserModel userModel;
    private PlaceDetailsModel.PlaceDetails placeDetails;
    private double userLat = 0.0, userLng = 0.0;
    private GoogleMap mMap;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        placeModel = (PlaceModel) intent.getSerializableExtra("data");
        userLat = intent.getDoubleExtra("lat", 0.0);
        userLng = intent.getDoubleExtra("lng", 0.0);

    }


    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(placeModel);

        binding.llOpenHour.setOnClickListener(v -> {
            if (binding.expandedLayout.isExpanded()) {
                binding.expandedLayout.collapse(true);

                binding.arrowDown.clearAnimation();
                binding.arrowDown.animate().setDuration(300).rotation(0).start();
            } else {
                binding.expandedLayout.expand(true);
                binding.arrowDown.clearAnimation();
                binding.arrowDown.animate().setDuration(300).rotation(180).start();

            }
        });
        setUpMap();
        getPlaceDetails(placeModel);
    }

    private void setUpMap() {
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment != null)
            fragment.getMapAsync(this);
    }


    @Override
    public void back() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            mMap.setTrafficEnabled(false);
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.maps));
            AddMarkers();
        }
    }

    private void AddMarkers() {

        View view = LayoutInflater.from(this).inflate(R.layout.map_you_icon,null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.map_shop_icon,null);

        IconGenerator iconGenerator= new IconGenerator(this);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(null);
        Marker marker1 = mMap.addMarker(new MarkerOptions().position(new LatLng(userLat, userLng)).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));

        iconGenerator.setContentView(view2);
        iconGenerator.setBackground(null);

        Marker marker2= mMap.addMarker(new MarkerOptions().position(new LatLng(placeModel.getLat(),placeModel.getLng())).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(),iconGenerator.getAnchorV()));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(),200);

        mMap.moveCamera(cameraUpdate);


    }

    private void getPlaceDetails(PlaceModel placeModel) {

        Log.e("place_id",placeModel.getPlace_id());
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        String fields = "opening_hours,photos,reviews";

        Api.getService("https://maps.googleapis.com/maps/api/")
                .getPlaceDetails(placeModel.getPlace_id(), fields, lang, getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceDetailsModel>() {
                    @Override
                    public void onResponse(Call<PlaceDetailsModel> call, Response<PlaceDetailsModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            dialog.dismiss();
                            updateHoursUI(response.body());
                        } else {
                            dialog.dismiss();


                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceDetailsModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            Log.e("Error", t.getMessage());
                            Toast.makeText(StoreDetailsActivity.this, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void updateHoursUI(PlaceDetailsModel body)
    {
        this.placeDetails = body.getResult();
        if (body.getResult().getOpening_hours() != null) {
            binding.llOpenHour.setVisibility(View.VISIBLE);
            if (body.getResult().getOpening_hours().getPeriods().size() == 7) {
                List<String> time = body.getResult().getOpening_hours().getWeekday_text();


                binding.tvD1.setText(time.get(0).split(":", 2)[0].trim());
                binding.tvD11.setText(time.get(0).split(":", 2)[1].trim());

                binding.tvD2.setText(time.get(1).split(":", 2)[0].trim());
                binding.tvD22.setText(time.get(1).split(":", 2)[1].trim());

                binding.tvD3.setText(time.get(2).split(":", 2)[0].trim());
                binding.tvD33.setText(time.get(2).split(":", 2)[1].trim());

                binding.tvD4.setText(time.get(3).split(":", 2)[0].trim());
                binding.tvD44.setText(time.get(3).split(":", 2)[1].trim());

                binding.tvD5.setText(time.get(4).split(":", 2)[0].trim());
                binding.tvD55.setText(time.get(4).split(":", 2)[1].trim());

                binding.tvD6.setText(time.get(5).split(":", 2)[0].trim());
                binding.tvD66.setText(time.get(5).split(":", 2)[1].trim());

                binding.tvD7.setText(time.get(6).split(":", 2)[0].trim());
                binding.tvD77.setText(time.get(6).split(":", 2)[1].trim());


            } else if (body.getResult().getOpening_hours().getPeriods().size() == 1) {
                List<String> time = body.getResult().getOpening_hours().getWeekday_text();


                binding.tvD1.setText(time.get(0).split(":")[0].trim());
                binding.tvD11.setText(R.string.all_day);

                binding.tvD2.setText(time.get(1).split(":")[0].trim());
                binding.tvD22.setText(R.string.all_day);

                binding.tvD3.setText(time.get(2).split(":")[0].trim());
                binding.tvD33.setText(R.string.all_day);

                binding.tvD4.setText(time.get(3).split(":")[0].trim());
                binding.tvD44.setText(R.string.all_day);

                binding.tvD5.setText(time.get(4).split(":")[0].trim());
                binding.tvD55.setText(R.string.all_day);

                binding.tvD6.setText(time.get(5).split(":")[0].trim());
                binding.tvD66.setText(R.string.all_day);

                binding.tvD7.setText(time.get(6).split(":")[0].trim());
                binding.tvD77.setText(R.string.all_day);

            }
        } else {
            binding.llOpenHour.setVisibility(View.GONE);
            //tv_today.setVisibility(View.GONE);
        }
    }




}
