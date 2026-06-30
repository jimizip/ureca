# JPABasic_2 - 변경 감지(Dirty Checking)와 쓰기 지연

JPABasic_1과 동일한 단일 엔티티(`Product`) 환경에서,
영속 상태 엔티티의 **변경 감지(Dirty Checking)** 동작을 확인하는 실습.

## 실습 목표
> 영속성 컨텍스트가 관리하는 엔티티의 필드를 바꾸면 별도 update 호출 없이 자동 반영(변경 감지)됨을 이해한다.

## 엔티티/설정 분석

JPABasic_1과 동일하다 (연관관계 없는 단일 엔티티).

### Product 엔티티
| 요소 | 코드 | 의미 |
|------|------|------|
| 엔티티 선언 | `@Entity` | JPA가 관리하는 클래스, DB 테이블과 매핑 |
| 기본키 | `@Id private int id` | `@GeneratedValue` 없이 애플리케이션이 직접 PK 지정 |
| 일반 필드 | `private String name` | 동일 이름 컬럼으로 기본 매핑 |

### 실행 환경 (`persistence.xml`)
- persistence-unit `my-pu`, `RESOURCE_LOCAL`, provider = Hibernate
- DB: `jdbc:mysql://localhost:3306/jpa_basic_1` (root / 1234)
- `show_sql`, `hbm2ddl.auto`, `dialect` **미설정** → SQL 콘솔 미출력, `product` 테이블 사전 존재 필요

## 핵심 개념

- **변경 감지(Dirty Checking)**: 영속 상태(managed) 엔티티는 애플리케이션이 필드를 바꾸면 그 변경이 flush 시점에 자동 감지되어 DB에 반영된다. 별도의 update 메서드 호출이 필요 없다. ([Hibernate User Guide 6.10 - Modifying managed/persistent state](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc-managed-state))
- **스냅샷(snapshot) 기반 비교**: 영속성 컨텍스트는 엔티티를 관리할 때 최초 상태의 스냅샷을 보관하고, flush 시 현재 상태와 비교해 달라진 부분을 SQL로 변환한다. ([Hibernate User Guide 7 - Flushing](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#flushing))
- **쓰기 지연(transactional write-behind)**: 변경은 메모리에 먼저 쌓이고 flush/commit 시점에 모아서 DB로 나간다. ([Hibernate User Guide 7 - Flushing](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#flushing))

## 면접 Q&A

**Q. 영속 상태 엔티티의 값을 바꾸면 왜 update를 호출 안 해도 반영되나?**
A. 영속성 컨텍스트가 변경 감지(Dirty Checking)를 한다. flush 시점에 스냅샷과 현재 상태를 비교해 달라진 부분을 UPDATE로 만들어 보낸다.

**Q. `persist` 직후 같은 트랜잭션에서 필드를 바꾸면 INSERT가 두 번 나가나, INSERT 후 UPDATE가 나가나?**
A. 둘 다 아니다. 신규 엔티티의 INSERT 자체가 쓰기 지연으로 commit까지 미뤄지므로, flush 시점의 최종 필드값으로 INSERT 한 번만 나간다. (RECAP #2 참고)

**Q. 변경 감지가 동작하려면 엔티티가 어떤 상태여야 하나?**
A. 영속(managed) 상태여야 한다. 준영속(detached)이나 비영속(new) 상태에서는 자동 감지 대상이 아니다.

## 참고 출처
- [Hibernate 6.5 User Guide - Persistence Context / Flushing](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html)
- [Jakarta Persistence 3.1 Spec - Entity Instance's Life Cycle](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a1929)
- pom.xml 의존성: `hibernate-core 6.5.2.Final`, `mysql-connector-j 8.3.0`

> Test.java 실험 복기는 [RECAP.md](RECAP.md) 참고
