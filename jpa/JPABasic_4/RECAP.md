# JPABasic_4 - Test.java 실험 복기

## 실행 방식
`Test.java`의 번호 주석 블록(`#1`~`#10`)을 하나씩 주석 해제하며 실행한다.
`#2`~`#4`는 실행뿐 아니라 **`persistence.xml`의 속성을 추가/변경하며** 효과를 관찰하는 게 목적이다.
현재 활성 블록은 `#10`, `persistence.xml`은 `show_sql=true` + `hbm2ddl.auto=update` (`format_sql`은 주석).

## 실험 블록 (#1 ~ #10)

### #1. persist() 초기 데이터 적재
- `show_sql` 없던 상태 기준으로 `Employee(1, 홍길동, 서울)` persist. SQL은 콘솔에 안 보임.

### #2. show_sql 추가 후 persist
- `persistence.xml`에 `hibernate.show_sql=true` 추가 → 콘솔에 INSERT SQL이 출력됨을 확인.

### #3. format_sql 추가 후 persist
- `hibernate.format_sql=true` 추가 → INSERT SQL이 줄바꿈/들여쓰기된 형태로 출력됨을 확인.

### #4. hbm2ddl.auto - create vs update
```
create: 시작 시 항상 drop 후 create (데이터 소실)
update: 시작 시 매핑-스키마 차이만 alter 적용
```
- 코드 블록 없이 설정 값을 바꿔가며 스키마 동작 차이를 관찰하는 항목.

### #5. flush() vs commit() - flush 없이
```java
em.persist(e);         // id=10
// em.flush();         // 주석: flush 호출 안 함
e.setName("AfterFlush");
```
- flush 호출 유무와 무관하게 결과는 동일: commit 시 최종 상태로 처리됨.
- **의도**: `flush()`가 INSERT/UPDATE 처리 자체를 바꾸지 않음을 확인(어차피 commit이 flush).

### #6. flush() vs commit() - flush + 10초 대기
```java
em.persist(e);
em.flush();            // INSERT SQL 수행 — 단 commit은 아님
Thread.sleep(10000);   // 이 10초 동안 다른 세션에서 조회 X (READ COMMITTED)
e.setName("AfterFlush");
```
- flush로 SQL은 나갔지만 트랜잭션 미확정 → 대기 중 외부 세션은 데이터를 못 봄. commit 후에야 보임.
- **핵심**: flush ≠ commit, 격리 수준(READ COMMITTED)이 가시성을 결정.

### #7. find() - select 확인
- `find(Employee.class, 10)` → SELECT 1회 수행, 엔티티 출력.

### #8. find() + Dirty Check (다중 변경)
```java
Employee e = em.find(Employee.class, 10);  // SELECT
e.setAddress("제주");   // dirty 1
e.setAddress("제주2");  // dirty 2
e.setName("홍길동");    // dirty 3
```
- 여러 번 변경해도 flush 시 **UPDATE 한 번**으로 병합. (SELECT + 단일 UPDATE)

### #9. merge() 3가지 경우
- **9-1. DB에 없는 객체**: `merge` → SELECT(없음 확인) + INSERT.
- **9-2. DB에 있고 변경 없음**: `merge` → SELECT만 (반영할 변경 없음).
- **9-3. DB에 있고 반환 객체 변경**: `Employee e2 = em.merge(e); e2.setAddress("부여");` → SELECT + UPDATE. 변경은 반환값 `e2`에 적용.

### #10. remove() - select + delete  (현재 활성)
```java
Employee e = em.find(Employee.class, 10);  // SELECT
em.remove(e);                               // 영속 엔티티 → commit 시 DELETE
```
- `find`로 영속화한 뒤 `remove` → commit 시 DELETE.
- **선행 조건**: id=10 데이터 존재.

## 정리
- `persistence.xml` 옵션: `show_sql`(SQL 로깅), `format_sql`(포맷), `hbm2ddl.auto`(create=매번 재생성 / update=차이만 alter).
- `flush()`는 SQL을 DB로 보내는 동기화, `commit()`은 트랜잭션 확정 — 둘은 다르다 (#5, #6).
- READ COMMITTED 하에서 flush 후 commit 전 데이터는 외부에서 안 보인다 (#6).
- 한 트랜잭션 내 다중 변경은 단일 UPDATE로 병합된다 (#8).
- `merge`는 SELECT 선행 후 상태에 따라 insert/update/무변경으로 갈린다 (#9).
