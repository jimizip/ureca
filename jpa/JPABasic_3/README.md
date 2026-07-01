# JPABasic_3 - 엔티티 생명주기 전이와 매핑 어노테이션

`find` / `merge` / `detach` / `remove` 등 `EntityManager`의 생명주기 조작 메서드와,
`@Table` / `@Column` 매핑 어노테이션을 한 폴더에서 실험하는 실습.

## 실습 목표
> 비영속(new)-영속(managed)-준영속(detached)-삭제(removed) 상태 전이를 메서드별로 익히고, `@Table`/`@Column`으로 테이블/컬럼명을 재지정하는 법을 이해한다.

## 엔티티/설정 분석

엔티티 2개. 연관관계는 없고, `Employee`에서 매핑 어노테이션을 사용한다.

| 엔티티 | 매핑 | PK | 비고 |
|--------|------|----|----|
| `Product` | 기본 매핑 | `@Id int id` | 테이블/컬럼명 = 클래스/필드명 |
| `Employee` | `@Table(name="emp")` | `@Id @Column(name="emp_id") int id` | 테이블 `emp`, PK 컬럼 `emp_id`. `name`/`address`는 기본 매핑 |

- `@Table(name="emp")` : 엔티티가 매핑될 테이블명을 `emp`로 재지정. ([Jakarta Persistence Spec 11.1 - O/R Mapping Annotations](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#annotations-for-objectrelational-mapping))
- `@Column(name="emp_id")` : 필드가 매핑될 컬럼명을 `emp_id`로 재지정. ([Jakarta Persistence Spec 11.1.9 - Column Annotation](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a14330))
- `@Id` : PK 지정, `@GeneratedValue` 없어 애플리케이션이 직접 값 지정. ([Jakarta Persistence Spec 11.1.21 - Id Annotation](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a14827))

### 실행 환경 (`persistence.xml`)
- persistence-unit `my-pu`, `RESOURCE_LOCAL`, provider = Hibernate
- DB: `jdbc:mysql://localhost:3306/jpa_basic_3` (root / 1234)
- `show_sql`, `hbm2ddl.auto`, `dialect` **미설정** → SQL 콘솔 미출력, 테이블(`product`, `emp`) 사전 존재 필요

## 핵심 개념

- **엔티티 상태(4가지)**: 비영속(new), 영속(managed), 준영속(detached), 삭제(removed). ([Jakarta Persistence Spec 3.2 - Entity Instance's Life Cycle](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a1929))
- **`find()`**: PK로 DB에서 엔티티를 조회하며, 반환된 엔티티는 영속 상태로 관리된다(이후 변경은 Dirty Checking 대상). ([Hibernate User Guide 6 - Persistence Context](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc))
- **`merge()`**: 준영속/비영속 엔티티의 상태를 **새 영속 인스턴스에 복사**한다. 인자 객체가 아니라 반환된 객체가 영속 대상이다(`e != e2`). DB에 있으면 update, 없으면 insert처럼 동작. ([Hibernate User Guide 6.12.2 - Merging detached data](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc-merge), [Jakarta Spec 3.2.7.1 - Merging Detached Entity State](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#merging-detached-entity-state))
- **`detach()`**: 엔티티를 영속성 컨텍스트에서 분리한다. 분리 이후 변경은 추적되지 않아 commit에 반영되지 않는다. ([Hibernate User Guide 6.12 - Working with detached data](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc-detach))
- **`remove()`**: 영속 상태 엔티티를 삭제 대상으로 만든다. 비영속(new)/준영속(detached) 엔티티에 대한 remove는 `IllegalArgumentException`을 던진다(관리 상태여야 함). ([Hibernate User Guide 6.4 - Deleting entities](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc-remove), [Jakarta Spec 3.2.3 - Removal](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1#a1946))

## 면접 Q&A

**Q. `merge()`가 반환하는 객체와 인자로 넘긴 객체는 같은가?**
A. 다르다. `merge`는 인자 객체의 상태를 새 영속 인스턴스로 복사해 반환한다. 이후 Dirty Checking은 반환된 객체(`e2`)에만 적용되고, 인자 객체(`e`)의 변경은 반영되지 않는다.

**Q. `persist()`와 `merge()`의 차이는?**
A. `persist`는 비영속 엔티티를 영속화하는 용도(이미 관리 중이거나 PK 충돌 시 문제). `merge`는 준영속/비영속 상태를 병합해 영속 사본을 만든다. 식별자가 이미 DB에 있으면 update로 동작한다.

**Q. `detach` 후 필드를 바꾸면 DB에 반영되나?**
A. 안 된다. 준영속 상태라 변경 감지 대상이 아니고, commit 시 flush에도 포함되지 않는다.

**Q. `new` 로 만든 엔티티를 `remove` 하면?**
A. 관리 상태가 아니므로 `IllegalArgumentException`이 발생한다. remove는 영속 상태 엔티티에만 적용된다.

## 참고 출처
- [Hibernate 6.5 User Guide - Persistence Context](https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#pc)
- [Jakarta Persistence 3.1 Spec - Entity Operations / O/R Mapping](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1)
- pom.xml 의존성: `hibernate-core 6.5.2.Final`, `mysql-connector-j 8.3.0`

> Test.java 실험 복기는 [RECAP.md](RECAP.md) 참고
