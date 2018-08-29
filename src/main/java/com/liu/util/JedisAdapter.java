package com.liu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean
{
    private static final Logger loger= LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool jedisPool;

    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool=new JedisPool("redis://localhost:6379/0");
    }
    public long sadd(String key,String value)
    {
        Jedis jedis=null;
        try{
           jedis=jedisPool.getResource();
           return jedis.sadd(key,value);
        }catch (Exception e)
        {
          loger.error("redis插入数据失败"+e.getMessage());
        }
        finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return 0;
    }
    public long srem(String key,String value)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e)
        {
            loger.error("移除元素失败"+e.getMessage());
        }
        finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0;
    }
    public long scard(String key)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            loger.error("获取元素个数失败");
        }finally {
             if(jedis!=null)
                 jedis.close();
        }
        return 0;
    }
    public boolean sismember(String key,String value)
    {
        Jedis jedis=null;
        try
        {
            jedis=jedisPool.getResource();
            return jedis.sismember(key,value);
        }catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null)
               jedis.close();
        }
        return false;
    }
    public List<String> brpop(int timeout,String key)
    {
        Jedis jedis=null;
        try{
            jedis= jedisPool.getResource();
            return jedis.brpop(timeout,key);
        }catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }
        finally {
            if(jedis!=null)
                jedis.close();
        }
        return null;
    }
    public long lpush(String key,String value)
    {
        Jedis jedis=null;
        try
        {
            jedis=jedisPool.getResource();
            return jedis.lpush(key,value);
        }catch (Exception e){
            loger.error("发生异常"+e.getMessage());
        }
        finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0;
    }
    public Jedis getJedis()
    {
        return jedisPool.getResource();
    }
    public Transaction multi(Jedis jedis)
    {
        try
        {
            return jedis.multi();
        }catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }return null;
    }
    public List<Object> exec(Transaction t,Jedis jedis)
    {
        try{
            return t.exec();
        }
        catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
            t.discard();
        }
        finally {
            if(t!=null)
            {
                try {
                    t.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(jedis!=null) {
                jedis.close();
            }
        }
        return null;
    }
    public long zadd(String key,double score,String value)
    {
        Jedis jedis=null;
        try {
            jedis=getJedis();
            return jedis.zadd(key,score,value);
        }catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0;
    }
    public long zrem(String key,String value)
    {
        Jedis jedis=null;
        try
        {
            jedis=getJedis();
            return jedis.zrem(key,value);
        }catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0;
    }
    public Set<String> zrange(String key,int start,int end)
    {
        Jedis jedis=null;
        try{
            jedis=getJedis();
            return jedis.zrange(key,start,end);
        }
        catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }finally {
          if(jedis!=null)
              jedis.close();
        }
        return null;
    }
    public long zcard(String key)
    {
        Jedis jedis=null;
        try{
            jedis=getJedis();
            return jedis.zcard(key);
        }catch (Exception e){
            loger.error("发生异常"+e.getMessage());
        }
        finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0;
    }
    public Set<String> zrevrange(String key,int offset,int count)
    {
        Jedis jedis=null;
        try {
           jedis=getJedis();
           return  jedis.zrevrange(key,offset,count);
        }catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null)
               jedis.close();
        }
        return null;
    }
    public Double zscore(String key,String member)
    {
        Jedis jedis=null;
        try{
            jedis=getJedis();
            return jedis.zscore(key,member);
        }catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return null;
    }
    public List<String> lrange(String key,int start,int end)
    {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            return jedis.lrange(key,start,end);
        }catch (Exception e)
        {
            loger.error("发生异常"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return null;
    }



}
