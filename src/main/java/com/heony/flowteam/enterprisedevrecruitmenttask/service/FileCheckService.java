package com.heony.flowteam.enterprisedevrecruitmenttask.service;

import com.heony.flowteam.enterprisedevrecruitmenttask.domain.BlockedExtension;
import com.heony.flowteam.enterprisedevrecruitmenttask.dto.FileCheckDto;
import com.heony.flowteam.enterprisedevrecruitmenttask.repository.BlockedExtensionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeType;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@NullMarked
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileCheckService {

    private final BlockedExtensionRepository blockedExtensionRepository;
    private final Tika tika = new Tika();

    public FileCheckDto.Response checkFiles(MultipartFile[] files) {
        List<String> blockedExtensions = blockedExtensionRepository.findAll().stream()
                .map(BlockedExtension::getExtension)
                .toList();

        List<String> allowedFiles = new ArrayList<>();
        List<String> blockedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                String extension = getExtension(fileName);

                // 확장자가 없는 경우 Tika로 Mime Type을 가져온 뒤 검증하기 (악의적으로 확장자를 없앤 뒤 업로드를 하는 경우를 방지하기 위함)
                if (StringUtils.isBlank(extension)) {
                    extension = detectExtension(file);
                }

                if (StringUtils.isNotBlank(extension) && blockedExtensions.contains(extension)) {
                    blockedFiles.add(fileName);
                } else {
                    allowedFiles.add(fileName);
                }
            }
        }

        return new FileCheckDto.Response(
                allowedFiles,
                blockedFiles
        );

    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private String detectExtension(MultipartFile file) {
        try {
            String mimeType = tika.detect(file.getInputStream());
            TikaConfig config = TikaConfig.getDefaultConfig();
            MimeType mime = config.getMimeRepository().forName(mimeType);
            String ext = mime.getExtension();

            // Tika는 확장자를 ".jpg" 형태로 반환하므로 앞의 점을 제거해야 함
            if (StringUtils.isNotBlank(ext) && ext.startsWith(".")) {
                return ext.substring(1).toLowerCase();
            }
            return "";
        } catch (Exception e) {
            // MIME 타입 감지 실패 시 빈 문자열 반환
            return "";
        }
    }
}
