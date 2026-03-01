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
import org.back.devsnackshop_back.mapper.CloudItemsMapper;
import org.back.devsnackshop_back.mapper.OsDistributionsMapper;
import org.back.devsnackshop_back.mapper.ServerPurposeMapper;
import org.back.devsnackshop_back.mapper.UserOsInstanceMapper;
import org.back.devsnackshop_back.repository.*;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    // application.properties에 설정된 경로를 가져오는 것을 추천합니다.
    @Value("${file.upload-path}")
    private String uploadPath;


    private final AttachmentRepository attachmentRepository;


    /**
     * 파일 저장 공통 로직
     * @param file 업로드된 파일
     * @param subFolder 저장할 하위 폴더 (예: "keys", "profiles")
     * @return 저장된 파일명
     */
    public AttachmentEntity saveFile(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 1. 파일명 생성
            String originalFileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + "_" + originalFileName;

            // 2. [수정] Path API를 사용하여 경로 생성 (OS에 맞는 구분자 자동 처리)
            Path rootPath = Paths.get(uploadPath, subFolder).normalize();
            File folder = rootPath.toFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 3. 물리 파일 저장
            Path targetPath = rootPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING); // Files.write보다 대용량에 효율적

            log.info("파일 물리 저장 성공: {}", targetPath);

            // 4. AttachmentEntity 객체 생성
            // DB 저장 시에는 슬래시(/)로 통일하는 것이 나중에 리눅스 서버 등에서 관리하기 편합니다.
            String dbFilePath = rootPath.toString().replace("\\", "/");

            return AttachmentEntity.builder()
                    .originFileName(originalFileName)
                    .storedFileName(savedFileName)
                    .filePath(dbFilePath) // 정규화된 경로 저장
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("파일 저장 중 입출력 오류 발생: ", e);
            throw new RuntimeException("파일 저장에 실패했습니다.");
        }
    }

    public byte[] downloadFile(Long attachmentId) {
        // 1. DB에서 파일 정보 조회
        AttachmentEntity attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        try {
            Path path = Paths.get(attachment.getFilePath()).resolve(attachment.getStoredFileName());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패", e);
        }
    }


}
