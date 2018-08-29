package com.liu.util;

public class RedisKey {
    private static String SPLIT=":";
    private static String BIZ_LIKE="LIKE";
    private  static String BIZ_DISLIKE="DISLIKE";
    private static String BIZ_EVENTQUEUE="EVENT_QUEUE";
    private static String BIZ_FOLLWER="BIZ_FOLLWER";
    private static String BIZ_FOLLWEE="BIZ_FOLLWEE";
    private static String BIZ_TIMELINE="TIMELINE";
    public static String getLikeKey(int commentId)
    {
        return BIZ_LIKE+SPLIT+String.valueOf(commentId);
    }
    public static String getDislikeKey(int commentId)
    {
        return BIZ_DISLIKE+SPLIT+String.valueOf(commentId);
    }
    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }
    public static String getfollwerKey(int entitytype,int entityId)
    {
        return BIZ_FOLLWER+SPLIT+String.valueOf(entitytype)+SPLIT+String.valueOf(entityId);
    }
    public static String getfollweeKey(int entitytype,int entityId)
    {
        return BIZ_FOLLWEE+SPLIT+String.valueOf(entitytype)+SPLIT+String.valueOf(entityId);
    }
    public static String getTimelineKey(int userId)
    {
        return BIZ_TIMELINE+SPLIT+String.valueOf(userId);
    }
}
