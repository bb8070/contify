# Contify — 콘텐츠 조회/검색 API (Spring Boot + QueryDSL)

## 1. 소개
콘텐츠 플랫폼에서 자주 사용하는 **목록 조회/검색 API**를 구현했습니다.  
단순 CRUD가 아니라 **검색 조건 + 페이징 + 정렬 + count 최적화**에 초점을 맞췄습니다.

## 2. 핵심 기능
- QueryDSL 기반 동적 검색 (keyword, category)
- Pageable 기반 페이징/정렬
- 다중 정렬 지원 (`sort=viewCount,desc&sort=createdAt,desc`)
- 목록 응답은 DTO로 반환하여 Entity 노출 방지
- `PageableExecutionUtils.getPage()`로 **count 쿼리 필요 시에만 실행**(최적화)

## 3. 기술 스택
- Java, Spring Boot
- Spring Data JPA / Hibernate
- QueryDSL
- H2 (local)
- Swagger(OpenAPI)

## 4. API 예시
- 기본 목록  
  `GET /api/contents?page=0&size=5`

- 검색 + 페이징  
  `GET /api/contents?keyword=SPRING&category=BACKEND&page=0&size=5`

- 다중 정렬  
  `GET /api/contents?page=0&size=5&sort=viewCount,desc&sort=createdAt,desc`

## 5. 트러블슈팅
### (1) QueryDSL containsIgnoreCase 사용 시 500 오류 (lower(CLOB))
- **증상**: keyword 검색 시 500 발생, `lower()` 함수 인자 타입 오류
- **원인**: 본문(body)이 `@Lob`로 매핑되어 CLOB 타입이 되었고, `containsIgnoreCase()`가 내부적으로 `lower()`를 사용
- **해결**: title(VARCHAR)은 `containsIgnoreCase`, body(CLOB)는 `contains`로 처리

### (2) count 최적화
- Page 응답에서 total count 계산은 비용이 크므로,
- `PageableExecutionUtils.getPage()`를 사용해 **필요한 경우에만 count 쿼리를 실행**

### (3) H2 DB에서 PK 자동 생성 전략 미지정으로 INSERT 실패
- H2 환경에서 PK 생성 전략을 명시하지 않아 auto increment가 적용되지 않는 문제를 경험했고,
  GenerationType.IDENTITY를 명시하여 DB별 동작 차이를 해결했다.

## 6. 로컬 실행
- `./gradlew bootRun`
- Swagger: `/swagger-ui.html`
- H2 Console: `/h2-console`