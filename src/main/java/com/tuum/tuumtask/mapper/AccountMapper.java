package com.tuum.tuumtask.mapper;

import com.tuum.tuumtask.model.Account;
import com.tuum.tuumtask.config.UUIDTypeHandler;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.UUID;

@Mapper
public interface AccountMapper {

    @Insert("""
        INSERT INTO account (id, customer_id, country, created_at)
        VALUES (#{id, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler},
                #{customerId, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler},
                #{country},
                #{createdAt})
    """)
    //insert new account to db
    void insert(Account account);

    @Select("""
        SELECT id, customer_id, country, created_at
        FROM account
        WHERE id = #{id, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler}
    """)
    @Results(id = "AccountResult", value = {
            @Result(property = "id", column = "id", jdbcType = JdbcType.OTHER, typeHandler = UUIDTypeHandler.class),
            @Result(property = "customerId", column = "customer_id", jdbcType = JdbcType.OTHER, typeHandler = UUIDTypeHandler.class),
            @Result(property = "country", column = "country"),
            @Result(property = "createdAt", column = "created_at")
    })
    //find existing account
    Account findById(UUID id);
}
