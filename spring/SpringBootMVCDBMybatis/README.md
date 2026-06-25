# SpringBootMVCDBMybatis

## 실습 목표

> 앞선 순수 JDBC 기반 CRUD(`SpringBootMVCDB`)를 **MyBatis** 로 전환해, SQL 매핑 프레임워크의 동작을 익힌다.
>
> - `mybatis-spring-boot-starter` 로 MyBatis 자동 구성
> - DAO 를 `@Mapper` 인터페이스로 선언하고 구현체 제거
> - SQL 을 XML 매퍼(`book-mapper.xml`)로 분리
> - 인터페이스 메서드 ↔ 매퍼 XML 의 `id` 자동 매핑
> - Controller - Service - DAO - DTO 계층 구조 유지

## 핵심 개념

### MyBatis 와 매퍼(Mapper)
MyBatis 는 SQL 을 XML 또는 어노테이션으로 외부화하고, 자바 메서드 호출과 SQL 실행을 자동 연결하는 **SQL Mapper** 프레임워크다. 순수 JDBC 의 `Connection`/`PreparedStatement`/`ResultSet` 보일러플레이트가 사라진다.

> "MyBatis is a first class persistence framework with support for custom SQL, stored procedures and advanced mappings. MyBatis eliminates almost all of the JDBC code and manual setting of parameters and retrieval of results."
>
> 출처: [MyBatis 3 - Introduction](https://mybatis.org/mybatis-3/)

### `@Mapper` 와 매퍼 인터페이스
MyBatis 는 매퍼 인터페이스의 메서드명을 매퍼 XML 의 SQL `id` 와 매핑한다. 인터페이스에 `@Mapper` 를 붙이면 MyBatis-Spring 이 해당 인터페이스의 구현체(프록시)를 만들어 스프링 빈으로 등록한다. 구현 클래스를 직접 작성하지 않는다.

> "The Mapper interface ... MyBatis-Spring will help you, you don't need to register beans manually, just add the @Mapper annotation."
>
> 출처: [mybatis-spring - @Mapper / MapperScan](https://mybatis.org/spring/mappers.html)

### 매퍼 XML 의 namespace 와 매핑
- `<mapper namespace="...BookDao">` → 매퍼 인터페이스의 FQCN
- `<select id="listBook">` → 인터페이스의 `listBook()` 메서드
- `resultType` → 결과를 매핑할 DTO 타입
- `parameterType` → 파라미터 타입
- `#{...}` → 바인딩 파라미터 (PreparedStatement 의 `?` 처럼 SQL Injection 방어)

> 출처: [MyBatis 3 - Mapper XML Files](https://mybatis.org/mybatis-3/sqlmap-xml.html)

### `@ResponseBody` / `@PathVariable`
Controller 동작은 JDBC 버전과 동일. 객체 반환 시 Jackson 이 JSON 직렬화, 경로 변수는 `@PathVariable` 로 바인딩.

> 출처: [Spring Framework - @ResponseBody](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responsebody.html)

## 코드 분석

### DAO - `BookDao` (`@Mapper` 인터페이스)
구현체(`BookDaoImpl`)가 없다. 인터페이스만 선언하고 SQL 은 XML 에 둔다.

```java
@Mapper // Spring 과 Mybatis 에게 mapper xml 의 namespace 와 연결되는 대상 interface 표시
public interface BookDao {
    List<BookDto> listBook();
    BookDto detailBook(int bookId);
    int insertBook(BookDto bookDto);
    int updateBook(BookDto bookDto);
    int deleteBook(int bookId);
}
```

### 매퍼 XML - `book-mapper.xml`
`namespace` 가 `BookDao` 인터페이스와 연결되고, 각 SQL `id` 가 메서드명과 일치한다.

```xml
<mapper namespace="com.mycom.myapp.dao.BookDao">

    <select id="listBook" resultType="com.mycom.myapp.dto.BookDto">
        select bookid bookId, bookname bookName, publisher, price from book;
    </select>

    <select id="detailBook" resultType="...BookDto" parameterType="int">
        select bookid bookId, bookname bookName, publisher, price
          from book
         where bookid = #{bookId};
    </select>

    <!-- return type primitive type 은 일반적으로 생략 -->
    <insert id="insertBook" parameterType="...BookDto">
        insert into book ( bookid, bookname, publisher, price )
                   values (#{bookId}, #{bookName}, #{publisher}, #{price});
    </insert>
</mapper>
```

- `select bookid bookId` 처럼 **컬럼 별칭** 으로 DB 컬럼(`bookid`)을 DTO 필드(`bookId`)에 맞춘다. snake_case ↔ camelCase 매핑.
- `#{bookId}` 는 파라미터 바인딩. DTO 를 넘기면 프로퍼티명으로 자동 바인딩된다.
- insert/update/delete 의 반환 타입은 영향받은 행 수(int)로 생략 가능.

### MyBatis 설정
`application.properties` 에서 MyBatis 설정 파일 위치를 지정하고, `mybatis-config.xml` 이 매퍼 XML 을 등록한다.

```properties
mybatis.config-location=classpath:/config/mybatis-config.xml
```

```xml
<!-- mybatis-config.xml -->
<configuration>
    <mappers>
        <mapper resource="mapper/book-mapper.xml"/>
    </mappers>
</configuration>
```

### Service - `BookServiceImpl` (`@Service`)
JDBC 버전과 동일. `BookDao` 를 주입받아 위임. 차이는 주입되는 `BookDao` 가 MyBatis 가 생성한 매퍼 프록시라는 점.

```java
// 단순 CRUD 로서 Service Layer 의 별도의 Business Logic 이 없다.
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookDao bookDao; // MyBatis 매퍼 프록시
}
```

### Controller - `BookController`
JDBC 버전과 동일. `/books` 만 JSP 반환, 나머지는 `@ResponseBody` JSON. 등록/수정/삭제는 처리 행 수로 `{"result":"success|fail"}` 반환.

> 참고: `common/DBManager` 가 남아 있으나 MyBatis 전환으로 더 이상 사용되지 않는다(순수 JDBC 잔재).

## 면접 Q&A

**Q. 순수 JDBC 대비 MyBatis 의 이점은?**
A. `Connection`/`Statement`/`ResultSet` 획득과 반납, 파라미터 세팅, 결과 매핑 같은 반복 코드를 MyBatis 가 처리해준다. SQL 은 XML 로 분리돼 관리가 쉽고, 결과는 DTO 로 자동 매핑된다. ([MyBatis Introduction](https://mybatis.org/mybatis-3/))

**Q. `BookDaoImpl` 구현체가 없는데 어떻게 동작하나?**
A. `@Mapper` 인터페이스를 MyBatis-Spring 이 스캔해 런타임에 프록시 구현체를 만들어 빈으로 등록한다. 메서드 호출이 매퍼 XML 의 동일 `id` SQL 실행으로 연결된다. ([mybatis-spring Mappers](https://mybatis.org/spring/mappers.html))

**Q. 매퍼 XML 의 namespace 는 왜 인터페이스 FQCN 인가?**
A. namespace 가 매퍼 인터페이스를, `id` 가 메서드를 가리켜야 MyBatis 가 메서드-SQL 을 매핑할 수 있기 때문이다.

**Q. `select bookid bookId` 처럼 별칭을 주는 이유는?**
A. DB 컬럼은 `bookid`(소문자), DTO 필드는 `bookId`(camelCase)다. 별칭으로 `resultType` 매핑 시 프로퍼티명을 맞춰준다. (또는 `mapUnderscoreToCamelCase` 설정으로도 해결 가능)

**Q. `#{}` 와 `${}` 의 차이는?**
A. `#{}` 는 PreparedStatement 파라미터 바인딩으로 SQL Injection 에 안전하다. `${}` 는 문자열 그대로 치환되어 위험하므로 동적 컬럼/테이블명 등 제한적으로만 쓴다. 본 실습은 모두 `#{}` 사용.

## 참고 출처

- [MyBatis 3 - Introduction](https://mybatis.org/mybatis-3/)
- [MyBatis 3 - Mapper XML Files](https://mybatis.org/mybatis-3/sqlmap-xml.html)
- [mybatis-spring - Mappers / @Mapper](https://mybatis.org/spring/mappers.html)
- [Spring Framework - @ResponseBody](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responsebody.html)
- pom.xml 의존성: `spring-boot-starter-parent` 4.1.0, `mybatis-spring-boot-starter` 4.0.1, `spring-boot-starter-webmvc`, `mysql-connector-j`, `tomcat-embed-jasper`, Java 21

## 더 나아가기 (선택)

- 매퍼 XML 대신 `@Select`/`@Insert` 어노테이션 방식 비교
- `mybatis.configuration.map-underscore-to-camel-case=true` 로 별칭 제거
- `<resultMap>` 으로 복잡한 매핑(연관 객체, 1:N) 처리
- 동적 SQL(`<if>`, `<foreach>`, `<where>`) 활용
- 미사용 `DBManager` 제거 리팩터링
