package me.zhengjie.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class WxLoginRequest implements Serializable {

    @NotBlank(message = "code不能为空")
    @ApiModelProperty(value = "微信登录临时凭证", required = true)
    private String code;

    @ApiModelProperty(value = "加密数据")
    private String encryptedData;

    @ApiModelProperty(value = "偏移量")
    private String iv;

    @ApiModelProperty(value = "原始数据")
    private String rawData;

    @ApiModelProperty(value = "签名")
    private String signature;
}
