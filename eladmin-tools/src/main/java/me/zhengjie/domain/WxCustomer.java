package me.zhengjie.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "wx_customer")
@ApiModel(value = "微信客户")
public class WxCustomer implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "open_id", unique = true, nullable = false)
    @ApiModelProperty(value = "微信OpenID")
    private String openId;

    @Column(name = "union_id")
    @ApiModelProperty(value = "微信UnionID")
    private String unionId;

    @Column(name = "nick_name")
    @ApiModelProperty(value = "昵称")
    private String nickName;

    @Column(name = "avatar_url")
    @ApiModelProperty(value = "头像URL")
    private String avatarUrl;

    @Column(name = "gender")
    @ApiModelProperty(value = "性别 0-未知 1-男 2-女")
    private Integer gender;

    @Column(name = "country")
    @ApiModelProperty(value = "国家")
    private String country;

    @Column(name = "province")
    @ApiModelProperty(value = "省份")
    private String province;

    @Column(name = "city")
    @ApiModelProperty(value = "城市")
    private String city;

    @Column(name = "language")
    @ApiModelProperty(value = "语言")
    private String language;

    @Column(name = "phone")
    @ApiModelProperty(value = "手机号")
    private String phone;

    @Column(name = "session_key")
    @ApiModelProperty(value = "微信会话密钥")
    private String sessionKey;

    @Column(name = "status")
    @ApiModelProperty(value = "状态 1-正常 0-禁用")
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;
}
