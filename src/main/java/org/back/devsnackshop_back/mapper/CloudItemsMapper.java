package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.response.CloudItemResponse;
import org.back.devsnackshop_back.dto.serververManage.response.OsDistributionsResponse;
import org.back.devsnackshop_back.entity.CloudEntity;
import org.back.devsnackshop_back.entity.OsDistributionsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CloudItemsMapper {
    @Mapping(source = "id", target = "value") // Entity의 id -> Response의 value
    @Mapping(source = "cloudTypeName", target = "text") // Entity의 cloudTypeName -> Response의 text
    CloudItemResponse toResponse(CloudEntity entity);


}
