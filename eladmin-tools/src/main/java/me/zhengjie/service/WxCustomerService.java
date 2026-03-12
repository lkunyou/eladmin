package me.zhengjie.service;

import me.zhengjie.domain.WxCustomer;
import me.zhengjie.service.dto.WxAddCustomerRequest;
import me.zhengjie.service.dto.WxCustomerDto;
import me.zhengjie.service.dto.WxLoginRequest;
import me.zhengjie.service.dto.WxQueryRequest;

public interface WxCustomerService {

    /**
     * 微信小程序登录
     *
     * @param request 登录请求参数
     * @return 客户信息
     */
    WxCustomerDto wxLogin(WxLoginRequest request);

    /**
     * 根据OpenID获取客户信息
     *
     * @param request 查询请求参数
     * @return 客户信息
     */
    WxCustomerDto getCustomerByOpenId(WxQueryRequest request);

    /**
     * 新增微信客户
     *
     * @param request 客户信息
     * @return 客户信息
     */
    WxCustomerDto addCustomer(WxAddCustomerRequest request);

    /**
     * 根据OpenID查询客户实体
     *
     * @param openId 微信OpenID
     * @return 客户实体
     */
    WxCustomer findByOpenId(String openId);
}
