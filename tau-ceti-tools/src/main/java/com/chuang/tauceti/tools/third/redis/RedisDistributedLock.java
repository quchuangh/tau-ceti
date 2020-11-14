package com.chuang.tauceti.tools.third.redis;

import com.chuang.tauceti.tools.basic.AbstractDistributedLock;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.async.RedisScriptingAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class RedisDistributedLock extends AbstractDistributedLock {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ThreadLocal<String> lockValue = new ThreadLocal<>();
	private final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);
	private static final String REDIS_LIB_MISMATCH = "Failed to convert nativeConnection. " +
			"Is your SpringBoot main version > 2.0 ? Only lib:lettuce is supported.";
	private static final String UNLOCK_LUA;
	static {
		UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
				"then " +
				"    return redis.call(\"del\",KEYS[1]) " +
				"else " +
				"    return 0 " +
				"end ";
	}

	public RedisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 加锁
	 */
	@Override
	public boolean lock(String key, long expireSeconds, int retryTimes, long sleepMillis) {
		boolean result = tryLock(key, expireSeconds);
		while((!result) && retryTimes-- > 0){
			try {
				logger.debug("Lock failed, retrying..." + retryTimes);
				Thread.sleep(sleepMillis);
			} catch (InterruptedException e) {
				return false;
			}
			result = tryLock(key, expireSeconds);
		}
		return result;
	}

	/**
	 * 尝试Lock
	 */
	@SuppressWarnings("unchecked")
	private boolean tryLock(String key, long expireSeconds) {

		String uuid = UUID.randomUUID().toString();
		try {
			String result = redisTemplate.execute((RedisCallback<String>) connection -> {
				try{
					Object nativeConnection = connection.getNativeConnection();

					byte[] keyByte = key.getBytes(StandardCharsets.UTF_8);
					byte[] valueByte = uuid.getBytes(StandardCharsets.UTF_8);

					String resultString = "";
					if(nativeConnection instanceof RedisAsyncCommands){
						RedisAsyncCommands<byte[], byte[]> command = (RedisAsyncCommands<byte[], byte[]>) nativeConnection;
						resultString = command
								.getStatefulConnection()
								.sync()
								.set(keyByte, valueByte, SetArgs.Builder.nx().ex(expireSeconds));
					}else if(nativeConnection instanceof RedisAdvancedClusterAsyncCommands){
						RedisAdvancedClusterAsyncCommands<byte[], byte[]> clusterAsyncCommands =
								(RedisAdvancedClusterAsyncCommands<byte[], byte[]>) nativeConnection;
						resultString = clusterAsyncCommands
								.getStatefulConnection()
								.sync()
								.set(keyByte, keyByte, SetArgs.Builder.nx().ex(expireSeconds));
					}else{
						logger.error(REDIS_LIB_MISMATCH);
					}
					return resultString;
				}catch (Exception e){
					logger.error("Failed to lock, closing connection",e);
					closeConnection(connection);
					return "";
				}
			});
			boolean eq = "OK".equals(result);
			if(eq) {
				lockValue.set(uuid);
			}
			return eq;
		} catch (Exception e) {
			logger.error("Set redis exception", e);
			return false;
		}
	}

	/**
	 * 释放锁
	 * 有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
	 * 使用lua脚本删除redis中匹配value的key
	 * @return false:   锁已不属于当前线程  或者 锁已超时
	 */
	@Override
	public boolean releaseLock(String key) {
		try {
			String lockValue = this.lockValue.get();
			if(lockValue==null){
				return false;
			}
			byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
			byte[] valueBytes = lockValue.getBytes(StandardCharsets.UTF_8);
			Object[] keyParam = new Object[]{keyBytes};

			Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
				try{
					Object nativeConnection = connection.getNativeConnection();
					if (nativeConnection instanceof RedisScriptingAsyncCommands) {
						/*
						 * 不要问我为什么这里的参数这么奇怪
						 */
						RedisScriptingAsyncCommands<Object, byte[]> command =
								(RedisScriptingAsyncCommands<Object, byte[]>) nativeConnection;

						RedisFuture<?> future = command.eval(UNLOCK_LUA, ScriptOutputType.INTEGER, keyParam, valueBytes);
						return getEvalResult(future,connection);
					}else{
						logger.warn(REDIS_LIB_MISMATCH);
						return 0L;
					}
				}catch (Exception e){
					logger.error("Failed to releaseLock, closing connection",e);
					closeConnection(connection);
					return 0L;
				}
			});
			return result != null && result > 0;
		} catch (Exception e) {
			logger.error("release lock exception", e);
		}
		return false;
	}

	private Long getEvalResult(RedisFuture<?> future, RedisConnection connection){
		try {
			Object o = future.get();
			return (Long)o;
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Future get failed, trying to close connection.", e);
			closeConnection(connection);
			return 0L;
		}
	}


	private void closeConnection(RedisConnection connection){
		try{
			connection.close();
		}catch (Exception e2){
			logger.error("close connection fail.", e2);
		}
	}

	/**
	 * 查看是否加锁
	 */
	@Override
	public boolean isLocked(String key) {
		Object o = redisTemplate.opsForValue().get(key);
		return o!=null;
	}
	
}
