package com.heony.flowteam.enterprisedevrecruitmenttask._common;

import com.heony.flowteam.enterprisedevrecruitmenttask.domain.BlockedExtension;
import com.heony.flowteam.enterprisedevrecruitmenttask.repository.BlockedExtensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final BlockedExtensionRepository blockedExtensionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (blockedExtensionRepository.count() == 0) {
            log.info("테스트데이터 초기화 중..");
            // 고정 확장자 중 일부 차단
            blockedExtensionRepository.save(BlockedExtension.create("exe"));
            blockedExtensionRepository.save(BlockedExtension.create("bat"));
            
            // 커스텀 확장자 예시
            blockedExtensionRepository.save(BlockedExtension.create("sh"));
            blockedExtensionRepository.save(BlockedExtension.create("py"));
            log.info("테스트데이터 초기화 완료!");
        }
    }
}
