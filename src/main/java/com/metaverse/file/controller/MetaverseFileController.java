package com.metaverse.file.controller;

import com.aliyuncs.exceptions.ClientException;
import com.metaverse.common.Utils.*;
import com.metaverse.common.config.BeanManager;
import com.metaverse.common.constant.UserConstant;
import com.metaverse.common.model.Result;
import com.metaverse.file.FileIdGen;
import com.metaverse.file.db.entity.MetaverseMultimediaFilesDO;
import com.metaverse.file.db.service.IMetaverseMultimediaFilesService;
import com.metaverse.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class MetaverseFileController {

//    private static final String REAL_RESOURCE_URL = "https://example.com/real-resource-url";

    private final IMetaverseMultimediaFilesService metaverseMultimediaFilesService;

    private final UserService userService;

    private final AliOSSUtils aliOSSUtils;

    @GetMapping("/proxy/accessResource")
    public Mono<ResponseEntity<byte[]>> getResource(@RequestParam("signedUrl") String signedUrl) {
        return WebClient.create()
                .get()
                .uri(SignatureValidator.validateSignedUrl(signedUrl, MetaverseContextUtil.getCurrentUserRegion().getId()))
                .retrieve()
                .bodyToMono(byte[].class)
                .map(ResponseEntity::ok);
    }


    @PostMapping("/upload")
    @ApiOperation(value = "文件上传", tags = "1.0.0")
    public Result<Long> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, ClientException {
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
    public Result<String> accessResource(@RequestParam("id") Long id) {
        MetaverseMultimediaFilesDO file = metaverseMultimediaFilesService.lambdaQuery().eq(MetaverseMultimediaFilesDO::getId, id).one();
        String encryptedUrl = file.getUrl();
        if (UserConstant.SUPER_ADMINISTRATOR_USER_ID.equals(MetaverseContextUtil.getCurrentUserId())) {
            Long uploaderRegionId = userService.findRegionIdByUserId(file.getUploaderId());
            String originalUrl = UrlEncryptorDecryptor.decryptUrl(encryptedUrl, uploaderRegionId);
            return Result.success(SignatureGenerator.generateSignedUrl(originalUrl, UserConstant.SUPER_ADMINISTRATOR_REGION_ID));
        } else {
            Long currentUserRegionId = MetaverseContextUtil.getCurrentUserRegion().getId();
            String originalUrl = UrlEncryptorDecryptor.decryptUrl(encryptedUrl, currentUserRegionId);
            return Result.success(SignatureGenerator.generateSignedUrl(originalUrl, currentUserRegionId));
        }
    }
}