package cn.iurac.testsystem.shiro.cache;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.cache.*;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.checkerframework.checker.units.qual.K;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GuavaCache<K,V> implements Cache<K,V> {

    private LoadingCache<K,V> loadingCache;

    public GuavaCache() {
        loadingCache = CacheBuilder.newBuilder()
                //设置并发级别为8，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(4)
                //设置写缓存后8秒钟过期
                .expireAfterWrite(1, TimeUnit.HOURS)
                //设置缓存容器的初始容量为10
                .initialCapacity(5)
                //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
                .maximumSize(50)
                .build(new CacheLoader<K,V>() {
                            @Override
                            public V load(K k) throws Exception {
                                return null;
                            }
                        });
    }

    @Override
    public V get(K k) throws CacheException {
        return loadingCache.getIfPresent(k);
    }

    @Override
    public V put(K k, V v) throws CacheException {
        loadingCache.put(k, v);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        V v = get(k);
        if(ObjectUtil.isNotNull(v)){
            loadingCache.invalidate(k);
        }
        return v;
    }

    @Override
    public void clear() throws CacheException {
        loadingCache.invalidateAll();
    }

    @Override
    public int size() {
        return loadingCache.asMap().size();
    }

    @Override
    public Set<K> keys() {
        return loadingCache.asMap().keySet();
    }

    @Override
    public Collection<V> values() {
        return loadingCache.asMap().values();
    }
}
