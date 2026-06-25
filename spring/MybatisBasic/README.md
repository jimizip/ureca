# MybatisBasic

## 실습 목표

> Spring 없이 **순수 MyBatis** 만으로 DB 조회를 수행하며 MyBatis 의 동작 원리를 익힌다.
>
> - `SqlSessionFactoryBuilder` -> `SqlSessionFactory` -> `SqlSession` 생성 흐름 이해
> - `mybatis-config.xml` 로 환경(DataSource, 트랜잭션, 매퍼) 직접 설정
> - 매퍼 인터페이스(`BookDao`)와 매퍼 XML 의 매핑
> - 구현 클래스를 작성하지 않고 `session.getMapper()` 로 프록시 사용

## 핵심 개념

### 순수 MyBatis 시작 흐름
Spring Boot 의 자동 구성 없이 MyBatis 단독으로 쓸 때의 기본 부트스트랩 순서다.

1. `Resources.getResourceAsReader()` 로 설정 XML 을 읽는다.
2. `SqlSessionFactoryBuilder().build(reader)` 로 `SqlSessionFactory` 를 만든다.
3. `factory.openSession()` 으로 `SqlSession` 을 연다 (DB 접근 단위).
4. `session.getMapper(BookDao.class)` 로 매퍼 구현체(프록시)를 얻는다.

> "Every MyBatis application centers around an instance of SqlSessionFactory. A SqlSessionFactory instance can be acquired by using the SqlSessionFactoryBuilder."
>
> 출처: [MyBatis 3 - Getting Started](https://mybatis.org/mybatis-3/getting-started.html)

### `SqlSession`
DB 와 상호작용하는 단위 객체. SQL 실행, 커밋/롤백, 매퍼 획득을 담당한다. 사용 후 `close()` 로 닫아야 한다. `openSession()` 기본값은 autocommit = false.

> "The SqlSession ... contains absolutely every method needed to execute SQL commands against the database."
>
> 출처: [MyBatis 3 - SqlSession](https://mybatis.org/mybatis-3/java-api.html#SqlSession)

### `mybatis-config.xml` 의 environment
Spring Boot 버전과 달리 DataSource/트랜잭션 매니저를 설정 파일에 직접 적는다.
- `<transactionManager type="JDBC"/>` : JDBC 커밋/롤백 사용
- `<dataSource type="POOLED">` : MyBatis 내장 커넥션 풀

> 출처: [MyBatis 3 - Configuration / environments](https://mybatis.org/mybatis-3/configuration.html#environments)

### 매퍼 인터페이스 + `getMapper`
구현 클래스를 직접 작성하지 않는다. 인터페이스(`BookDao`)와 매퍼 XML 의 `namespace`/`id` 를 MyBatis 가 연결해 런타임 프록시를 만든다.

> 출처: [MyBatis 3 - Mapper / getMapper](https://mybatis.org/mybatis-3/java-api.html#mapper-using)

## 코드 분석

### 설정 - `mybatis-config.xml`
환경과 매퍼를 직접 등록한다.

```xml
<environments default="development">
    <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="url" value="jdbc:mysql://localhost:3306/madang"/>
            <property name="username" value="root"/>
            <property name="password" value="1234"/>
        </dataSource>
    </environment>
</environments>
<mappers>
    <mapper resource="mapper/book-mapper.xml"/>
</mappers>
```

### 매퍼 - `book-mapper.xml`
`namespace` 가 `BookDao` 인터페이스, `id="listBook"` 이 메서드와 매핑된다.

```xml
<mapper namespace="ch01.BookDao">
    <select id="listBook" resultType="ch01.BookDto">
        select bookid bookId, bookname bookName, publisher, price from book;
    </select>
</mapper>
```

`bookid bookId` 별칭으로 DB 컬럼(`bookid`)을 DTO 필드(`bookId`)에 맞춘다.

### 매퍼 인터페이스 - `BookDao`
목록 조회만 활성. 나머지 CRUD 는 주석으로 남겨 확장 지점만 표시.

```java
public interface BookDao {
    List<BookDto> listBook();
//  BookDto detailBook(int bookId);
//  int insertBook(BookDto bookDto);
//  int updateBook(BookDto bookDto);
//  int deleteBook(int bookId);
}
```

### 실행 - `Test`
부트스트랩 흐름이 그대로 드러난다.

```java
// SqlSessionFactoryBuilder -> SqlSessionFactory -> SqlSession
Reader reader = Resources.getResourceAsReader("config/mybatis-config.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
SqlSession session = sqlSessionFactory.openSession(); // autocommit 기본 false

// 구현 클래스 작성 없이 매퍼 프록시 획득
BookDao bookDao = session.getMapper(BookDao.class);

List<BookDto> bookList = bookDao.listBook(); // 목록
for (BookDto bookDto : bookList) System.out.println(bookDto);

session.close(); // 세션 반납
```

## 면접 Q&A

**Q. `SqlSessionFactoryBuilder`, `SqlSessionFactory`, `SqlSession` 의 관계는?**
A. Builder 가 설정을 읽어 Factory 를 만들고(앱당 한 번), Factory 가 Session 을 연다(작업 단위마다). Builder 는 일회성, Factory 는 애플리케이션 수명, Session 은 요청/작업 단위로 짧게 쓰고 닫는다. ([Getting Started](https://mybatis.org/mybatis-3/getting-started.html))

**Q. `BookDao` 구현 클래스가 없는데 어떻게 동작하나?**
A. `session.getMapper(BookDao.class)` 가 매퍼 XML 의 `namespace`/`id` 를 인터페이스/메서드와 연결한 프록시 객체를 반환한다. 구현은 MyBatis 가 런타임에 만든다.

**Q. Spring Boot MyBatis 버전과 이 순수 버전의 차이는?**
A. 순수 버전은 `SqlSessionFactory`/`SqlSession` 을 직접 만들고 닫는다. Spring Boot 버전은 `mybatis-spring-boot-starter` 가 이를 자동 구성하고, `@Mapper` 빈을 주입해주므로 세션 관리 코드가 사라진다.

**Q. `openSession()` 의 autocommit 기본값은?**
A. false. 변경(insert/update/delete) 후엔 `session.commit()` 을 호출해야 반영된다. 조회만 하는 본 실습은 commit 이 필요 없다.

## 참고 출처

- [MyBatis 3 - Getting Started](https://mybatis.org/mybatis-3/getting-started.html)
- [MyBatis 3 - Java API (SqlSession, Mapper)](https://mybatis.org/mybatis-3/java-api.html)
- [MyBatis 3 - Configuration / environments](https://mybatis.org/mybatis-3/configuration.html#environments)
- pom.xml 의존성: `org.mybatis:mybatis` 3.5.19, `com.mysql:mysql-connector-j` 8.3.0 (Spring 미사용)

## 더 나아가기 (선택)

- 주석 처리된 detail/insert/update/delete 매퍼와 메서드 구현 (변경 후 `commit()` 필요)
- `try-with-resources` 로 `SqlSession` 자동 close
- 이 순수 MyBatis 가 `SpringBootMVCDBMybatis` 에서 어떻게 자동 구성으로 바뀌는지 비교
