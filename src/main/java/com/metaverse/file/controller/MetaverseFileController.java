package com.metaverse.file.controller;

import com.aliyuncs.exceptions.ClientException;
import com.metaverse.common.Utils.*;
import com.metaverse.common.config.BeanManager;
import com.metaverse.common.constant.UserConstant;
import com.metaverse.common.model.Result;
import com.metaverse.file.FileIdGen;
import com.metaverse.file.db.entity.MetaverseMultimediaFilesDO;
import com.metaverse.file.db.service.IMetaverseMultimediaFilesService;
import com.metaverse.file.req.SignedEncryptedUrlReq;
import com.metaverse.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Validated
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class MetaverseFileController {
    private final IMetaverseMultimediaFilesService metaverseMultimediaFilesService;
    private final UserService userService;
    private final AliOSSUtils aliOSSUtils;

    @PostMapping("/proxy/accessResource")
    @ApiOperation(value = "检验url资源访问权限和时间", tags = "1.0.0")
    public Result<String> getResource(@RequestBody @ApiParam(name = "加签后的路由地址", required = true) @Valid SignedEncryptedUrlReq req) {
        Long currentUserRegionId = MetaverseContextUtil.getCurrentUserRegion().getId();
        return Result.success(UrlEncryptorDecryptor.decryptUrl(SignatureValidator.validateSignedUrl(req.getSignedEncryptedUrl(), currentUserRegionId), currentUserRegionId));
    }

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传", tags = "1.0.0")
    public Result<Long> uploadFile(@RequestParam("file") @ApiParam(name = "文件", required = true) MultipartFile file) throws IOException, ClientException {
        // todo 只允许 图片 视频 音频
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "mp4", "avi", "mov", "wmv", "mp3", "wav", "flac", "aac");
        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("文件格式不允许，只能上传图片、视频和音频文件");
        }
        String url = aliOSSUtils.upload(file);
        Long fileId = BeanManager.getBean(FileIdGen.class).nextId();
        metaverseMultimediaFilesService.save(new MetaverseMultimediaFilesDO()
                .setUploaderId(MetaverseContextUtil.getCurrentUserId())
                .setUploadTime(LocalDateTime.now())
                .setId(fileId)
                .setUrl(UrlEncryptorDecryptor.encryptUrl(url, MetaverseContextUtil.getCurrentUserRegion().getId())));
        return Result.success(fileId);
    }

    @GetMapping("/accessResource")
    @ApiOperation(value = "访问资源", tags = "1.0.0")
    public Result<String> accessResource(@RequestParam(value = "id", required = false) @ApiParam(name = "文件id", required = true) @NotNull(message = "文件id不能为空") Long id) {
        MetaverseMultimediaFilesDO file = metaverseMultimediaFilesService.lambdaQuery().eq(MetaverseMultimediaFilesDO::getId, id).one();
        String encryptedUrl = file.getUrl();
        Long currentUserRegionId = MetaverseContextUtil.getCurrentUserRegion().getId();
        Long regionId = UserConstant.SUPER_ADMINISTRATOR_USER_ID.equals(MetaverseContextUtil.getCurrentUserId()) ? userService.findRegionIdByUserId(file.getUploaderId()) : currentUserRegionId;
        String originalUrl = UrlEncryptorDecryptor.decryptUrl(encryptedUrl, regionId);
        return Result.success(SignatureGenerator.generateSignedUrl(encryptedUrl, currentUserRegionId));
    }
}