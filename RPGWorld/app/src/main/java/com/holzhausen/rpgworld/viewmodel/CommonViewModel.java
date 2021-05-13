package com.holzhausen.rpgworld.viewmodel;

import androidx.lifecycle.ViewModel;

import com.holzhausen.rpgworld.model.SkillFragmentInfo;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class CommonViewModel extends ViewModel {

    private BehaviorSubject<SkillFragmentInfo> skillListSubject;

    public void setSkillListSubject(SkillFragmentInfo skills) {
        skillListSubject = BehaviorSubject.createDefault(skills);
    }

    public Flowable<SkillFragmentInfo> getSkillListFlowable(){
        return skillListSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void updateSkills(SkillFragmentInfo skills){
        skillListSubject.onNext(skills);
    }

}
