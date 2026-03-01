package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.middlewareManage.response.SimpleMiddlewareListResponse;
import org.back.devsnackshop_back.entity.MiddlewareEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MiddlewareMapper {
    @Mapping(expression = "java(entity.getMiddlewareName() + \" \" + entity.getVersion())", target = "name")
    @Mapping(source = "defaultPath", target = "path")
    SimpleMiddlewareListResponse toResponse(MiddlewareEntity entity);
}
