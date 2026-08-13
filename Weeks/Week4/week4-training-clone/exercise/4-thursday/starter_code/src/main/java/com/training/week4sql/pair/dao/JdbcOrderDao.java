package com.training.week4sql.pair.dao;

import com.training.week4sql.pair.domain.CustomerOrder;
import com.training.week4sql.pair.domain.OrderLine;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;

/** TODO: implement OrderDao with PreparedStatement only for dynamic values. */
public final class JdbcOrderDao implements OrderDao {
    private final Connection connection;

    public JdbcOrderDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long createOpenOrder(String customerEmail) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void addLine(long orderId, int lineNo, long productId, int qty, double unitPrice) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void markPaid(long orderId) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public double computeOrderTotal(long orderId) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<OrderLine> linesFor(long orderId) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public CustomerOrder findOrder(long orderId) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }
}
