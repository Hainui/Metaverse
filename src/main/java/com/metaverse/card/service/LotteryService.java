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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryService {

    private final IMetaverseCardProbabilityService cardProbabilityService;
    private final IMetaverseLotteryCardRecordService lotteryCardRecordService;

    public List<CardResp> drawCards(int count, Long userId) {
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

        LocalDate today = LocalDate.now();
        LocalDate lastDrawDate = record.getLastDrawTime() != null ? record.getLastDrawTime().toLocalDate() : null;

        int dailyDrawCount = lastDrawDate != null && !lastDrawDate.equals(today) ? drawCount : record.getDailyDrawCount() + drawCount;

        record.setDailyDrawCount(dailyDrawCount)
                .setCumulativeDrawCount(record.getCumulativeDrawCount() + drawnCards.size())
                .setLastDrawTime(LocalDateTime.now());

        List<Long> drawnCardIds = Optional.ofNullable(JSONArray.parseArray(record.getDrawnCardIds(), Long.class))
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);
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
        // 获取今天的日期
        LocalDate today = LocalDate.now();

        // 获取给定时间的日期部分
        LocalDate dateTimeDate = dateTime.toLocalDate();

        // 比较日期是否相同
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