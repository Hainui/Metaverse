package com.metaverse.card.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.metaverse.card.db.entity.MetaverseCardProbabilityDO;
import com.metaverse.card.db.entity.MetaverseLotteryCardRecordDO;
import com.metaverse.card.db.service.IMetaverseCardProbabilityService;
import com.metaverse.card.db.service.IMetaverseLotteryCardRecordService;
import com.metaverse.card.resp.CardResp;
import com.metaverse.card.utils.ProbabilityBasedSelection;
import com.metaverse.common.constant.RepositoryConstant;
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

        boolean isTodayDraw = isToday(record.getLastDrawTime());
        int dailyDrawCount = isTodayDraw ? record.getDailyDrawCount() + drawCount : drawCount;

        if (dailyDrawCount > MAX_DAILY_DRAWS) {
            throw new IllegalArgumentException("Daily draw limit exceeded while updating record.");
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

    @Transactional
    public CardResp singleDraw(Long userId) {
        return drawCards(1, userId).get(0);
    }

    @Transactional
    public List<CardResp> fiveDraws(Long userId) {
        return drawCards(5, userId);
    }

    @Transactional
    public List<CardResp> tenDraws(Long userId) {
        return drawCards(10, userId);
    }
}