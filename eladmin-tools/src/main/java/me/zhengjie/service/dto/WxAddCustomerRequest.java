package me.zhengjie.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class WxAddCustomerRequest implements Serializable {

    @NotBlank(message = "openId不能为空")
    @ApiModelProperty(value = "微信OpenID", required = true)
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

    @ApiModelProperty(value = "加密签名")
    private String signature;

    @ApiModelProperty(value = "时间戳")
    private String timestamp;
}
