package com.metaverse.user.controller;


import com.aliyuncs.exceptions.ClientException;
import com.metaverse.common.Utils.AliOSSUtils;
import com.metaverse.common.model.Result;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * <p>
 * 聊天记录表 文件上传和下载路径
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RestController
@RequestMapping("/db/metaverse-chat-record-do")
@RequiredArgsConstructor
public class MetaverseChatRecordController {

    private final AliOSSUtils aliOSSUtils;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传", tags = "1.0.0")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, ClientException {
        String url = aliOSSUtils.upload(file);
        return Result.success(url);
    }
}