package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.response.OsDistributionsResponse;
import org.back.devsnackshop_back.entity.OsDistributionsEntity;
import org.back.devsnackshop_back.entity.ServerPurposeEntity;
import org.mapstruct.Mapping;

public interface ServerPurposeMapper {

    @Mapping(target = "text", expression = "java(entity.getServerPurposeValue() + \" \" + entity.getDiscription())")
    @Mapping(source = "id", target = "value")
    OsDistributionsResponse toResponse(ServerPurposeEntity entity);


}
