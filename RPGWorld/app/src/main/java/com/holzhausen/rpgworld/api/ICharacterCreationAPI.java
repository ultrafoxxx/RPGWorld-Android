package com.holzhausen.rpgworld.api;

import com.holzhausen.rpgworld.model.BasicTraitInfo;
import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.SavePlayer;
import com.holzhausen.rpgworld.model.Skill;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ICharacterCreationAPI {

    // String BASIC_URL = "https://rpgworld.herokuapp.com/api/";

    String BASIC_URL = "http://192.168.1.36:8000/api/";

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

    @POST(BASIC_URL + "player/{playerId}/create_player/")
    Observable<Player> savePlayer(@Path("playerId") String playerId, @Body SavePlayer savePlayer);


}
