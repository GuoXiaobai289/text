package cn.iurac.testsystem.shiro.cache;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;

@Component
public class GuavaCacheManager extends AbstractCacheManager {
    @Override
    protected Cache createCache(String s) throws CacheException {
        return new GuavaCache();
    }
}
