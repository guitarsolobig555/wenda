package com.liu.dao;

import com.liu.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDAO {
    String TABLE_NAME="comment";
    String INSERT_FIELDS="content,user_id,entity_id,entity_type,created_date,status";
    String SELECT_FILEDS="id,"+INSERT_FIELDS;

    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values(#{content},#{userId},#{entityId},#{entityType},#{createdDate},#{status})"})
    int addComment(Comment comment);

    @Select({"select",SELECT_FILEDS,"from",TABLE_NAME,"where entity_id=#{entityId} and entity_type=#{entityType} and status=#{status} order by id desc"})
    List<Comment> selectByEntityId(@Param("entityId") int entityId,
                                   @Param("entityType") int entityType,
                                   @Param("status") int status);

    @Update({"update",TABLE_NAME,"set status=#{status} where id=#{id}"})
    int deleteComment(@Param("id") int id,
                       @Param("status") int status);

    @Select({"select count(id) from",TABLE_NAME,"where entity_id=#{entityId} and entity_type=#{entityType} and status=#{status}"})
    int getCommentCount(@Param("entityId") int entityId,
                        @Param("entityType") int entityType,
                        @Param("status") int status);
    @Select({"select",SELECT_FILEDS,"FROM",TABLE_NAME,"where id=#{commentId}"})
   Comment getComment(@Param("commentId") int commentId);
    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);
    @Select({"select * from",TABLE_NAME,"where user_id=#{userId}"})
    List<Comment> selectCommetByUerId(@Param("userId") int userId);

}
