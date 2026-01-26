# Troubleshooting

## 1) QueryDSL containsIgnoreCase 사용 시 500 오류 (lower(CLOB))
- 증상: `/api/contents?keyword=...` 호출 시 500
- 원인: body가 `@Lob`로 CLOB 매핑되어 `containsIgnoreCase()` 내부 `lower(CLOB)`가 DB에서 실패
- 해결: title은 `containsIgnoreCase`, body는 `contains` 사용
- 배운 점: ORM 사용 시에도 DB 타입/함수 제약을 고려해야 한다

## 2) Page count 최적화
- 증상: 목록 조회마다 count 쿼리가 항상 실행되어 비용 증가 가능
- 해결: `PageableExecutionUtils.getPage()`로 count를 필요 시에만 실행(lazy)
- 배운 점: 목록 API는 조회 쿼리뿐 아니라 count 쿼리까지 함께 성능을 고려해야 한다


## 3) H2 DB에서 PK 자동생성 전략 미지정으로 INSERT실패
- 증상 : data.sql 실행시 아래와 같은 오류 발생 / id 컬럼이 NOT NULL인데 INSERT 구문에 id 값이 없어 예외 발생
- 원인 : JPA Entity에서 기본키에 @GeneratedValue 전략을 명시하지 않음
- 해결 : 기본키 생성 전략을 명시적으로 IDENTITY로 지정
- 배운 점 : JPA의 기본 키 생성 전략은 DB마다 다르게 해석될 수 있으므로 학습 / 포트폴리오 환경에서는 전략을 명시하는 것이 안전하다