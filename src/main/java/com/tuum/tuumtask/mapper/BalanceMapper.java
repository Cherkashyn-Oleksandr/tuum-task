package com.tuum.tuumtask.mapper;

import com.tuum.tuumtask.model.Balance;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface BalanceMapper {

    @Insert("""
        INSERT INTO balance (id, account_id, currency, amount)
        VALUES (#{id, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler},
                #{accountId, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler},
                #{currency},
                #{amount})
    """)
    void insert(Balance balance);

    @Select("""
        SELECT id, account_id, currency, amount
        FROM balance
        WHERE account_id = #{accountId, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler}
    """)
    List<Balance> findByAccountId(UUID accountId);

    @Select("""
        SELECT id, account_id, currency, amount
        FROM balance
        WHERE account_id = #{accountId, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler}
          AND currency = #{currency}
    """)
    Balance findByAccountIdAndCurrency(
            @Param("accountId") UUID accountId,
            @Param("currency") String currency
    );

    @Update("""
        UPDATE balance
        SET amount = #{amount}
        WHERE id = #{id, jdbcType=OTHER, typeHandler=com.tuum.tuumtask.config.UUIDTypeHandler}
    """)
    void update(Balance balance);
}
