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

## 5. 로컬 실행
- `./gradlew bootRun`
- Swagger: `/swagger-ui.html`
- H2 Console: `/h2-console`