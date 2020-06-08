package com.hattli.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hattli.R;
import com.hattli.activities_fragments.activity_home.fragments.Fragment_Main;
import com.hattli.databinding.NearbyRowBinding;
import com.hattli.models.PlaceModel;

import java.util.List;

import io.paperdb.Paper;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.MyHolder> {

    private List<PlaceModel> placeModelList;
    private Context context;
    private Fragment fragment;
    private double user_lat = 0.0, user_lng = 0.0;
    private LayoutInflater inflater;
    private String lang;

    public NearbyAdapter(List<PlaceModel> placeModelList, Context context, Fragment fragment, double user_lat, double user_lng) {
        this.placeModelList = placeModelList;
        this.context = context;
        this.fragment = fragment;
        this.user_lat = user_lat;
        this.user_lng = user_lng;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", "ar");
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        NearbyRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.nearby_row, parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        PlaceModel placeModel = placeModelList.get(position);
        holder.binding.setModel(placeModel);
        holder.binding.setLang(lang);


        holder.itemView.setOnClickListener(v -> {
            PlaceModel placeModel1 = placeModelList.get(holder.getAdapterPosition());
            if (fragment instanceof Fragment_Main){
                Fragment_Main fragment_main = (Fragment_Main) fragment;
                fragment_main.setPlaceData(placeModel1);
            }

        });
    }

    @Override
    public int getItemCount() {
        return placeModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        NearbyRowBinding binding;

        public MyHolder(NearbyRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }
}
