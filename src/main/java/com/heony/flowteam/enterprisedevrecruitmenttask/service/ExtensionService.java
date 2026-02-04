package com.heony.flowteam.enterprisedevrecruitmenttask.service;

import com.heony.flowteam.enterprisedevrecruitmenttask._common.exception.CustomException;
import com.heony.flowteam.enterprisedevrecruitmenttask._common.exception.ErrorCode;
import com.heony.flowteam.enterprisedevrecruitmenttask.domain.BlockedExtension;
import com.heony.flowteam.enterprisedevrecruitmenttask.dto.BlockedExtensionDto;
import com.heony.flowteam.enterprisedevrecruitmenttask.repository.BlockedExtensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NullMarked
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtensionService {

    private final BlockedExtensionRepository blockedExtensionRepository;

    // 고정 확장자 목록
    @Value("${extension.fixed-extensions}")
    private Set<String> FIXED_EXTENSIONS;

    // 커스텀 확장자 개수 제한
    @Value("${extension.max-custom-extensions-count:-1}")
    private int MAX_CUSTOM_EXTENSIONS_COUNT = 200;

    /**
     * 모든 차단된 확장자 목록 조회
     */
    public List<BlockedExtensionDto.Response> getAllBlockedExtensions() {
        return blockedExtensionRepository.findAll().stream()
                .map(BlockedExtensionDto.Response::from)
                .collect(Collectors.toList());
    }

    /**
     * 고정 확장자 목록 조회 (화면 표시용)
     */
    public Set<String> getFixedExtensions() {
        return FIXED_EXTENSIONS;
    }

    /**
     * 커스텀 확장자 개수 제한 조회 (화면 표시용)
     * */
    public String getMaxCustomExtensionsCount() {
        return MAX_CUSTOM_EXTENSIONS_COUNT < 0 ? "-" : ""+MAX_CUSTOM_EXTENSIONS_COUNT;
    }


    /**
     * 확장자 차단 추가
     */
    @Transactional
    public BlockedExtensionDto.Response addExtension(String extension) {
        String targetExt = extension.toLowerCase().trim();

        // 1. 이미 존재하는지 확인
        if (blockedExtensionRepository.existsByExtension(targetExt)) {
            throw new CustomException(ErrorCode.ALREADY_BLOCKED_EXTENSION);
        }

        // 2. 커스텀 확장자인 경우 개수 제한 확인 (고정 확장자가 아닌 경우)
        if (!FIXED_EXTENSIONS.contains(targetExt)) {
            long currentCount = blockedExtensionRepository.count();

            long customCount = blockedExtensionRepository.findAll().stream()
                    .filter(e -> !FIXED_EXTENSIONS.contains(e.getExtension()))
                    .count();
            if ((MAX_CUSTOM_EXTENSIONS_COUNT > 0) && (customCount >= MAX_CUSTOM_EXTENSIONS_COUNT)) {
                throw new CustomException(ErrorCode.EXCEEDED_MAX_CUSTOM_EXTENSION_COUNT);
            }
        }

        // 3. 저장
        BlockedExtension saved = blockedExtensionRepository.save(BlockedExtension.create(targetExt));
        return BlockedExtensionDto.Response.from(saved);
    }

    /**
     * 확장자 차단 해제 (삭제)
     */
    @Transactional
    public void removeExtension(String extension) {
        String targetExt = extension.toLowerCase().trim();
        
        if (!blockedExtensionRepository.existsByExtension(targetExt)) {
            throw new CustomException(ErrorCode.NOT_FOUND, true, "확장자:"+targetExt);
        }
        
        blockedExtensionRepository.deleteByExtension(targetExt);
    }

}
