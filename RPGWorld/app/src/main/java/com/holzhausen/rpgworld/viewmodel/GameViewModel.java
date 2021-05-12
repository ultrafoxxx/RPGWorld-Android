package com.holzhausen.rpgworld.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.Quest;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class GameViewModel extends ViewModel {

    private BehaviorSubject<List<Quest>> questListSubject;

    private PublishSubject<LatLng> currentLocationSubject;

    private BehaviorSubject<Player> playerSubject;

    private PublishSubject<Quest> questSubject;

    public void setQuestListSubject(List<Quest> quests) {
        this.questListSubject = BehaviorSubject.createDefault(quests);
    }

    public void setQuestList(List<Quest> quests) {
        questListSubject.onNext(quests);
    }

    public Flowable<List<Quest>> getQuestListFlowable(){
        return questListSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void createDistanceSubject() {
        currentLocationSubject = PublishSubject.create();
    }

    public void setCurrentLocation(LatLng currentLocation){
        currentLocationSubject.onNext(currentLocation);
    }

    public Flowable<LatLng> getCurrentLocationFlowable(){
        return currentLocationSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void setPlayerSubject(Player player) {
        playerSubject = BehaviorSubject.createDefault(player);
    }

    public void sendPlayer(Player player) {
        playerSubject.onNext(player);
    }

    public Flowable<Player> getPlayerFlowable() {
        return playerSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void createQuestSubject(){
        questSubject = PublishSubject.create();
    }

    public void setQuest(Quest quest){
        questSubject.onNext(quest);
    }

    public Flowable<Quest> getQuestFlowable(){
        return questSubject.toFlowable(BackpressureStrategy.BUFFER);
    }
}
