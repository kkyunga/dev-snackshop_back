package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.response.OsDistributionsResponse;
import org.back.devsnackshop_back.entity.OsDistributionsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OsDistributionsMapper {

    @Mapping(target = "value", expression = "java(entity.getDistroName() + \" \" + entity.getVersion())")
    OsDistributionsResponse toResponse(OsDistributionsEntity entity);

}
