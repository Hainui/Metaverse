package com.metaverse.card.controller;

import com.metaverse.card.service.MetaverseCardProbabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 抽卡概率表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-10-21 21:54:05
 */
@RestController
@RequestMapping("/metaverseCardProbability")
@RequiredArgsConstructor
public class MetaverseCardProbabilityController {

    private final MetaverseCardProbabilityService cardProbabilityService;

}
