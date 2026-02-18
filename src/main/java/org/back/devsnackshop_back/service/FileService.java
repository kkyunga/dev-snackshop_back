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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@Slf4j
public class FileService {
    // application.properties에 설정된 경로를 가져오는 것을 추천합니다.
    @Value("${file.upload-path}")
    private String uploadPath;

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
            // 1. 파일명 생성 (원본명 + UUID)
            String originalFileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + "_" + originalFileName;

            // 2. 저장 경로 설정 및 폴더 생성
            String fullPath = uploadPath + File.separator + subFolder;
            File folder = new File(fullPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 3. 물리 파일 저장
            Path targetPath = Paths.get(fullPath).resolve(savedFileName);
            Files.write(targetPath, file.getBytes());

            log.info("파일 물리 저장 성공: {}", targetPath);

            // 4. AttachmentEntity 객체 생성 (Builder 사용)
            return AttachmentEntity.builder()
                    .originFileName(originalFileName)
                    .storedFileName(savedFileName)
                    .filePath(fullPath)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("파일 저장 중 입출력 오류 발생: ", e);
            throw new RuntimeException("파일 저장에 실패했습니다.");
        }
    }
}
