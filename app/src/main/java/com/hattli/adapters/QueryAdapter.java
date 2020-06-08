package com.hattli.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hattli.R;
import com.hattli.activities_fragments.activity_home.fragments.Fragment_Main;
import com.hattli.databinding.QueryRowBinding;
import com.hattli.models.QuerySearchModel;

import java.util.List;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.MyHolder> {

    private List<QuerySearchModel> querySearchModelList;
    private Context context;
    private Fragment_Main fragment;
    private int selectedPos = 0;
    private LayoutInflater inflater;
    public QueryAdapter(List<QuerySearchModel> querySearchModelList, Context context, Fragment_Main fragment) {
        this.querySearchModelList = querySearchModelList;
        this.context = context;
        this.fragment = fragment;
        inflater = LayoutInflater.from(context);


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QueryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.query_row,parent,false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        QuerySearchModel querySearchModel = querySearchModelList.get(position);
        holder.binding.setModel(querySearchModel);


        if (selectedPos == position)
        {
            holder.binding.setIsSelected(true);
        }else
            {
                holder.binding.setIsSelected(false);
            }

        holder.itemView.setOnClickListener(v -> {
            selectedPos = holder.getAdapterPosition();
            fragment.setQueryItemData(holder.getAdapterPosition());
            notifyDataSetChanged();

        });
    }

    @Override
    public int getItemCount() {
        return querySearchModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private QueryRowBinding binding;
        public MyHolder(QueryRowBinding binding ) {
            super(binding.getRoot());
            this.binding =binding;


        }

    }
}
