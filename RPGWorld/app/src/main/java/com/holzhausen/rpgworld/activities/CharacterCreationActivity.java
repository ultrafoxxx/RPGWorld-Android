package com.holzhausen.rpgworld.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.api.ApiCaller;
import com.holzhausen.rpgworld.api.ICharacterCreationAPI;
import com.holzhausen.rpgworld.fragments.BasicInfoFragment;
import com.holzhausen.rpgworld.fragments.LoadingFragment;
import com.holzhausen.rpgworld.model.BasicTraitInfo;
import com.holzhausen.rpgworld.model.Skill;
import com.holzhausen.rpgworld.viewmodel.CharacterCreationViewModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.holzhausen.rpgworld.api.ICharacterCreationAPI.BASIC_URL;

public class CharacterCreationActivity extends AppCompatActivity {

    private List<BasicTraitInfo> genders;

    private List<BasicTraitInfo> backgrounds;

    private List<BasicTraitInfo> heroClasses;

    private List<Skill> skills;

    private ICharacterCreationAPI api;

    private CompositeDisposable compositeDisposable;

    private CharacterCreationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_creation);

        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, LoadingFragment.class, null)
                    .commit();
        }

        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        final Gson gson = new GsonBuilder().setLenient().create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASIC_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(ICharacterCreationAPI.class);

        compositeDisposable = new CompositeDisposable();
        viewModel = new ViewModelProvider(this).get(CharacterCreationViewModel.class);

        callApis();


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
    }

    private void onGetBackgrounds(List<BasicTraitInfo> backgrounds) {
        this.backgrounds = backgrounds;
    }

    private void onGetClasses(List<BasicTraitInfo> heroClasses) {
        this.heroClasses = heroClasses;
    }


    private void onGetGenders(List<BasicTraitInfo> genders) {
        this.genders = genders;
        viewModel.setBasicTraitInfoSubject(this.genders);
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