package me.zhengjie.service;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import me.zhengjie.domain.WechatCustomer;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface WechatService {

    WxMaJscode2SessionResult wxLogin(String code) throws Exception;

    WxMaUserInfo decryptUserInfo(String sessionKey, String encryptedData, String iv) throws Exception;

    boolean checkAesSignature(HttpServletRequest request) throws Exception;

    boolean checkAesSignature(String rawData, String signature, String sessionKey) throws Exception;

    WechatCustomer saveOrUpdateWechatCustomer(String openId, String unionId, WxMaUserInfo userInfo);

    WechatCustomer findByOpenId(String openId);

    WechatCustomer createWechatCustomer(WechatCustomer wechatCustomer);

    Map<String, Object> processWxLogin(String code, String encryptedData, String iv) throws Exception;
}
