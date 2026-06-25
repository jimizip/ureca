# SpringBootMVCDB

## 실습 목표

> Spring Boot MVC 에서 **DB 연동 CRUD** 를 계층형 구조(Layered Architecture)로 구현한다.
>
> - `DataSource`(커넥션 풀)로부터 `Connection` 을 받아 순수 JDBC 로 DB 처리
> - Controller - Service - DAO - DTO 계층 분리
> - JSP 뷰 응답과 `@ResponseBody` 기반 JSON 응답 혼용
> - 인터페이스 기반 설계 + `@Autowired` 의존성 주입

## 핵심 개념

### 계층형 아키텍처 (Controller - Service - DAO - DTO)
요청 처리 책임을 계층별로 분리한다.
- **Controller**: HTTP 요청/응답 처리, 뷰 또는 JSON 반환
- **Service**: 비즈니스 로직 (본 실습은 단순 CRUD 라 위임만 함)
- **DAO(Repository)**: DB 접근, SQL 실행
- **DTO**: 계층 간 데이터 전달 객체 (book 테이블 매핑)

### DataSource 와 커넥션 풀
`spring-boot-starter-jdbc` 의존성으로 Spring Boot 가 `DataSource` 빈을 자동 구성한다. 기본 구현은 **HikariCP** 커넥션 풀. 매 요청마다 새 커넥션을 만들지 않고 풀에서 빌려 쓰고 반납한다.

> "Production database connections can also be auto-configured by using a pooling DataSource. Spring Boot ... prefers HikariCP for its performance and concurrency."
>
> 출처: [Spring Boot - Configure a DataSource](https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.datasource)

### `@ResponseBody` 와 JSON 변환
메서드 반환값을 뷰 이름이 아니라 **HTTP 응답 본문** 으로 직접 쓴다. 객체를 반환하면 Spring 이 `HttpMessageConverter`(Jackson)로 JSON 직렬화한다.

> "The @ResponseBody annotation on a method ... causes the return value to be serialized to the response body through an HttpMessageConverter."
>
> 출처: [Spring Framework - @ResponseBody](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responsebody.html)

### `@PathVariable`
URL 경로 일부를 메서드 파라미터로 바인딩한다. `/books/detail/{bookId}` 의 `{bookId}` 를 받는다.

> 출처: [Spring Framework - @PathVariable](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/pathvariable.html)

### JSP ViewResolver
`application.properties` 의 prefix/suffix 설정으로 뷰 이름 → 실제 JSP 경로를 매핑한다. JSP 렌더링을 위해 `tomcat-embed-jasper` 의존성이 필요하다.

## 코드 분석

### DTO - `BookDto`
book 테이블에 매핑되는 기본 데이터 객체.

```java
// DTO : Data Transfer Object  <= Model 의 기본 객체 ( 특정 엔티티와 매핑 )
// book 테이블 매핑
public class BookDto {
    private int bookId;
    private String bookName;
    private String publisher;
    private int price;
    // 기본 생성자 + 전체 필드 생성자 + getter/setter
}
```

### DAO - `BookDaoImpl` (`@Repository`)
순수 JDBC 로 CRUD 수행. `DataSource` 에서 커넥션을 받아 `PreparedStatement` 실행 후 반납.

```java
@Repository
public class BookDaoImpl implements BookDao {

    @Autowired
    DataSource dataSource; // Connection Pool, 생성자 DI 가 추천되는 방법이지만, 의도적으로 필드 DI 사용

    @Override
    public List<BookDto> listBook(){
        ...
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) { ... list.add(bookDto); }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(rs, pstmt, con); // 자원 반납
        }
        return list;
    }
}
```

- `PreparedStatement` 와 `?` 바인딩으로 SQL Injection 방어.
- 조회/변경 후 `finally` 에서 `DBManager` 로 자원 반납. 풀 커넥션은 `close()` 가 재정의되어 실제로는 풀에 반납됨.

### 공통 - `DBManager`
`Connection`, `PreparedStatement`, `ResultSet` 자원 반납 유틸. 가변인자(`AutoCloseable...`) 버전도 제공.

```java
// DataSource 가 관리하는 Connection 객체는 close() overriding (재정의) 되어 있다.
public static void releaseConnection(ResultSet rs, PreparedStatement pstmt, Connection con) {
    try {
        if(rs != null) rs.close();
        if(pstmt != null) pstmt.close();
        if(con != null) con.close();
    } catch(Exception e) { e.printStackTrace(); }
}
```

