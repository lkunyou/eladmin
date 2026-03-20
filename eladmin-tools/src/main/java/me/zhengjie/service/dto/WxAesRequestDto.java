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
package me.zhengjie.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class WxAesRequestDto implements Serializable {

    @NotBlank(message = "加密数据不能为空")
    @ApiModelProperty(value = "AES加密后的数据(Base64编码)", required = true)
    private String encryptedData;

    @NotBlank(message = "iv不能为空")
    @ApiModelProperty(value = "加密算法初始向量(Base64编码)", required = true)
    private String iv;

    @ApiModelProperty(value = "会话密钥(Base64编码)，与code二选一")
    private String sessionKey;

    @ApiModelProperty(value = "微信登录code，与sessionKey二选一")
    private String code;
}
