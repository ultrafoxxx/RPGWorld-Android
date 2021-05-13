package com.holzhausen.rpgworld.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.model.Skill;
import com.holzhausen.rpgworld.util.SkillAdapterHelper;
import com.holzhausen.rpgworld.viewmodel.CharacterCreationViewModel;
import com.holzhausen.rpgworld.viewmodel.CommonViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class SkillElementAdapter extends RecyclerView.Adapter<SkillElementAdapter.ViewHolder>{

    private List<Skill> skills;

    private final Disposable disposable;

    private final Context context;

    private final SkillAdapterHelper helper;

    public SkillElementAdapter(Context context, CommonViewModel viewModel, SkillAdapterHelper helper) {
        this.context = context;
        this.helper = helper;
        disposable = viewModel.getSkillListFlowable().subscribe(skills -> {
            this.skills = skills.getSkills();
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.skill_element, parent, false);
        return new SkillElementAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getSkillName().setText(skills.get(position).getName());
        holder.getSkillAmount().setText(String.valueOf(skills.get(position).getSkillAmount()));
        holder.getSeekBar().setProgress(skills.get(position).getSkillAmount());
        holder.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int oldProgress = skills.get(position).getSkillAmount();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(helper.updateRemainingSkill(oldProgress, progress)){
                    holder.getSkillAmount().setText(String.valueOf(progress));
                    oldProgress = progress;
                    skills.get(position).setSkillAmount(progress);
                }
                else {
                    seekBar.setProgress(oldProgress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        disposable.dispose();
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView skillName;

        private final SeekBar seekBar;

        private final TextView skillAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            skillName = itemView.findViewById(R.id.skill_name);
            seekBar = itemView.findViewById(R.id.seekBar);
            skillAmount = itemView.findViewById(R.id.skill_amount_element);
        }

        public TextView getSkillName() {
            return skillName;
        }

        public SeekBar getSeekBar() {
            return seekBar;
        }

        public TextView getSkillAmount() {
            return skillAmount;
        }
    }
}
