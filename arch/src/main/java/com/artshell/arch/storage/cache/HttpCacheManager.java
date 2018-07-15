package com.artshell.arch.storage.cache;

import android.arch.persistence.room.Room;

import com.artshell.arch.app.AppContext;
import com.artshell.arch.storage.db.HttpCacheDatabase;
import com.artshell.arch.storage.db.entity.HttpCache;
import com.artshell.arch.storage.server.HttpManager;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.StalePolicy;
import com.nytimes.android.external.store3.base.impl.room.StoreRoom;
import com.nytimes.android.external.store3.base.room.RoomPersister;

import java.util.Date;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

/**
 * @author artshell on 2018/6/30
 */
public class HttpCacheManager {

    /**
     * GET 请求(无参数)
     * @see Mixture
     * @return
     */
    public static StoreRoom<String, Mixture> store() {
        return StoreHolder.MIXTURE;
    }

    /**
     * GET 请求 + 查询参数
     * @see Mixture2
     * @return
     */
    public static StoreRoom<String, Mixture2> storeWithParameter() {
        return StoreParameterHolder.MIXTURE;
    }

    /**
     * POST 请求(Key/Value字段)
     * @see Mixture2
     * @return
     */
    public static StoreRoom<String, Mixture2> storeWithCouples() {
        return StoreCouplesHolder.MIXTURE;
    }

    private static final class StoreHolder {
        private static final StoreRoom<String, Mixture> MIXTURE = createStore(
                mixture -> HttpManager.get(String.class, mixture.getPath()).singleOrError());
    }

    private static final class StoreParameterHolder {
        private static final StoreRoom<String, Mixture2> MIXTURE = createStore(
                mixture -> HttpManager.get(String.class, mixture.getPath(), mixture.getPairs()).singleOrError());
    }

    private static final class StoreCouplesHolder {
        private static final StoreRoom<String, Mixture2> MIXTURE = createStore(
                mixture -> HttpManager.post(String.class, mixture.getPath(), mixture.getPairs()).singleOrError());
    }

    public static <I extends Key> StoreRoom<String, I> createStore(Fetcher<String, I> fetcher) {
        HttpCacheDatabase db = Room.databaseBuilder(AppContext.getAppContext(), HttpCacheDatabase.class, "db_http_cache").build();

        return StoreRoom.from(fetcher, new RoomPersister<String, String, I>() {
            @Nonnull
            @Override
            public Observable<String> read(@Nonnull I key) {
                HttpCache cache = db.cacheDao().fetch(key.getKey());
                return cache == null || cache.content == null || cache.content.isEmpty()
                        ? Observable.empty()
                        : Observable.just(cache.content);
            }

            @Nonnull
            @Override
            public void write(@Nonnull I key, @Nonnull String s) {
                HttpCache cache = new HttpCache();
                cache.key = key.getKey();
                cache.content = s;
                cache.time = new Date();
                db.cacheDao().insertCache(cache);
            }
        }, StalePolicy.NETWORK_BEFORE_STALE);
    }
}
