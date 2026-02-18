package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.entity.CloudEntity;
import org.back.devsnackshop_back.entity.OsDistributionsEntity;
import org.back.devsnackshop_back.entity.UserOsInstanceEntity;
import org.back.devsnackshop_back.repository.CloudRepository;
import org.back.devsnackshop_back.repository.OsDistributionsRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserOsInstanceMapper {
    @Autowired
    protected OsDistributionsRepository osRepository;

    @Autowired
    protected CloudRepository cloudRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "label", target = "alias")
    @Mapping(source = "ip", target = "ipAddress")
    @Mapping(source = "port", target = "portNumber")
    @Mapping(source = "osType", target = "serverOsType")

    // ID 기반 조회 메서드 연결
    @Mapping(target = "osId", expression = "java(resolveOs(dto))")
    @Mapping(target = "cloudId", expression = "java(resolveCloud(dto))")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    public abstract UserOsInstanceEntity toEntity(ServerCreateRequest dto);

    // osVersion("1")을 ID로 사용하여 조회
    protected OsDistributionsEntity resolveOs(ServerCreateRequest dto) {
        if (dto.getOsVersion() == null) return null;
        try {
            Long osId = Long.parseLong(dto.getOsVersion());
            return osRepository.findById(osId).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // cloudService("1")를 ID로 사용하여 조회
    protected CloudEntity resolveCloud(ServerCreateRequest dto) {
        if (dto.getCloudService() == null) return null;
        try {
            Long cloudId = Long.parseLong(dto.getCloudService());
            return cloudRepository.findById(cloudId).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}