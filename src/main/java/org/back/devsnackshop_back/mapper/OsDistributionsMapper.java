package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.response.OsDistributionsResponse;
import org.back.devsnackshop_back.entity.OsDistributionsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OsDistributionsMapper {
    @Mapping(source = "id", target = "value") // Entity의 id를 Response의 value로 매핑
    @Mapping(target = "text", expression = "java(entity.getDistroName() + \" \" + entity.getVersion())")
    OsDistributionsResponse toResponse(OsDistributionsEntity entity);

}
