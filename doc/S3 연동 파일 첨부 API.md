# 파일 업로드 & S3 연동 API 정리 (Thumbnail Upload)

본 문서는 콘텐츠 썸네일 이미지 업로드 API의 설계 및 구현 내용을 설명한다.  
파일 업로드는 보안, 저장소 연동, 접근 제어까지 고려하여 실서비스 확장 가능한 구조로 설계되었다.

---

## API 개요

```
POST /api/contents/{id}/thumbnail
```

### 특징

- Multipart 기반 이미지 업로드 API
- 서버 → AWS S3 업로드 연동
- 썸네일 URL 및 S3 Object Key DB 관리
- 파일 확장자/MIME/크기 검증
- 기존 썸네일 교체 시 이전 객체 정리
- S3 Private 객체 + Presigned URL 기반 접근(선택)

### Path Variable

| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `id` | `Long` | ⭕ | 콘텐츠 ID |

### Request (Multipart)

| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `file` | `MultipartFile` | ⭕ | 업로드할 이미지 파일 |

### 호출 예시

```http
POST /api/contents/1/thumbnail
Content-Type: multipart/form-data
```

### Response Example

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공했습니다",
  "data": {
    "thumbnailUrl": "https://cdn.example.com/contify/dev/contents/1/thumbnails/8f3c2e9f.webp"
  }
}
```

> S3 객체 접근 방식에 따라 **Public URL** 또는 **Presigned URL**이 반환된다.

---

## 구현 설명

### 파일 업로드 파이프라인

```java
@Transactional
public String uploadThumbnail(Long userId, Long contentId, MultipartFile file) {
    Content content = contentRepository.findById(contentId)
        .orElseThrow(NotFoundException::new);

    fileValidator.validateImage(file);

    UploadResult uploaded = s3Uploader.uploadThumbnail(contentId, file);

    String oldKey = content.getThumbnailKey();
    content.changeThumbnail(uploaded.key(), uploaded.url());

    s3Uploader.delete(oldKey);

    return uploaded.url();
}
```

- 업로드 전 서버 단에서 파일 검증 수행
- S3 업로드 성공 후 DB에 URL/Key 반영
- 기존 썸네일 존재 시 이전 객체 삭제

### S3 Object Key 설계

```
contify/dev/contents/{contentId}/thumbnails/{uuid}.{ext}
```

- 원본 파일명 미사용 → UUID 기반 Key 생성
- 경로 추측 및 충돌 방지
- prefix 규칙을 고정하여 운영/디버깅 편의성 확보

### 파일 검증 로직

```java
fileValidator.validateImage(file);
```

- 확장자 화이트리스트: `jpg`, `jpeg`, `png`, `webp`
- MIME 타입 검증: `image/*`
- 파일 크기 제한: Spring 설정 + 서비스단 이중 검증

### S3 접근 제어 (Presigned URL)

- S3 버킷은 **Private** 유지
- 조회 시 Presigned URL 발급

```java
URL url = s3Uploader.presignGetUrl(content.getThumbnailKey(), Duration.ofMinutes(5));
```

- 제한 시간 동안만 객체 접근 허용
- 퍼블릭 오픈 방식 대비 보안 강화

---

## 예외 처리

| 상황 | HTTP Status | 설명 |
|------|-------------|------|
| 콘텐츠 없음 | `404` | 존재하지 않는 콘텐츠 ID |
| 잘못된 파일 형식/크기 | `400` | 허용되지 않은 확장자/MIME/용량 |
| 업로드 실패 | `500` | S3 업로드 중 오류 발생 |

---

## 설계 의도 요약

- 파일 업로드를 단순 저장이 아닌 **보안·운영 관점**까지 고려한 파이프라인으로 설계
- 원본 파일명 대신 **UUID 기반 Key** 사용
- URL과 Object Key를 **분리 저장**하여 교체/삭제 시 정합성 유지
- S3 객체 접근은 **Presigned URL** 방식으로 Private 리소스 보호
- 향후 본문 이미지 업로드/리사이징/CDN 연계로 확장 가능

---

## 관련 문서

- 콘텐츠 상세 조회 API: `docs/상세 조회 API README.md`
- ERD: `docs/ERD.md`
- Trouble Shooting: `docs/troubleshooting.md`