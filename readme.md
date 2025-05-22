# SpringBoot MSA

## 개발 우선순위

📌 공통 기반 작업
Spring Cloud Gateway 세팅 (서브도메인 & 패스 기반 분기)
Spring Cloud Config + Eureka 등록
공통 DTO, Exception, Filter 모듈 생성

🔐 auth-service 개발
로그인/회원가입 API

JWT 발급 및 필터

권한(Role)에 따라 라우팅

🌐 사이트/메뉴 관리 기능 (cms-menu-service)
/site1, /site2 정보 등록 및 활성화

각 메뉴 트리 구성 정보 저장

📝 콘텐츠 관리 기능 (cms-content-service)
게시판, 공지사항 등 콘텐츠 CRUD

메뉴 ID 기준 콘텐츠 분기 처리

🛠 admin-site-service
관리자용 대시보드

메뉴/사이트/회원 관리 UI

📦 file-service
이미지, 첨부파일 업로드 (S3/로컬 선택 가능)

## ✅ 전체 구조 요약

### 1. Gateway Layer

- **Spring Cloud Gateway**
- Eureka 연동: `lb://서비스명`
- JWT 인증 처리 예정

### 2. Service Layer

- **auth-service**: 사용자 인증 및 JWT 발급 (WebFlux)
- **user-service**: 사용자 관리 서비스 (WebFlux)
- 모든 서비스는 Eureka Client로 등록
- Config Server에서 설정 동적 로딩

### 3. Infra Layer

- **Eureka Server**: 서비스 디스커버리
- **Config Server**: 중앙 설정 관리 (native mode, `/config-repo`)
- **Kafka**: 이벤트 발행/수신
- **Redis**: Pub/Sub 실시간 이벤트 공유
- **MariaDB**: 각 서비스 데이터 저장소

### 4. Common Library

- **KafkaEventPublisher**
- **KafkaDbEventProvider**
- **EventDefinitionRepository** (R2DBC 기반)
- 공통 모듈에서 R2dbcEntityTemplate 주입 문제 해결 필요

### 5. DevOps / 운영 구조

- **Docker Compose** 기반 통합 운영
- **Jenkins** 기반 CI/CD 예정
- 설정 mount: `/config-repo`
- Spring Boot Actuator + Sleuth/Zipkin 고려

### 6. 이벤트 흐름

- Kafka Topic → Consumer → DB 저장 또는 비즈니스 로직 처리
- 이벤트 정의는 DB에 저장 → 서비스에서 조회 후 처리

## ✅ 현재까지 구현 완료된 항목

- auth-service 기능 완성
- Kafka 이벤트 발행 구현
- Eureka Server + Client 구성 완료
- Redis Pub/Sub 연동
- Config Server 설정 완료
- Docker Compose 구성 완료

## ✅ 향후 고려 요소

- Gateway JWT 필터 연동
- Kafka 이벤트 표준화 (eventType, version 등)
- Spring Cloud Bus로 설정 핫리로드
- Prometheus / Zipkin 적용

--------------------------------


✅ 다음 우선순위 정리

1. 🔍 통합 테스트 작성 (E2E 테스트)
   목표: 전체 인증 흐름을 검증

포인트:

OAuth2 로그인 요청 → JWT/세션 발급 확인

Kafka 이벤트가 정상 발행되는지 확인 (@EmbeddedKafka 사용 권장)

DB 상태 검증 (User, Session 저장 여부)

툴 추천: SpringBootTest + WebTestClient, @Testcontainers, @EmbeddedKafka

2. 📘 Swagger(OpenAPI) 문서화
   목표: 인증 API를 프론트/타팀 개발자와 공유

포인트:

/auth/oauth2/authorize, /auth/oauth2/callback/{provider} 등 주요 API 명세화

JWT/Session 발급 응답 구조 명시

라이브러리: springdoc-openapi-webflux-ui

yaml
복사
편집

# build.gradle

implementation 'org.springdoc:springdoc-openapi-starter-webflux-ui:2.2.0'

3. 🤝 user-service와 연동
   목표: 로그인 시 user-service에서 사용자 정보 조회 또는 등록/수정

포인트:

user-service에 WebClient 또는 FeignClient 호출

로그인 시 사용자 존재하지 않으면 user-service에 최초 등록 (이벤트 기반 선호)

Kafka → user-service → DB 반영까지 흐름 확인

4. 🧪 user-service 단의 통합 대응
   KafkaConsumer에서 받은 사용자 이벤트 처리 로직 완성

사용자 존재 여부 체크 후 insert or update

user-service에서 사용자 조회 시 토큰 기반 인증 체크 (Authorization: Bearer ...)

auth-service → user-service WebClient 연동 테스트

5. 🔐 보안 관련 보강
   Redis 기반 Session TTL 설정

Refresh Token 저장소 보안 검토 (Redis → 암호화 여부, 탈취 방지)

JWT 서명 비밀키 관리 (config-server 또는 HashiCorp Vault 연동 고려)

6. 🛠 운영 편의성 도구
   로그 추적용 Correlation ID 연동 (gateway → downstream 서비스까지)

Kafka 이벤트 추적 및 실패 재처리 구조 정립

actuator + prometheus + grafana 연동 (optional)