package com.metaverse.pay.config;


import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.metaverse.common.config.BeanManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WxPayConfiguration {

    @Bean
    public static WxPayConfig wxPayConfig() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId("your_appid");
        payConfig.setMchId("your_mch_id");
        payConfig.setMchKey("your_mch_key");
        payConfig.setKeyPath("path_to_your_cert.p12"); // 商户证书路径
        payConfig.setTradeType("NATIVE"); // 交易类型，这里使用 NATIVE（原生扫码支付）
        return payConfig;
    }

    @Bean
    public WxPayService wxPayService() {
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(BeanManager.getBean(WxPayConfig.class));
        return wxPayService;
    }
}