### Service - `BookServiceImpl` (`@Service`)

```java
// 단순 CRUD 로서 Service Layer 의 별도의 Business Logic 이 없다.
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookDao bookDao; // interface 타입 주입
    // DAO 호출 위임
}
```

인터페이스 타입(`BookDao`)으로 주입받아 구현체와 결합도를 낮춘다.

### Controller - `BookController` (`@Controller`)

```java
@Controller
public class BookController {
    @Autowired
    BookService bookService;

    @GetMapping("/books")          // books.jsp 뷰 반환
    public String bookMain() { return "books"; }

    @GetMapping("/books/list")     // JSON 응답
    @ResponseBody
    public List<BookDto> listBook(){ return bookService.listBook(); }

    @GetMapping("/books/detail/{bookId}")
    @ResponseBody
    public BookDto detailBook(@PathVariable Integer bookId){ ... }

    @PostMapping("/books/insert")
    @ResponseBody
    public Map<String, String> insertBook(BookDto bookDto){
        int ret = bookService.insertBook(bookDto);
        // ret == 1 이면 success, 아니면 fail
    }
}
```

`/books` 만 JSP 뷰를 반환하고, 나머지는 `@ResponseBody` 로 JSON 응답. 등록/수정/삭제는 처리 결과 행 수(`ret`)로 성공 여부를 판단해 `{"result": "success|fail"}` 반환.

## 면접 Q&A

**Q. 왜 계층(Controller/Service/DAO/DTO)을 나누나?**
A. 책임 분리로 유지보수성과 테스트 용이성을 높인다. 웹 처리(Controller), 비즈니스 로직(Service), DB 접근(DAO)이 분리되면 한 계층 변경이 다른 계층에 미치는 영향이 줄어든다.

**Q. `DataSource` 를 직접 `getConnection()` 하는데 매번 새 커넥션인가?**
A. 아니다. `spring-boot-starter-jdbc` 가 자동 구성하는 `DataSource` 는 HikariCP 커넥션 풀이라, `getConnection()` 은 풀에서 빌려오고 `close()` 는 풀에 반납하는 동작이다. ([DataSource 문서](https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.datasource))

**Q. `@Controller` 인데 어떻게 JSON 을 응답하나?**
A. 메서드에 `@ResponseBody` 를 붙이면 반환 객체가 뷰가 아니라 응답 본문으로 직렬화된다. Jackson `HttpMessageConverter` 가 객체를 JSON 으로 변환한다. `@RestController` 는 클래스 전체에 `@ResponseBody` 를 적용한 것과 같다. ([@ResponseBody 문서](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responsebody.html))

**Q. `PreparedStatement` 를 쓰는 이유는?**
A. `?` 플레이스홀더에 값을 바인딩해 SQL Injection 을 방어하고, 쿼리 파싱 재사용으로 성능 이점도 있다.

**Q. 필드 주입(`@Autowired` 필드)을 썼는데 권장 방식인가?**
A. 아니다. 코드 주석에도 "생성자 DI 가 추천되는 방법이지만 의도적으로 필드 DI 사용" 이라 적혀 있다. 실무에선 불변성/테스트 용이성 때문에 생성자 주입이 권장된다.

## 참고 출처

- [Spring Boot - SQL Databases / DataSource](https://docs.spring.io/spring-boot/reference/data/sql.html)
- [Spring Framework - @ResponseBody](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responsebody.html)
- [Spring Framework - @PathVariable](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/pathvariable.html)
- [Spring Framework - Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- pom.xml 의존성: `spring-boot-starter-parent` 4.1.0, `spring-boot-starter-jdbc`, `spring-boot-starter-webmvc`, `mysql-connector-j`, `tomcat-embed-jasper`, Java 21

## 더 나아가기 (선택)

- 순수 JDBC 대신 `JdbcTemplate` / `JdbcClient` 로 보일러플레이트 제거
- `@Transactional` 로 트랜잭션 처리
- 필드 주입을 생성자 주입으로 리팩터링
- DAO 의 `e.printStackTrace()` 대신 예외 변환 및 로깅 처리
- MyBatis / JPA 로 매핑 자동화 비교
