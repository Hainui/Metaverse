package com.metaverse.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * {
 * "recipient": "张三",
 * "street": "北京市海淀区中关村大街1号院",
 * "city": "北京市",
 * "province": "北京",
 * "postal_code": "100080"
 * }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DeliveryAddressDto implements Serializable {
    /**
     * 收货人姓名
     */
    private String recipient;
    /**
     * 城市
     */
    private String city;
    /**
     * 街道
     */
    private String street;
    /**
     * 省
     */
    private String province;
    /**
     * 邮政编码
     */
    private String postalCode;
}
