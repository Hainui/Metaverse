package com.metaverse.logistics.service;

import com.alibaba.fastjson.JSON;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.logistics.LogisticsIdGen;
import com.metaverse.logistics.db.entity.MetaversePhysicalDistributionDO;
import com.metaverse.logistics.db.service.IMetaversePhysicalDistributionService;
import com.metaverse.logistics.dto.DeliveryAddressDto;
import com.metaverse.logistics.req.FillAddressReq;
import com.metaverse.logistics.req.SetTrackingNumberReq;
import com.metaverse.logistics.resp.userPhysicalDistributionViewResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhysicalDistributionService {
    private final LogisticsIdGen logisticsIdGen;
    private final IMetaversePhysicalDistributionService physicalDistributionService;

    @Transactional(rollbackFor = Exception.class)
    public Boolean fillAddress(Long currentUserId, FillAddressReq req) {
        if (req.getId() == null) {
            return physicalDistributionService.save(new MetaversePhysicalDistributionDO()
                    .setId(logisticsIdGen.nextId())
                    .setVersion(0L)
                    .setUserId(req.getUserId())
                    .setCreateBy(currentUserId)
                    .setItemName(req.getItemName()));
        } else {
            MetaversePhysicalDistributionDO distributionDO = loadAssertNotExist(req.getId());
            return physicalDistributionService.updateById(distributionDO
                    .setPhone(req.getPhone())
                    .setDeliveryAddress(JSON.toJSONString(req.getDeliveryAddress()))
                    .setVersion(distributionDO.getVersion() + 1));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean setTrackingNumber(Long currentUserId, SetTrackingNumberReq req) {
        MetaversePhysicalDistributionDO distributionDO = loadAssertNotExist(req.getId());
        return physicalDistributionService.updateById(distributionDO
                .setTrackingNumber(req.getTrackingNumber())
                .setUpdateBy(currentUserId)
                .setVersion(distributionDO.getVersion() + 1));
    }

    @NotNull
    private MetaversePhysicalDistributionDO loadAssertNotExist(Long id) {
        MetaversePhysicalDistributionDO distributionDO = physicalDistributionService.lambdaQuery()
                .eq(MetaversePhysicalDistributionDO::getId, id)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        if (distributionDO == null) {
            throw new IllegalArgumentException("未找到该物流信息");
        }
        return distributionDO;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceipt(Long currentUserId, Long id) {
        MetaversePhysicalDistributionDO distributionDO = loadAssertNotExist(id);
        return physicalDistributionService.updateById(distributionDO
                .setConfirmedReceipt(true)
                .setUpdateBy(currentUserId)
                .setVersion(distributionDO.getVersion() + 1));
    }

    public List<userPhysicalDistributionViewResp> userPhysicalDistributionView(Long currentUserId) {
        return physicalDistributionService.lambdaQuery()
                .eq(MetaversePhysicalDistributionDO::getUserId, currentUserId)
                .orderByAsc(MetaversePhysicalDistributionDO::getConfirmedReceipt)
                .list()
                .stream()
                .map(this::convertTouserPhysicalDistributionViewResp)
                .collect(Collectors.toList());
    }

    private userPhysicalDistributionViewResp convertTouserPhysicalDistributionViewResp(MetaversePhysicalDistributionDO metaversePhysicalDistributionDO) {
        if (metaversePhysicalDistributionDO == null) {
            return null;
        }

        return new userPhysicalDistributionViewResp()
                .setId(metaversePhysicalDistributionDO.getId())
                .setConfirmedReceipt(metaversePhysicalDistributionDO.getConfirmedReceipt())
                .setTrackingNumber(metaversePhysicalDistributionDO.getTrackingNumber())
                .setItemQuantity(metaversePhysicalDistributionDO.getItemQuantity())
                .setItemName(metaversePhysicalDistributionDO.getItemName())
                .setPhone(metaversePhysicalDistributionDO.getPhone())
                .setDeliveryAddress(JSON.parseObject(metaversePhysicalDistributionDO.getDeliveryAddress(), DeliveryAddressDto.class));
    }
}
