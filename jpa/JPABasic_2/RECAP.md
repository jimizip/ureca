# JPABasic_2 - Test.java 실험 복기

## 실행 방식
`Test.java`의 번호 주석 블록(`#1`, `#2`)을 하나씩 주석 해제하며 실행한다.
공통 흐름: `emf 생성 → em 생성 → tx begin → (블록) → tx commit → close`.

> 주의: `persistence.xml`에 `hbm2ddl.auto`/`show_sql`이 없어 `product` 테이블을 미리 만들어야 하고,
> SQL은 콘솔에 찍히지 않는다 (DB에서 결과 확인).

## 실험 블록 (#1 ~ #2)

### #1. 반복문으로 여러 건 등록  (현재 주석 처리)
```java
for (int i = 1; i <= 3; i++) {
    Product p = new Product();
    p.setId(i);
    p.setName("Phone" + i);
    em.persist(p);   // 이 시점 insert X — 세 건 모두 영속성 컨텍스트 스냅샷으로 등록
}
```
- **실험 의도**: `persist` 호출 시 즉시 INSERT가 아니라, 세 건이 영속성 컨텍스트에 쌓였다가 commit에 모여 나감(쓰기 지연)을 확인.
- **결과**: commit 시 `product` 3건(id=1~3, Phone1~3) INSERT.

### #2. Dirty Check - persist 후 변경  (현재 활성)
```java
Product p = new Product();
p.setId(4);
p.setName("Watch");
em.persist(p);          // 영속성 컨텍스트에 p 스냅샷 등록
p.setName("Glasses");   // 영속 상태 객체 변경 → 변경 감지 대상
```
- **실험 의도**: 영속 상태 엔티티의 필드 변경이 별도 update 호출 없이 자동 반영되는지 확인.
- **결과**: commit 시 `product` 1건 INSERT, `name = 'Glasses'`.
  - 신규 엔티티라 INSERT 자체가 쓰기 지연으로 commit까지 미뤄짐 → flush 시점 최종값(`Glasses`)으로 **INSERT 한 번**만 나감. 별도 UPDATE 없음.
  - 만약 `find`로 가져온 기존 영속 엔티티를 바꿨다면 flush 시 UPDATE가 생성됐을 것 (전형적 Dirty Checking).

## 정리
- `persist()`는 등록일 뿐, INSERT는 flush/commit에서 나간다 (쓰기 지연) — #1.
- 영속 상태 엔티티 변경은 스냅샷 비교로 자동 감지되어 flush 때 반영된다 (Dirty Checking) — #2.
- 단, 신규 엔티티는 commit 전 변경 시 INSERT 한 번에 최종값이 실린다. INSERT+UPDATE 아님.
- 변경 감지는 영속(managed) 상태에서만 동작.
