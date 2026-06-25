# 실행 방법

## 환경 요구사항

- JDK 21 (pom.xml `java.version=21`)
- Maven (프로젝트 내 `mvnw` 래퍼 포함)
- MySQL 8.x (로컬, `localhost:3306`)
- Spring Boot 4.1.0

## DB 설정

### 접속 정보 (`application.properties`)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/madang?useUnicode=true
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

> 로컬 학습용 기본 계정(`root`/`1234`). 실제 환경에서는 외부 설정/환경변수로 분리할 것.

### 스키마

`madang` 데이터베이스의 `book` 테이블을 사용한다. 컬럼: `bookid`, `bookname`, `publisher`, `price`.

```sql
CREATE DATABASE IF NOT EXISTS madang DEFAULT CHARACTER SET utf8mb4;
USE madang;

CREATE TABLE book (
    bookid    INT PRIMARY KEY,
    bookname  VARCHAR(100),
    publisher VARCHAR(100),
    price     INT
);
```

> book 테이블 컬럼은 코드의 SQL/매핑 기준으로 추론한 스키마다. `madang` 은 교재 예제 DB 로, 실제 컬럼 정의가 있으면 그쪽을 따른다.

### JSP ViewResolver 설정

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

뷰 이름 `books` → `/WEB-INF/jsp/books.jsp` 로 매핑. JSP 렌더링은 `tomcat-embed-jasper` 의존성으로 동작한다.

## 빌드 및 실행

```bash
# 컴파일
./mvnw clean compile

# 실행
./mvnw spring-boot:run
```

실행 후 브라우저에서:
- 메인 페이지: `http://localhost:8080/` (index.html)
- 도서 관리 화면: `http://localhost:8080/books`

## 테스트

```bash
./mvnw test
```

`SpringBootMvcdbApplicationTests` 의 컨텍스트 로딩 테스트가 실행된다. DB 연결이 필요하므로 MySQL 이 기동 중이어야 한다.
