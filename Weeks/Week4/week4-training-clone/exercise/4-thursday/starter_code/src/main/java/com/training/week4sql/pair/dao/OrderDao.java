package com.training.week4sql.pair.dao;

import com.training.week4sql.pair.domain.CustomerOrder;
import com.training.week4sql.pair.domain.OrderLine;
import java.util.List;

/**
 * TODO (Pair B after swap): create OPEN order, append lines, mark PAID, compute total.
 */
public interface OrderDao {
    long createOpenOrder(String customerEmail) throws Exception;

    void addLine(long orderId, int lineNo, long productId, int qty, double unitPrice) throws Exception;

    void markPaid(long orderId) throws Exception;

    double computeOrderTotal(long orderId) throws Exception;

    List<OrderLine> linesFor(long orderId) throws Exception;

    CustomerOrder findOrder(long orderId) throws Exception;
}
