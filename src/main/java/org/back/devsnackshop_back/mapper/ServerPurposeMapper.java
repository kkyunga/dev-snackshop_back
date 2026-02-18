package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.response.ServerPurposeResponse;
import org.back.devsnackshop_back.entity.ServerPurposeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServerPurposeMapper {

    // getDiscription -> getDescription 으로 수정
    @Mapping(target = "text", expression = "java(entity.getServerPurposeValue() + \"(\" + entity.getDescription() + \")\")")
    @Mapping(source = "id", target = "value") // Entity의 id를 Response의 value로 매핑
    ServerPurposeResponse toResponse(ServerPurposeEntity entity);

}