# 실행 방법

## 환경 요구사항

- JDK (Spring Boot parent 미사용, 일반 Java 프로젝트). MyBatis 3.5.19 / mysql-connector-j 8.3.0 기준 Java 8 이상
- Maven
- MySQL 8.x (로컬, `localhost:3306`)
- Spring 미사용 (순수 MyBatis)

## DB 설정

접속 정보는 `src/main/resources/config/mybatis-config.xml` 안에 직접 설정된다 (별도 `application.properties` 없음).

```xml
<dataSource type="POOLED">
    <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/madang"/>
    <property name="username" value="root"/>
    <property name="password" value="1234"/>
</dataSource>
```

> 로컬 학습용 기본 계정(`root`/`1234`). 실제 환경에서는 외부 설정으로 분리할 것.

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

> book 테이블 컬럼은 매퍼 XML 의 SQL 기준으로 추론한 스키마다. `madang` 은 교재 예제 DB 로, 실제 정의가 있으면 그쪽을 따른다.

## 빌드 및 실행

웹 애플리케이션이 아니라 `ch01.Test` 의 `main()` 으로 실행하는 콘솔 예제다.

### IDE 에서 실행 (권장)
`src/main/java/ch01/Test.java` 를 열고 `main()` 실행. 콘솔에 `book` 테이블 전체 목록이 `BookDto` 의 `toString()` 형태로 출력된다.

```
BookDto [bookId=1, bookName=축구의 역사, publisher=굿스포츠, price=7000]
...
```

### 명령행 빌드
```bash
mvn clean compile
# 의존성 포함 실행은 exec 플러그인 또는 IDE 사용 권장
```

## 테스트

별도 단위 테스트 없음. `ch01.Test.main()` 실행으로 동작을 확인한다. 실행 시 MySQL 이 기동 중이고 `madang.book` 에 데이터가 있어야 출력이 보인다.
