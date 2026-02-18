package org.back.devsnackshop_back.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.dto.serververManage.response.CloudItemResponse;
import org.back.devsnackshop_back.dto.serververManage.response.OsDistributionsResponse;
import org.back.devsnackshop_back.dto.serververManage.response.ServerListResponse;
import org.back.devsnackshop_back.dto.serververManage.response.ServerPurposeResponse;
import org.back.devsnackshop_back.entity.*;
import org.back.devsnackshop_back.mapper.*;
import org.back.devsnackshop_back.repository.*;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServerManageService {
    private final UserRepository userRepository;
    private final InstalledMiddlewareRepository installedMiddlewareRepository;
    private final UserOsInstanceRepository userOsInstanceRepository;

    private final UserOsInstanceMapper  userOsInstanceMapper;
    private final OsDistributionsRepository osDistributionsRepository;
    private final ServerPurposeRepository serverPurposeRepository;
    private final CloudRepository cloudRepository;
    private final AttachmentRepository attachmentRepository;


    private final OsDistributionsMapper osDistributionsMapper;
    private final ServerPurposeMapper serverPurposeMapper;
    private final CloudItemsMapper cloudItemsMapper;
    private final FileService fileService;

    public void createServer(ServerCreateRequest serverCreateRequest,MultipartFile keyFile, Authentication authentication) {


        UserOsInstanceEntity instanceEntity = userOsInstanceRepository.findByIpAddress(serverCreateRequest.getIp());
        if(instanceEntity.getId() !=0){
            throw new DataIntegrityViolationException("이미 등록된 IP 주소(" + serverCreateRequest.getIp() + ")입니다.");
        }



        UserOsInstanceEntity serverEntity = userOsInstanceMapper.toEntity(serverCreateRequest);
        // 2. [수정] 실제 DB에 저장된 유저 정보를 가져옵니다. (ID가 포함된 유저)
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));


        serverEntity.setUser(userEntity);

        // 3. 파일 처리 로직
        if (keyFile != null && !keyFile.isEmpty()) {
            // FileService에서 물리 파일 저장 후 엔티티 "생성" (아직 저장은 안 됨)
            AttachmentEntity fileInfo = fileService.saveFile(keyFile, "keys");

            if (fileInfo != null) {
                // attachment 테이블에 실제 저장하여 ID(PK) 생성
                AttachmentEntity savedFile = attachmentRepository.save(fileInfo);

                // 생성된 파일의 ID를 서버 엔티티에 셋팅
                serverEntity.setAttachmentId(savedFile.getId());
            }
        }
        log.info(serverEntity.toString());
        userOsInstanceRepository.save(serverEntity);


    }

    public List<ServerListResponse> serverList(Authentication authentication) {
        // 1. 사용자 조회 (get() 대신 orElseThrow로 예외 상황 방어)
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found with Email: " + authentication.getName()));

        // 2. 해당 사용자의 OS 인스턴스 목록 조회
        List<UserOsInstanceEntity> userOsEntities = userOsInstanceRepository.findByUser(user);

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

    public boolean validateKeyFile(MultipartFile file, Authentication authentication) {
        if (file == null || file.isEmpty()) {
            log.warn("수신된 파일이 비어있습니다.");
            return false;
        }

        PemReader pemReader = null;
        try {
            // MultipartFile로부터 스트림을 직접 생성
            pemReader = new PemReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            PemObject pemObject = pemReader.readPemObject();

            if (pemObject == null) {
                log.warn("PEM 객체를 읽을 수 없습니다. (헤더/푸터 누락 가능성)");
                return false;
            }

            String type = pemObject.getType().toUpperCase(); // 대소문자 구분 방지
            log.info("검증된 PEM 타입: {}", type);

            // 헤더에 PRIVATE KEY 또는 RSA 문구가 포함되어 있는지 확인
            return type.contains("PRIVATE KEY") || type.contains("RSA");

        } catch (Exception e) {
            log.error("PEM 형식 검증 실패: {}", e.getMessage());
            return false;
        } finally {
            if (pemReader != null) {
                try {
                    pemReader.close();
                } catch (IOException ignored) {
                    // 자원 해제 중 에러는 비즈니스 로직에 영향을 주지 않으므로 무시 가능
                }
            }
        }
    }


    public HashMap<String, Object> getServerSpecItems() {
        List<OsDistributionsEntity> osDiss = osDistributionsRepository.findAll();
        List< ServerPurposeEntity>  purposes= serverPurposeRepository.findAll();
        List<CloudEntity> cloudList = cloudRepository.findAll();


        List<OsDistributionsResponse> osList = osDiss.stream()
                .map(osDistributionsMapper::toResponse)
                .collect(Collectors.toList());


        List<ServerPurposeResponse> serverPurposeList = purposes.stream()
                .map(serverPurposeMapper::toResponse)
                .collect(Collectors.toList());



        List<CloudItemResponse> cloudItemList = cloudList.stream()
                .map(cloudItemsMapper::toResponse)
                .collect(Collectors.toList());



        HashMap<String, Object>  serverSpecItems = new HashMap<>();
        serverSpecItems.put("osList",osList);
        serverSpecItems.put("cloudItemList",cloudItemList);
        serverSpecItems.put("serverPurposeList",serverPurposeList);

        return serverSpecItems;




    }
}
