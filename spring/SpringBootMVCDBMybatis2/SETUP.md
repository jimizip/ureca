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
spring.datasource.url=jdbc:mysql://localhost:3306/madang?useUnicode=true
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

> 로컬 학습용 기본 계정(`root`/`1234`). 실제 환경에서는 외부 설정/환경변수로 분리할 것.

### 스키마

`madang` 데이터베이스의 `emp` 테이블을 사용한다. 컬럼: `employeeId`, `first_name`, `last_name`, `email`, `hire_date`.

```sql
CREATE DATABASE IF NOT EXISTS madang DEFAULT CHARACTER SET utf8mb4;
USE madang;

CREATE TABLE emp (
    employeeId INT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    email      VARCHAR(100),
    hire_date  DATE
);
```

> emp 테이블 컬럼은 매퍼 XML 의 SQL 기준으로 추론한 스키마다. 컬럼명이 snake_case(`first_name`)와 camelCase(`employeeId`)가 섞여 있어 실제 정의가 있으면 그쪽을 따른다.

## MyBatis 설정

```properties
# MyBatis 설정 파일 위치
mybatis.config-location=classpath:/config/mybatis-config.xml

# DAO SQL 로깅 (debug 레벨로 실행 SQL 확인)
logging.level.com.mycom.myapp.dao=debug
```

```xml
<!-- mybatis-config.xml -->
<configuration>
    <settings>
        <!-- snake_case 컬럼 <-> camelCase 필드 자동 매핑 -->
        <setting name="mapUnderscoreToCamelCase" value="true" />
    </settings>
    <mappers>
        <mapper resource="mapper/emp-mapper.xml"/>
        <mapper resource="mapper/emp-mapper-2.xml"/>
    </mappers>
</configuration>
```

- `mapper/emp-mapper.xml` : 기본 CRUD
- `mapper/emp-mapper-2.xml` : LIKE 검색, resultMap, 동적 SQL (동일 namespace)

## JSP ViewResolver 설정

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

뷰 `emps`/`salaries`/`stores` → `/WEB-INF/jsp/{이름}.jsp`. JSP 렌더링은 `tomcat-embed-jasper` 로 동작.

## 빌드 및 실행

```bash
# 컴파일
./mvnw clean compile

# 실행
./mvnw spring-boot:run
```

실행 후 브라우저에서:
- 메인: `http://localhost:8080/` (index.html)
- 사원 화면: `http://localhost:8080/emps`
- 급여 화면: `http://localhost:8080/salaries`
- 매장 화면: `http://localhost:8080/stores`

> `logging.level.com.mycom.myapp.dao=debug` 설정으로 실행되는 SQL 과 파라미터를 콘솔에서 확인할 수 있다.

## 테스트

```bash
./mvnw test
```

`SpringBootMvcdbMybatis2ApplicationTests` 의 컨텍스트 로딩 테스트가 실행된다. DB 연결과 매퍼 로딩이 필요하므로 MySQL 이 기동 중이어야 한다.
