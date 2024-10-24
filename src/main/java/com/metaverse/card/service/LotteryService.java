package com.metaverse.card.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.metaverse.card.db.entity.MetaverseCardProbabilityDO;
import com.metaverse.card.db.entity.MetaverseLotteryCardRecordDO;
import com.metaverse.card.db.service.IMetaverseCardProbabilityService;
import com.metaverse.card.db.service.IMetaverseLotteryCardRecordService;
import com.metaverse.card.resp.CardResp;
import com.metaverse.card.resp.LotteryRecordResp;
import com.metaverse.card.utils.ProbabilityBasedSelection;
import com.metaverse.common.constant.PresentConstant;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.common.constant.UserConstant;
import com.metaverse.logistics.req.FillAddressReq;
import com.metaverse.logistics.service.PhysicalDistributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryService {
    private static final int MAX_DAILY_DRAWS = 100;
    private final IMetaverseCardProbabilityService cardProbabilityService;
    private final IMetaverseLotteryCardRecordService lotteryCardRecordService;
    private final PhysicalDistributionService physicalDistributionService;

    public List<CardResp> drawCards(int count, Long userId) {
        int dailyDrawCount = getDailyDrawCount(userId);
        if (dailyDrawCount + count > MAX_DAILY_DRAWS) {
            throw new IllegalArgumentException("今日你的抽卡额度已耗尽,期待明天你再次激活这份幸运");
        }

        List<MetaverseCardProbabilityDO> allCards = cardProbabilityService.list();
        Map<String, BigDecimal> probabilityMap = allCards.stream()
                .collect(Collectors.toMap(
                        MetaverseCardProbabilityDO::getLevel,
                        MetaverseCardProbabilityDO::getDropRate,
                        (existing, replacement) -> existing
                ));

        List<String> drawnLevels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            drawnLevels.add(ProbabilityBasedSelection.selectElementBasedOnProbability(probabilityMap));
        }

        List<MetaverseCardProbabilityDO> cardProbabilityDOList = new ArrayList<>();
        for (String drawnLevel : drawnLevels) {
            cardProbabilityDOList.add(ProbabilityBasedSelection.selectRandomElement(allCards.stream()
                    .filter(card -> card.getLevel().equals(drawnLevel))
                    .collect(Collectors.toList())));
        }

        updateDrawRecord(userId, cardProbabilityDOList, count); // 传递抽奖次数
        return convertToResponseList(cardProbabilityDOList);
    }

    private int getDailyDrawCount(Long userId) {
        MetaverseLotteryCardRecordDO record = lotteryCardRecordService.lambdaQuery()
                .eq(MetaverseLotteryCardRecordDO::getUserId, userId)
                .one();

        if (record == null || record.getLastDrawTime() == null || !isToday(record.getLastDrawTime())) {
            return 0;
        }

        return record.getDailyDrawCount();
    }

    private void updateDrawRecord(Long userId, List<MetaverseCardProbabilityDO> drawnCards, int drawCount) {
        MetaverseLotteryCardRecordDO record = Optional.ofNullable(lotteryCardRecordService.lambdaQuery()
                        .eq(MetaverseLotteryCardRecordDO::getUserId, userId)
                        .last(RepositoryConstant.FOR_UPDATE)
                        .one())
                .orElse(new MetaverseLotteryCardRecordDO()
                        .setUserId(userId)
                        .setDailyDrawCount(0)
                        .setCumulativeDrawCount(0L)
                        .setLastDrawTime(LocalDateTime.now())
                        .setSavedAt(LocalDateTime.now())
                        .setUpdatedAt(LocalDateTime.now())
                        .setVersion(1L));
        // 累计抽奖次数每达到一千次 送吧唧
        if (record.getCumulativeDrawCount() % 1000 + drawCount >= 1000) {
            physicalDistributionService.fillAddress(UserConstant.SYSTEM_ID, new FillAddressReq().setUserId(userId).setItemName(PresentConstant.BA_JI));
        }

        boolean isTodayDraw = isToday(record.getLastDrawTime());
        int dailyDrawCount = isTodayDraw ? record.getDailyDrawCount() + drawCount : drawCount;

        if (dailyDrawCount > MAX_DAILY_DRAWS) {
            throw new IllegalArgumentException("在更新记录时，每日提取限额已超出。.");
        }

        record.setDailyDrawCount(dailyDrawCount)
                .setCumulativeDrawCount(record.getCumulativeDrawCount() + drawnCards.size())
                .setLastDrawTime(LocalDateTime.now());

        List<Long> drawnCardIds = Optional.ofNullable(JSONArray.parseArray(record.getDrawnCardIds(), Long.class))
                .orElseGet(Collections::emptyList);
        drawnCardIds.addAll(drawnCards.stream().map(MetaverseCardProbabilityDO::getId).collect(Collectors.toSet()));
        record.setDrawnCardIds(JSON.toJSONString(drawnCardIds));

        lotteryCardRecordService.saveOrUpdate(record);
    }


    /**
     * 判断给定的 LocalDateTime 是否在今天。
     *
     * @param dateTime 要判断的 LocalDateTime 对象
     * @return 如果 dateTime 在今天，则返回 true；否则返回 false
     */
    private boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate dateTimeDate = dateTime.toLocalDate();
        return dateTimeDate.equals(today);
    }

    public List<CardResp> convertToResponseList(List<MetaverseCardProbabilityDO> drawnCards) {
        return drawnCards.stream()
                .map(card -> new CardResp(card.getId(), card.getName(), card.getLevel()))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public CardResp singleDraw(Long userId) {
        return drawCards(1, userId).get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CardResp> fiveDraws(Long userId) {
        return drawCards(5, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CardResp> tenDraws(Long userId) {
        return drawCards(10, userId);
    }

    public LotteryRecordResp userLotteryRecord(Long currentUserId) {
        MetaverseLotteryCardRecordDO userLotteryRecord = lotteryCardRecordService
                .lambdaQuery()
                .eq(MetaverseLotteryCardRecordDO::getUserId, currentUserId)
                .one();
        LotteryRecordResp response = new LotteryRecordResp();
        if (userLotteryRecord != null) {
            response.setUserId(currentUserId)
                    .setDailyDrawCount(userLotteryRecord.getDailyDrawCount())
                    .setCumulativeDrawCount(userLotteryRecord.getCumulativeDrawCount())
                    .setLastDrawTime(userLotteryRecord.getLastDrawTime());
            String drawnCardIdsJson = userLotteryRecord.getDrawnCardIds();
            if (drawnCardIdsJson != null && !drawnCardIdsJson.isEmpty()) {
                List<Long> cardIds = JSON.parseArray(drawnCardIdsJson, Long.class);
                List<CardResp> cardResps = fetchCardDetails(cardIds);
                response.setDrawnCardIds(cardResps);
            }
        }
        return response;
    }

    public List<CardResp> fetchCardDetails(List<Long> cardIds) {
        // 使用MyBatis Plus的QueryWrapper来构建查询条件
        QueryWrapper<MetaverseCardProbabilityDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", cardIds);

        // 查询数据库获取卡片概率信息
        List<MetaverseCardProbabilityDO> cardProbabilities = cardProbabilityService.list(queryWrapper);

        // 将卡片概率信息转换为CardResp对象列表
        return cardProbabilities.stream()
                .map(cardProbability -> new CardResp(
                        cardProbability.getId(),
                        cardProbability.getName(),
                        cardProbability.getLevel()
                ))
                .collect(Collectors.toList());
    }
}