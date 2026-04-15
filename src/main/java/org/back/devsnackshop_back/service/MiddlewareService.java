package org.back.devsnackshop_back.service;

import org.back.devsnackshop_back.dto.middlewareManage.response.MiddlewareListResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.middlewareManage.InstallRequest;
import org.back.devsnackshop_back.dto.middlewareManage.response.MiddlewareUserOsListResponse;
import org.back.devsnackshop_back.dto.middlewareManage.response.SimpleMiddlewareListResponse;
import org.back.devsnackshop_back.entity.*;
import org.back.devsnackshop_back.mapper.MiddlewareMapper;
import org.back.devsnackshop_back.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MiddlewareService {
    private final UserOsInstanceRepository userOsInstanceRepository;
    private final InstalledMiddlewareRepository installedMiddlewareRepository;
    private final MiddlewareRepository middlewareRepository;

    private final MiddlewareMapper middlewareMapper;

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void saveMiddlewareInstall(InstallRequest request) {
        UserOsInstanceEntity instance = userOsInstanceRepository.findByIdWithAll(request.getUserOsInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("서버 정보를 찾을 수 없습니다."));

        List<InstalledMiddlewareEntity> entityList = new ArrayList<>();
        for (int i = 0; i < request.getMiddlewares().size(); i++) {
            String name = request.getMiddlewares().get(i);
            String version = (request.getMwVersion() != null && request.getMwVersion().size() > i)
                    ? request.getMwVersion().get(i) : "";

            MiddlewareEntity mw = middlewareRepository.findByMiddlewareNameAndVersion(name, version);

            if (mw == null) {
                log.error("DB에서 미들웨어를 찾을 수 없음: {} - {}", name, version);
                continue; // 혹은 예외 처리
            }

            InstalledMiddlewareEntity mdEntity = InstalledMiddlewareEntity.builder()
                    .userOsId(instance)
                    .middlewareId(mw)
                    .installPath(mw.getDefaultPath())
                    .portNumber(instance.getPortNumber())
                    .build();
            entityList.add(mdEntity);
        }

        if (!entityList.isEmpty()) {
            installedMiddlewareRepository.saveAll(entityList);
            installedMiddlewareRepository.flush(); // 즉시 반영 강제
            log.info("Successfully saved {} entities to DB", entityList.size());
        }
    }

    @Transactional(readOnly = true)
    public List<MiddlewareListResponse> middlewareList() {
        List<MiddlewareEntity> mdList = middlewareRepository.findByMiddlewareList();
        HashMap<String, MiddlewareListResponse> map = new HashMap<>();

        for (MiddlewareEntity md : mdList) {
            if (map.get(md.getMiddlewareName()) == null) {
                MiddlewareListResponse response = MiddlewareListResponse.builder()
                        .name(md.getMiddlewareName())
                        .category(md.getMiddlewareType())
                        .versions(new ArrayList<>(List.of(md.getVersion().split(","))))
                        .defaultPath(md.getDefaultPath())
                        .defaultPort(md.getDefaultPort())
                        .build();
                map.put(md.getMiddlewareName(), response);
            } else {
                MiddlewareListResponse response = map.get(md.getMiddlewareName());
                response.getVersions().add(md.getVersion());
            }
        }

        return new ArrayList<>(map.values());
    }

    @Transactional(readOnly = true)
    public List<MiddlewareUserOsListResponse> middlewareUserOsList(long userOsId) {
        List<MiddlewareUserOsListResponse> result = new ArrayList<>();

        List<InstalledMiddlewareEntity> mdList = installedMiddlewareRepository.findAllByUserOsId(userOsId);

        for (InstalledMiddlewareEntity md : mdList) {
            MiddlewareUserOsListResponse res = MiddlewareUserOsListResponse.builder()
                    .name(md.getMiddlewareId().getMiddlewareName())
                    .type(md.getMiddlewareId().getMiddlewareType())
                    .version(md.getMiddlewareId().getVersion())
                    .path(md.getInstallPath())
                    .build();

            result.add(res);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public HashMap<String, List<SimpleMiddlewareListResponse>> simpleMiddlewareList() {
        HashMap<String, List<SimpleMiddlewareListResponse>> resultMap = new HashMap<>();
        List<MiddlewareEntity> middleware = middlewareRepository.findByIsSimpleInstall("Y");

        List<SimpleMiddlewareListResponse> resultList = middleware.stream()
                .map(middlewareMapper::toResponse)
                .toList();

        resultMap.put("middleware", resultList);

        return resultMap;
    }
}
