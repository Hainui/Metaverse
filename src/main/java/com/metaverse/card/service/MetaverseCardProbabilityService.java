package com.metaverse.card.service;

import com.metaverse.card.db.entity.MetaverseCardProbabilityDO;
import com.metaverse.card.db.service.IMetaverseCardProbabilityService;
import com.metaverse.card.req.CardTypeReq;
import com.metaverse.card.resp.CardInfoResP;
import com.metaverse.card.resp.CardLevelInfoResP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseCardProbabilityService {

    private final IMetaverseCardProbabilityService cardProbabilityService;

    public boolean addCardType(CardTypeReq card, Long userId) throws Exception {
        boolean levelExists = cardProbabilityService
                .lambdaQuery()
                .eq(MetaverseCardProbabilityDO::getLevel, card.getLevel())
                .exists();
        if (!levelExists) {
            throw new Exception("卡片级别必须在库中已经存在");
        }
        boolean nameUnique = cardProbabilityService
                .lambdaQuery()
                .eq(MetaverseCardProbabilityDO::getName, card.getName())
                .exists();
        if (!nameUnique) {
            throw new Exception("卡片名称不能和数据库中的其他卡片名称相同");
        }
        MetaverseCardProbabilityDO cardProbabilityDO = new MetaverseCardProbabilityDO()
                .setName(card.getName())
                .setLevel(card.getLevel())
                .setCreatedAt(LocalDateTime.now())
                .setUpdateBy(userId)
                .setVersion(1L);
        return cardProbabilityService.save(cardProbabilityDO);

    }

    public List<CardLevelInfoResP> getCardInfo() {
        List<MetaverseCardProbabilityDO> cardProbabilities = cardProbabilityService.list();
        List<String> cardLevelOrder = Arrays.asList("C", "S", "R", "SR", "SSR", "L");
        Map<String, List<CardInfoResP>> cardInfoMapByLevel = new LinkedHashMap<>();
        cardLevelOrder.forEach(level -> cardInfoMapByLevel.put(level, new ArrayList<>()));
        cardProbabilities.forEach(cardProbability -> {
            String cardLevel = cardProbability.getLevel();
            CardInfoResP cardInfoResP = new CardInfoResP(cardProbability.getId(), cardProbability.getName(), cardProbability.getDropRate());
            cardInfoMapByLevel.get(cardLevel).add(cardInfoResP);
        });
        return cardLevelOrder.stream()
                .map(level -> new CardLevelInfoResP(level, cardInfoMapByLevel.get(level)))
                .collect(Collectors.toList());
    }
}
