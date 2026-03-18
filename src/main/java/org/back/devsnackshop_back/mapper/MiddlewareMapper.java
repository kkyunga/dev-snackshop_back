package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.middlewareManage.response.MiddlewareListResponse;
import org.back.devsnackshop_back.dto.middlewareManage.response.SimpleMiddlewareListResponse;
import org.back.devsnackshop_back.entity.MiddlewareEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MiddlewareMapper {
    @Mapping(expression = "java(entity.getMiddlewareName() + \" \" + entity.getVersion())", target = "name")
    @Mapping(source = "defaultPath", target = "path")
    SimpleMiddlewareListResponse toResponse(MiddlewareEntity entity);

    @Mapping(target = "name", source = "middlewareName")
    @Mapping(target = "category", source = "middlewareType")
    @Mapping(target = "versions", source = "version")
    @Mapping(target = "defaultPath", source = "defaultPath")
    @Mapping(target = "defaultPort", source = "defaultPort")
    MiddlewareListResponse toMdListResponse(MiddlewareEntity entity);

    default List<String> versionToList(String version) {
        if (version == null) return Collections.emptyList();
        return Collections.singletonList(version);
    }
}
