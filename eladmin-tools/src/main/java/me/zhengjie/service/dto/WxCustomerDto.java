package me.zhengjie.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class WxCustomerDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "微信OpenID")
    private String openId;

    @ApiModelProperty(value = "微信UnionID")
    private String unionId;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "头像URL")
    private String avatarUrl;

    @ApiModelProperty(value = "性别 0-未知 1-男 2-女")
    private Integer gender;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "状态 1-正常 0-禁用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;
}
