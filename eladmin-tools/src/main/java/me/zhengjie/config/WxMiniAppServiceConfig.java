package me.zhengjie.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxMiniAppServiceConfig {

    @Autowired
    private WxMiniAppConfig wxMiniAppConfig;

    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(wxMiniAppConfig.getAppid());
        config.setSecret(wxMiniAppConfig.getSecret());
        config.setToken(wxMiniAppConfig.getToken());
        config.setAesKey(wxMiniAppConfig.getAesKey());

        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}
