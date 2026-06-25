# SpringBootMVCDBMybatis2

## 실습 목표

> 기본 MyBatis CRUD(`SpringBootMVCDBMybatis`)를 넘어, **MyBatis 의 실전 기능** 을 사원(Emp) 도메인으로 익힌다.
>
> - `mapUnderscoreToCamelCase` 로 snake_case 컬럼 ↔ camelCase 필드 자동 매핑
> - 매퍼 XML 파일 분리(동일 namespace 2개 파일)
> - `LIKE` 검색
> - `<resultMap>` 으로 일부 필드 수동 매핑
> - 동적 SQL(`<where>`, `<if>`)
> - 생성자 주입(constructor injection)
> - 클래스 레벨 `@ResponseBody` + `@RequestMapping`
> - 뷰 이동 전용 `PageController` 분리

## 핵심 개념

### `mapUnderscoreToCamelCase`
DB 의 snake_case 컬럼명(`first_name`)을 자바 camelCase 프로퍼티(`firstName`)로 MyBatis 가 자동 매핑하게 하는 설정. 매퍼마다 컬럼 별칭을 줄 필요가 없어진다.

> "mapUnderscoreToCamelCase — Enables automatic mapping from classic database column names A_COLUMN to camel case classic Java property names aColumn."
>
> 출처: [MyBatis 3 - Configuration / settings](https://mybatis.org/mybatis-3/configuration.html#settings)

### `<resultMap>`
컬럼명과 DTO 필드명이 불규칙하거나, DTO 의 일부 필드만 매핑할 때 수동으로 매핑 규칙을 정의한다.

> "The resultMap element is the most important and powerful element in MyBatis ... lets you ... do mappings that JDBC does not support."
>
> 출처: [MyBatis 3 - Result Maps](https://mybatis.org/mybatis-3/sqlmap-xml.html#result-maps)

### 동적 SQL (`<where>`, `<if>`)
조건에 따라 SQL 을 동적으로 조립한다. `<if>` 로 파라미터 존재 여부에 따라 조건절을 추가하고, `<where>` 가 앞쪽의 불필요한 `AND`/`OR` 를 자동 제거하고 조건이 하나라도 있을 때만 `WHERE` 를 붙인다.

> "The where element knows to only insert WHERE if there is any content ... and if that content begins with AND or OR, it knows to strip it off."
>
> 출처: [MyBatis 3 - Dynamic SQL](https://mybatis.org/mybatis-3/dynamic-sql.html)

### 생성자 주입 (Constructor Injection)
이전 실습의 필드 주입과 달리 `final` 필드 + 생성자로 의존성을 주입한다. 불변성 보장, 필수 의존성 명시. Spring 권장 방식.

> 출처: [Spring Framework - Constructor-based DI](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html)

### 클래스 레벨 `@ResponseBody` + `@RequestMapping`
컨트롤러 클래스에 `@ResponseBody` 를 붙이면 모든 메서드가 JSON 응답. `@RequestMapping("/emps")` 로 공통 URL prefix 지정.

## 코드 분석

### DAO - `EmpDao` (`@Mapper`)
CRUD 5개 + 검색/매핑/동적SQL 3개 메서드. 두 매퍼 XML 파일이 같은 namespace 를 공유한다.

```java
@Mapper
public interface EmpDao {
    // emp crud  (emp-mapper.xml)
    List<EmpDto> listEmp();
    EmpDto detailEmp(int employeeId);
    int insertEmp(EmpDto empDto);
    int updateEmp(EmpDto empDto);
    int deleteEmp(int employeeId);

    // 검색, ResultMap, 동적 SQL  (emp-mapper-2.xml)
    List<EmpDto> listEmpLike(String searchWord);   // email
    List<EmpDto> listEmpMap();
    List<EmpDto> listEmpWhereIf(Map<String, String> map);
}
```

### 매퍼 1 - `emp-mapper.xml` (기본 CRUD)
`mapUnderscoreToCamelCase` 덕분에 `first_name` 을 별칭 없이 그대로 select 한다.

```xml
<!-- mapUnderscoreToCamelCase 를 통해서 자동으로 mybatis 에 의해 자바 필드 표현으로 변경됨. -->
<select id="listEmp" resultType="com.mycom.myapp.dto.EmpDto">
    select employeeId, first_name, last_name, email, hire_date from emp;
</select>
```

### 매퍼 2 - `emp-mapper-2.xml` (LIKE / resultMap / 동적 SQL)

**LIKE 검색** - `concat` 으로 접두사 검색:
```xml
<select id="listEmpLike" parameterType="string" resultType="...EmpDto">
    select ... from emp
     where email like concat( #{searchWord}, '%' );
</select>
```

**resultMap** - 일부 필드만 수동 매핑:
```xml
<!-- Dto 의 일부 필드만 처리, 컬럼명과 필드명이 비규칙적이고 달라서 수동 매핑이 필요한 경우 -->
<resultMap id="empMap" type="com.mycom.myapp.dto.EmpDto">
    <result property="employeeId" column="employeeId"/>
    <result property="firstName" column="first_name"/>
    <result property="lastName" column="last_name"/>
</resultMap>

<select id="listEmpMap" resultMap="empMap">
    select employeeId, first_name, last_name from emp;
</select>
```

**동적 SQL** - 조건부 WHERE:
```xml
<select id="listEmpWhereIf" parameterType="map" resultType="...EmpDto">
    select ... from emp
    <where>
        <if test="firstName != null"> first_name = #{firstName} </if>  <!-- 맨 앞 where, and 없다 -->
        <if test="lastName != null"> and last_name = #{lastName} </if>
        <if test="email != null"> and email = #{email} </if>
    </where>
</select>
```

### Service - `EmpServiceImpl` (생성자 주입)

```java
@Service
public class EmpServiceImpl implements EmpService {
    private final EmpDao empDao;   // 생성자 주입

    public EmpServiceImpl(EmpDao empDao) {
        this.empDao = empDao;
    }
}
```

### Controller - `EmpController` (`/emps`, 전체 JSON)

```java
@Controller
@ResponseBody              // 클래스 전체 메소드 응답이 모두 json
@RequestMapping("/emps")
public class EmpController {
    private final EmpService empService;   // 생성자 주입

    public EmpController(EmpService empService) { this.empService = empService; }

    @GetMapping("/list")
    public List<EmpDto> listEmp(){ return empService.listEmp(); }

    @GetMapping("/listEmpWhereIf")
    public List<EmpDto> listEmpWhereIf(@RequestParam Map<String, String> map){
        return empService.listEmpWhereIf(map);
    }
}
```

`@RequestParam Map<String,String>` 으로 쿼리스트링 전체를 Map 으로 받아 동적 SQL 에 그대로 전달한다.

### Controller - `PageController` (뷰 이동 전용)
데이터 응답 컨트롤러(`EmpController`)와 뷰 이동 컨트롤러를 분리했다.

```java
@Controller  // @ResponseBody 없음 -> 뷰 이름 반환
public class PageController {
    @GetMapping("/emps")     public String emps()     { return "emps"; }
    @GetMapping("/salaries") public String salaries() { return "salaries"; }
    @GetMapping("/stores")   public String stores()   { return "stores"; }
}
```

> 같은 `/emps` 경로라도 `PageController` 의 `GET /emps`(JSP)와 `EmpController` 의 `GET /emps/list`(JSON)는 하위 경로가 달라 충돌하지 않는다.

## 면접 Q&A

**Q. `mapUnderscoreToCamelCase` 는 무엇을 해결하나?**
A. DB 컬럼은 snake_case(`first_name`), 자바 프로퍼티는 camelCase(`firstName`)인 관례 차이를 자동 매핑한다. 켜두면 매 SQL 마다 `as` 별칭을 줄 필요가 없다. ([Configuration settings](https://mybatis.org/mybatis-3/configuration.html#settings))

**Q. `<resultMap>` 은 언제 쓰나?**
A. 컬럼명과 DTO 필드명이 자동 매핑으로 안 맞거나, DTO 일부 필드만 채우거나, 연관 객체(1:1, 1:N) 같은 복잡 매핑이 필요할 때. ([Result Maps](https://mybatis.org/mybatis-3/sqlmap-xml.html#result-maps))

**Q. `<where>` 를 직접 `WHERE 1=1` 로 안 쓰고 쓰는 이유는?**
A. `<where>` 는 조건이 하나라도 있을 때만 `WHERE` 를 넣고, 첫 조건 앞의 `AND`/`OR` 를 자동 제거한다. `1=1` 같은 편법 없이 깔끔한 SQL 이 만들어진다. ([Dynamic SQL](https://mybatis.org/mybatis-3/dynamic-sql.html))

**Q. 동일 namespace 매퍼를 두 파일로 나눠도 되나?**
A. 된다. namespace 가 같으면 같은 매퍼 인터페이스(`EmpDao`)에 매핑된다. 본 실습은 CRUD 와 검색/동적SQL 을 파일로 분리해 관리한다.

**Q. 이전 실습의 필드 주입 대신 생성자 주입으로 바꾼 이유는?**
A. `final` 로 불변성 보장, 필수 의존성 누락 시 컴파일/기동 단계에서 발견, 테스트 시 주입 용이. Spring 공식 권장 방식이다.

## 참고 출처

- [MyBatis 3 - Configuration / settings](https://mybatis.org/mybatis-3/configuration.html#settings)
- [MyBatis 3 - Result Maps](https://mybatis.org/mybatis-3/sqlmap-xml.html#result-maps)
- [MyBatis 3 - Dynamic SQL](https://mybatis.org/mybatis-3/dynamic-sql.html)
- [mybatis-spring - Mappers / @Mapper](https://mybatis.org/spring/mappers.html)
- [Spring Framework - @ResponseBody](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responsebody.html)
- pom.xml 의존성: `spring-boot-starter-parent` 4.1.0, `mybatis-spring-boot-starter` 4.0.1, `spring-boot-starter-webmvc`, `mysql-connector-j`, `tomcat-embed-jasper`, Java 21

## 더 나아가기 (선택) / 코드 점검

발견된 개선점:

1. **`insertEmp` 의 `${hireDate}`** — `emp-mapper.xml` insert 문에서 `hire_date` 만 `${hireDate}` 로 작성됨. `${}` 는 문자열 그대로 치환이라 SQL Injection 위험 및 따옴표 누락으로 오류 가능. `#{hireDate}` 로 바꿔야 안전하다.

2. **`listEmpLike` 의 `serchWord` 오타** — `EmpController` 의 `@RequestParam String serchWord` 가 오타(`searchWord` 아님). 요청 시 쿼리 파라미터를 `serchWord` 로 보내야 동작한다. 매퍼는 단일 파라미터라 내부 `#{searchWord}` 와는 무관하게 동작하지만, API 사용자 혼란을 줄이려면 철자 통일 권장.

추가 학습:
- `<foreach>` 로 IN 절 동적 생성
- 동적 SQL `<choose>`/`<set>`/`<trim>`
- `salaries.jsp`, `stores.jsp` 에 대응하는 데이터 API 추가
