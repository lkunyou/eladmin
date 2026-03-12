package me.zhengjie.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.domain.WxCustomer;
import me.zhengjie.service.dto.WxCustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WxCustomerMapper extends BaseMapper<WxCustomerDto, WxCustomer> {
}
