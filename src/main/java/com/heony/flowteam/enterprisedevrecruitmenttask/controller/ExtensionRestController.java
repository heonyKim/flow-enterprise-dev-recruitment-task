package com.heony.flowteam.enterprisedevrecruitmenttask.controller;

import com.heony.flowteam.enterprisedevrecruitmenttask.dto.BlockedExtensionDto;
import com.heony.flowteam.enterprisedevrecruitmenttask.service.ExtensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Extension API", description = "파일 확장자 차단 관리 API")
@RestController
@RequestMapping("/api/v1/extensions")
@RequiredArgsConstructor
public class ExtensionRestController {

    private final ExtensionService extensionService;

    @Operation(summary = "차단된 확장자 목록 조회", description = "DB에 저장된 모든 차단 확장자 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<List<BlockedExtensionDto.Response>> getBlockedExtensions() {
        return ResponseEntity.ok(extensionService.getAllBlockedExtensions());
    }

    @Operation(summary = "확장자 차단 추가", description = "고정 확장자 체크 또는 커스텀 확장자 추가 시 호출됩니다.")
    @PostMapping
    public ResponseEntity<BlockedExtensionDto.Response> addExtension(
            @Valid @RequestBody BlockedExtensionDto.CreateRequest request) {
        // 컨트롤러 레벨 검증: 서비스로 넘기기 전 기본적인 값 존재 여부는 @Valid로 처리됨
        // 서비스 호출
        BlockedExtensionDto.Response response = extensionService.addExtension(request.extension());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "확장자 차단 해제", description = "확장자 차단을 해제(삭제)합니다.")
    @DeleteMapping("/{extension}")
    public ResponseEntity<Void> removeExtension(@PathVariable @Schema(description = "확장자 명", example = "sh") String extension) {
        extensionService.removeExtension(extension);
        return ResponseEntity.noContent().build();
    }
}
