# JPABasic_1 - Test.java 실험 복기

## 실행 방식
`Test.java`의 번호 주석 블록(`#1`, `#2`)을 하나씩 주석 해제하며 실행한다.
공통 흐름은 `emf 생성 → em 생성 → tx begin → (블록) → tx commit → close`.

> 주의: `persistence.xml`에 `hbm2ddl.auto`가 없어 `product` 테이블은 미리 만들어 둬야 하고,
> `show_sql`도 없어 콘솔에 SQL이 찍히지 않는다 (DB에서 결과 확인).

## 실험 블록 (#1 ~ #2)

### #1. 1건 persist  (현재 주석 처리)
```java
Product p = new Product();
p.setId(1);
p.setName("Book");   // p는 아직 일반 자바 객체 (new 상태)
em.persist(p);       // 영속성 컨텍스트에 등록 — 이 시점 DB insert X (쓰기 지연)
```
- **실험 의도**: `persist` 호출만으로 INSERT가 나가지 않음(쓰기 지연)을 확인.
- **결과**: `persist` 시점엔 SQL 미수행, `commit` 시점에 `product` 1건 INSERT.

### #2. 여러 건 persist  (현재 활성)
```java
Product p2 = new Product(); p2.setId(2); p2.setName("Phone");
Product p3 = new Product(); p3.setId(3); p3.setName("Car");
em.persist(p2);
em.persist(p3);
```
- **실험 의도**: 여러 엔티티를 등록해도 INSERT가 `commit` 시점에 모여 나가는지 확인.
- **결과**: `commit` 시 `product` 2건(id=2 Phone, id=3 Car) INSERT.

## 정리
- `persist()`는 영속성 컨텍스트 **등록**일 뿐, SQL은 `flush`/`commit`에서 나간다 (쓰기 지연).
- `commit()`이 내부적으로 `flush()`를 호출해 모인 INSERT를 한 번에 확정한다.
- PK는 `@GeneratedValue` 없이 `setId`로 직접 지정 — 중복 id로 두 번 실행하면 PK 충돌 발생.
- `show_sql`/`hbm2ddl`이 꺼져 있어, SQL 확인과 테이블 준비는 직접 챙겨야 한다.
