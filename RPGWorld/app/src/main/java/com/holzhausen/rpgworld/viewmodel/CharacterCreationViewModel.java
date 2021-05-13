package com.holzhausen.rpgworld.viewmodel;

import androidx.lifecycle.ViewModel;

import com.holzhausen.rpgworld.model.BasicTraitInfo;
import com.holzhausen.rpgworld.model.Skill;
import com.holzhausen.rpgworld.model.SkillFragmentInfo;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class CharacterCreationViewModel extends CommonViewModel {

    private BehaviorSubject<List<BasicTraitInfo>> basicTraitInfoListSubject;

    private BehaviorSubject<String> titleSubject;

    private PublishSubject<BasicTraitInfo> basicTraitInfoSubject;

    public CharacterCreationViewModel(){
        super();
        basicTraitInfoSubject = PublishSubject.create();
    }

    public void changeInfoSet(List<BasicTraitInfo> basicTraitInfos){
        basicTraitInfoListSubject.onNext(basicTraitInfos);
    }

    public Flowable<List<BasicTraitInfo>> getBasicTraitInfoSubjectOFlowable(){
        return basicTraitInfoListSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void setBasicTraitInfoListSubject(List<BasicTraitInfo> firstList) {
        basicTraitInfoListSubject = BehaviorSubject.createDefault(firstList);
    }

    public void changeTitle(String title) {
        titleSubject.onNext(title);
    }

    public Flowable<String> getTitleSubjectFlowable(){
        return titleSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void setTitleSubject(String firstTitle) {
        titleSubject = BehaviorSubject.createDefault(firstTitle);
    }

    public void sendChosenBasicTrait(BasicTraitInfo basicTraitInfo){
        basicTraitInfoSubject.onNext(basicTraitInfo);
    }

    public Flowable<BasicTraitInfo> getChosenTraitInfoFlowable(){
        return basicTraitInfoSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

}
