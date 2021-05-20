package com.holzhausen.rpgworld.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.adapter.SkillElementAdapter;
import com.holzhausen.rpgworld.api.ApiCaller;
import com.holzhausen.rpgworld.api.IGameAPI;
import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.Skill;
import com.holzhausen.rpgworld.model.SkillAmount;
import com.holzhausen.rpgworld.model.SkillFragmentInfo;
import com.holzhausen.rpgworld.model.UpdatePlayerSkill;
import com.holzhausen.rpgworld.viewmodel.GameViewModel;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;


public class PlayerInfoFragment extends Fragment {

    private TextView xpAmount;

    private TextView level;

    private TextView playerName;

    private TextView skillsToAllocate;

    private ProgressBar skillProgressBar;

    private Button allocateSkillsButton;

    private RecyclerView skillsRecyclerView;

    private GameViewModel viewModel;

    private Player player;

    private CompositeDisposable compositeDisposable;

    private SkillElementAdapter adapter;

    private boolean wasDataSet;

    private final static int NEXT_LEVEL_XP_PROGRESSION = 5;


    public PlayerInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_info, container, false);
        assignViews(view);
        compositeDisposable = new CompositeDisposable();
        viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        Disposable disposable = viewModel.getPlayerFlowable().subscribe(player -> {
            this.player = player;
            if(!wasDataSet){
                setViewData();
            }
        });
        compositeDisposable.add(disposable);
        return view;
    }

    private boolean updateRemainingSkill(int lastProgress, int newProgress){
        int difference = newProgress - lastProgress;
        if(player.getSkillPoints() < difference) {
            return false;
        }
        player.setSkillPoints(player.getSkillPoints() - difference);
        skillsToAllocate.setText(String.valueOf(player.getSkillPoints()));
        allocateSkillsButton.setEnabled(true);
        return true;
    }

    private void setViewData() {
        setXpTextAndProgressbar();
        String levelLabel = "Level: " + player.getLevel();
        level.setText(levelLabel);
        playerName.setText(player.getName());
        skillsToAllocate.setText(String.valueOf(player.getSkillPoints()));
        SkillFragmentInfo fragmentInfo = new SkillFragmentInfo("", player.getSkillAmountList()
                .stream()
                .map(skillAmount -> {
                    Skill skill = new Skill();
                    skill.setSkillAmount(skillAmount.getSkillAmount());
                    skill.setName(skillAmount.getSkillName());
                    return skill;
                }).collect(Collectors.toList()));
        viewModel.setSkillListSubject(fragmentInfo);
        adapter = new SkillElementAdapter(getContext(), viewModel,
                this::updateRemainingSkill);
        skillsRecyclerView.setAdapter(adapter);
        skillsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allocateSkillsButton.setOnClickListener(this::onAssignSkills);
        allocateSkillsButton.setEnabled(false);
        wasDataSet = true;
    }

    private void onAssignSkills(View view) {
        Retrofit retrofit = ApiCaller.getRetrofitClient();
        IGameAPI api = retrofit.create(IGameAPI.class);
        ApiCaller<Player> apiCaller = new ApiCaller<>();
        List<SkillAmount> playerSkillList = adapter.getSkills().stream().map(skill -> {
            SkillAmount skillAmount = new SkillAmount();
            skillAmount.setSkillName(skill.getName());
            skillAmount.setSkillAmount(skill.getSkillAmount());
            return skillAmount;
        }).collect(Collectors.toList());
        UpdatePlayerSkill updatePlayerSkill = new UpdatePlayerSkill();
        updatePlayerSkill.setPlayerIdentifier(player.getIdentifier());
        updatePlayerSkill.setRemainingSkills(player.getSkillPoints());
        updatePlayerSkill.setSkillAmounts(playerSkillList);
        Disposable disposable = apiCaller.getObjectFromApi(api.assignSkills(updatePlayerSkill),
                this::onUpdateSkills, this::onError);
        compositeDisposable.add(disposable);
    }

    private void onUpdateSkills(Player player) {
        allocateSkillsButton.setEnabled(false);
        viewModel.sendPlayer(player);
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    private void setXpTextAndProgressbar() {
        int currentXp = player.getExperiencePoints();
        int xpToNextLevel = 500 +
                ((player.getLevel() / NEXT_LEVEL_XP_PROGRESSION) + 1) * 500;
        String xpLabel = currentXp + "/" + xpToNextLevel + "XP";
        xpAmount.setText(xpLabel);
        skillProgressBar.setMax(xpToNextLevel);
        skillProgressBar.setProgress(currentXp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void assignViews(View view) {

        xpAmount = view.findViewById(R.id.xp_amount_info);
        level = view.findViewById(R.id.level_info);
        playerName = view.findViewById(R.id.player_name_info);
        skillsToAllocate = view.findViewById(R.id.skills_amount_info);
        skillProgressBar = view.findViewById(R.id.skills_progress);
        allocateSkillsButton = view.findViewById(R.id.assign_skills_button);
        skillsRecyclerView = view.findViewById(R.id.skillInfoRecyclerView);

    }
}