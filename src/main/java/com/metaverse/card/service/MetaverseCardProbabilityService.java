package com.metaverse.card.service;

import com.metaverse.card.db.service.IMetaverseCardProbabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseCardProbabilityService {

    private final IMetaverseCardProbabilityService cardProbabilityService;

}
