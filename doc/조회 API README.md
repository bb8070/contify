# 조회 API 정리 (Contents)

본 문서는 콘텐츠 목록 조회 API의 설계 및 구현 내용을 설명한다.
단순한 CRUD가 아닌, **조회 성능·확장성·데이터 정합성**을 고려한 API 설계를 목표로 한다.

---

## API 개요

```http
GET /api/contents
```

### 특징

* QueryDSL 기반 동적 검색
* enum 기반 카테고리 필터링
* Pageable 기반 페이징/정렬
* DTO Projection + join 방식으로 N+1 문제 방지
* count 쿼리 분리로 페이징 성능 최적화

---

## Request Parameters

| 이름       | 타입                     | 필수 | 설명                        |
| -------- | ---------------------- | -- | ------------------------- |
| category | ContentCategory (enum) | ❌  | 콘텐츠 카테고리                  |
| page     | int                    | ❌  | 페이지 번호 (0부터 시작)           |
| size     | int                    | ❌  | 페이지 사이즈                   |
| sort     | string                 | ❌  | 정렬 조건 (예: createdAt,desc) |

### 호출 예시

```http
GET /api/contents?category=BACKEND&page=0&size=10&sort=createdAt,desc
```

---

## Response Example

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공했습니다",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Spring Boot 시작",
        "category": "BACKEND",
        "viewCount": 10,
        "createdByName": "작성자1",
        "createdAt": "2026-01-28T20:30:12"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 3,
    "totalPages": 1
  }
}
```

---

## 구현 설명

### Projection 기반 조회

엔티티를 그대로 반환하지 않고, 목록 조회에 필요한 컬럼만 DTO로 조회한다.

```java
.select(Projections.constructor(
    ContentListItem.class,
    c.id,
    c.title,
    c.category,
    c.viewCount,
    u.name,
    c.createdAt
))
.from(c)
.join(c.createdBy, u)
```

* 불필요한 엔티티 로딩 제거
* 작성자(User) 정보는 join으로 한 번에 조회
* N+1 문제 발생 없음

---

### N+1 문제 방지 전략

* 연관관계: `ManyToOne(fetch = LAZY)` 유지
* 목록 조회 시 엔티티 접근 ❌
* **Projection + join 방식으로 단일 SQL 조회**

목록 조회 시 추가적인 User 조회 쿼리가 발생하지 않는다.

---

### count 쿼리 최적화

페이징을 위한 count 쿼리는 연관 테이블 join을 제거하여 수행한다.

```java
JPAQuery<Long> countQuery = queryFactory
    .select(c.count())
    .from(c)
    .where(where);
```

```java
PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
```

* 불필요한 join 제거
* 대용량 데이터 환경에서도 성능 유지

---

## 설계 의도 요약

* 조회 API는 서비스에서 가장 호출 빈도가 높다고 가정
* enum 기반 필터링으로 데이터 정합성 확보
* Projection을 통한 성능 최적화
* count 쿼리 분리로 페이징 비용 최소화

---

## 관련 문서

* ERD: `docs/ERD.md`
* Troubleshooting: `docs/troubleshooting.md`
