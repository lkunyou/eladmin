package me.zhengjie.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class WxQueryRequest implements Serializable {

    @NotBlank(message = "openId不能为空")
    @ApiModelProperty(value = "微信OpenID", required = true)
    private String openId;

    @ApiModelProperty(value = "加密签名")
    private String signature;

    @ApiModelProperty(value = "时间戳")
    private String timestamp;
}
