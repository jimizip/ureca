# JPABasic_4 - persistence.xml 옵션과 flush vs commit

`persistence.xml`에 Hibernate 속성(`show_sql`, `format_sql`, `hbm2ddl.auto`)을 하나씩 추가하며
개발 편의성을 확인하고, `flush()`와 `commit()`의 차이를 실험하는 실습.

## 실습 목표
> Hibernate 실행 옵션의 효과(SQL 로깅, 스키마 자동 생성)와 `flush`/`commit`의 시점 차이를 이해한다.

## 엔티티/설정 분석

엔티티는 매핑 어노테이션 없는 단일 `Employee`(`@Id int id`, `name`, `address`). 이 폴더의 핵심은 **설정**이다.

### persistence.xml 추가 속성
| 속성 | 현재 값 | 효과 |
|------|--------|------|
| `hibernate.show_sql` | `true` (활성) | 생성된 SQL을 콘솔에 출력 |
| `hibernate.format_sql` | 주석 처리 | 켜면 SQL을 보기 좋게 줄바꿈/들여쓰기 |
| `hibernate.hbm2ddl.auto` | `update` (활성) | 시작 시 매핑과 스키마 차이를 감지해 `alter` 적용 |

- `hibernate.show_sql` : 생성 SQL 콘솔 로깅, 기본값 `false`. ([Hibernate User Guide A.3 - JDBC Settings](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#settings-jdbc))
- `hibernate.format_sql` : 로깅되는 SQL 포맷팅. ([Hibernate User Guide A.3 - JDBC Settings](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#settings-jdbc))
- `hibernate.hbm2ddl.auto` : 스키마 자동 관리 전략. `create`는 시작 시 항상 drop 후 create, `update`는 변경분만 alter. ([Hibernate User Guide A.18 - Schema Tooling Settings](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#settings-schema))

> JPABasic_1~3과 달리 `show_sql`/`hbm2ddl.auto`가 켜져 있어, SQL이 콘솔에 찍히고 테이블(`Employee`)이 자동 생성/변경된다.

### 실행 환경
- persistence-unit `my-pu`, `RESOURCE_LOCAL`, provider = Hibernate
- DB: `jdbc:mysql://localhost:3306/jpa_basic_4` (root / 1234)

## 핵심 개념

- **`flush()`**: 영속성 컨텍스트의 변경(Dirty)을 DB에 SQL로 반영(동기화)한다. 단 트랜잭션 커밋은 아니다. `flush` 호출 여부가 INSERT/UPDATE 처리 자체를 바꾸지는 않는다(어차피 commit 시 flush됨). ([Hibernate User Guide 7 - Flushing](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#flushing))
- **AUTO flush**: 기본 flush 모드. 커밋 직전, 겹치는 JPQL 실행 직전 등에 자동 flush된다. ([Hibernate User Guide 7.1 - AUTO flush](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#flushing-auto))
- **`flush()` vs `commit()`**: `flush`는 SQL을 DB로 보내지만 트랜잭션은 열려 있어, `READ COMMITTED` 격리 하에서 **다른 트랜잭션은 commit 전까지 그 변경을 볼 수 없다**(#6의 10초 대기 실험). `commit`이 트랜잭션을 확정해야 외부에서 보인다.
- **Dirty Checking과 UPDATE 병합**: 한 트랜잭션에서 같은 엔티티를 여러 번 변경해도 flush 시 **하나의 UPDATE**로 합쳐진다(#8). ([Hibernate User Guide 6.10 - Modifying managed state](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc-managed-state))
- **`merge()`의 select 선행**: `merge`는 대상 식별자로 먼저 SELECT 해 현재 DB 상태를 가져온 뒤 병합한다. 없으면 insert, 있고 변경 없으면 update 없음, 있고 변경 있으면 update. ([Hibernate User Guide 6.12.2 - Merging detached data](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc-merge))

## 면접 Q&A

**Q. `flush()`를 호출하면 commit이 되나?**
A. 아니다. `flush`는 영속성 컨텍스트의 변경을 SQL로 DB에 보내는 동기화일 뿐, 트랜잭션 확정은 `commit`이 한다. flush 후에도 롤백하면 반영이 취소된다.

**Q. `flush` 이후 다른 세션에서 그 데이터를 바로 조회할 수 있나?**
A. `READ COMMITTED` 격리 수준에서는 commit 전까지 보이지 않는다. flush로 SQL은 나갔지만 트랜잭션이 열려 있어 외부에서는 커밋된 데이터만 읽는다.

**Q. `hbm2ddl.auto`의 `create`와 `update` 차이는?**
A. `create`는 애플리케이션 시작 시 항상 기존 테이블을 drop하고 새로 만든다(데이터 소실). `update`는 매핑과 스키마 차이만 감지해 alter로 반영한다.

**Q. 같은 엔티티를 한 트랜잭션에서 3번 바꾸면 UPDATE가 3번 나가나?**
A. 아니다. Dirty Checking 결과가 flush 시 하나의 UPDATE로 합쳐져 나간다.

## 참고 출처
- [Hibernate 6.5 User Guide - Flushing / Configuration Settings](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#flushing)
- [Hibernate 6.5 User Guide - Appendix A: Configuration Settings](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#settings)
- pom.xml 의존성: `hibernate-core 6.5.2.Final`, `mysql-connector-j 8.3.0`

> Test.java 실험 복기는 [RECAP.md](RECAP.md) 참고
