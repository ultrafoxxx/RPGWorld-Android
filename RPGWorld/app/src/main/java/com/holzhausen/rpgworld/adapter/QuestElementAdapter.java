package com.holzhausen.rpgworld.adapter;

import android.content.Context;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.api.ApiCaller;
import com.holzhausen.rpgworld.api.IGameAPI;
import com.holzhausen.rpgworld.model.Objective;
import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.PlayerQuest;
import com.holzhausen.rpgworld.model.Quest;
import com.holzhausen.rpgworld.viewmodel.GameViewModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;

public class QuestElementAdapter extends RecyclerView.Adapter<QuestElementAdapter.ViewHolder> {

    private List<Quest> quests;

    private final CompositeDisposable compositeDisposable;

    private final Context context;

    private final GameViewModel viewModel;

    private Player player;


    public QuestElementAdapter(Context context, GameViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = viewModel.getQuestListFlowable().subscribe(quests -> {
            this.quests = quests;
            notifyDataSetChanged();
        });
        compositeDisposable.add(disposable);
        disposable = viewModel
                .getPlayerFlowable()
                .subscribe(player -> {
                    this.player = player;
                });
        compositeDisposable.add(disposable);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quest_element, parent, false);
        return new QuestElementAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getQuestName().setText(quests.get(position).getName());
        String xpLabel = quests.get(position).getExperienceReward() + "XP";
        holder.getXpNumber().setText(xpLabel);
        Objective currentObjective = quests
                .get(position)
                .getObjectives()
                .stream()
                .filter(objective -> objective.getId() == quests.get(position).getActiveObjective())
                .findFirst()
                .orElse(new Objective());
        Disposable disposable = viewModel
                .getCurrentLocationFlowable()
                .subscribe(latLng -> setDistance(holder, latLng, currentObjective));
        compositeDisposable.add(disposable);
        String minLevelLabel = "Suggested level: " + quests.get(position).getMinLevel();
        holder.getMinLevel().setText(minLevelLabel);
        holder.getQuestDescription().setText(quests.get(position).getDescription());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            holder.getQuestDescription().setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        holder.getObjectiveName().setText(currentObjective.getName());
        if(!quests.get(position).isActive()){
            holder.getAcceptButton().setVisibility(View.VISIBLE);
            holder.getAcceptButton()
                    .setOnClickListener(view -> onAccepted(view, quests.get(position)));
        }
    }

    private void onAccepted(View view, Quest quest) {
        view.setEnabled(false);
        Retrofit retrofit = ApiCaller.getRetrofitClient();
        IGameAPI api = retrofit.create(IGameAPI.class);
        ApiCaller<Object> apiCaller = new ApiCaller<>();
        PlayerQuest playerQuest = new PlayerQuest();
        playerQuest.setPlayerIdentifier(player.getIdentifier());
        playerQuest.setQuestId(quest.getQuestId());
        Disposable disposable = apiCaller.getObjectFromApi(api.acceptQuest(playerQuest),
                response -> onResponse(view, quest), error -> onError(error, view));
        compositeDisposable.add(disposable);
    }

    private void onResponse(View view, Quest quest) {
        view.setVisibility(View.GONE);
        viewModel.setQuest(quest);
    }

    private void onError(Throwable throwable, View view) {
        view.setEnabled(true);
        throwable.printStackTrace();
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
    }


    private void setDistance(ViewHolder holder, LatLng latLng, Objective currentObjective) {

        LatLng objectiveLocation = new LatLng(currentObjective.getLatitude(), currentObjective.getLongitude());
        double distance = SphericalUtil.computeDistanceBetween(latLng, objectiveLocation);
        String distanceLabel = "";
        if(distance >= 1000){
            distance /= 100;
            distance = Math.round(distance) / 10.0;
            distanceLabel = distance + " km";
        }
        else {
            long resultDistance = Math.round(distance);
            distanceLabel = resultDistance + " m";
        }
        holder.getDistance().setText(distanceLabel);
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        compositeDisposable.dispose();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView questName;

        private final TextView xpNumber;

        private final TextView distance;

        private final TextView minLevel;

        private final Button acceptButton;

        private final TextView objectiveName;

        private final TextView questDescription;

        private final ConstraintLayout detailsView;

        private boolean isExpanded;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questName = itemView.findViewById(R.id.quest_name);
            questName.setOnClickListener(this);
            xpNumber = itemView.findViewById(R.id.xp_number);
            distance = itemView.findViewById(R.id.distance);
            minLevel = itemView.findViewById(R.id.suggested_level);
            acceptButton = itemView.findViewById(R.id.accept_quest_button);
            objectiveName = itemView.findViewById(R.id.current_objective);
            questDescription = itemView.findViewById(R.id.quest_description);
            detailsView = itemView.findViewById(R.id.details_view);

        }

        @Override
        public void onClick(View v) {
            if(v.getId() == questName.getId()){
                if(isExpanded){
                    detailsView.setVisibility(View.GONE);
                    isExpanded = false;
                }
                else {
                    detailsView.setVisibility(View.VISIBLE);
                    isExpanded = true;
                }
            }
        }

        public TextView getQuestName() {
            return questName;
        }

        public TextView getXpNumber() {
            return xpNumber;
        }

        public TextView getDistance() {
            return distance;
        }

        public TextView getMinLevel() {
            return minLevel;
        }

        public Button getAcceptButton() {
            return acceptButton;
        }

        public TextView getObjectiveName() {
            return objectiveName;
        }

        public TextView getQuestDescription() {
            return questDescription;
        }

        public ConstraintLayout getDetailsView() {
            return detailsView;
        }
    }

}
