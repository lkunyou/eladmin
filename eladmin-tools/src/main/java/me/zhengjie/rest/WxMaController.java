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
package me.zhengjie.rest;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousAccess;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.config.WxMaProperties;
import me.zhengjie.service.WxUserService;
import me.zhengjie.service.dto.WxAesRequestDto;
import me.zhengjie.service.dto.WxLoginDto;
import me.zhengjie.service.dto.WxUserCreateDto;
import me.zhengjie.service.dto.WxUserDto;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.WxAesUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wx")
@Api(tags = "工具：微信小程序管理")
public class WxMaController {

    private final WxMaService wxMaService;
    private final WxMaProperties wxMaProperties;
    private final WxUserService wxUserService;
    private final WxAesUtils wxAesUtils;

    @AnonymousPostMapping("/login")
    @ApiOperation("微信登录")
    public ResponseEntity<Object> login(@Validated @RequestBody WxLoginDto dto, HttpServletRequest request) throws Exception {
        WxUserDto wxUserDto = wxUserService.login(dto, request);
        Map<String, Object> result = new HashMap<>();
        result.put("user", wxUserDto);
        result.put("openId", wxUserDto.getOpenId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @AnonymousAccess
    @RequestMapping(value = "/aes/verify", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation("AES数据校验接口(支持GET/POST)")
    public ResponseEntity<Object> aesVerify(@RequestBody(required = false) String body,
                                            @RequestParam(required = false) String data,
                                            @RequestParam(required = false) String iv,
                                            @RequestParam(required = false) String sessionKey,
                                            @RequestParam(required = false) String code,
                                            HttpServletRequest request) {
        try {
            String method = request.getMethod();
            String encryptedData;
            String decryptIv;
            String decryptSessionKey;
            String decryptCode;

            if ("POST".equalsIgnoreCase(method)) {
                if (StringUtils.isBlank(body)) {
                    return new ResponseEntity<>("请求体不能为空", HttpStatus.BAD_REQUEST);
                }
                JSONObject jsonBody = JSON.parseObject(body);
                encryptedData = jsonBody.getString("data");
                decryptIv = jsonBody.getString("iv");
                decryptSessionKey = jsonBody.getString("sessionKey");
                decryptCode = jsonBody.getString("code");
            } else {
                encryptedData = data;
                decryptIv = iv;
                decryptSessionKey = sessionKey;
                decryptCode = code;
            }

            if (StringUtils.isBlank(encryptedData)) {
                return new ResponseEntity<>("加密数据不能为空", HttpStatus.BAD_REQUEST);
            }

            JSONObject result = wxAesUtils.verifyAndDecrypt(encryptedData, decryptIv, decryptSessionKey, decryptCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            return new ResponseEntity<>("AES解密失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @AnonymousGetMapping("/userInfo")
    @ApiOperation("获取微信客户信息")
    public ResponseEntity<WxUserDto> getUserInfo(@RequestParam String openId) {
        WxUserDto wxUserDto = wxUserService.getUserInfo(openId);
        return new ResponseEntity<>(wxUserDto, HttpStatus.OK);
    }

    @AnonymousAccess
    @PostMapping("/user/create")
    @ApiOperation("新增微信客户(AES加密)")
    public ResponseEntity<WxUserDto> createWithAes(@Validated @RequestBody WxAesRequestDto dto) {
        JSONObject decryptedData = wxAesUtils.verifyAndDecrypt(
                dto.getEncryptedData(), dto.getIv(), dto.getSessionKey(), dto.getCode());
        
        WxUserCreateDto createDto = new WxUserCreateDto();
        createDto.setOpenId(decryptedData.getString("openId"));
        createDto.setUnionId(decryptedData.getString("unionId"));
        createDto.setNickName(decryptedData.getString("nickName"));
        createDto.setAvatarUrl(decryptedData.getString("avatarUrl"));
        createDto.setGender(decryptedData.getInteger("gender"));
        createDto.setCountry(decryptedData.getString("country"));
        createDto.setProvince(decryptedData.getString("province"));
        createDto.setCity(decryptedData.getString("city"));
        createDto.setLanguage(decryptedData.getString("language"));
        createDto.setPhone(decryptedData.getString("phone"));
        createDto.setRemark(decryptedData.getString("remark"));
        
        WxUserDto wxUserDto = wxUserService.create(createDto);
        return new ResponseEntity<>(wxUserDto, HttpStatus.CREATED);
    }

    @AnonymousAccess
    @PutMapping("/user/update")
    @ApiOperation("更新微信客户(AES加密)")
    public ResponseEntity<WxUserDto> updateWithAes(@Validated @RequestBody WxAesRequestDto dto) {
        JSONObject decryptedData = wxAesUtils.verifyAndDecrypt(
                dto.getEncryptedData(), dto.getIv(), dto.getSessionKey(), dto.getCode());
        
        WxUserCreateDto updateDto = new WxUserCreateDto();
        updateDto.setOpenId(decryptedData.getString("openId"));
        updateDto.setUnionId(decryptedData.getString("unionId"));
        updateDto.setNickName(decryptedData.getString("nickName"));
        updateDto.setAvatarUrl(decryptedData.getString("avatarUrl"));
        updateDto.setGender(decryptedData.getInteger("gender"));
        updateDto.setCountry(decryptedData.getString("country"));
        updateDto.setProvince(decryptedData.getString("province"));
        updateDto.setCity(decryptedData.getString("city"));
        updateDto.setLanguage(decryptedData.getString("language"));
        updateDto.setPhone(decryptedData.getString("phone"));
        updateDto.setRemark(decryptedData.getString("remark"));
        
        WxUserDto wxUserDto = wxUserService.update(updateDto);
        return new ResponseEntity<>(wxUserDto, HttpStatus.OK);
    }

    @AnonymousAccess
    @GetMapping("/user/delete/{id}")
    @ApiOperation("删除微信客户(AES校验)")
    public ResponseEntity<Object> deleteWithAes(@PathVariable Long id,
                                                @RequestParam String encryptedData,
                                                @RequestParam String iv,
                                                @RequestParam(required = false) String sessionKey,
                                                @RequestParam(required = false) String code) {
        wxAesUtils.verifyAndDecrypt(encryptedData, iv, sessionKey, code);
        wxUserService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AnonymousAccess
    @PostMapping("/user/delete/{id}")
    @ApiOperation("删除微信客户(AES校验-POST)")
    public ResponseEntity<Object> deleteWithAesPost(@PathVariable Long id,
                                                    @Validated @RequestBody WxAesRequestDto dto) {
        wxAesUtils.verifyAndDecrypt(dto.getEncryptedData(), dto.getIv(), dto.getSessionKey(), dto.getCode());
        wxUserService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("新增微信客户(后台管理)")
    @ApiOperation("新增微信客户(后台管理)")
    @PostMapping("/user")
    public ResponseEntity<WxUserDto> create(@Validated @RequestBody WxUserCreateDto dto) {
        WxUserDto wxUserDto = wxUserService.create(dto);
        return new ResponseEntity<>(wxUserDto, HttpStatus.CREATED);
    }

    @Log("更新微信客户(后台管理)")
    @ApiOperation("更新微信客户(后台管理)")
    @PutMapping("/user")
    public ResponseEntity<WxUserDto> update(@Validated @RequestBody WxUserCreateDto dto) {
        WxUserDto wxUserDto = wxUserService.update(dto);
        return new ResponseEntity<>(wxUserDto, HttpStatus.OK);
    }

    @Log("删除微信客户(后台管理)")
    @ApiOperation("删除微信客户(后台管理)")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        wxUserService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("查询微信客户列表")
    @GetMapping("/users")
    public ResponseEntity<Object> queryAll(Pageable pageable) {
        List<WxUserDto> list = wxUserService.queryAll(pageable);
        return new ResponseEntity<>(PageUtil.toPage(list, list.size()), HttpStatus.OK);
    }

    @AnonymousAccess
    @PostMapping("/phone/decrypt")
    @ApiOperation("解密微信手机号")
    public ResponseEntity<Object> decryptPhone(@Validated @RequestBody WxAesRequestDto dto) {
        String sessionKey = dto.getSessionKey();
        if (StringUtils.isBlank(sessionKey)) {
            sessionKey = wxAesUtils.getSessionKeyByCode(dto.getCode());
        }
        JSONObject result = wxAesUtils.decryptToJson(dto.getEncryptedData(), sessionKey, dto.getIv());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
