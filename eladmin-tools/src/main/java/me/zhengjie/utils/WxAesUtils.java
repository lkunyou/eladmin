/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.utils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class WxAesUtils {

    private final WxMaService wxMaService;

    public String decryptData(String encryptedData, String sessionKey, String iv) {
        try {
            byte[] sessionKeyBytes = Base64.decodeBase64(sessionKey);
            byte[] ivBytes = Base64.decodeBase64(iv);
            byte[] encryptedBytes = Base64.decodeBase64(encryptedData);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new BadRequestException("AES解密失败: " + e.getMessage());
        }
    }

    public JSONObject decryptToJson(String encryptedData, String sessionKey, String iv) {
        String decryptedData = decryptData(encryptedData, sessionKey, iv);
        return JSON.parseObject(decryptedData);
    }

    public String getSessionKeyByCode(String code) {
        try {
            WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);
            return session.getSessionKey();
        } catch (Exception e) {
            log.error("获取sessionKey失败", e);
            throw new BadRequestException("获取sessionKey失败: " + e.getMessage());
        }
    }

    public String getOpenIdByCode(String code) {
        try {
            WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);
            return session.getOpenid();
        } catch (Exception e) {
            log.error("获取openId失败", e);
            throw new BadRequestException("获取openId失败: " + e.getMessage());
        }
    }

    public WxMaUserInfo getUserInfo(String sessionKey, String encryptedData, String iv) {
        try {
            return wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            throw new BadRequestException("获取用户信息失败: " + e.getMessage());
        }
    }

    public WxMaPhoneNumberInfo getPhoneNumber(String sessionKey, String encryptedData, String iv) {
        try {
            return wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        } catch (Exception e) {
            log.error("获取手机号失败", e);
            throw new BadRequestException("获取手机号失败: " + e.getMessage());
        }
    }

    public JSONObject verifyAndDecrypt(String encryptedData, String iv, String sessionKey, String code) {
        String decryptSessionKey = sessionKey;
        
        if (StringUtils.isBlank(decryptSessionKey)) {
            if (StringUtils.isBlank(code)) {
                throw new BadRequestException("sessionKey和code不能同时为空");
            }
            decryptSessionKey = getSessionKeyByCode(code);
        }
        
        return decryptToJson(encryptedData, decryptSessionKey, iv);
    }
}
