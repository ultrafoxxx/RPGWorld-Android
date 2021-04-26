package com.holzhausen.rpgworld.api;

import com.holzhausen.rpgworld.model.BasicTraitInfo;
import com.holzhausen.rpgworld.model.Skill;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ICharacterCreationAPI {

    final String BASIC_URL = "https://rpgworld.herokuapp.com/api/";

    @GET(BASIC_URL + "genders/")
    Observable<List<BasicTraitInfo>> getGenders();

    @GET(BASIC_URL + "backgrounds/")
    Observable<List<BasicTraitInfo>> getBackgrounds();

    @GET(BASIC_URL + "classes/")
    Observable<List<BasicTraitInfo>> getHeroClasses();

    @GET(BASIC_URL + "perks/")
    Observable<List<BasicTraitInfo>> getPerks();

    @GET(BASIC_URL + "skills/")
    Observable<List<Skill>> getSkills();


}
