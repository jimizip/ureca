# JPABasic_3 - Test.java 실험 복기

## 실행 방식
`Test.java`의 번호 주석 블록(`#1`~`#9`)을 하나씩 주석 해제하며 실행한다.
공통 흐름: `emf 생성 → em 생성 → tx begin → (블록) → tx commit → close`.
현재 활성 블록은 `#9`.

> 주의: `persistence.xml`에 `hbm2ddl.auto`/`show_sql`이 없어 테이블(`product`, `emp`)을 미리 만들어야 하고,
> SQL은 콘솔에 찍히지 않는다. `#2`, `#4`, `#7`, `#8`은 사전에 대상 데이터가 DB에 있어야 한다.

## 실험 블록 (#1 ~ #9)

### #1. Product 1건 persist
- 비영속 `Product`를 `persist` → commit 시 `product` INSERT. (JPABasic_1 복습)

### #2. find() + Dirty Check (Product)
```java
Product p = em.find(Product.class, 1);   // DB 조회 + 영속화
p.setName("Phone");                        // 영속 상태 변경 → Dirty Check
```
- `find`로 가져온 엔티티는 영속 상태 → `setName` 변경이 flush 시 UPDATE로 반영.
- **선행 조건**: id=1 데이터가 `product`에 존재해야 함.

### #3. persist - PK 중복 (Employee)
```java
Employee e = new Employee(); e.setId(1); ...
em.persist(e);
```
- 이미 `emp`에 emp_id=1이 있으면 flush 시 PK 제약 위반 → `PersistenceException`(제약 위반) 발생.
- **의도**: 이미 존재하는 PK로 persist 시 실패함을 확인.

### #4. find() + Dirty Check (Employee)
- `find(Employee.class, 1)` → 영속화, `setAddress("대전")` 변경 감지 → flush 시 UPDATE.
- **선행 조건**: emp_id=1 데이터 존재.

### #5. merge() - insert + 반환 객체 정체성
```java
Employee e = new Employee(); e.setId(3); ...
Employee e2 = em.merge(e);   // 영속 대상은 e가 아니라 반환값 e2
System.out.println(e == e2); // false
e.setAddress("ABC");   // e는 비영속 → 반영 X
e2.setAddress("DEF");  // e2는 영속 → Dirty Check 반영
```
- **핵심**: `b = merge(a)` 에서 영속화되는 건 반환값 `b`. `a`는 id 등 정보 제공용일 뿐.
- DB에 emp_id=3 없으면 INSERT. `e`의 변경은 무시, `e2`의 변경만 UPDATE.
- 주석 안내: `#5-1`(merge만) 테스트 후 3번 데이터 삭제하고 `#5-2` 테스트.

### #6. merge() - update
- DB에 emp_id=3이 이미 있으면 `merge`가 update로 동작. 반환 객체가 영속화됨.

### #7. detach() - 영속성 컨텍스트 분리
```java
Employee e = em.find(Employee.class, 2);
e.setAddress("ABC");
em.detach(e);           // 이 시점 이후 e는 준영속 → 추적 중단
e.setAddress("DEF");
```
- `detach` 후 변경(`DEF`)은 물론, detach 직전 변경(`ABC`)도 flush에 포함되지 않아 DB 미반영.
- **선행 조건**: emp_id=2 존재.

### #8-1. remove() - 영속 엔티티 삭제
```java
Employee e = em.find(Employee.class, 2);
em.remove(e);   // 영속 상태 → commit 시 DELETE
```
- **선행 조건**: emp_id=2 존재.

### #8-2. remove() - 새 객체 삭제 시도
```java
Employee e = new Employee(); e.setId(3); ...
em.remove(e);   // 비영속(new) → IllegalArgumentException
```
- 관리 상태가 아닌 엔티티 remove → 예외.

### #9. @Table / @Column 매핑 확인  (현재 활성)
```java
Employee e = new Employee(); e.setId(1); e.setName("일길동"); e.setAddress("대전");
em.persist(e);
```
- commit 시 `emp` 테이블에 INSERT, PK 컬럼은 `emp_id`. `@Table`/`@Column` 매핑이 실제 SQL 대상 테이블/컬럼명에 반영됨.

## 정리
- 상태 전이 메서드: `persist`(new→managed), `find`(DB→managed), `merge`(detached/new→managed 사본), `detach`(managed→detached), `remove`(managed→removed).
- `merge`는 **반환 객체가 영속 대상** — 인자 객체는 영속화되지 않는다 (#5의 `e != e2`).
- `remove`/`persist`는 상태 전제가 있다: remove는 영속 상태만, persist는 신규 PK여야 한다 (#3, #8-2 예외).
- `detach` 후에는 detach 이전 변경도 flush에 반영되지 않는다 (#7).
- `@Table`/`@Column`으로 매핑 테이블/컬럼명을 재지정할 수 있다 (#9).
