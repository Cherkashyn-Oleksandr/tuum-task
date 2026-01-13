package com.tuum.tuumtask.mapper;

import com.tuum.tuumtask.config.UUIDTypeHandler;
import com.tuum.tuumtask.model.Transaction;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TransactionMapper {

    @Insert("""
        INSERT INTO account_transaction (
            id, account_id, amount, currency, direction, description, created_at
        )
        VALUES (
            #{id, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler},
            #{accountId, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler},
            #{amount},
            #{currency},
            #{direction},
            #{description},
            #{createdAt, jdbcType = TIMESTAMP}
        )
    """)
    //insert new completed transaction to db
    void insert(Transaction transaction);

    @Select("""
        SELECT id, account_id, amount, currency, direction, description, created_at
        FROM account_transaction
        WHERE account_id = #{accountId, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler}
        ORDER BY created_at DESC
        LIMIT #{limit} OFFSET #{offset}
    """)
    @Results({
            @Result(property = "id", column = "id", typeHandler = UUIDTypeHandler.class),
            @Result(property = "accountId", column = "account_id", typeHandler = UUIDTypeHandler.class),
            @Result(property = "amount", column = "amount"),
            @Result(property = "currency", column = "currency"),
            @Result(property = "direction", column = "direction"),
            @Result(property = "description", column = "description"),
            @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP)
    })
    //find transactions for account
    List<Transaction> findByAccountId(
            @Param("accountId") UUID accountId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}
