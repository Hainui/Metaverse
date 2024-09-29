package com.metaverse.user.resp;

import com.metaverse.user.domain.MetaverseUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class MetaverseGroupRequestResp {

    @ApiModelProperty(value = "身份证编号,唯一标识id")
    private Long userId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "出生时间")
    private LocalDateTime birthTime;

    @ApiModelProperty(value = "性别")
    private MetaverseUser.Gender gender;

    @ApiModelProperty(value = "头像文件ID")
    private Long avatarFileId;

    @ApiModelProperty("请求状态，0表示待处理，1表示同意，2表示拒绝")
    private Integer status;

    @ApiModelProperty("附带的消息")
    private String requestMessage;

    @ApiModelProperty("请求创建时间")
    private LocalDateTime requestTime;

}
