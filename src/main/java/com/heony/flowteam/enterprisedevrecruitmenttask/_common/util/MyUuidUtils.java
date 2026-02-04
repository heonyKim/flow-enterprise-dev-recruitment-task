package com.heony.flowteam.enterprisedevrecruitmenttask._common.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.impl.NameBasedGenerator;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class MyUuidUtils {

    private static final NoArgGenerator V7_GEN = Generators.timeBasedEpochGenerator();      // v7
    private static final NoArgGenerator V6_GEN = Generators.timeBasedReorderedGenerator();  // v6
    private static final NoArgGenerator V1_GEN = Generators.timeBasedGenerator();           // v1
    private static final NoArgGenerator V4_GEN = Generators.randomBasedGenerator();         // v4

    private static final NameBasedGenerator V5_DNS;
    private static final NameBasedGenerator V5_URL;
    private static final NameBasedGenerator V5_OID;
    private static final NameBasedGenerator V5_X500;

    static {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            V5_DNS  = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_DNS, sha1);
            V5_URL  = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_URL, sha1);
            V5_OID  = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_OID, sha1);
            V5_X500 = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_X500, sha1);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize name-based UUID generators", e);
        }
    }


    // 100ns 단위 Gregorian(1582-10-15) → Unix epoch(1970-01-01) 오프셋
    private static final long UUID_GREGORIAN_OFFSET_100NS = 0x01B21DD213814000L; // 122192928000000000

    private MyUuidUtils() {}

    /* ----------------------------- 생성기 단축 메서드 ----------------------------- */

    /** 버전으로 생성 (1,4,6,7 지원) */
    public static UUID generate(int version) {
        return switch (version) {
            case 1 -> generateV1();
            case 4 -> generateV4();
            case 6 -> generateV6();
            case 7 -> generateV7();
            default -> throw new IllegalArgumentException("Unsupported UUID version: " + version);
        };
    }

    public static UUID generateV7(long epochMillis) {
        // 상위 48비트에 epochMillis
        long time = epochMillis & 0x0000FFFFFFFFFFFFL; // 48 bits
        long randA = ThreadLocalRandom.current().nextLong() & 0x0FFFL; // 12 bits

        // MSB: [0..47]=time_ms, [48..51]=version(7), [52..63]=rand_a(12)
        long msb = (time << 16) | 0x7000 | randA;

        // LSB: 상위 2비트는 variant '10', 나머지 62비트 랜덤
        long randB = ThreadLocalRandom.current().nextLong();
        long lsb = (randB & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;

        return new UUID(msb, lsb);
    }

    public static UUID generateV7(Instant instant) {
        return generateV7(instant.toEpochMilli());
    }

    /** UUIDv7 생성 (Unix epoch millis + randomness, 정렬 친화) */
    public static UUID generateV7() {

        return V7_GEN.generate();
    }

    /** UUIDv6 생성 (Gregorian 100ns 기반, 정렬 친화) */
    public static UUID generateV6() {
        return V6_GEN.generate();
    }

    /** UUIDv1 생성 (호스트 식별자에 민감할 수 있음) */
    public static UUID generateV1() {
        return V1_GEN.generate();
    }

    /** UUIDv4 생성 (난수 기반) */
    public static UUID generateV4() {
        return V4_GEN.generate();
    }



    /* ----------------------------- 시간 추출 유틸 ----------------------------- */

    /**
     * UUID에서 Instant 추출.
     * - v7: 상위 48비트 = Unix epoch millis → Instant.ofEpochMilli
     * - v1/v6: 100ns 단위 Gregorian 타임스탬프 → Unix epoch 보정 뒤 Instant
     */
    public static Instant instantFrom(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        int v = uuid.version();
        return switch (v) {
            case 7 -> Instant.ofEpochMilli(unixMillisFromV7(uuid));
            case 1 -> instantFromV1(uuid);
            case 6 -> instantFromV6(uuid);
            default -> throw new IllegalArgumentException("instantFrom only supports v1, v6, v7 (got v" + v + ")");
        };
    }

    /** v7 전용: Unix epoch millis 추출 (상위 48비트). */
    public static long unixMillisFromV7(UUID uuidV7) {
        requireVersion(uuidV7, 7);
        long msb = uuidV7.getMostSignificantBits();
        // v7 레이아웃: [0..47]=unix_ms, [48..51]=version(7), [52..63]=rand_a
        // 상위 48비트를 얻기 위해 16비트 우시프트
        return msb >>> 16;
    }

    private static Instant instantFromV1(UUID uuidV1) {
        requireVersion(uuidV1, 1);
        long gregorian100ns = extractGregorian100nsFromV1(uuidV1);
        return gregorian100nsToInstant(gregorian100ns);
    }

    private static Instant instantFromV6(UUID uuidV6) {
        requireVersion(uuidV6, 6);
        long gregorian100ns = extractGregorian100nsFromV6(uuidV6);
        return gregorian100nsToInstant(gregorian100ns);
    }

    private static void requireVersion(UUID uuid, int expected) {
        if (uuid.version() != expected) {
            throw new IllegalArgumentException("Expected UUID v" + expected + " but got v" + uuid.version());
        }
    }

    /** v1: (time_hi & 0x0FFF)<<48 | (time_mid<<32) | time_low */
    private static long extractGregorian100nsFromV1(UUID uuidV1) {
        long msb = uuidV1.getMostSignificantBits();
        long timeLow  =  msb & 0xFFFFFFFFL;             // 32 bits
        long timeMid  = (msb >>> 32) & 0xFFFFL;         // 16 bits
        long timeHi   = (msb >>> 48) & 0x0FFFL;         // 12 bits (버전 비트 제거)
        return (timeHi << 48) | (timeMid << 32) | timeLow;
    }

    /**
     * v6: 시간 필드가 정렬을 위해 재배치됨.
     * 상위 32비트(time_high), 그 다음 16비트(time_mid), 하위 12비트(time_low),
     * 중간의 4비트는 버전(6)이므로 제외.
     *
     * 재조립: (time_high<<28) | (time_mid<<12) | time_low  => 60bit Gregorian 100ns
     */
    private static long extractGregorian100nsFromV6(UUID uuidV6) {
        long msb = uuidV6.getMostSignificantBits();
        long timeHigh = (msb >>> 32) & 0xFFFFFFFFL; // 상위 32
        long timeMid  = (msb >>> 16) & 0xFFFFL;     // 다음 16
        long timeLow  =  msb         & 0x0FFFL;     // 하위 12 (버전 4비트는 무시됨)
        return (timeHigh << 28) | (timeMid << 12) | timeLow;
    }

    /** Gregorian 100ns → Instant */
    private static Instant gregorian100nsToInstant(long gregorian100ns) {
        long unix100ns = gregorian100ns - UUID_GREGORIAN_OFFSET_100NS;
        long epochSec = unix100ns / 10_000_000L;                 // 1초 = 10^7 * 100ns
        int  nanoAdj  = (int) ((unix100ns % 10_000_000L) * 100); // 남은 100ns → ns
        return Instant.ofEpochSecond(epochSec, nanoAdj);
    }

    /* ----------------------------- 보조 유틸 ----------------------------- */

    /** v5(SHA-1) 네임스페이스(DNS/URL/OID/X500) + name → UUID */
    public static UUID generateV5Dns(String name)  { return V5_DNS.generate(name); }
    public static UUID generateV5Url(String name)  { return V5_URL.generate(name); }
    public static UUID generateV5Oid(String name)  { return V5_OID.generate(name); }
    public static UUID generateV5X500(String name) { return V5_X500.generate(name); }

    /** UUID v6 또는 v7 인지 판별함. 사전적 정렬이 가능하면 true */
    public static boolean isLexicographicallySortable(UUID uuid) {
        return uuid != null && (uuid.version() == 6 || uuid.version() == 7);
    }

    /** 하이픈 제거 32자 문자열 */
    public static String toCompact(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
