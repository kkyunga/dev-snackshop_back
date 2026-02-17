package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.entity.OsDistributionsEntity;
import org.back.devsnackshop_back.entity.UserOsInstanceEntity;
import org.back.devsnackshop_back.repository.OsDistributionsRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserOsInstanceMapper {
    @Autowired
    protected OsDistributionsRepository osRepository;

    @Mapping(target = "id", ignore = true) // 생성 시 ID는 무시
    @Mapping(source = "label", target = "alias")
    @Mapping(source = "ip", target = "ipAddress")
    @Mapping(source = "port", target = "portNumber")
    @Mapping(source = "osType", target = "serverOsType") // osType("Linux") -> serverOsType

    @Mapping(target="osId", expression = "java(resolveOs(dto))")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    public abstract UserOsInstanceEntity toEntity(ServerCreateRequest dto);


    public OsDistributionsEntity resolveOs(ServerCreateRequest dto) {
        if(dto.getOsType() == null || dto.getOsVersion() == null){
            return null;
        }
        //OsType과 osVersion으로 Db에서 해당하는 OsDistributionsEntity 반환
        return (OsDistributionsEntity) osRepository.findByDistroNameAndVersion(dto.getOsType(),dto.getOsVersion()).orElse(null);
    }

}
