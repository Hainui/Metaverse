package com.metaverse.common.schedule;

import com.metaverse.common.Utils.RedisServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

//登陆令牌(寻时)
@EnableScheduling
@Configuration
@Slf4j
@RequiredArgsConstructor
public class LoginSchedule {

    private final RedisServer redisServer;

    @Scheduled(cron = "0 0/2 * * * ?")//轮循任务 每两分钟执行一次
    public void run() {
        try {
            //让轮训不能一直发送,免得封号
//            if (new Random().nextBoolean()) {
//                log.info("随机打烊中...");
//                return;
//            }
            //设置工作时间
//            GregorianCalendar calendar = new GregorianCalendar();
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            if (hour > 22 || hour < 7) {
//                logger.info("打烊时间不工作，AI 下班了！");
//                return;
//            }
            // 删除所有过期的token，并且获取到当前最新的token
            Map<Long, String> tokenMap = redisServer.getAllTokens();
            // todo tokenMap可以用于统计生成当前在线用户信息
        } catch (Exception e) {
            log.error("处理登陆令牌时发生异常", e);

        }
    }
}
