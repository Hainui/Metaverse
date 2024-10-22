package com.metaverse.card.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaverse.card.db.entity.MetaverseCardProbabilityDO;
import com.metaverse.card.db.entity.MetaverseLotteryCardRecordDO;
import com.metaverse.card.db.service.impl.MetaverseCardProbabilityServiceImpl;
import com.metaverse.card.db.service.impl.MetaverseLotteryCardRecordServiceImpl;
import com.metaverse.card.resp.lotteryCardRecordResp;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryService {

    private final MetaverseCardProbabilityServiceImpl cardProbabilityService;
    private final MetaverseLotteryCardRecordServiceImpl lotteryCardRecordService;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    private List<lotteryCardRecordResp> drawCards(int count, Long userId) throws JsonProcessingException {
        List<MetaverseCardProbabilityDO> allCards = cardProbabilityService.list();

        Map<MetaverseCardProbabilityDO, BigDecimal> probabilityMap = allCards.stream()
                .collect(Collectors.toMap(
                        card -> card,
                        MetaverseCardProbabilityDO::getDropRate,
                        (existing, replacement) -> existing
                ));

        List<MetaverseCardProbabilityDO> drawnCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            drawnCards.add(ProbabilityBasedSelection.selectElementBasedOnProbability(probabilityMap));
        }
        updateDrawRecord(userId, drawnCards);
        return convertToResponseList(drawnCards);
    }

    private void updateDrawRecord(Long userId, List<MetaverseCardProbabilityDO> drawnCards) throws JsonProcessingException {
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
        HashSet<Object> drawnCardIdSet = new HashSet<>();
        try {
            drawnCardIdSet = objectMapper.readValue(record.getDrawnCardIds(), HashSet.class);
        } catch (Exception e) {
            log.error("解析已抽到的卡ID集合失败", e);
        }
        drawnCardIdSet.addAll(drawnCards.stream().map(MetaverseCardProbabilityDO::getId).collect(Collectors.toSet()));
        record.setDrawnCardIds(objectMapper.writeValueAsString(drawnCardIdSet));

        lotteryCardRecordService.saveOrUpdate(record);
    }


    public List<lotteryCardRecordResp> convertToResponseList(List<MetaverseCardProbabilityDO> drawnCards) {
        return drawnCards.stream()
                .map(card -> new lotteryCardRecordResp(card.getId(), card.getName(), card.getLevel()))
                .collect(Collectors.toList());
    }

    public List<lotteryCardRecordResp> singleDraw(Long userId) throws JsonProcessingException {
        return drawCards(1, userId);
    }

    public List<lotteryCardRecordResp> fiveDraws(Long userId) throws JsonProcessingException {
        return drawCards(5, userId);
    }

    public List<lotteryCardRecordResp> tenDraws(Long userId) throws JsonProcessingException {
        return drawCards(10, userId);
    }
}