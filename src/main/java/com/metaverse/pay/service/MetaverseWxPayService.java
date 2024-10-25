package com.metaverse.pay.service;


import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseWxPayService {

    private final WxPayService wxPayService;

    public WxPayUnifiedOrderResult createOrder(String outTradeNo, BigDecimal amount, String body) throws Exception {
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setBody(body);
        request.setOutTradeNo(outTradeNo);
        request.setTotalFee(amount.multiply(BigDecimal.valueOf(100)).intValue()); // 单位为分
        request.setSpbillCreateIp("127.0.0.1"); // 客户端 IP 地址
        request.setNotifyUrl("https://yourdomain.com/metaverseWxPay/notify"); // 支付结果通知 URL
        request.setTradeType("NATIVE");

        return wxPayService.createOrder(request);
    }
}