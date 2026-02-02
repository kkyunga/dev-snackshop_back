package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.entity.ServerEntity;
import org.back.devsnackshop_back.entity.SoftWareEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServerManageMapper {

    // 1. DTO -> Entity 변환
    // 필드명이 동일하므로 설정 없이 자동 매핑됩니다.
    @Mapping(target = "id", ignore = true)
    ServerEntity toEntity(ServerCreateRequest request);

    // 2. Entity -> DTO 변환 (이 부분에서 에러가 났던 것)
    ServerCreateRequest toDto(ServerEntity entity);

    // DTO 내부의 SoftwareDto -> Entity 변환용
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serverEntity", ignore = true)
    SoftWareEntity toSoftwareEntity(ServerCreateRequest.SoftwareDto dto);
    // Entity 내부의 SoftWareEntity -> DTO 변환용 (에러 해결의 핵심)
    ServerCreateRequest.SoftwareDto toSoftwareDto(SoftWareEntity entity);

    //매핑 끝나면 실행 자식한테 부모 셋팅 해주기
    @AfterMapping
    default  void linkParent(@MappingTarget ServerEntity serverEntity){
        if(serverEntity.getSoftWareEntityList()!=null){
            serverEntity.getSoftWareEntityList().forEach(sw -> sw.setServerEntity(serverEntity));
        }
    }
}
