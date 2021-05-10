package com.holzhausen.rpgworld.api;

import com.holzhausen.rpgworld.model.Player;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IMainMenuAPI {

    String BASIC_URL = "http://192.168.1.36:8000/api/";

    @GET(BASIC_URL + "player/{playerId}/")
    Observable<Player> getPlayer(@Path("playerId") String playerId);

}
