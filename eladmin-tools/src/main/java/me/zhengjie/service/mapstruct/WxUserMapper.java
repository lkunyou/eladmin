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
package me.zhengjie.service.mapstruct;

import me.zhengjie.domain.WxUser;
import me.zhengjie.service.dto.WxUserCreateDto;
import me.zhengjie.service.dto.WxUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WxUserMapper {

    WxUserDto toDto(WxUser wxUser);

    List<WxUserDto> toDto(List<WxUser> wxUsers);

    WxUser toEntity(WxUserCreateDto dto);

    void updateEntity(WxUserCreateDto dto, @MappingTarget WxUser wxUser);
}
