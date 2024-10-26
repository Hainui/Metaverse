package com.metaverse.pay.order.repository.Impl;

import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.pay.order.db.entity.MetaversePayOrderDO;
import com.metaverse.pay.order.db.service.IMetaversePayOrderService;
import com.metaverse.pay.order.domain.MetaversePayOrder;
import com.metaverse.pay.order.repository.MetaversePayOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MetaversePayOrderRepositoryImpl implements MetaversePayOrderRepository {

    private final IMetaversePayOrderService iMetaversePayOrderService;

    @Override
    public Long createPayOrder(Long id, Long currentUserId, String body, Integer quantity, BigDecimal money) {
        iMetaversePayOrderService.save(new MetaversePayOrderDO()
                .setId(id)
                .setBody(body)
                .setQuantity(quantity)
                .setMoney(money)
                .setVersion(0L)
                .setCreateBy(currentUserId)
                .setCreateAt(LocalDateTime.now()));
        return id;
    }

    @Override
    public MetaversePayOrder findByIdWithWriteLock(Long payOrderId) {
        MetaversePayOrderDO payOrderDO = iMetaversePayOrderService.lambdaQuery()
                .eq(MetaversePayOrderDO::getId, payOrderId)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        return convertToDomain(payOrderDO);
    }

    @Override
    public boolean paymentSuccess(Long payOrderId, Long newVersion) {
        return iMetaversePayOrderService.lambdaUpdate()
                .eq(MetaversePayOrderDO::getId, payOrderId)
                .set(MetaversePayOrderDO::getVersion, newVersion)
                .set(MetaversePayOrderDO::getPayAt, LocalDateTime.now())
                .set(MetaversePayOrderDO::getStatus, Boolean.TRUE)
                .update();
    }

    @Override
    public void setWeChatPayNo(Long id, Long newVersion, String weChatPayNo) {
        iMetaversePayOrderService.lambdaUpdate()
                .eq(MetaversePayOrderDO::getId, id)
                .set(MetaversePayOrderDO::getVersion, newVersion)
                .set(MetaversePayOrderDO::getWeChatPayNo, weChatPayNo);
    }

    @Override
    public void setAlipayPayNo(Long id, Long newVersion, String alipayPayNo) {
        iMetaversePayOrderService.lambdaUpdate()
                .eq(MetaversePayOrderDO::getId, id)
                .set(MetaversePayOrderDO::getVersion, newVersion)
                .set(MetaversePayOrderDO::getAlipayPayNo, alipayPayNo);
    }

    private MetaversePayOrder convertToDomain(MetaversePayOrderDO payOrderDO) {
        if (payOrderDO == null) return null;
        return new MetaversePayOrder()
                .setId(payOrderDO.getId())
                .setBody(payOrderDO.getBody())
                .setStatus(payOrderDO.getStatus())
                .setPayAt(payOrderDO.getPayAt())
                .setQuantity(payOrderDO.getQuantity())
                .setAlipayPayNo(payOrderDO.getAlipayPayNo())
                .setWeChatPayNo(payOrderDO.getWeChatPayNo())
                .setMoney(payOrderDO.getMoney())
                .setVersion(payOrderDO.getVersion())
                .setCreateBy(payOrderDO.getCreateBy())
                .setCreateAt(payOrderDO.getCreateAt());
    }
}
