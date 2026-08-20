# SoSo BackEnd

소상공인을 위한 통합 경량화 ERP 솔루션,
SoSo 프로젝트의 백엔드(Spring Boot) 저장소입니다.

##  Live Demo

 **[Soso Live Demo](https://emsemsdl.shop/)**

###  테스트 계정

| 권한 구분 | 아이디(ID) | 비밀번호(PW) |
| :--- | :--- | :--- | 
|  사업자(Business) | `kakao123` | `a1234!@#$` |
|  거래처(Partner) | `test111` | `a1234!@#$` |

##  Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot (v4.0.6)
- **Database**: MySQL, Redis
- **Persistence**: MyBatis
- **Security**: Spring Security, JWT (Auth0)
- **AI Integration**: Spring AI (Google GenAI)
- **Cloud/Storage**: Google Cloud Storage (GCP)
- **Other Features**: WebSocket, Spring Mail

##  Project Structure

- `src/main/java`: 컨트롤러(Controller), 서비스(Service), 데이터 접근(Mapper/Repository) 등 Java 비즈니스 로직
- `src/main/resources`: `application.yml`, MyBatis XML 매퍼 파일 등 설정
- `src/test/java`: 테스트 코드 작성 공간

##  Architecture & Rules

### 1. Directory Structure
- **Domain-Driven**: `com.soso.domain` 하위에 각 도메인(member, board, product 등)별로 패키지를 구성하여 모듈 간 결합도를 낮췄습니다.
- **Layered Architecture**: 각 도메인 내부는 `controllers`, `services`, `dao`(MyBatis Mapper), `dto` 패키지로 분리되어 명확한 계층 역할을 가집니다.
- **Global / Common**: 전역 설정, 예외 처리, 유틸리티 클래스 등은 `com.soso.global` 패키지에서 공통으로 관리합니다.

### 2. Data Access
- **MyBatis 사용**: 영속성 프레임워크로 MyBatis를 활용합니다.
- Java 인터페이스는 각 도메인의 `dao` 패키지에 위치하며, 실행될 SQL 쿼리는 `src/main/resources/mappers` 폴더 내 XML 파일로 관리하여 SQL과 비즈니스 로직을 분리합니다.

### 3. Exception Handling 
- **GlobalExceptionHandler**: `com.soso.global.error` 패키지에 정의된 `@RestControllerAdvice`를 통해 시스템 전역에서 발생하는 예외를 한 곳에서 처리합니다.

### 4. API Response (API 응답 규약)
- 기본적으로 `ResponseEntity<Map<String, Object>>` 구조를 사용하여, 요청의 성공/실패 여부(`status`)와 안내 메시지(`message`), 데이터 등을 반환합니다.


##  Getting Started

### Prerequisites
- Java 17 이상
- MySQL, Redis
- (필요시) Google Cloud Storage 인증 정보, Google GenAI API 설정 등


### Clone the repository and move to the directory

   ```bash
   git clone <repository-url>
   cd soso
   ```

### Environment Variables

   src/main/resources/ 경로에 샘플 파일을 복사하여 실제 설정 파일을 생성합니다.

   ```bash
   cp application-secret-sample.properties application-secret.properties
   ```
   생성된 src/main/resources/application-secret.properties 파일을 열어 DB(MySQL, Redis) 연결 정보, JWT Secret 설정, GCP 설정 등을 작성하세요.


### Installation
   ```bash
   # Windows
   mvnw.cmd spring-boot:run

   # macOS / Linux
   ./mvnw spring-boot:run
   ```
### Build
```bash
# 프로덕션용 JAR 파일 빌드 (Windows)
mvnw.cmd clean package

# 프로덕션용 JAR 파일 빌드 (macOS / Linux)
./mvnw clean package