package com.hattli.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.hattli.R;
import com.hattli.activities_fragments.activity_home.HomeActivity;
import com.hattli.activities_fragments.activity_store_details.StoreDetailsActivity;
import com.hattli.adapters.NearbyAdapter;
import com.hattli.adapters.QueryAdapter;
import com.hattli.databinding.FragmentMainBinding;
import com.hattli.models.NearbyModel;
import com.hattli.models.NearbyStoreDataModel;
import com.hattli.models.PhotosModel;
import com.hattli.models.PlaceModel;
import com.hattli.models.QuerySearchModel;
import com.hattli.models.UserModel;
import com.hattli.preferences.Preferences;
import com.hattli.remote.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Main extends Fragment {
    private HomeActivity activity;
    private FragmentMainBinding binding;
    private Preferences preferences;
    private String lang;
    private TimerTask timerTask;
    private Timer timer;
    private UserModel userModel;
    private Location location;
    private List<String> queriesList;
    private List<QuerySearchModel> en_ar_queriesList;
    private RecyclerView.LayoutManager manager,managerQueries;
    private List<PlaceModel> nearbyModelList;
    private NearbyAdapter nearbyAdapter;
    private QueryAdapter queryAdapter;

    public static Fragment_Main newInstance() {
        return new Fragment_Main();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();


    }

    private void initView() {
        nearbyModelList = new ArrayList<>();
        queriesList = new ArrayList<>();
        en_ar_queriesList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");

        binding.progBarStore.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarFetchLocation.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        addQueriesData();

        manager = new LinearLayoutManager(activity);
        managerQueries = new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false);
        queryAdapter = new QueryAdapter(en_ar_queriesList,activity,this);
        binding.recViewCategory.setAdapter(queryAdapter);


        binding.recViewCategory.setLayoutManager(managerQueries);
        binding.recViewStore.setLayoutManager(manager);




    }

    private void addQueriesData() {
        queriesList.add("restaurant");
        queriesList.add("bakery");
        queriesList.add("supermarket");
        queriesList.add("cafe");
        queriesList.add("store");
        queriesList.add("florist");

        en_ar_queriesList.add(new QuerySearchModel(getString(R.string.restaurant),R.drawable.ic_restaurant));
        en_ar_queriesList.add(new QuerySearchModel(getString(R.string.bakery),R.drawable.ic_sweet));
        en_ar_queriesList.add(new QuerySearchModel(getString(R.string.supermarket),R.drawable.ic_nav_store));
        en_ar_queriesList.add(new QuerySearchModel(getString(R.string.cafe),R.drawable.ic_cup));
        en_ar_queriesList.add(new QuerySearchModel(getString(R.string.store),R.drawable.ic_store));
        en_ar_queriesList.add(new QuerySearchModel(getString(R.string.florist),R.drawable.ic_gift));


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void setLocationData(Location location) {
        binding.llFetchLocation.setVisibility(View.GONE);
        binding.llContainer.setVisibility(View.VISIBLE);
        this.location = location;
        getStores(queriesList.get(0));
    }

    private void getStores(String query) {

        if (location!=null)
        {
            binding.tvNoData.setVisibility(View.GONE);
            binding.progBarStore.setVisibility(View.VISIBLE);
            nearbyModelList.clear();
            if (nearbyAdapter!=null){
                nearbyAdapter.notifyDataSetChanged();
            }
            String loc = location.getLatitude()+","+location.getLongitude();
            Log.e("loc",loc);
            Api.getService("https://maps.googleapis.com/maps/api/")
                    .getNearbyStores(loc,15000,query,lang,getString(R.string.map_api_key))
                    .enqueue(new Callback<NearbyStoreDataModel>() {
                        @Override
                        public void onResponse(Call<NearbyStoreDataModel> call, Response<NearbyStoreDataModel> response) {
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                binding.progBarStore.setVisibility(View.GONE);
                                if (response.body().getResults().size()>0)
                                {
                                    binding.tvNoData.setVisibility(View.GONE);
                                    updateUi(response.body(),location);
                                }else
                                {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            }else
                            {

                                binding.progBarStore.setVisibility(View.GONE);

                                try {
                                    Log.e("error_code",response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                        @Override
                        public void onFailure(Call<NearbyStoreDataModel> call, Throwable t) {
                            try {

                                Log.e("Error",t.getMessage());
                                binding.progBarStore.setVisibility(View.GONE);
                                Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                            }catch (Exception e)
                            {

                            }
                        }
                    });
        }

    }

    private void updateUi(NearbyStoreDataModel nearbyStoreDataModel, Location location) {
        nearbyModelList.clear();



        nearbyModelList.addAll(getPlaceModelFromResult(nearbyStoreDataModel.getResults()));

        if (nearbyAdapter==null){
            nearbyAdapter = new NearbyAdapter(nearbyModelList,activity,this,location.getLatitude(),location.getLongitude());
            binding.recViewStore.setAdapter(nearbyAdapter);
        }else {
            nearbyAdapter.notifyDataSetChanged();
        }


    }

    private List<PlaceModel> getPlaceModelFromResult(List<NearbyModel> nearbyModelList)
    {
        List<PlaceModel> returnedList = new ArrayList<>();
        for (NearbyModel nearbyModel : nearbyModelList)
        {
            PlaceModel placeModel;

            double distance = SphericalUtil.computeDistanceBetween(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(nearbyModel.getGeometry().getLocation().getLat(),nearbyModel.getGeometry().getLocation().getLng()));
            String dist = String.format(Locale.ENGLISH, "%.2f", (distance / 1000));


            if (nearbyModel.getPhotos()!=null)
            {
                placeModel = new PlaceModel(nearbyModel.getId(),nearbyModel.getPlace_id(),nearbyModel.getName(),nearbyModel.getIcon(),nearbyModel.getPhotos(),nearbyModel.getRating(),nearbyModel.getGeometry().getLocation().getLat(),nearbyModel.getGeometry().getLocation().getLng(),nearbyModel.getVicinity());
                placeModel.setDistance(dist);
            }else
            {
                placeModel = new PlaceModel(nearbyModel.getId(),nearbyModel.getPlace_id(),nearbyModel.getName(),nearbyModel.getIcon(),new ArrayList<PhotosModel>(),nearbyModel.getRating(),nearbyModel.getGeometry().getLocation().getLat(),nearbyModel.getGeometry().getLocation().getLng(),nearbyModel.getVicinity());
                placeModel.setDistance(dist);

            }



            if (nearbyModel.getOpening_hours()!=null)
            {
                placeModel.setOpenNow(nearbyModel.getOpening_hours().isOpen_now());

            }else
            {
                placeModel.setOpenNow(false);


            }
            returnedList.add(placeModel);
        }


        if (returnedList.size()>0){
            Collections.sort(returnedList, (o1, o2) -> {
                if (Double.parseDouble(o1.getDistance())<Double.parseDouble(o2.getDistance())){
                    return -1;
                }else if (Double.parseDouble(o1.getDistance())>Double.parseDouble(o2.getDistance())){
                    return 1;
                }else {
                    return 0;
                }
            });
        }


        return returnedList;
    }

    public void setQueryItemData(int adapterPosition) {
        getStores(queriesList.get(adapterPosition));
    }

    public void setPlaceData(PlaceModel placeModel) {
        Intent intent = new Intent(activity, StoreDetailsActivity.class);
        intent.putExtra("data",placeModel);
        intent.putExtra("lat",location.getLatitude());
        intent.putExtra("lng",location.getLongitude());
        startActivity(intent);
    }
}
