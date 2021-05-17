package com.holzhausen.rpgworld.viewmodel;

import androidx.lifecycle.ViewModel;

import com.holzhausen.rpgworld.model.Message;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class ObjectiveViewModel extends ViewModel {

    private PublishSubject<List<Message>> messageSubject;

    public void createMessageSubject() {
        messageSubject = PublishSubject.create();
    }

    public void sendMessage(List<Message> message){
        messageSubject.onNext(message);
    }

    public Flowable<List<Message>> getMessageFlowable(){
        return messageSubject.toFlowable(BackpressureStrategy.BUFFER);
    }
}
