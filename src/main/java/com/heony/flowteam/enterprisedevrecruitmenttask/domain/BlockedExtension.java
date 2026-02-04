package com.heony.flowteam.enterprisedevrecruitmenttask.domain;

import com.heony.flowteam.enterprisedevrecruitmenttask._common.exception.CustomException;
import com.heony.flowteam.enterprisedevrecruitmenttask._common.exception.ErrorCode;
import com.heony.flowteam.enterprisedevrecruitmenttask._common.util.MyUuidUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "blocked_extension",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_blocked_extension_extension", columnNames = "extension")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockedExtension {

    @Id
    @Column(columnDefinition = "UUID")
    @Comment("PK (UUID v7)")
    private UUID id;

    @Column(nullable = false, length = 20)
    @Comment("차단된 확장자 명")
    private String extension;

    @Column(nullable = false, updatable = false)
    @Comment("생성 일시")
    private Instant createdAt;

    private BlockedExtension(String extension) {
        this.id = MyUuidUtils.generateV7();
        this.extension = extension;
        this.createdAt = Instant.now();
    }

    // 정적 팩토리 메서드
    public static BlockedExtension create(String extension) {
        if (StringUtils.isBlank(extension)) {
            throw new CustomException(ErrorCode.CANNOT_BE_BLANK);
        }
        // 소문자로 저장
        return new BlockedExtension(extension.toLowerCase().trim());
    }
}
