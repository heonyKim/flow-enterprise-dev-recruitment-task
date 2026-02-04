package com.heony.flowteam.enterprisedevrecruitmenttask.controller;

import com.heony.flowteam.enterprisedevrecruitmenttask.dto.FileCheckDto;
import com.heony.flowteam.enterprisedevrecruitmenttask.service.FileCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File Check API", description = "파일 확장자 검증 API")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileCheckRestController {

    private final FileCheckService fileCheckService;

    @Operation(summary = "파일 확장자 검증", description = "업로드된 파일들의 확장자가 차단되었는지 검증합니다.")
    @PostMapping(value = "/check", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileCheckDto.Response> checkFiles(
            @RequestPart("files") @Schema(description = "검증할 파일 목록") MultipartFile[] files
    ) {
        FileCheckDto.Response response = fileCheckService.checkFiles(files);
        return ResponseEntity.ok(response);
    }
}
