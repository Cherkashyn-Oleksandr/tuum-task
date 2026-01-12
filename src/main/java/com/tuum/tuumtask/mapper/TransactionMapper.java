package com.tuum.tuumtask.mapper;

import com.tuum.tuumtask.model.Transaction;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TransactionMapper {

    @Insert("""
        INSERT INTO account_transaction
        (id, account_id, amount, currency, direction, description, created_at)
        VALUES
        (#{id},
         #{accountId},
         #{amount},
         #{currency},
         #{direction},
         #{description},
         #{createdAt})
    """)
    void insert(Transaction transaction);

    @Select("""
        SELECT id, account_id, amount, currency, direction, description, created_at
        FROM account_transaction
        WHERE account_id = #{accountId}
        ORDER BY created_at DESC
    """)
    List<Transaction> findByAccountId(UUID accountId);
}
