package com.heony.flowteam.enterprisedevrecruitmenttask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class FileCheckDto {

    @Schema(name = "FileCheckDto.Response", description = "파일 검증 결과 응답")
    public record Response(
            @Schema(description = "허용된 파일명 목록", example = "[\"image.png\", \"document.pdf\"]")
            List<String> allowedFiles,

            @Schema(description = "차단된 파일명 목록", example = "[\"script.sh\"]")
            List<String> blockedFiles
    ) {
    }
}
