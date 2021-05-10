package com.holzhausen.rpgworld.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.api.ApiCaller;
import com.holzhausen.rpgworld.api.ICharacterCreationAPI;
import com.holzhausen.rpgworld.fragments.BasicInfoFragment;
import com.holzhausen.rpgworld.fragments.LoadingFragment;
import com.holzhausen.rpgworld.fragments.SkillFragment;
import com.holzhausen.rpgworld.model.BasicTraitInfo;
import com.holzhausen.rpgworld.model.CharacterCreationPhase;
import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.SavePlayer;
import com.holzhausen.rpgworld.model.Skill;
import com.holzhausen.rpgworld.model.SkillAmount;
import com.holzhausen.rpgworld.model.SkillFragmentInfo;
import com.holzhausen.rpgworld.viewmodel.CharacterCreationViewModel;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;

public class CharacterCreationActivity extends AppCompatActivity {

    private List<BasicTraitInfo> genders;

    private List<BasicTraitInfo> backgrounds;

    private List<BasicTraitInfo> heroClasses;

    private List<Skill> skills;

    private ICharacterCreationAPI api;

    private CompositeDisposable compositeDisposable;

    private CharacterCreationViewModel viewModel;

    private CharacterCreationPhase characterCreationPhase;
    
    private BasicTraitInfo chosenGender;

    private BasicTraitInfo chosenHeroClass;

    private BasicTraitInfo chosenBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_creation);

        viewModel = new ViewModelProvider(this).get(CharacterCreationViewModel.class);
        viewModel.setTitleSubject(getString(R.string.choose_gender_title));
        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, LoadingFragment.class, null)
                    .addToBackStack("basicTraitInfo")
                    .commit();
        }

        Retrofit retrofit = ApiCaller.getRetrofitClient();

        api = retrofit.create(ICharacterCreationAPI.class);

        compositeDisposable = new CompositeDisposable();

        characterCreationPhase = CharacterCreationPhase.GENDER;

        Disposable disposable = viewModel.getChosenTraitInfoFlowable().subscribe(this::onTraitChosen);
        compositeDisposable.add(disposable);

        callApis();


    }

    private void onTraitChosen(BasicTraitInfo basicTraitInfo) {

        switch (characterCreationPhase) {
            case GENDER:
                finishIfEmptyOrNull(heroClasses);
                viewModel.changeInfoSet(heroClasses);
                viewModel.changeTitle(getString(R.string.hero_class_title));
                chosenGender = basicTraitInfo;
                characterCreationPhase = CharacterCreationPhase.HERO_CLASS;
                break;
            case HERO_CLASS:
                finishIfEmptyOrNull(backgrounds);
                viewModel.changeInfoSet(backgrounds);
                viewModel.changeTitle(getString(R.string.background_title));
                chosenHeroClass = basicTraitInfo;
                characterCreationPhase = CharacterCreationPhase.BACKGROUND;
                break;
            case BACKGROUND:
                finishIfEmptyOrNull(skills);
                chosenBackground = basicTraitInfo;
                characterCreationPhase = CharacterCreationPhase.SKILLS;
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container, SkillFragment.class, null)
                        .commit();
        }


    }

    private void finishIfEmptyOrNull(List list){
        if(list == null || list.isEmpty()){
            Toast.makeText(this, getString(R.string.connection_info), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void callApis() {

        ApiCaller<BasicTraitInfo> basicTraitInfoApiCaller = new ApiCaller<>();
        ApiCaller<Skill> skillApiCaller = new ApiCaller<>();


        Disposable disposable = basicTraitInfoApiCaller
                .getListOfObjectsFromApi(api.getGenders(), this::onGetGenders, this::onError);
        compositeDisposable.add(disposable);
        disposable = basicTraitInfoApiCaller.getListOfObjectsFromApi(api.getHeroClasses(), this::onGetClasses,
                this::onError);
        compositeDisposable.add(disposable);
        disposable = basicTraitInfoApiCaller.getListOfObjectsFromApi(api.getBackgrounds(), this::onGetBackgrounds,
                this::onError);
        compositeDisposable.add(disposable);
        disposable = skillApiCaller.getListOfObjectsFromApi(api.getSkills(), this::onGetSkills,
                this::onError);
        compositeDisposable.add(disposable);

    }

    private void onGetSkills(List<Skill> skills) {
        this.skills = skills;
        SkillFragmentInfo skillFragmentInfo = new SkillFragmentInfo("", skills);
        viewModel.setSkillListSubject(skillFragmentInfo);
        Disposable disposable = viewModel.getSkillListFlowable().subscribe(chosenSkills ->{
                this.skills = chosenSkills.getSkills();
                if(isNotNull(chosenBackground, chosenGender, chosenHeroClass, chosenSkills.getSkills())){
                    saveCharacter(chosenSkills);
                }
        });
        compositeDisposable.add(disposable);
    }

    private void saveCharacter(SkillFragmentInfo skillFragmentInfo) {
        SavePlayer savePlayer = new SavePlayer();
        savePlayer.setName(skillFragmentInfo.getName());
        savePlayer.setBackgroundName(chosenBackground.getName());
        savePlayer.setClassName(chosenHeroClass.getName());
        savePlayer.setGenderName(chosenGender.getName());
        savePlayer.setSkills(skills.stream().map(skill -> {
            SkillAmount skillAmount = new SkillAmount();
            skillAmount.setSkillAmount(skill.getSkillAmount());
            skillAmount.setSkillName(skill.getName());
            return skillAmount;
        }).collect(Collectors.toList()));
        ApiCaller<Player> apiCaller = new ApiCaller<>();
        Disposable disposable = apiCaller
                .getObjectFromApi(api.savePlayer("0", savePlayer),
                        this::onGetIdentifier, this::onError);
        compositeDisposable.add(disposable);
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, LoadingFragment.class, null)
                .commit();
    }

    private void onGetIdentifier(Player player) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.contains(getString(R.string.player_identifier))){
            sharedPreferences.edit().putString(getString(R.string.player_identifier), player.getIdentifier()).apply();
        }
        Intent intent = new Intent(this, GPSActivity.class);
        intent.putExtra("player", player);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private boolean isNotNull(Object... objects){
        for (Object object : objects){
            if(object == null) {
                return false;
            }
        }
        return true;
    }

    private void onGetBackgrounds(List<BasicTraitInfo> backgrounds) {
        this.backgrounds = backgrounds;
    }

    private void onGetClasses(List<BasicTraitInfo> heroClasses) {
        this.heroClasses = heroClasses;
    }


    private void onGetGenders(List<BasicTraitInfo> genders) {
        this.genders = genders;
        viewModel.setBasicTraitInfoListSubject(this.genders);
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, BasicInfoFragment.class, null)
                .commit();

    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(this, getString(R.string.error_info), Toast.LENGTH_SHORT).show();
        finish();
    }




}