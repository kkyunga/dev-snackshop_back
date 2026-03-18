package org.back.devsnackshop_back.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.dto.serververManage.ServerUpdateRequest;
import org.back.devsnackshop_back.dto.serververManage.response.*;
import org.back.devsnackshop_back.dto.systemLog.ServerConnection;
import org.back.devsnackshop_back.entity.*;
import org.back.devsnackshop_back.mapper.*;
import org.back.devsnackshop_back.repository.*;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
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

    @Transactional
    public void createServer(ServerCreateRequest serverCreateRequest,MultipartFile keyFile, Authentication authentication) {
        Optional<UserOsInstanceEntity> instanceEntity = userOsInstanceRepository.findByIpAddressAndPortNumber(serverCreateRequest.getIp(), Long.parseLong(serverCreateRequest.getPort()));
        if(instanceEntity.isPresent()){
            throw new DataIntegrityViolationException("이미 등록된 IP 주소(" + serverCreateRequest.getIp() + ")입니다.");
        }

        UserOsInstanceEntity serverEntity = userOsInstanceMapper.createEntityFromDto(serverCreateRequest);
        // 2. [수정] 실제 DB에 저장된 유저 정보를 가져옵니다. (ID가 포함된 유저)
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        serverEntity.setUser(userEntity);

        // 3. 파일 처리 로직
        byte[] keyBytes = null;
        if (keyFile != null && !keyFile.isEmpty()) {
            try {
                keyBytes = keyFile.getBytes(); // SSH 테스트를 위해 바이트 미리 확보
            } catch (IOException e) {
                throw new RuntimeException("키 파일을 읽는 중 오류가 발생했습니다.");
            }
            // FileService에서 물리 파일 저장 후 엔티티 "생성" (아직 저장은 안 됨)
            AttachmentEntity fileInfo = fileService.saveFile(keyFile, "keys");

            if (fileInfo != null) {
                // attachment 테이블에 실제 저장하여 ID(PK) 생성
                AttachmentEntity savedFile = attachmentRepository.save(fileInfo);

                // 생성된 파일의 ID를 서버 엔티티에 셋팅
                serverEntity.setAttachmentId(savedFile.getId());
            }
        }

        try {
            log.info("SSH 접속 검증 시작: {}", serverEntity.getIpAddress());
            // executeSshCommand가 성공하면 그대로 진행, 실패하면 Exception 발생
            executeSshCommand(serverEntity, keyBytes, serverEntity.getPassword());
        } catch (JSchException | IOException e) {
            log.error("SSH 접속 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("서버 접속에 실패했습니다. 정보를 다시 확인해주세요. (" + e.getMessage() + ")");
        }

        log.info(serverEntity.toString());
        userOsInstanceRepository.save(serverEntity);
    }

    @Transactional(readOnly = true)
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
                            .cloudService(entity.getCloud().getCloudTypeName())
                            .country(entity.getCountry())
                            .ip(entity.getIpAddress())
                            .port(entity.getPortNumber())
                            .os(entity.getOs().getDistroName() + " " + entity.getOs().getVersion())
                            .middlewares(mdResList)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateServer(ServerUpdateRequest dto, MultipartFile keyFile) {
        // 1. 기존 서버 엔티티 조회
        UserOsInstanceEntity userOs = userOsInstanceRepository.findById(dto.getUserOsId())
                .orElseThrow(() -> new EntityNotFoundException("서버 정보를 찾을 수 없습니다."));

        Long currentId = userOs.getAttachmentId();
        boolean isNewFilePresent = (keyFile != null && !keyFile.isEmpty());

        // 2. 파일 조건별 처리
        if (dto.isKeyFileDelete()) {
            // 기존 파일 삭제 (연결된 파일이 있을 경우)
            if (currentId != null) {
                fileService.deleteFile(currentId);
                userOs.setAttachmentId(null);
            }

            // 새로운 파일이 있으면 추가
            if (isNewFilePresent) {
                AttachmentEntity saved = fileService.saveFile(keyFile, "keys");
                userOs.setAttachmentId(saved.getId());
            }

        } else {
            // isKeyFileDelete == false: 새 파일만 있으면 추가
            if (isNewFilePresent) {
                AttachmentEntity saved = fileService.saveFile(keyFile, "keys");
                userOs.setAttachmentId(saved.getId());
            }
        }

        // 3. 일반 필드 매핑 업데이트
        userOsInstanceMapper.updateEntityFromDto(dto, userOs);
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public ServerDetailInfoResponse findServer(Long id) throws JSchException, IOException {
        UserOsInstanceEntity entity = userOsInstanceRepository.findById(id).orElseThrow( ()-> new EntityNotFoundException("서버를 찾을 수 없습니다."));
        String cpuModel = "";
        String authType ="";
        String fileName = "";
        if (entity.getAttachmentId() != null) {
            // PEM 파일 방식 (파일 서비스에서 실제 파일 바이트를 가져와야 함)
            AttachmentEntity attachmentEntity = attachmentRepository.findById(entity.getAttachmentId()).orElseThrow(
                    () -> new EntityNotFoundException("파일을 찾을 수 없습니다.")
            );
            fileName = attachmentEntity.getOriginFileName();
            String[] splitFileArr = attachmentEntity.getOriginFileName().split("\\.");
            String fileExt = splitFileArr[splitFileArr.length-1];

            if("".equals(fileExt) && !entity.getPassword().isEmpty()) {
                authType = "비밀번호 인증";
            }
            else if("pem".equals(fileExt)) {
                authType ="키파일 인증";
            }

            byte[] pemPrivateKey = fileService.downloadFile(entity.getAttachmentId());
            cpuModel = executeSshCommand(entity, pemPrivateKey, null);
        } else {
            cpuModel = executeSshCommand(entity, null, entity.getPassword());
        }
        return userOsInstanceMapper.toDetailServerInfoResponse(entity, cpuModel,authType, fileName);
    }

    @Transactional
    public void serverRemove(long userOsId,
                             Authentication authentication) {
        try {
            UserEntity user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + authentication.getName()));

            UserOsInstanceEntity userOs = userOsInstanceRepository.findByUserAndId(user, userOsId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자의 서버 정보를 찾을 수 없습니다."));

            installedMiddlewareRepository.deleteByUserOsId(userOs);
            userOsInstanceRepository.deleteById(userOsId);
        } catch (Exception e) {
            throw e;
        }
    }

    private String executeSshCommand(UserOsInstanceEntity entity, byte[] pemKey, String password) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = null;

        // 1. 인증 방식 설정
        if (pemKey != null) {
            // PEM 키 데이터가 있으면 개인키 인증 등록
            jsch.addIdentity("key-auth", pemKey, null, null);
        }

        session = jsch.getSession(entity.getUsername(), entity.getIpAddress(), entity.getPortNumber().intValue());

        if (password != null) {
            // 패스워드가 있으면 패스워드 설정
            session.setPassword(password);
        }

        // 2. 세션 옵션 설정
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(5000); // 타임아웃 5초

        // 3. CPU 모델 정보 추출 명령어 실행
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        // 리눅스에서 CPU 모델명 한 줄만 깔끔하게 가져오는 명령
        channel.setCommand("grep -m 1 'model name' /proc/cpuinfo | awk -F: '{print $2}' | sed 's/^[ \t]*//'");

        InputStream in = channel.getInputStream();
        channel.connect();

        String result = new BufferedReader(new InputStreamReader(in))
                .lines().collect(Collectors.joining(" ")).trim();

        channel.disconnect();
        session.disconnect();

        return result.isEmpty() ? "정보 없음" : result;
    }

    @Transactional(readOnly = true)   // ← 이것만 추가
    public ServerConnection getConnection(Long serverId) {

        UserOsInstanceEntity entity = userOsInstanceRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("서버를 찾을 수 없습니다. id=" + serverId));
        String pemKeyPath = "C:\\dev_storage\\snack_shop\\keys\\5d7161a7-b5fd-4cfa-bdd2-6eb8b41cf67c_server-test_key.pem";
        if(entity.getAttachmentId() != null && "".equals(entity.getPassword()))
        {
            return ServerConnection.withPemPath(
                    entity.getIpAddress(),
                    40022,
                    "vboxuser",
                    pemKeyPath
            );
        }else
        {
            return ServerConnection.withPassword(
                    entity.getIpAddress(),
                    40022,      // ✅
                    "vboxuser",
                    entity.getPassword()
            );
        }
    }
}
