package com.holzhausen.rpgworld.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;



import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.holzhausen.rpgworld.api.ICharacterCreationAPI.BASIC_URL;

public class ApiCaller<T> {


    public Disposable getListOfObjectsFromApi(Observable<List<T>> observable,
                                              Consumer<List<T>> onSuccess,
                                              Consumer<Throwable> onError){

        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError);

    }

    public Disposable getObjectFromApi(Observable<T> observable,
                                       Consumer<T> onSuccess,
                                       Consumer<Throwable> onError) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError);
    }

    public static Retrofit getRetrofitClient(){
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        final Gson gson = new GsonBuilder().setLenient().create();

        return new Retrofit.Builder()
                .baseUrl(BASIC_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
