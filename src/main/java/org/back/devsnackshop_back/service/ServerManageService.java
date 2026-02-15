package org.back.devsnackshop_back.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.dto.serververManage.response.ServerListResponse;
import org.back.devsnackshop_back.entity.*;
import org.back.devsnackshop_back.mapper.ServerManageMapper;
import org.back.devsnackshop_back.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServerManageService {
    private final ServerRepository serverRepository;
    private final SoftWareRepository softWareRepository;
    private final ServerManageMapper serverManageMapper;

    private final UserRepository userRepository;
    private final InstalledMiddlewareRepository installedMiddlewareRepository;
    private final UserOsInstanceRepository userOsInstanceRepository;

    public void createServer(ServerCreateRequest request) {

    }

    public List<ServerListResponse> serverList(Authentication authentication) {
        // 1. 사용자 조회 (get() 대신 orElseThrow로 예외 상황 방어)
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found with Email: " + authentication.getName()));

        // 2. 해당 사용자의 OS 인스턴스 목록 조회
        List<UserOsInstanceEntity> userOsEntities = userOsInstanceRepository.findByUserId(user);

        // 3. Stream API를 이용한 변환 로직
        return userOsEntities.stream()
                .map(entity -> {
                    // 각 인스턴스에 설치된 미들웨어 목록 조회 및 DTO 변환
                    List<String> mdResList = installedMiddlewareRepository
                            .findAllByUserOsId(entity.getId())
                            .stream()
                            .map(a -> a.getMiddlewareId().getMiddlewareName()
                                    + " "
                                    + a.getMiddlewareId().getVersion())
                            .toList();


                    // ServerListResponse 빌드
                    return ServerListResponse.builder()
                            .id(entity.getId())
                            .label(entity.getAlias())
                            .cloudService(entity.getCloudId().getCloudTypeName())
                            .country(entity.getCountry())
                            .ip(entity.getIpAddress())
                            .port(entity.getPortNumber())
                            .os(entity.getOsId().getDistroName() + " " + entity.getOsId().getVersion())
                            .middlewares(mdResList)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
