# 파일 확장자 차단 과제 (Flow Enterprise Dev Recruitment Task)

마드라스체크(플로우) 엔터프라이즈/SaaS 부문 개발자 채용 과제 제출물입니다.
파일 확장자에 따라 특정 형식의 파일을 첨부하거나 전송하지 못하도록 제한하는 기능을 구현하였습니다.

## 1. 개발 환경 및 기술 스택
- **Language**: Java 21
- **Framework**: Spring Boot 3.5.10
- **Database**: H2 Database (File-based)
- **Template Engine**: Thymeleaf
- **Library**: Apache Tika 3.2.3 (MIME Type 검증)
- **Build Tool**: Gradle

## 2. 실행 방법
1. 프로젝트 루트 디렉토리에서 터미널을 엽니다.
2. 다음 명령어를 실행하여 애플리케이션을 빌드하고 실행합니다.
   ```bash
   ./gradlew bootRun
   ```
   (Windows 환경에서는 `gradlew.bat bootRun`)
3. 브라우저에서 로컬기준 `http://localhost:8080`으로 접속하여 과제 결과물을 확인합니다.

## 3. 주요 기능 및 구현 내용

### 3.1. 확장자 차단 관리 (기본 요구사항)
- **고정 확장자**: `bat`, `cmd`, `com`, `cpl`, `exe`, `scr`, `js` 등의 확장자를 체크박스로 관리합니다. 변경 사항은 즉시 DB에 반영됩니다. 해당 값은 `application.yml` 에서 `fixed-extensions` 값을 통하여 변경 가능합니다.
- **커스텀 확장자**: 최대 200개까지 추가 가능한 커스텀 확장자를 입력받아 관리합니다. 중복 입력 방지 및 유효성 검사가 적용되어 있습니다. 해당 최대값은 `application.yml` 에서 `extension.max-custom-extensions-count` 값을 통하여 변경 가능합니다.

### 3.2. 파일 검증 테스트 (추가 구현 사항)
단순히 확장자 목록만 관리하는 것 이외에, 실제 파일 업로드 시 차단 로직이 정상적으로 동작하는지 확인할 수 있는 기능을 추가하였습니다.
- **테스트 UI 제공**: 메인 화면 하단에 파일을 직접 업로드하여 차단 여부를 테스트할 수 있는 영역을 추가 구성 하였습니다.
- **검증 API**: `POST /api/v1/files/check` 엔드포인트를 통해 파일 검증 로직을 수행합니다.
- **API 접근 로깅**: API에 접근 할 때마다 request.log 라는 로그백 파일에 별도로 기록합니다.

## 4. 과제 해결 전략 및 고민한 점
- 실제 서비스 운영 관점에서 필요하다고 판단되는 부분들을 추가로 고민하고 구현하였습니다.

1. **실질적인 차단 검증**: 확장자 DB 관리 기능만으로는 실제 파일 차단 동작을 확신하기 어렵다고 판단하여, 파일을 직접 업로드해볼 수 있는 테스트 기능을 구현했습니다.
2. **확장자 변조 대응**: 확장자가 없는 파일 일 때, 파일의 실제 Byte Array을 **Apache Tika** 라이브러리로 MIME Type을 추출하고, 이를 기반으로 파일 형식을 식별합니다.
- 악의적인 사용자가 파일 확장자를 제거하여 업로드를 시도하는 경우를 방지하기 위함 입니다.

## 5. API 명세
- 해당 host에서 `/swagger-ui`로 접근 시, SWAGGER-UI으로 API 명세서를 제공합니다. (e.g. 로컬 기동 시, `http://localhost:8080/swagger-ui`)

