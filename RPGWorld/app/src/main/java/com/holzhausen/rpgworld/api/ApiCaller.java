package com.holzhausen.rpgworld.api;


import java.util.List;



import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ApiCaller<T> {


    public Disposable getListOfObjectsFromApi(Observable<List<T>> observable,
                                              Consumer<List<T>> onSuccess,
                                              Consumer<Throwable> onError){

        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError);

    }

}
