package com.liu.service;

import com.liu.util.JedisAdapter;
import com.liu.util.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;
    public long addLike(int id,int commentId)
    {
      String key= RedisKey.getLikeKey(commentId);
      String diskey=RedisKey.getDislikeKey(commentId);
      jedisAdapter.sadd(key,String.valueOf(id));
      jedisAdapter.srem(diskey,String.valueOf(id));
      return jedisAdapter.scard(key);
    }
    public long disLike(int id,int commentId)
    {
        String key= RedisKey.getLikeKey(commentId);
        String diskey=RedisKey.getDislikeKey(commentId);
        jedisAdapter.sadd(diskey,String.valueOf(id));
        jedisAdapter.srem(key,String.valueOf(id));
        return jedisAdapter.scard(key);
    }
    public int getStatus(int userId,int commentId)
    {
        String key=RedisKey.getLikeKey(commentId);
        if(jedisAdapter.sismember(key,String.valueOf(userId)))
        {
            return 1;
        }
        else
        {
            String diskey=RedisKey.getDislikeKey(commentId);
            if(jedisAdapter.sismember(diskey,String.valueOf(userId)))
            {
                return -1;
            }
            return 0;
        }

    }
    public long getCount(int commentId)
    {
        String key=RedisKey.getLikeKey(commentId);
        return jedisAdapter.scard(key);
    }
    public long dislikedoubleClick(int userId,int commentId)
    {
        String key=RedisKey.getLikeKey(commentId);
        String dislikeKey=RedisKey.getDislikeKey(commentId);
        jedisAdapter.srem(dislikeKey,String.valueOf(userId));
        return jedisAdapter.scard(key);
    }
    public long likedoubleClick(int userId,int commentId)
    {
        String key=RedisKey.getLikeKey(commentId);
        jedisAdapter.srem(key,String.valueOf(userId));
        return jedisAdapter.scard(key);
    }




}
