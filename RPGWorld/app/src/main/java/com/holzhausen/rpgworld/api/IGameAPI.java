package com.holzhausen.rpgworld.api;

import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.PlayerQuest;
import com.holzhausen.rpgworld.model.Quest;
import com.holzhausen.rpgworld.model.UpdatePlayerSkill;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IGameAPI {

    String BASIC_URL = "http://192.168.1.36:8000/api/";

    @GET(BASIC_URL + "quests/{identifier}/player_quests/")
    Observable<List<Quest>> getQuestForPlayer(@Path("identifier") String identifier);

    @POST(BASIC_URL + "playerquests/accept_quest/")
    Observable<Object> acceptQuest(@Body PlayerQuest playerQuest);

    @POST(BASIC_URL + "playerskills/update_skills/")
    Observable<Player> assignSkills(@Body UpdatePlayerSkill playerSkill);

}
