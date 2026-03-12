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
package me.zhengjie.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.RequiredArgsConstructor;
import me.zhengjie.config.WxMaProperties;
import me.zhengjie.domain.WxUser;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.repository.WxUserRepository;
import me.zhengjie.service.WxUserService;
import me.zhengjie.service.dto.WxLoginDto;
import me.zhengjie.service.dto.WxUserCreateDto;
import me.zhengjie.service.dto.WxUserDto;
import me.zhengjie.service.mapstruct.WxUserMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WxUserServiceImpl implements WxUserService {

    private final WxMaService wxMaService;
    private final WxMaProperties wxMaProperties;
    private final WxUserRepository wxUserRepository;
    private final WxUserMapper wxUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxUserDto login(WxLoginDto dto, HttpServletRequest request) throws Exception {
        WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(dto.getCode());
        String openId = session.getOpenid();
        String sessionKey = session.getSessionKey();
        
        Optional<WxUser> wxUserOptional = wxUserRepository.findByOpenId(openId);
        WxUser wxUser;
        
        if (wxUserOptional.isPresent()) {
            wxUser = wxUserOptional.get();
            wxUser.setSessionKey(sessionKey);
        } else {
            wxUser = new WxUser();
            wxUser.setOpenId(openId);
            wxUser.setSessionKey(sessionKey);
        }
        
        if (dto.getEncryptedData() != null && dto.getIv() != null) {
            WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, dto.getEncryptedData(), dto.getIv());
            wxUser.setNickName(userInfo.getNickName());
            wxUser.setAvatarUrl(userInfo.getAvatarUrl());
            wxUser.setGender(Integer.valueOf(userInfo.getGender()));
            wxUser.setCountry(userInfo.getCountry());
            wxUser.setProvince(userInfo.getProvince());
            wxUser.setCity(userInfo.getCity());
            wxUser.setLanguage(userInfo.getLanguage());
            if (userInfo.getUnionId() != null) {
                wxUser.setUnionId(userInfo.getUnionId());
            }
        }
        
        wxUser = wxUserRepository.save(wxUser);
        return wxUserMapper.toDto(wxUser);
    }

    @Override
    public WxUserDto getUserInfo(String openId) {
        WxUser wxUser = wxUserRepository.findByOpenId(openId)
                .orElseThrow(() -> new BadRequestException("微信用户不存在"));
        return wxUserMapper.toDto(wxUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxUserDto create(WxUserCreateDto dto) {
        if (wxUserRepository.findByOpenId(dto.getOpenId()).isPresent()) {
            throw new BadRequestException("该OpenId已存在");
        }
        WxUser wxUser = wxUserMapper.toEntity(dto);
        wxUser = wxUserRepository.save(wxUser);
        return wxUserMapper.toDto(wxUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxUserDto update(WxUserCreateDto dto) {
        WxUser wxUser = wxUserRepository.findByOpenId(dto.getOpenId())
                .orElseThrow(() -> new BadRequestException("微信用户不存在"));
        wxUserMapper.updateEntity(dto, wxUser);
        wxUser = wxUserRepository.save(wxUser);
        return wxUserMapper.toDto(wxUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        wxUserRepository.deleteById(id);
    }

    @Override
    public List<WxUserDto> queryAll(Pageable pageable) {
        return wxUserMapper.toDto(wxUserRepository.findAll(pageable).getContent());
    }

    @Override
    public WxUser findByOpenId(String openId) {
        return wxUserRepository.findByOpenId(openId).orElse(null);
    }
}
