package com.heony.flowteam.enterprisedevrecruitmenttask.dto;

import com.heony.flowteam.enterprisedevrecruitmenttask.domain.BlockedExtension;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public class BlockedExtensionDto {

    @Schema(name = "BlockedExtensionDtoCreateRequest", description = "확장자 추가 요청 DTO")
    public record CreateRequest(
            @Schema(description = "차단할 파일 확장자 (최대 20자)", example = "sh")
            @NotBlank(message = "확장자는 필수 입력 값입니다.")
            @Size(max = 20, message = "확장자는 최대 20자까지 입력 가능합니다.")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "확장자는 영문자와 숫자만 가능합니다.")
            String extension
    ) {}

    @Schema(name = "BlockedExtensionDtoResponse", description = "확장자 정보 응답 DTO")
    public record Response(
            @Schema(description = "고유 ID (UUID v7)", example = "{UUID}")
            UUID id,

            @Schema(description = "확장자 명", example = "sh")
            String extension,

            @Schema(description = "생성 일시")
            Instant createdAt
    ) {
        public static Response from(BlockedExtension entity) {
            return new Response(entity.getId(), entity.getExtension(), entity.getCreatedAt());
        }
    }
}
