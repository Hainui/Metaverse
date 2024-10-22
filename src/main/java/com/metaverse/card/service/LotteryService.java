package com.metaverse.card.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.metaverse.card.db.entity.MetaverseCardProbabilityDO;
import com.metaverse.card.db.entity.MetaverseLotteryCardRecordDO;
import com.metaverse.card.db.service.IMetaverseCardProbabilityService;
import com.metaverse.card.db.service.impl.MetaverseLotteryCardRecordServiceImpl;
import com.metaverse.card.resp.CardResp;
import com.metaverse.card.utils.ProbabilityBasedSelection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryService {

    private final IMetaverseCardProbabilityService cardProbabilityService;
    private final MetaverseLotteryCardRecordServiceImpl lotteryCardRecordService;

    @Transactional(rollbackFor = Exception.class)
    public List<CardResp> drawCards(int count, Long userId) throws JsonProcessingException {
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
        // 根据抽到的级别获取具体的卡片
        List<MetaverseCardProbabilityDO> drawnCards = allCards.stream()
                .filter(card -> drawnLevels.contains(card.getLevel()))
                .collect(Collectors.toList());
        if (!drawnCards.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(drawnCards.size());
            MetaverseCardProbabilityDO randomCard = drawnCards.get(randomIndex);
        }
        updateDrawRecord(userId, drawnCards);
        return convertToResponseList(drawnCards);
    }

    private void updateDrawRecord(Long userId, List<MetaverseCardProbabilityDO> drawnCards) {
        MetaverseLotteryCardRecordDO record = lotteryCardRecordService.lambdaQuery().eq(MetaverseLotteryCardRecordDO::getUserId, userId).one();
        if (record == null) {
            record = new MetaverseLotteryCardRecordDO()
                    .setUserId(userId)
                    .setDailyDrawCount(0)
                    .setCumulativeDrawCount(0L)
                    .setDrawnCardIds("[]")
                    .setLastDrawTime(LocalDateTime.now())
                    .setSavedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now())
                    .setVersion(1L);
        }
        record.setDailyDrawCount(record.getDailyDrawCount() + 1)
                .setCumulativeDrawCount(record.getCumulativeDrawCount() + 1)
                .setLastDrawTime(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        HashSet<Long> drawnCardIdSet = JSON.parseObject(record.getDrawnCardIds(), HashSet.class);
        drawnCardIdSet.addAll(drawnCards.stream().map(MetaverseCardProbabilityDO::getId).collect(Collectors.toSet()));
        record.setDrawnCardIds(JSON.toJSONString(drawnCardIdSet));
        lotteryCardRecordService.saveOrUpdate(record);
    }


    public List<CardResp> convertToResponseList(List<MetaverseCardProbabilityDO> drawnCards) {
        return drawnCards.stream()
                .map(card -> new CardResp(card.getId(), card.getName(), card.getLevel()))
                .collect(Collectors.toList());
    }

    public CardResp singleDraw(Long userId) throws JsonProcessingException {
        return drawCards(1, userId).get(0);
    }

    public List<CardResp> fiveDraws(Long userId) throws JsonProcessingException {
        return drawCards(5, userId);
    }

    public List<CardResp> tenDraws(Long userId) throws JsonProcessingException {
        return drawCards(10, userId);
    }
}