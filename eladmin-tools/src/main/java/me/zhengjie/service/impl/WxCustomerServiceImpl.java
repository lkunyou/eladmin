package me.zhengjie.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.zhengjie.config.WxMiniAppConfig;
import me.zhengjie.domain.WxCustomer;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.repository.WxCustomerRepository;
import me.zhengjie.service.WxCustomerService;
import me.zhengjie.service.dto.WxAddCustomerRequest;
import me.zhengjie.service.dto.WxCustomerDto;
import me.zhengjie.service.dto.WxLoginRequest;
import me.zhengjie.service.dto.WxQueryRequest;
import me.zhengjie.service.mapstruct.WxCustomerMapper;
import me.zhengjie.utils.WxAesUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WxCustomerServiceImpl implements WxCustomerService {

    private final WxMaService wxMaService;
    private final WxCustomerRepository wxCustomerRepository;
    private final WxCustomerMapper wxCustomerMapper;
    private final WxAesUtils wxAesUtils;
    private final WxMiniAppConfig wxMiniAppConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxCustomerDto wxLogin(WxLoginRequest request) {
        try {
            // 调用微信接口获取session_key和openid
            WxMaJscode2SessionResult session = wxMaService.getUserService()
                    .getSessionInfo(request.getCode());

            if (session == null || session.getOpenid() == null) {
                throw new BadRequestException("微信登录失败，无法获取用户信息");
            }

            String openId = session.getOpenid();
            String sessionKey = session.getSessionKey();
            String unionId = session.getUnionid();

            // 查询用户是否已存在
            WxCustomer customer = wxCustomerRepository.findByOpenId(openId)
                    .orElse(new WxCustomer());

            // 如果是新用户或需要更新用户信息
            if (customer.getId() == null) {
                customer.setOpenId(openId);
                customer.setUnionId(unionId);
                customer.setSessionKey(sessionKey);
                customer.setCreateTime(Timestamp.from(Instant.now()));
            } else {
                // 更新session_key
                customer.setSessionKey(sessionKey);
                if (unionId != null && customer.getUnionId() == null) {
                    customer.setUnionId(unionId);
                }
            }

            // 如果有加密数据，解密获取用户信息
            if (request.getEncryptedData() != null && request.getIv() != null) {
                try {
                    WxMaUserInfo userInfo = wxMaService.getUserService()
                            .getUserInfo(sessionKey, request.getEncryptedData(), request.getIv());

                    if (userInfo != null) {
                        customer.setNickName(userInfo.getNickName());
                        customer.setAvatarUrl(userInfo.getAvatarUrl());
                        customer.setGender(parseGender(userInfo.getGender()));
                        customer.setCountry(userInfo.getCountry());
                        customer.setProvince(userInfo.getProvince());
                        customer.setCity(userInfo.getCity());
                        customer.setLanguage(userInfo.getLanguage());
                    }
                } catch (Exception e) {
                    log.warn("解密用户信息失败: {}", e.getMessage());
                }
            }

            // 验证签名（如果提供了）
            if (request.getRawData() != null && request.getSignature() != null) {
                boolean valid = checkUserInfo(sessionKey, request.getRawData(), request.getSignature());
                if (!valid) {
                    log.warn("用户数据签名验证失败");
                }
            }

            customer.setUpdateTime(Timestamp.from(Instant.now()));
            WxCustomer saved = wxCustomerRepository.save(customer);
            return wxCustomerMapper.toDto(saved);

        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BadRequestException("微信登录失败: " + e.getMessage());
        }
    }

    @Override
    public WxCustomerDto getCustomerByOpenId(WxQueryRequest request) {
        // 验证签名
        validateRequest(request.getOpenId(), request.getSignature(), request.getTimestamp());

        WxCustomer customer = wxCustomerRepository.findByOpenId(request.getOpenId())
                .orElseThrow(() -> new BadRequestException("客户不存在"));
        return wxCustomerMapper.toDto(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxCustomerDto addCustomer(WxAddCustomerRequest request) {
        // 验证签名
        validateRequest(request.getOpenId(), request.getSignature(), request.getTimestamp());

        // 检查是否已存在
        if (wxCustomerRepository.existsByOpenId(request.getOpenId())) {
            throw new BadRequestException("该微信客户已存在");
        }

        WxCustomer customer = new WxCustomer();
        customer.setOpenId(request.getOpenId());
        customer.setUnionId(request.getUnionId());
        customer.setNickName(request.getNickName());
        customer.setAvatarUrl(request.getAvatarUrl());
        customer.setGender(request.getGender());
        customer.setCountry(request.getCountry());
        customer.setProvince(request.getProvince());
        customer.setCity(request.getCity());
        customer.setLanguage(request.getLanguage());
        customer.setPhone(request.getPhone());
        customer.setStatus(1);
        customer.setCreateTime(Timestamp.from(Instant.now()));
        customer.setUpdateTime(Timestamp.from(Instant.now()));

        WxCustomer saved = wxCustomerRepository.save(customer);
        return wxCustomerMapper.toDto(saved);
    }

    @Override
    public WxCustomer findByOpenId(String openId) {
        return wxCustomerRepository.findByOpenId(openId).orElse(null);
    }

    /**
     * 验证请求签名
     * 使用微信SDK SHA1 进行签名验证
     */
    private void validateRequest(String data, String signature, String timestamp) {
        if (signature == null || timestamp == null) {
            // 如果未提供签名，可以选择跳过验证或抛出异常
            // 这里选择跳过，允许未签名的请求
            return;
        }

        try {
            // 构造待验证的字符串（与前端约定好的格式）
            String source = data + timestamp + wxMiniAppConfig.getAesKey();
            // 使用微信SDK SHA1 方法生成签名
            String calculatedSignature = SHA1.gen(source);

            if (!calculatedSignature.equals(signature)) {
                throw new BadRequestException("签名验证失败");
            }
        } catch (Exception e) {
            throw new BadRequestException("签名验证失败: " + e.getMessage());
        }
    }

    /**
     * 验证用户数据签名
     *
     * @param sessionKey 会话密钥
     * @param rawData    原始数据
     * @param signature  签名
     * @return 是否验证通过
     */
    private boolean checkUserInfo(String sessionKey, String rawData, String signature) {
        try {
            String source = rawData + sessionKey;
            String calculatedSignature = SHA1.gen(source);
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析性别
     */
    private Integer parseGender(Object gender) {
        if (gender == null) {
            return 0;
        }
        try {
            return Integer.parseInt(gender.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
