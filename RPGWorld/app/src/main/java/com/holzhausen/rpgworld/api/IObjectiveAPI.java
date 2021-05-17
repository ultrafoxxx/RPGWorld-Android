package com.holzhausen.rpgworld.api;

import com.holzhausen.rpgworld.model.Dialog;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IObjectiveAPI {

    String BASIC_URL = "http://192.168.1.36:8000/api/";

    @GET("dialogs/{objectiveId}/get_objective_dialogs/")
    Observable<List<Dialog>> getDialogsForObjective(@Path("objectiveId") int objectiveId);

}
