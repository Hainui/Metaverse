package com.metaverse.pay.order.domain;

import com.metaverse.common.config.BeanManager;
import com.metaverse.common.model.IAggregateRoot;
import com.metaverse.pay.order.MetaversePayOrderIdGen;
import com.metaverse.pay.order.repository.MetaversePayOrderRepository;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class MetaversePayOrder implements IAggregateRoot<MetaversePayOrder> {

    protected static final Long MODEL_VERSION = 1L;

    /**
     * 主键支付单ID
     */
    private Long id;

    /**
     * 物品名称
     */
    private String body;

    /**
     * 物品数量
     */
    private Integer quantity;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 支付单状态，0未支付，1支付成功
     */
    private Boolean status;

    /**
     * 微信支付单号
     */
    private String weChatPayNo;

    /**
     * 支付宝支付单号
     */
    private String alipayPayNo;

    /**
     * 订单创建时间
     */
    private LocalDateTime createAt;

    /**
     * 支付时间
     */
    private LocalDateTime payAt;

    /**
     * 支付单创建人ID
     */
    private Long createBy;

    /**
     * 版本号
     */
    private Long version;

    public static Long create(Long currentUserId, String body, Integer quantity, BigDecimal money) {
        MetaversePayOrderIdGen idGen = BeanManager.getBean(MetaversePayOrderIdGen.class);
        MetaversePayOrderRepository repository = BeanManager.getBean(MetaversePayOrderRepository.class);
        return repository.createPayOrder(idGen.nextId(), currentUserId, body, quantity, money);
    }

    public boolean paymentSuccess() {
        MetaversePayOrderRepository repository = BeanManager.getBean(MetaversePayOrderRepository.class);
        if (this.status.equals(Boolean.TRUE)) {// noChange
            return false;
        }
        return repository.paymentSuccess(pkVal(), changeVersion());
    }

    public void weChatPayNo(String weChatPayNo) {
        MetaversePayOrderRepository repository = BeanManager.getBean(MetaversePayOrderRepository.class);
        repository.setWeChatPayNo(pkVal(), changeVersion(), weChatPayNo);
    }

    public void alipayPayNo(String alipayPayNo) {
        MetaversePayOrderRepository repository = BeanManager.getBean(MetaversePayOrderRepository.class);
        repository.setAlipayPayNo(pkVal(), changeVersion(), alipayPayNo);
    }

    @NotNull
    public static MetaversePayOrder writeLoadAndAssertNotExist(Long payOrderId) {
        MetaversePayOrderRepository repository = BeanManager.getBean(MetaversePayOrderRepository.class);
        MetaversePayOrder payOrder = repository.findByIdWithWriteLock(payOrderId);
        if (payOrder == null) {
            throw new IllegalArgumentException("PayOrder not found");
        }
        return payOrder;
    }

    @Override
    public Long pkVal() {
        return id;
    }

    @Override
    public Long modelVersion() {
        return MODEL_VERSION;
    }

    @Override
    public Long changeVersion() {
        return ++version;
    }
}
