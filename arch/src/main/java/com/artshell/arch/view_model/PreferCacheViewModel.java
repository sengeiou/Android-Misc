package com.artshell.arch.view_model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.artshell.arch.utils.MainLiveDataStreams;
import com.artshell.arch.storage.cache.Mixture;
import com.artshell.arch.storage.cache.Mixture2;
import com.artshell.arch.storage.Resource;
import com.artshell.arch.storage.cache.PreferCacheManager;

import java.util.Map;

import io.reactivex.schedulers.Schedulers;

/**
 * 常用请求(单接口数据, 从缓存中获取, 没有则自动从服务器端获取)
 * Created by artshell on 2018/3/16.
 */

public class PreferCacheViewModel extends ContextViewModel {

    public PreferCacheViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Get请求
     * @param target
     * @param path   url path
     * @param <T>
     * @return
     */
    public <T> LiveData<Resource<T>> get(Class<T> target, String path) {
        return MainLiveDataStreams.fromPublisher(
                PreferCacheManager.store()
                        .get(new Mixture(path))
                        .map(raw -> threadGson.get().fromJson(raw, target))
                        .toFlowable()
                        .subscribeOn(Schedulers.io()));
    }

    /**
     * Get请求带参数
     * @param target
     * @param path   url path
     * @param pairs
     * @param <T>
     * @return
     */
    public <T> LiveData<Resource<T>> getWithParameter(Class<T> target, String path, Map<String, String> pairs) {
        return MainLiveDataStreams.fromPublisher(
                PreferCacheManager.storeWithParameter()
                        .get(new Mixture2(path, pairs))
                        .map(raw -> threadGson.get().fromJson(raw, target))
                        .toFlowable()
                        .subscribeOn(Schedulers.io()));
    }

    /**
     * Post请求
     * @param target
     * @param path   url path
     * @param pairs
     * @param <T>
     * @return
     */
    public <T> LiveData<Resource<T>> post(Class<T> target, String path, Map<String, String> pairs) {
        return MainLiveDataStreams.fromPublisher(
                PreferCacheManager.storeWithCouples()
                        .get(new Mixture2(path, pairs))
                        .map(raw -> threadGson.get().fromJson(raw, target))
                        .toFlowable()
                        .subscribeOn(Schedulers.io()));
    }
}
