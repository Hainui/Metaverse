package com.metaverse.pay.controller;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.metaverse.pay.service.MetaverseWxPayService;
import com.metaverse.pay.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/metaverseWxPay")
public class WxPayController {

    private final MetaverseWxPayService metaverseWxPayService;

    private final WxPayService wxPayService;

    @PostMapping("/qrcode")
    public ResponseEntity<byte[]> generateQRCode(@RequestBody Map<String, String> params) {
        String outTradeNo = "20241026" + System.currentTimeMillis(); // 生成唯一的订单号
        BigDecimal amount = new BigDecimal("1.00"); // 订单金额
        String body = "测试商品";

        try {
            WxPayUnifiedOrderResult result = metaverseWxPayService.createOrder(outTradeNo, amount, body);
            String codeUrl = result.getCodeURL(); // 使用 getQrCode 方法获取二维码链接

            if (codeUrl == null || codeUrl.isEmpty()) {
                throw new RuntimeException("无法获取二维码链接");
            }

            byte[] qrCodeImage = QRCodeGenerator.generateQRCodeImage(codeUrl, 200, 200);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/notify")
    public ResponseEntity<String> handleNotify(@RequestBody String xmlData) {
        try {
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);

            // 处理支付成功逻辑
            if ("SUCCESS".equals(notifyResult.getReturnCode()) && "SUCCESS".equals(notifyResult.getResultCode())) {
                String outTradeNo = notifyResult.getOutTradeNo();
                String transactionId = notifyResult.getTransactionId();

                // 在这里处理业务逻辑，例如更新订单状态等
                System.out.println("支付成功，订单号: " + outTradeNo + ", 微信支付订单号: " + transactionId);

                // 返回微信支付成功的响应
                return ResponseEntity.ok(WxPayNotifyResponse.success("支付成功"));
            } else {
                // 返回微信支付失败的响应
                return ResponseEntity.ok(WxPayNotifyResponse.fail("支付失败"));
            }
        } catch (WxPayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("处理支付通知失败");
        }
    }
}