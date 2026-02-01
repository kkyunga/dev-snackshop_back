package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.entity.ServerEntity;
import org.back.devsnackshop_back.entity.SoftWareEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServerManageMapper {

    // 1. DTO -> ServerEntity 변환 (ID는 자동생성이므로 무시)
    @Mapping(target = "id", ignore = true)
    ServerEntity toServerEntity(ServerCreateRequest request);


    // 2. SoftwareDto -> SoftWareEntity 변환
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serverId", ignore = true)
    SoftWareEntity toSoftwareEntity(ServerCreateRequest.SoftwareDto dto);



    List<SoftWareEntity> toSoftwareEntityList(List<ServerCreateRequest.SoftwareDto> dtos);
 }
