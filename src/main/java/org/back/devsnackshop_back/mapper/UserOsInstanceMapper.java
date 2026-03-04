package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.dto.serververManage.response.ServerDetailInfoResponse;
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

    // 🚨 핵심 수정 부분: target을 엔티티의 새로운 필드명(os, cloud)으로 변경
    @Mapping(target = "os", expression = "java(resolveOs(dto))")
    @Mapping(target = "cloud", expression = "java(resolveCloud(dto))")
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


    @Mapping(source = "entity.alias", target = "label")
    @Mapping(source = "entity.ipAddress", target = "ip")
    @Mapping(source = "entity.portNumber", target = "port")
    @Mapping(source = "entity.serverOsType", target = "osType")

    // 엔티티 구조에 따른 매핑 (os.distroName + os.version 조합)
    @Mapping(target = "osVersion", expression = "java(entity.getOs().getDistroName() + \" \" + entity.getOs().getVersion())")

    // cloud.cloudTypeName 매핑
    @Mapping(source = "entity.cloud.cloudTypeName", target = "cloudService")

    // 외부 파라미터 매핑 (이름을 cpuInfo로 일치시켰습니다)
    @Mapping(source = "cpuInfo", target = "cpuInfo")
    @Mapping(source = "authType", target = "authType")

    // 나머지 필드들
    @Mapping(source = "entity.country", target = "country")
    @Mapping(source = "entity.username", target = "username")
    @Mapping(source = "entity.password", target = "password")
    public abstract ServerDetailInfoResponse toDetailServerInfoResponse(UserOsInstanceEntity entity, String cpuInfo,String authType);}