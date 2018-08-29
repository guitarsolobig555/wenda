package com.liu.service;

import com.liu.util.JedisAdapter;
import com.liu.util.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;
    private static Logger logger= LoggerFactory.getLogger(FollowService.class);

    public boolean addfollower(int userId,int entityType,int entityId)
    {
        String key= RedisKey.getfollwerKey(entityType,entityId);
        String followeeKey=RedisKey.getfollweeKey(entityType,userId);
        Jedis jedis=jedisAdapter.getJedis();
        Transaction t= jedisAdapter.multi(jedis);
        Date date=new Date();
        t.zadd(key,date.getTime(),String.valueOf(userId));
        t.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> list=jedisAdapter.exec(t,jedis);
        logger.info(String.valueOf(list.get(0)));
        return list.size()==2&& (Long)list.get(0) >0 && (Long)list.get(1) >0;
    }
    public boolean unfollower(int userId,int entityType,int entityId)
    {
        String key= RedisKey.getfollwerKey(entityType,entityId);
        String followeeKey=RedisKey.getfollweeKey(entityType,userId);
        Jedis jedis=jedisAdapter.getJedis();
        Transaction t=jedisAdapter.multi(jedis);
        t.zrem(key,String.valueOf(userId));
        t.zrem(followeeKey,String.valueOf(entityId));
        List<Object> list=jedisAdapter.exec(t,jedis);
        return list.size()==2&&(long)list.get(0)==1&&(long)list.get(1)==1;
    }
    public Set<String> follower(int enventType,int entityId)
    {
        String key=RedisKey.getfollwerKey(enventType,entityId);
        return jedisAdapter.zrange(key,0,10);
    }
    public Set<String> followee(int userId,int enventType)
    {
        String key=RedisKey.getfollweeKey(enventType,userId);
        return jedisAdapter.zrange(key,0,10);
    }
    public long getFollowerCount(int entityType,int entityId)
    {
        String key=RedisKey.getfollwerKey(entityType,entityId);
       return jedisAdapter.zcard(key);
    }
    public long getFolloweeCount(int userId,int entityType)
    {
        String key=RedisKey.getfollweeKey(entityType,userId);
        return jedisAdapter.zcard(key);
    }
    //粉丝数量
    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count)
    {
        String key=RedisKey.getfollwerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(key,offset,offset+count));
    }
    //关注数量
    public List<Integer> getFollowees(int userId,int entityType,int count)
    {
        String key=RedisKey.getfollweeKey(entityType,userId);
        return getIdsFromSet(jedisAdapter.zrevrange(key,0,count));
    }
    public List<Integer> getFollowers(int entityType,int entityId,int count)
    {
        String key=RedisKey.getfollwerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(key,0,count));
    }
    public List<Integer> getFollowees(int uerId,int entityType,int offset,int count)
    {
        String key=RedisKey.getfollweeKey(entityType,uerId);
        return getIdsFromSet(jedisAdapter.zrevrange(key,offset,offset+count));
    }

    public List<Integer> getIdsFromSet(Set<String> idset)
    {
        List<Integer> ids=new ArrayList<>();
        for(String str:idset)
        {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    public boolean isFollwer(int userId,int entityType,int entityid)
    {
        String key=RedisKey.getfollwerKey(entityType,entityid);
        return jedisAdapter.zscore(key,String.valueOf(userId))!=null;
    }

}
