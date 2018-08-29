package com.liu.dao;

import com.liu.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDAO {
    String TABLE_NAME="question";
    String INSERT_FIELDS="title ,content,created_date,user_id,comment_count";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;
    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    List<Question> selectLatestQuestions(@Param("userId") int suerId,@Param("offset") int offset,
                                         @Param("limit") int limit);
    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
    Question selectById(@Param("id") int id);

    @Update({"update",TABLE_NAME,"set comment_count=#{count} where id=#{id}"})
    int updateComment(@Param("id") int id,
                      @Param("count") int count);


}
