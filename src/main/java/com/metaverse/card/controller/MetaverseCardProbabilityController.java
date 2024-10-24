package com.metaverse.card.controller;

import com.metaverse.card.req.CardTypeReq;
import com.metaverse.card.resp.CardLevelInfoResp;
import com.metaverse.card.service.MetaverseCardProbabilityService;
import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
@Validated
public class MetaverseCardProbabilityController {

    private final MetaverseCardProbabilityService cardProbabilityService;

    @GetMapping("/cardView")
    @ApiOperation(value = "卡片视图", tags = "1.0.0")
    public Result<List<CardLevelInfoResp>> cardView() {
        return Result.success(cardProbabilityService.cardView());
    }

    @PostMapping("/addCardType")
    @ApiOperation(value = "新增卡的种类", tags = "1.0.0")
    public Result<Boolean> addCardType(@RequestBody @Valid @ApiParam(name = "新增卡片参数", required = true) CardTypeReq card) {
        return Result.success(cardProbabilityService.addCardType(card, MetaverseContextUtil.getCurrentUserId()));
    }
}
