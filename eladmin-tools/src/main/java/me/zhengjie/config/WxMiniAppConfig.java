package me.zhengjie.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMiniAppConfig {

    private String appid;
    private String secret;
    private String token;
    private String aesKey;
}
