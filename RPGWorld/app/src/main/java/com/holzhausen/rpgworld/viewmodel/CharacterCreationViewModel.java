package com.holzhausen.rpgworld.viewmodel;

import androidx.lifecycle.ViewModel;

import com.holzhausen.rpgworld.model.BasicTraitInfo;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

public class CharacterCreationViewModel extends ViewModel {

    private BehaviorSubject<List<BasicTraitInfo>> basicTraitInfoSubject;

    public void changeInfoSet(List<BasicTraitInfo> basicTraitInfos){
        basicTraitInfoSubject.onNext(basicTraitInfos);
    }

    public Flowable<List<BasicTraitInfo>> getBasicTraitInfoSubjectOFlowable(){
        return basicTraitInfoSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void setBasicTraitInfoSubject(List<BasicTraitInfo> firstList) {
        basicTraitInfoSubject = BehaviorSubject.createDefault(firstList);
    }
}
