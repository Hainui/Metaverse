package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 区服表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-03
 */
@Getter
@Setter
@TableName("region")
@ApiModel(value = "RegionDO对象", description = "区服表")
public class RegionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("区服ID，使用雪花算法生成")
    private Long id;

    @ApiModelProperty("区服名称")
    private String name;

    @ApiModelProperty("区服请求地址列表，JSON类型")
    private String serverLocation;

    @ApiModelProperty("区服创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty("区服创建人 ID")
    private Long createBy;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("最近一次修改时间")
    private LocalDateTime updateAt;

    @ApiModelProperty("版本号")
    private Long version;


}
