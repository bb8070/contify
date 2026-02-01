# 상세 조회 API 정리 (Content Detail)

본 문서는 콘텐츠 **단건 조회 API**의 설계 및 구현 내용을 설명한다.
목록 조회와 달리, 상세 조회는 **엔티티 활용과 연관 데이터 접근**을 고려하여 설계되었다.

---

## API 개요

```http
GET /api/contents/{id}
```

### 특징

* 단건 조회 API
* 콘텐츠 상세 정보 + 작성자 정보 포함
* 조회수 증가 로직 포함
* fetch join을 통한 연관 엔티티 즉시 로딩
* 트랜잭션 내 조회 + 상태 변경 처리



---

## Path Variable

| 이름 | 타입   | 필수 | 설명     |
| -- | ---- | -- | ------ |
| id | Long | ⭕  | 콘텐츠 ID |

### 호출 예시

```http
GET /api/contents/1
```

---

## Response Example

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공했습니다",
  "data": {
    "id": 1,
    "title": "Spring Boot 시작",
    "body": "스프링 부트로 콘텐츠 API를 만들자",
    "category": "BACKEND",
    "viewCount": 11,
    "createdBy": {
      "id": 1,
      "name": "작성자1",
      "email": "writer1@test.com"
    },
    "createdAt": "2026-01-28T20:30:12",
    "updatedAt": "2026-01-28T20:45:01"
  }
}
```

---

## 구현 설명

### 엔티티 기반 조회

상세 조회는 콘텐츠 엔티티와 연관된 작성자(User) 정보가 함께 필요하므로,
엔티티 기반 조회 방식을 사용한다.

```java
Content content = queryFactory
    .selectFrom(c)
    .join(c.createdBy, u).fetchJoin()
    .where(c.id.eq(id))
    .fetchOne();
```

* `fetchJoin()`을 통해 Content와 User를 한 번의 쿼리로 조회
* 연관 엔티티 접근 시 추가 쿼리 발생 없음 (N+1 방지)

---

### 조회수 증가 처리

상세 조회 시 조회수를 1 증가시키는 비즈니스 로직을 포함한다.

```java
@Transactional
public ContentDetailResponse getDetail(Long id) {
    Content content = contentRepository.findByIdWithUser(id)
        .orElseThrow(() -> new NotFoundException());

    content.increaseViewCount();

    return ContentDetailResponse.from(content);
}
```

* 트랜잭션 내에서 조회 + 상태 변경 처리
* Dirty Checking을 통해 update 쿼리 자동 실행

---

### N+1 문제 대응

* 연관관계 기본 전략: `LAZY`
* 상세 조회에서는 User 정보 접근이 명확히 필요하므로 `fetchJoin()` 사용

```java
.join(c.createdBy, u).fetchJoin()
```

이를 통해 단건 조회 시에도 불필요한 추가 쿼리를 방지한다.

---

## 예외 처리

| 상황     | HTTP Status | 설명             |
| ------ | ----------- | -------------- |
| 콘텐츠 없음 | 404         | 존재하지 않는 콘텐츠 ID |

---

## 설계 의도 요약

* 목록 조회와 상세 조회를 **서로 다른 전략**으로 분리
* 상세 조회는 엔티티 활용을 허용하여 도메인 로직 처리
* fetch join을 사용해 N+1 문제 예방
* 조회수 증가 로직을 트랜잭션 단위로 안전하게 처리

---

## 관련 문서

* 조회 API: `docs/조회 API README.md`
* ERD: `docs/ERD.md`
* Troubleshooting: `docs/troubleshooting.md`
