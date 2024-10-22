package com.metaverse.card.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.metaverse.card.resp.CardResp;
import com.metaverse.card.service.LotteryService;
import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 抽奖记录表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-10-21 22:02:55
 */
@RestController
@RequestMapping("/metaverseLotteryCardRecord")
@RequiredArgsConstructor
@Validated
public class MetaverseLotteryCardRecordController {

    private final LotteryService lotteryService;

    @GetMapping("/singleDraw")
    public Result<CardResp> singleDraw() throws JsonProcessingException {
        return Result.success(lotteryService.singleDraw(MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/fiveDraws")
    public List<CardResp> fiveDraws() throws JsonProcessingException {
        return lotteryService.fiveDraws(MetaverseContextUtil.getCurrentUserId());
    }

    @GetMapping("/tenDraws")
    public List<CardResp> tenDraws() throws JsonProcessingException {
        return lotteryService.tenDraws(MetaverseContextUtil.getCurrentUserId());
    }
}
