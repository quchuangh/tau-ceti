package com.chuang.tauceti.tools.third.redis;

import com.chuang.tauceti.tools.basic.ObjectKit;
import com.chuang.tauceti.tools.basic.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class RedisCache<K, V> implements Cache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);
    private final String name;
    private final RedisHCached cached;

    public RedisCache(String name, RedisHCached cached){
        this.name=name;
        this.cached=cached;
    }

    /**
     * 获得byte[]型的key
     */
    private byte[] getByteKey(K key){
        if(key instanceof String){
            String preKey = key.toString();
            return preKey.getBytes();
        }else{
            return ObjectKit.serialize(key);
        }
    }


    private byte[] getByteName(){
        return name.getBytes();

    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<V> get(K key) {
        return (Optional<V>)cached.getValue(getByteName(), getByteKey(key));

    }

    @Override
    public Boolean put(K key, V value) {
        logger.debug("根据key存储 key [" + key + "]");
        return cached.updateValue(getByteName(), getByteKey(key), ObjectKit.serialize(value));

    }

    @Override
    public Boolean remove(K key) {
        logger.debug("从redis中删除 key [" + key + "]");
        return cached.deleteValue(getByteName(),getByteKey(key)) > 0;
    }

    @Override
    public Boolean clear() {
        logger.debug("从redis中删除所有元素");
        return cached.delete(getByteName());
    }

    @Override
    public int size()  {
        return cached.getFieldSize(getByteName()).intValue();

    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<K> keys()  {
        return (Set<K>)cached.getFields(getByteName());

    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<V> values() {
        return (Collection<V>) cached.getValuesByKey(getByteName());

    }
}
