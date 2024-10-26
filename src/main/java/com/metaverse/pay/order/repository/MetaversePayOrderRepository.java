package com.metaverse.pay.order.repository;

import com.metaverse.pay.order.domain.MetaversePayOrder;

import java.math.BigDecimal;

public interface MetaversePayOrderRepository {
    Long createPayOrder(Long id, Long currentUserId, String body, Integer quantity, BigDecimal money);

    MetaversePayOrder findByIdWithWriteLock(Long payOrderId);

    boolean paymentSuccess(Long payOrderId, Long newVersion);

    void setWeChatPayNo(Long id, Long newVersion, String weChatPayNo);

    void setAlipayPayNo(Long id, Long newVersion, String alipayPayNo);
}
