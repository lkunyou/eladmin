package me.zhengjie.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.rest.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.service.WxCustomerService;
import me.zhengjie.service.dto.WxAddCustomerRequest;
import me.zhengjie.service.dto.WxCustomerDto;
import me.zhengjie.service.dto.WxLoginRequest;
import me.zhengjie.service.dto.WxQueryRequest;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Api(tags = "微信小程序客户管理")
@RequestMapping("/api/wx/customer")
public class WxCustomerController {

    private final WxCustomerService wxCustomerService;

    @AnonymousAccess
    @Log("微信小程序登录")
    @ApiOperation("微信小程序登录")
    @PostMapping("/login")
    public ResponseEntity<WxCustomerDto> wxLoginPost(@Validated @RequestBody WxLoginRequest request) {
        return new ResponseEntity<>(wxCustomerService.wxLogin(request), HttpStatus.OK);
    }

    @AnonymousAccess
    @Log("微信小程序登录")
    @ApiOperation("微信小程序登录(GET方式)")
    @GetMapping("/login")
    public ResponseEntity<WxCustomerDto> wxLoginGet(@Validated WxLoginRequest request) {
        return new ResponseEntity<>(wxCustomerService.wxLogin(request), HttpStatus.OK);
    }

    @Log("获取微信客户信息")
    @ApiOperation("获取微信客户信息")
    @PostMapping("/info")
    public ResponseEntity<WxCustomerDto> getCustomerInfoPost(@Validated @RequestBody WxQueryRequest request) {
        return new ResponseEntity<>(wxCustomerService.getCustomerByOpenId(request), HttpStatus.OK);
    }

    @Log("获取微信客户信息")
    @ApiOperation("获取微信客户信息(GET方式)")
    @GetMapping("/info")
    public ResponseEntity<WxCustomerDto> getCustomerInfoGet(@Validated WxQueryRequest request) {
        return new ResponseEntity<>(wxCustomerService.getCustomerByOpenId(request), HttpStatus.OK);
    }

    @AnonymousAccess
    @Log("新增微信客户")
    @ApiOperation("新增微信客户")
    @PostMapping("/add")
    public ResponseEntity<WxCustomerDto> addCustomerPost(@Validated @RequestBody WxAddCustomerRequest request) {
        return new ResponseEntity<>(wxCustomerService.addCustomer(request), HttpStatus.CREATED);
    }

    @AnonymousAccess
    @Log("新增微信客户")
    @ApiOperation("新增微信客户(GET方式)")
    @GetMapping("/add")
    public ResponseEntity<WxCustomerDto> addCustomerGet(@Validated WxAddCustomerRequest request) {
        return new ResponseEntity<>(wxCustomerService.addCustomer(request), HttpStatus.CREATED);
    }
}
