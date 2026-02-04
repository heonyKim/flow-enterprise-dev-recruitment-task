package com.heony.flowteam.enterprisedevrecruitmenttask.controller;

import com.heony.flowteam.enterprisedevrecruitmenttask.dto.BlockedExtensionDto;
import com.heony.flowteam.enterprisedevrecruitmenttask.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ExtensionClientController {

    @Value("${spring.servlet.multipart.max-request-size}")
    private DataSize dataSize;

    private final ExtensionService extensionService;

    @GetMapping("/")
    public String index(Model model) {

        // 고정 확장자 목록
        Set<String> fixedExtensions = extensionService.getFixedExtensions();

        // 등록가능한 커스텀 확장자 최대 개수
        String maxCustomExtensionsCount = extensionService.getMaxCustomExtensionsCount();
        
        // 현재 DB에 저장된 차단된 확장자 목록
        List<BlockedExtensionDto.Response> blockedList = extensionService.getAllBlockedExtensions();
        
        // 고정 확장자 중 체크된 것들 가공
        Set<String> checkedFixedExtensions = blockedList.stream()
                .map(BlockedExtensionDto.Response::extension)
                .filter(fixedExtensions::contains)
                .collect(Collectors.toSet());

        // 커스텀 확장자 목록 (고정 확장자가 아닌 것들)
        List<BlockedExtensionDto.Response> customExtensions = blockedList.stream()
                .filter(dto -> !fixedExtensions.contains(dto.extension()))
                .toList();

        model.addAttribute("maxUploadBytesSize", dataSize.toBytes());
        model.addAttribute("fixedExtensions", fixedExtensions);
        model.addAttribute("maxCustomExtensionsCount", maxCustomExtensionsCount);
        model.addAttribute("checkedFixedExtensions", checkedFixedExtensions);
        model.addAttribute("customExtensions", customExtensions);
        model.addAttribute("customCount", customExtensions.size());
        
        return "index";
    }
}
