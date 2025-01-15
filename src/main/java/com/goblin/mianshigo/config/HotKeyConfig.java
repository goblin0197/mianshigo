package com.goblin.mianshigo.config;

import com.jd.platform.hotkey.client.ClientStarter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hotkey")
public class HotKeyConfig {
    /**
     * 应用名称
     */
    private String appName = "app";
    /**
     * 本地缓存最大数量
     */
    private int caffeineSize = 1000;
    /**
     * 批量推送 key 的间隔时间
     */
    private long pushPeriod = 1000L;
    /**
     * Etcd 服务器完整地址
     */
    private String etcdServer = "http://127.0.0.1:2379";

    /**
     * 初始化 hotkey
     */
    @Bean
    public void initHotKey() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName)
                .setCaffeineSize(caffeineSize)
                .setEtcdServer(etcdServer)
                .setPushPeriod(pushPeriod)
                .build();
        starter.startPipeline();
    }
}
