package com.liu.dao;

import com.liu.model.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDAO
{
    String TABLE_NAME="loginticket";
    String INSERT_FIELDS="user_id,expired,status,ticket";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;

    @Insert({"insert into",TABLE_NAME," (",INSERT_FIELDS,")values(#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket loginTicket);

    @Update({"update",TABLE_NAME,"set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("status") int status,
                      @Param("ticket") String ticket);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where ticket=#{ticket}"})
    LoginTicket selectByTicket(@Param("ticket") String ticket);
}
