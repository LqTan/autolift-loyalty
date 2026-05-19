package com.autolift.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;

class CacheConfigTest {

  @Test
  void cacheManagerShouldBeRedisCacheManager() {
    LettuceConnectionFactory connectionFactory = createConnectionFactory();

    CacheConfig cacheConfig = new CacheConfig();
    CacheManager cacheManager = cacheConfig.cacheManager(connectionFactory);

    assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
  }

  @Test
  void cacheManagerShouldBeAbleToCreateUpliftScoresCache() {
    LettuceConnectionFactory connectionFactory = createConnectionFactory();

    CacheConfig cacheConfig = new CacheConfig();
    RedisCacheManager cacheManager = (RedisCacheManager) cacheConfig.cacheManager(connectionFactory);

    var cache = cacheManager.getCache("upliftScores");
    assertThat(cache).isNotNull();
    assertThat(cache.getName()).isEqualTo("upliftScores");
  }

  @Test
  void cacheManagerShouldBeAbleToCreateCampaignsCache() {
    LettuceConnectionFactory connectionFactory = createConnectionFactory();

    CacheConfig cacheConfig = new CacheConfig();
    RedisCacheManager cacheManager = (RedisCacheManager) cacheConfig.cacheManager(connectionFactory);

    var cache = cacheManager.getCache("campaigns");
    assertThat(cache).isNotNull();
    assertThat(cache.getName()).isEqualTo("campaigns");
  }

  @Test
  void redisConnectionFactoryShouldBeLettuce() {
    LettuceConnectionFactory connectionFactory = createConnectionFactory();

    assertThat(connectionFactory).isNotNull();
    assertThat(connectionFactory.getClass().getSimpleName()).isEqualTo("LettuceConnectionFactory");
  }

  private LettuceConnectionFactory createConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
    LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
    factory.start();
    return factory;
  }
}