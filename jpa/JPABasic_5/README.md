# JPABasic_5 - 기본키 생성 전략과 복합키 매핑

기본키 자동 생성 전략(`@GeneratedValue`)과 복합키 매핑 두 방식(`@IdClass`, `@EmbeddedId`)을
서로 다른 엔티티로 실험하는 실습. 실행 파일이 `Test.java`(생성 전략)와 `Test2.java`(복합키)로 나뉜다.

## 실습 목표
> `@GeneratedValue`의 전략별 차이와, 복합키를 `@IdClass` / `@EmbeddedId`로 매핑하는 두 가지 방법을 이해한다.

## 엔티티 매핑 분석

| 엔티티 | PK 방식 | 매핑 어노테이션 | 복합키 클래스 |
|--------|---------|----------------|--------------|
| `Employee` | 단일 PK + 자동 생성 | `@Id @GeneratedValue(strategy=IDENTITY)` | - |
| `Product` | 복합 PK | `@IdClass(ProductKey.class)` + 각 필드에 `@Id` | `ProductKey` (엔티티 외부, 어노테이션 없음) |
| `Student` | 복합 PK | `@EmbeddedId StudentKey id` | `StudentKey` (`@Embeddable`) |

### 기본키 생성 전략 (`Employee`)
`GenerationType` enum은 `{ TABLE, SEQUENCE, IDENTITY, UUID, AUTO }`. ([Jakarta Persistence Spec 11.1.20 - GeneratedValue Annotation](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a14790))
- `IDENTITY` : DB의 identity 컬럼(Auto Increment) 사용. 코드 주석상 권장.
- `SEQUENCE` : DB 시퀀스 사용.
- `TABLE` : 별도 키 관리 테이블로 PK 생성.
- `UUID` : RFC 4122 UUID 생성.
- `AUTO` : provider가 DB에 맞게 전략 자동 선택(기본값).

### 복합키 두 방식
- **`@IdClass`** (`Product`): 엔티티의 각 PK 필드에 `@Id`를 달고, 별도 키 클래스를 `@IdClass`로 지정. 키 클래스는 엔티티 밖에 있고 어노테이션이 없다. ([Jakarta Persistence Spec 11.1.22 - IdClass Annotation](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a14836))
- **`@EmbeddedId`** (`Student`): 키 클래스를 `@Embeddable`로 만들고, 엔티티는 그 타입 필드 하나에 `@EmbeddedId`를 단다. ([Jakarta Persistence Spec 11.1.17 - EmbeddedId Annotation](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a14687))
- **복합키 클래스 공통 조건**: public, 기본 생성자, `equals`/`hashCode` 구현, `Serializable` 구현. (`ProductKey`, `StudentKey` 모두 준수) ([Jakarta Persistence Spec 2.4 - Primary Keys and Entity Identity](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a132))

### 실행 환경 (`persistence.xml`)
- persistence-unit `my-pu`, `RESOURCE_LOCAL`, provider = Hibernate
- DB: `jdbc:mysql://localhost:3306/jpa_basic_5` (root / 1234)
- `show_sql=true`, `hbm2ddl.auto=update` (SQL 콘솔 출력 + 스키마 자동 관리)

## 핵심 개념

- **`@GeneratedValue`**: 기본키 값 생성 전략을 지정한다. 단순 PK에만 사용하며, `strategy` 기본값은 `AUTO`. ([Jakarta Persistence Spec 11.1.20 - GeneratedValue](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a14790))
- **복합키(Composite Primary Key)**: 두 개 이상의 컬럼으로 이뤄진 PK. 반드시 키 클래스를 정의해야 하며, `@IdClass` 또는 `@EmbeddedId`로 표현한다. ([Jakarta Persistence Spec 2.4 - Primary Keys and Entity Identity](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a132))
- **복합키 조회**: `find`의 두 번째 인자에 키 클래스 인스턴스를 넘긴다. 이때 `equals`/`hashCode`가 영속성 컨텍스트의 동일성 판단에 쓰이므로 반드시 구현해야 한다. ([Jakarta Persistence Spec 2.4](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a132))

## 면접 Q&A

**Q. `@IdClass`와 `@EmbeddedId`의 차이는?**
A. `@IdClass`는 엔티티 각 PK 필드에 `@Id`를 달고 외부 키 클래스를 `@IdClass`로 연결하는 방식(키 클래스에 어노테이션 없음). `@EmbeddedId`는 키 클래스를 `@Embeddable`로 만들고 엔티티에 그 타입 필드 하나로 PK를 표현하는 방식. 후자가 PK를 객체로 묶어 다루기 편하다.

**Q. 복합키 클래스가 지켜야 할 조건은?**
A. public 접근 제어, 기본 생성자, `equals`/`hashCode` 구현, `Serializable` 구현. `equals`/`hashCode`는 영속성 컨텍스트가 엔티티 동일성을 식별자로 판단하는 데 필요하다.

**Q. `IDENTITY`와 `SEQUENCE` 전략의 차이는?**
A. `IDENTITY`는 DB의 auto-increment(identity) 컬럼에 의존해 INSERT 시점에 PK가 정해진다. `SEQUENCE`는 DB 시퀀스 객체에서 값을 미리 받아 사용한다. MySQL은 시퀀스가 없어 보통 IDENTITY를 쓴다.

## 참고 출처
- [Jakarta Persistence 3.1 Spec - Primary Keys / GeneratedValue / IdClass / EmbeddedId](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a132)
- [Hibernate 6.5 User Guide](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html)
- pom.xml 의존성: `hibernate-core 6.5.2.Final`, `mysql-connector-j 8.3.0`

> Test.java / Test2.java 실험 복기는 [RECAP.md](RECAP.md) 참고
