# ERD

## Overview
본 프로젝트는 Content와 사용자(User) 도메인을 중심으로 설계되었다.
콘텐츠는 반드시 하나의 사용자에 의해 작성되며,
사용자 1 : 콘텐츠 N 구조의 관계를 가진다.

___

## Tables

### users
- **id**(PK)
- email (UNIQUE)
- name
- role (enum : USER , ADMIN)
- created_at
- updated_at

---

### contents
- **id**(PK)
- title
- body
- category(enum: BACKEND, INFRA, ...)
- view_count
- created_user_id (FK -> user.id)
- created_at
- updated_at
- thumbnail_url   ← 추가
- thumbnail_key   ← 추가 (S3 object key)

### 파일 업로드 구조 변경

콘텐츠 썸네일 업로드 기능 추가에 따라 `contents` 테이블에 파일 메타데이터 컬럼을 확장하였다.


| 컬럼명 | 타입 | 설명 |
|-------|------|------|
| thumbnail_url | varchar | 썸네일 접근 URL (Public 또는 Presigned URL) |
| thumbnail_key | varchar | S3 Object Key (삭제/교체/Presigned URL 생성에 사용) |

- 파일 접근 URL과 S3 Object Key를 분리 저장하여,  
  파일 교체/삭제 및 접근 제어 정책 변경 시 정합성을 유지한다.

---

### Tag
- **id**(PK)
- name

---

### Content_Tag
- Content와 Tag 의 `Many-to-Many` 관계를 풀기 위한 중간 엔티티.
- **id** (PK)
- content_id (FK -> contents.id)
- tag_id (FK -> tag.id)

---

## Relationship

- users (1) - (N) contents
- FK : contents.created_user_id -> users.id


- tag (1) ── N content_tag N ── (1) contents
- contents와 tag는 다대다(N:N) 관계이다.
- 이를 풀기 위해 content_tag 중간 테이블을 사용한다.
- content_tag는 다음과 같은 외래 키를 가진다.
- FK : content_tag.content_id → contents.id  
- FK : content_tag.tag_id → tag.id

---

## Indexes
- idx_contents_category (category)
- idx_contents_created_at (created_at)

---

## Notes
- 연관관계는 ManyToOne 단방향으로 설계하여 단순성과 성능을 우선하였다.
- 목록 조회 성능을 고려하여 category, created_at 컬럼에 인덱스를 추가하였다.



