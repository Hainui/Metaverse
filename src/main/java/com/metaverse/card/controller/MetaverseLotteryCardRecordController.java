package com.metaverse.card.controller;

import com.metaverse.card.resp.CardResp;
import com.metaverse.card.resp.LotteryRecordResp;
import com.metaverse.card.service.LotteryService;
import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "单抽一次", tags = "1.0.0")
    public Result<CardResp> singleDraw() {
        return Result.success(lotteryService.singleDraw(MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/fiveDraws")
    @ApiOperation(value = "连抽五次", tags = "1.0.0")
    public Result<List<CardResp>> fiveDraws() {
        return Result.success(lotteryService.fiveDraws(MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/tenDraws")
    @ApiOperation(value = "连抽十次", tags = "1.0.0")
    public Result<List<CardResp>> tenDraws() {
        return Result.success(lotteryService.tenDraws(MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/userLotteryRecord")
    @ApiOperation(value = "返回用户抽卡信息", tags = "1.0.0")
    public Result<LotteryRecordResp> userLotteryRecord() {
        return Result.success(lotteryService.userLotteryRecord(MetaverseContextUtil.getCurrentUserId()));
    }
}



