# 실행 방법

## 환경 요구사항

- JDK 21 (pom.xml `java.version=21`)
- Maven (프로젝트 내 `mvnw` 래퍼 포함)
- MySQL 8.x (로컬, `localhost:3306`)
- Spring Boot 4.1.0
- MyBatis (`mybatis-spring-boot-starter` 4.0.1)

## DB 설정

### 접속 정보 (`application.properties`)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/board?useUnicode=true
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

> 로컬 학습용 기본 계정(`root`/`1234`). 실제 환경에서는 외부 설정/환경변수로 분리할 것.
> 이전 실습들과 달리 데이터베이스명은 `board`.

### 스키마

테이블 3개를 사용한다: `users`(회원), `board`(게시글), `board_user_read`(사용자-게시글 조회 기록).
아래는 매퍼 XML 의 SQL 기준으로 추론한 스키마다. 실제 정의가 있으면 그쪽을 따른다.

```sql
CREATE DATABASE IF NOT EXISTS board DEFAULT CHARACTER SET utf8mb4;
USE board;

-- 회원
CREATE TABLE users (
    user_seq           INT PRIMARY KEY AUTO_INCREMENT,
    user_name          VARCHAR(50),
    user_password      VARCHAR(255),
    user_email         VARCHAR(100) UNIQUE,
    user_profile_image VARCHAR(255),
    user_register_date DATETIME
);

-- 게시글
CREATE TABLE board (
    board_id   INT PRIMARY KEY AUTO_INCREMENT,
    user_seq   INT,
    title      VARCHAR(200),
    content    TEXT,
    reg_dt     DATETIME DEFAULT CURRENT_TIMESTAMP,
    read_count INT DEFAULT 0,
    FOREIGN KEY (user_seq) REFERENCES users(user_seq)
);

-- 사용자별 게시글 조회 기록 (조회수 중복 방지)
CREATE TABLE board_user_read (
    board_id INT,
    user_seq INT,
    PRIMARY KEY (board_id, user_seq)
);
```

> `board_user_read` 는 (사용자, 게시글) 단위로 최초 조회 시 1건 기록되어, 같은 사용자의 재조회 시 `read_count` 가 중복 증가하지 않게 한다.

## MyBatis 설정

```properties
mybatis.config-location=classpath:/config/mybatis-config.xml
logging.level.com.mycom.myapp=debug
```

```xml
<!-- mybatis-config.xml -->
<configuration>
    <settings>
        <setting name="mapUnderscoreToCamelCase" value="true" />
    </settings>
    <mappers>
        <mapper resource="mapper/user-mapper.xml"/>
        <mapper resource="mapper/login-mapper.xml"/>
        <mapper resource="mapper/board-mapper.xml"/>
    </mappers>
</configuration>
```

## JSP ViewResolver 설정

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

뷰: `register`, `login`, `board`, `error` → `/WEB-INF/jsp/{이름}.jsp`. `tomcat-embed-jasper` 로 JSP 렌더링.

## 빌드 및 실행

```bash
# 컴파일
./mvnw clean compile

# 실행
./mvnw spring-boot:run
```

실행 후 흐름:
1. `http://localhost:8080/` (index.html) 진입
2. `http://localhost:8080/pages/register` 회원 가입
3. `http://localhost:8080/pages/login` 로그인 (세션 생성)
4. `http://localhost:8080/pages/board` 게시판 (로그인 필요, 미인증 시 로그인 페이지로 redirect)

> `logging.level.com.mycom.myapp=debug` 로 인터셉터 로그와 실행 SQL 을 콘솔에서 확인할 수 있다.

> 참고: `BoardController.detailBoard` 에 활성화된 NPE 코드(`String s = null; s.length();`)가 있어 상세 조회가 실패한다. 정상 동작 확인 전 제거 필요 (README 코드 점검 참고).

## 테스트

```bash
./mvnw test
```

`SpringBootMvcdbMybatisBoardApplicationTests` 의 컨텍스트 로딩 테스트가 실행된다. DB 연결과 매퍼 로딩이 필요하므로 MySQL 이 기동 중이어야 한다.
