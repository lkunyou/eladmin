package me.zhengjie.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.WechatCustomer;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.repository.WechatCustomerRepository;
import me.zhengjie.service.WechatService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements WechatService {

    private final WxMaService wxMaService;
    private final WechatCustomerRepository wechatCustomerRepository;

    @Override
    public WxMaJscode2SessionResult wxLogin(String code) throws Exception {
        return wxMaService.jsCode2SessionInfo(code);
    }

    @Override
    public WxMaUserInfo decryptUserInfo(String sessionKey, String encryptedData, String iv) throws Exception {
        return wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
    }

    @Override
    public boolean checkAesSignature(HttpServletRequest request) throws Exception {
        String signature = request.getParameter("signature");
        String rawData = request.getParameter("rawData");
        String sessionKey = request.getParameter("sessionKey");
        return checkAesSignature(rawData, signature, sessionKey);
    }

    @Override
    public boolean checkAesSignature(String rawData, String signature, String sessionKey) throws Exception {
        if (StringUtils.isAnyBlank(rawData, signature, sessionKey)) {
            return false;
        }
        String sha1 = DigestUtils.sha1Hex(rawData + sessionKey);
        return StringUtils.equals(sha1, signature);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WechatCustomer saveOrUpdateWechatCustomer(String openId, String unionId, WxMaUserInfo userInfo) {
        Optional<WechatCustomer> existing = wechatCustomerRepository.findByOpenId(openId);
        WechatCustomer customer;
        if (existing.isPresent()) {
            customer = existing.get();
        } else {
            customer = new WechatCustomer();
            customer.setOpenId(openId);
        }
        customer.setUnionId(unionId);
        customer.setNickName(userInfo.getNickName());
        customer.setAvatarUrl(userInfo.getAvatarUrl());
        customer.setGender(Integer.valueOf(userInfo.getGender()));
        customer.setCountry(userInfo.getCountry());
        customer.setProvince(userInfo.getProvince());
        customer.setCity(userInfo.getCity());
        return wechatCustomerRepository.save(customer);
    }

    @Override
    public WechatCustomer findByOpenId(String openId) {
        return wechatCustomerRepository.findByOpenId(openId)
                .orElseThrow(() -> new BadRequestException("微信客户不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WechatCustomer createWechatCustomer(WechatCustomer wechatCustomer) {
        if (wechatCustomerRepository.existsByOpenId(wechatCustomer.getOpenId())) {
            throw new BadRequestException("该微信用户已存在");
        }
        return wechatCustomerRepository.save(wechatCustomer);
    }

    @Override
    public Map<String, Object> processWxLogin(String code, String encryptedData, String iv) throws Exception {
        WxMaJscode2SessionResult session = wxLogin(code);
        String sessionKey = session.getSessionKey();
        String openId = session.getOpenid();

        WxMaUserInfo userInfo = null;
        if (StringUtils.isNotBlank(encryptedData) && StringUtils.isNotBlank(iv)) {
            userInfo = decryptUserInfo(sessionKey, encryptedData, iv);
            saveOrUpdateWechatCustomer(openId, session.getUnionid(), userInfo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("openId", openId);
        result.put("unionId", session.getUnionid());
        result.put("sessionKey", sessionKey);
        if (userInfo != null) {
            result.put("nickName", userInfo.getNickName());
            result.put("avatarUrl", userInfo.getAvatarUrl());
        }
        return result;
    }
}
