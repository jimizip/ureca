# JPABasic_1 - JPA 기본 환경과 엔티티 영속화

Spring 없이 로컬 `main`에서 JPA(Hibernate) 실행 환경을 띄우고,
가장 단순한 엔티티(`Product`) 1건/여러 건을 `persist` 하는 첫 실습.

## 실습 목표
> `EntityManager` 생성 → 트랜잭션 → `persist` → `commit` 까지 JPA 기본 흐름과 엔티티 매핑(`@Entity`, `@Id`)을 익힌다.

## 엔티티/설정 분석

연관관계가 없는 단일 엔티티라 매핑표 대신 구성 요소로 정리한다.

### Product 엔티티
| 요소 | 코드 | 의미 |
|------|------|------|
| 엔티티 선언 | `@Entity` | JPA가 관리하는 클래스, DB 테이블과 매핑 |
| 기본키 | `@Id private int id` | 영속 대상 엔티티는 반드시 PK 필드 필요, `@Id`로 표시 |
| 일반 필드 | `private String name` | 별도 어노테이션 없으면 동일 이름 컬럼으로 매핑 |

- `@GeneratedValue`가 없어 PK는 **애플리케이션이 직접 지정**한다 (`p.setId(2)`).
- 테이블/컬럼명 미지정 → 클래스/필드명 기준 기본 매핑(`Product`, `id`, `name`).

### 실행 환경 (`persistence.xml`)
| 항목 | 값 | 비고 |
|------|----|----|
| persistence-unit | `my-pu` | `Persistence.createEntityManagerFactory("my-pu")`로 로딩 |
| transaction-type | `RESOURCE_LOCAL` | 서버(JTA) 없이 로컬에서 직접 트랜잭션 관리 |
| provider | `HibernatePersistenceProvider` | JPA 구현체로 Hibernate 사용 |
| DB | `jdbc:mysql://localhost:3306/jpa_basic_1` | root / 1234 |

- `hibernate.show_sql`, `hibernate.hbm2ddl.auto`, `dialect` 설정이 **없다**.
  → 실행 시 SQL이 콘솔에 출력되지 않고, 테이블 자동 생성도 안 된다.
  → `product` 테이블이 **DB에 미리 존재해야** insert가 성공한다.

## 핵심 개념

- **EntityManagerFactory / EntityManager**: `EntityManagerFactory`는 앱당 1개 생성하는 무거운 객체, `EntityManager`는 작업 단위마다 생성하는 가벼운 객체. ([Jakarta Persistence Spec 3.1.1 - EntityManager Interface](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a1066))
- **영속성 컨텍스트(Persistence Context)**: 엔티티를 관리하는 1차 캐시. `persist`된 엔티티는 여기서 관리된다. ([Hibernate User Guide - Persistence Context](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc))
- **쓰기 지연(transactional write-behind)**: `persist` 시점엔 INSERT SQL을 바로 보내지 않고, `flush`/`commit` 시점에 모아서 DB에 반영한다. ([Hibernate User Guide - Flushing](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#flushing))
- **엔티티 생명주기**:
  - `persist()` : new 객체를 영속성 컨텍스트에 등록 (관리 시작), SQL 미수행
  - `flush()` : 관리 중인 변경을 DB에 반영 (new → insert, 변경 → update)
  - `commit()` : 내부적으로 `flush()` 호출 후 트랜잭션 확정
  ([Jakarta Persistence Spec 3.2 - Entity Instance's Life Cycle](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a1929))

## 면접 Q&A

**Q. `persist()`를 호출하면 바로 DB에 INSERT 되나?**
A. 아니다. `persist`는 엔티티를 영속성 컨텍스트에 등록만 한다. 실제 INSERT는 쓰기 지연으로 `flush`/`commit` 시점에 나간다. (Test.java 주석 #1 참고)

**Q. `commit()`을 빼면 어떻게 되나?**
A. `commit`이 내부적으로 `flush()`를 호출해 SQL을 확정한다. 빠뜨리면 트랜잭션이 확정되지 않아 DB에 반영되지 않는다.

**Q. PK를 직접 지정하는데 `@GeneratedValue`가 왜 없나?**
A. 이 실습은 PK 생성 전략을 다루기 전 단계라 애플리케이션이 `setId`로 직접 값을 넣는다. 자동 생성이 필요하면 `@GeneratedValue`를 추가해야 한다.

## 참고 출처
- [Jakarta Persistence 3.1 Spec - Entity / EntityManager](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1)
- [Hibernate 6.5 User Guide - Persistence Context / Flushing](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html)
- pom.xml 의존성: `hibernate-core 6.5.2.Final`, `mysql-connector-j 8.3.0`

> Test.java 실험 복기는 [RECAP.md](RECAP.md) 참고
