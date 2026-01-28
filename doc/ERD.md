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

---

## Relationship
- users (1) - (N) contents
- FK : contents.created_user_id -> users.id

---

## Indexes
- idx_contents_category (category)
- idx_contents_created_at (created_at)

---

## Notes
- 연관관계는 ManyToOne 단방향으로 설계하여 단순성과 성능을 우선하였다.
- 목록 조회 성능을 고려하여 category, created_at 컬럼에 인덱스를 추가하였다.

