package com.metaverse.card.service;

import com.metaverse.card.CardIdGen;
import com.metaverse.card.db.entity.MetaverseCardProbabilityDO;
import com.metaverse.card.db.service.IMetaverseCardProbabilityService;
import com.metaverse.card.req.CardTypeReq;
import com.metaverse.card.resp.CardInfoResp;
import com.metaverse.card.resp.CardLevelInfoResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseCardProbabilityService {

    private final IMetaverseCardProbabilityService cardProbabilityService;
    private final CardIdGen cardIdGen;

    public boolean addCardType(CardTypeReq card, Long userId) {
        boolean levelExists = cardProbabilityService
                .lambdaQuery()
                .eq(MetaverseCardProbabilityDO::getLevel, card.getLevel())
                .exists();
        if (!levelExists) {
            throw new IllegalArgumentException("卡片级别必须在库中已经存在");
        }
        boolean nameUnique = cardProbabilityService
                .lambdaQuery()
                .eq(MetaverseCardProbabilityDO::getName, card.getName())
                .exists();
        if (!nameUnique) {
            throw new IllegalArgumentException("卡片名称不能和数据库中的其他卡片名称相同");
        }
        return cardProbabilityService.save(new MetaverseCardProbabilityDO()
                .setId(cardIdGen.nextId())
                .setName(card.getName())
                .setLevel(card.getLevel())
                .setCreatedAt(LocalDateTime.now())
                .setCreateBy(userId)
                .setVersion(1L));
    }

    public List<CardLevelInfoResp> cardView() {
        Map<String, List<MetaverseCardProbabilityDO>> map = cardProbabilityService.list()
                .stream()
                .collect(Collectors.groupingBy(MetaverseCardProbabilityDO::getLevel));
        List<CardLevelInfoResp> cardLevelInfoResps = new ArrayList<>();
        map.forEach((key, value) -> cardLevelInfoResps.add(new CardLevelInfoResp(key, value.stream().map(v -> new CardInfoResp(v.getId(), v.getName())).collect(Collectors.toList()), value.get(0).getDropRate())));
        return cardLevelInfoResps;

//        List<String> cardLevelOrder = Arrays.asList("C", "S", "R", "SR", "SSR", "L");
//        Map<String, List<CardInfoResp>> cardInfoMapByLevel = new LinkedHashMap<>();
//        cardLevelOrder.forEach(level -> cardInfoMapByLevel.put(level, new ArrayList<>()));
//        cardProbabilities.forEach(cardProbability -> {
//            String cardLevel = cardProbability.getLevel();
//            CardInfoResp cardInfoResP = new CardInfoResp(cardProbability.getId(), cardProbability.getName());
//            cardInfoMapByLevel.get(cardLevel).add(cardInfoResP);
//        });
//        return cardLevelOrder.stream()
//                .map(level -> new CardLevelInfoResp(level, cardInfoMapByLevel.get(level)))
//                .collect(Collectors.toList());
    }
}
