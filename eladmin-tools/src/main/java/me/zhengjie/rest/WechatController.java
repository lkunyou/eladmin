package me.zhengjie.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousAccess;
import me.zhengjie.domain.WechatCustomer;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.WechatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wechat")
@Api(tags = "工具：微信小程序管理")
public class WechatController {

    private final WechatService wechatService;

    @AnonymousAccess
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation("微信登录")
    public ResponseEntity<Object> wxLogin(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String encryptedData,
            @RequestParam(required = false) String iv,
            @RequestBody(required = false) Map<String, Object> body,
            HttpServletRequest request) throws Exception {

        String finalCode = code;
        String finalEncryptedData = encryptedData;
        String finalIv = iv;

        if (body != null) {
            finalCode = body.get("code") != null ? body.get("code").toString() : code;
            finalEncryptedData = body.get("encryptedData") != null ? body.get("encryptedData").toString() : encryptedData;
            finalIv = body.get("iv") != null ? body.get("iv").toString() : iv;
        }

        if (finalCode == null) {
            throw new BadRequestException("code不能为空");
        }

        Map<String, Object> result = wechatService.processWxLogin(finalCode, finalEncryptedData, finalIv);
        return ResponseEntity.ok(result);
    }

    @AnonymousAccess
    @RequestMapping(value = "/checkSignature", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation("AES签名校验")
    public ResponseEntity<Object> checkSignature(
            @RequestParam(required = false) String rawData,
            @RequestParam(required = false) String signature,
            @RequestParam(required = false) String sessionKey,
            @RequestBody(required = false) Map<String, Object> body,
            HttpServletRequest request) throws Exception {

        String finalRawData = rawData;
        String finalSignature = signature;
        String finalSessionKey = sessionKey;

        if (body != null) {
            finalRawData = body.get("rawData") != null ? body.get("rawData").toString() : rawData;
            finalSignature = body.get("signature") != null ? body.get("signature").toString() : signature;
            finalSessionKey = body.get("sessionKey") != null ? body.get("sessionKey").toString() : sessionKey;
        }

        boolean valid = wechatService.checkAesSignature(finalRawData, finalSignature, finalSessionKey);
        Map<String, Object> result = new HashMap<>();
        result.put("valid", valid);
        result.put("message", valid ? "签名校验通过" : "签名校验失败");
        return ResponseEntity.ok(result);
    }

    @AnonymousAccess
    @RequestMapping(value = "/customer/info", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation("获取微信客户信息")
    public ResponseEntity<Object> getCustomerInfo(
            @RequestParam(required = false) String openId,
            @RequestBody(required = false) Map<String, Object> body) {

        String finalOpenId = openId;
        if (body != null && body.get("openId") != null) {
            finalOpenId = body.get("openId").toString();
        }

        if (finalOpenId == null) {
            throw new BadRequestException("openId不能为空");
        }

        WechatCustomer customer = wechatService.findByOpenId(finalOpenId);
        return ResponseEntity.ok(customer);
    }

    @AnonymousAccess
    @RequestMapping(value = "/customer", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation("新增微信客户")
    public ResponseEntity<Object> createCustomer(
            @RequestParam(required = false) String openId,
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) String avatarUrl,
            @RequestBody(required = false) WechatCustomer wechatCustomer) {

        WechatCustomer customer;
        if (wechatCustomer != null) {
            customer = wechatCustomer;
        } else {
            customer = new WechatCustomer();
            customer.setOpenId(openId);
            customer.setNickName(nickName);
            customer.setAvatarUrl(avatarUrl);
        }

        WechatCustomer savedCustomer = wechatService.createWechatCustomer(customer);
        return ResponseEntity.ok(savedCustomer);
    }
}
