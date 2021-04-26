package com.holzhausen.rpgworld.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.model.BasicTraitInfo;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

public class BasicInfoElementAdapter extends RecyclerView.Adapter<BasicInfoElementAdapter.ViewHolder>{

    private List<BasicTraitInfo> basicTraitInfos;

    private final Disposable disposable;

    private final Context context;

    public BasicInfoElementAdapter(Flowable<List<BasicTraitInfo>> flowable, Context context){
        this.basicTraitInfos = new LinkedList<>();
        disposable = flowable.subscribe(items -> {
            basicTraitInfos = items;
            notifyDataSetChanged();
        });
        this.context = context;

    }


    @NonNull
    @Override
    public BasicInfoElementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasicInfoElementAdapter.ViewHolder holder, int position) {
        String drawableUriPrefix = context.getString(R.string.drawablePrefix);
        holder.getInfoName().setText(basicTraitInfos.get(position).getName());
        holder.getImageView()
                .setImageResource(context
                        .getResources()
                        .getIdentifier(drawableUriPrefix + basicTraitInfos
                                .get(position)
                                .getImageAssetName(), null, context.getPackageName()));
    }

    @Override
    public int getItemCount() {
        return basicTraitInfos.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        disposable.dispose();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView infoName;

        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            infoName = itemView.findViewById(R.id.info_name);
            imageView = itemView.findViewById(R.id.info_representation);
        }

        public TextView getInfoName() {
            return infoName;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

}
