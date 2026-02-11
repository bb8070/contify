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

## 4) QueryDSL category 비교 오류 (String -> Enum 전환)
- 증상 : 빌드 시 아래 컴파일 에러 발생 error: no suitable method found for eq(String)  method SimpleExpression.eq(ContentCategory) is not applicable
- 원인 : `Content.category`를 `String` 에서 `ContentCategory(enum)` 로 변경했는데, QueryDSL 조건은 여전히 `String` 으로 비교하고 있었다.
- 해결 : 검색 조건 자체를 enum 으로 변경하고 , QueryDSL 조건도 enum 비교로 수정했다.
- 배운 점 : enum으로 설계하면 `대소문자 무시 비교`같은 문자열 처리 자체가 필요 없는 문제가 된다. 검색 조건은 도메인 타입 `enum`을 그대로 사용하는 것이 안전하고 유지보수에 유리하다.

## 5) QueryDSL Projection 생성자 오류 (ConstructorExpression)
- 증상 : 조회 시 런타입 예외 발생 `com.querydsl.core.types.ExpressionException: no Constructor found for class ContentListItem with parameters:`
- 원인 : DTO 생성자 파라미터의 "타입 + 순서"가 쿼리와 정확히 일치해야 한다. QueryDSL이 생성자를 찾을 때 , 아래 시그니처로 생성자를 찾고 있었지만 DTO에 존재하지 않았다.
- 해결 : 쿼리의 Projection 순서 / 타입과 동일한 생성자를 DTO에 추가 (또는 수정)했다.
- 배운 점 : Projections.constructor 는 컴파일 타임이 아니라 런타임에 생성자 매칭이 일어나므로, DTO 변경시 쉽게 깨질 수 있다. DTO 생성자 파라미터 순서를 고정하거나, 필요 시 @QueryProjection 같은 방식도 고려할 수 있다.

## 6) ManyToOne 도입 후 data.sql 초기 실패 가능성
- 증상 : 애플리케이션 시작 시 data.sql 실행 단계에서 실패하거나, content insert가 정상 수행되지 않음.
- 원인 : `Content`에 `createdBy(User)`를 `@ManyToOne(optional=false)`로 추가하면서 `content.created_user_id` FK 컬럼이 NOT NULL이 되었고, 기존 `data.sql`의 `contents` insert 에는 해당 값이 존재하지 않았다.
- 해결 : 초기 데이터 삽입 순서를 ùsers` 테이블 먼저 수행함.
- 배운 점 : FK가 생기면 초기 데이터는 반드시 "참조 테이블 > 참조하는 테이블" 순서로 넣어야 한다. 데이터 정합성을 강제하는 설계는 초기 데이터와 마이그레이션에도 영향을 준다.

## 7) Fetch Join vs Projection 선택 기준 (N+1 관점)
- 상황 : Content 목록 조회에서 작성자 (User) 정보까지 함께 내려줘야하는 요구가 생김
- 결론 : 엔터티 로딩 후 연관 객체 접근을 한다면 `fetchJoin()` 이 필요할 수 있음 (N+1 방지) / 하지만 본 프로젝트는 DTO Projection 방식을 사용하므로, 필요한 작성자 컬럼은 join으로 함께 조회하여 해결했다.
- 배운 점 : Projection은 필요한 컬럼만 조회할 수 있어 성능/구조가 명확하다. `fetchJoin` 은 `엔터티 그래프를 로딩` 하는 목적에 더 적합하다.

## 8) AWS SDK 자격증명 로딩 실패
- 증상 : S3 업로드 시 아래 오류 발생
        `SdkClientException: Unable to load credentials from any of the providers in the chain`
        `EnvironmentVariableCredentialsProvider(): Unable to load credentials`
- 원인 : AWS SDK(v2)는 자격증명을 환경변수, 로컬 프로파일, IAM Role 등의 체인에서 탐색한다. 로컬 개발 환경에서 Access Key/Secret Key가 IDE 실행 환경에 주입되지 않아 SDK가 자격증명을 찾지 못했다.
- 해결 : IntelliJ Run/Debug Configuration에 환경변수로 자격증명 주입
- 배운 점 : AWS 연동 이슈는 코드보다 **실행 환경(IDE/OS) 설정**이 원인인 경우가 많다. Access Key는 코드/설정 파일에 하드코딩하지 않고 환경변수/프로파일 방식으로 관리해야 한다.

## 9) S3 객체 URL 접근 불가 (403 AccessDenied)
- 증상 : 업로드 후 반환된 URL로 접근 시 `AccessDenied` 응답
- 원인 : S3 객체는 기본적으로 Private 이며, Public Read 권한이 없으면 URL 직접 접근이 불가능하다. 또한 URL 접근 이슈처럼 보였으나, 실제로는 업로드된 Object Key 경로(prefix)와 콘솔에서 확인한 경로가 달라 “파일이 없는 것처럼 보이는” 착시가 발생했다.
- 해결 : S3 콘솔에서 실제 업로드된 Object Key를 확인하여 코드에서 생성하는 key 규칙과 일치하도록 수정 / (실무형) S3 객체는 Private 유지 + Presigned URL을 통해 제한 시간 동안 접근 허용
- 배운 점 : S3 접근 오류는 업로드 실패가 아니라 **권한/정책 문제**인 경우가 많다. Public 오픈보다 Presigned URL 방식이 보안과 운영 측면에서 적합하다.

## 10) URL은 생성되지만 파일이 보이지 않는 문제 (경로 착각)
- 증상 : 업로드 API 응답으로 URL은 반환되지만, S3 콘솔에서 기대한 폴더에 파일이 보이지 않음
- 원인 : S3는 실제 디렉터리 구조가 아닌 **Object Key(prefix) 기반 저장소**이기 때문에, 코드에서 생성한 key 경로와 콘솔에서 확인한 prefix가 달라 다른 위치에 파일이 저장되었다.
- 해결 : Object Key 규칙을 표준화 `contify/dev/contents/{contentId}/thumbnails/{uuid}.{ext}` 콘솔에서 prefix 검색 및 Object Key 기준으로 실제 저장 위치 검증
- 배운 점 : S3는 “폴더” 개념이 아닌 문자열 Key 기반 저장소이므로, 업로드/조회/삭제 로직 전반에서 **Key 규칙의 일관성**이 운영 편의성과 디버깅 난이도를 좌우한다.
