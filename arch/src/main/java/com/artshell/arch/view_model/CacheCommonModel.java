package com.artshell.arch.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import com.artshell.arch.storage.CacheManager;
import com.artshell.arch.storage.Mixture;
import com.artshell.arch.storage.Mixture2;
import com.artshell.arch.storage.Observers;
import com.artshell.arch.storage.Result;
import com.artshell.arch.utils.RxSchedulers;
import com.google.gson.Gson;

import java.util.Map;

/**
 * 常用请求(单接口数据, 从缓存中获取, 没有则从服务器端获取)
 * (不适用于多接口数据或一个接口又依赖其它接口的数据)根据需要可选择重写相应方法
 * Created by artshell on 2018/3/16.
 */

public class CacheCommonModel extends ViewModel {
    private static Gson gson = new Gson();

    // Get请求
    public <T> LiveData<Result<T>> get(String cacheKey, Class<T> target, String url) {
        MediatorLiveData<Result<T>> resultData = new MediatorLiveData<>();
        CacheManager.store()
                .get(new Mixture(cacheKey, url))
                .map(raw -> gson.fromJson(raw, target))
                .toObservable()
                .compose(RxSchedulers.ioToMain())
                .subscribe(Observers.create(resultData));
        return resultData;
    }

    // Get请求带参数
    public <T> LiveData<Result<T>> getWithParameter(String cacheKey, Class<T> target, String url, Map<String, String> pairs) {
        MediatorLiveData<Result<T>> resultData = new MediatorLiveData<>();
        CacheManager.storeWithParameter()
                .get(new Mixture2(cacheKey, url, pairs))
                .map(raw -> gson.fromJson(raw, target))
                .toObservable()
                .compose(RxSchedulers.ioToMain())
                .subscribe(Observers.create(resultData));
        return resultData;
    }

    // Post请求带字段
    public <T> LiveData<Result<T>> post(String cacheKey, Class<T> target, String url, Map<String, String> pairs) {
        MediatorLiveData<Result<T>> resultData = new MediatorLiveData<>();
        CacheManager.storeWithField()
                .get(new Mixture2(cacheKey, url, pairs))
                .map(raw -> gson.fromJson(raw, target))
                .toObservable()
                .compose(RxSchedulers.ioToMain())
                .subscribe(Observers.create(resultData));
        return resultData;
    }
}
