# JPABasic_5 - Test.java / Test2.java 실험 복기

실행 파일이 둘로 나뉜다.
- `Test.java` : 기본키 생성 전략(`@GeneratedValue`) 실험 (`Employee`)
- `Test2.java` : 복합키 매핑 실험 (`Product` = `@IdClass`, `Student` = `@EmbeddedId`)

각 파일에서 번호 주석 블록을 하나씩 주석 해제하며 실행한다.
`persistence.xml`은 `show_sql=true` + `hbm2ddl.auto=update`.
현재 활성: `Test.java` `#2`, `Test2.java` `#4`.

---

## Test.java - 기본키 생성 전략

### #1. IDENTITY 전략으로 여러 건 persist
```java
Employee emp = new Employee();   // id 미지정
emp.setName("홍길동"); ...
em.persist(emp);                  // IDENTITY → INSERT 시 DB auto-increment가 PK 부여
```
- `@GeneratedValue(strategy=IDENTITY)` 상태에서 id를 세팅하지 않고 persist → DB가 PK 자동 부여.

### #2. AUTO 전략 관련 - 수동 id 지정  (현재 활성)
```java
Employee emp = new Employee();
emp.setId(1);   // 주석: 0은 오류 없이 처리(초기화 상태로 인식, id로 취급 X)
emp.setName("홍길동"); ...
em.persist(emp);
```
- **주석 의도**: `@GeneratedValue(AUTO)`로 바꾼 뒤 수동 id를 지정하면 전략 충돌로 오류가 날 수 있음을 확인하는 항목.
- `setId(0)`은 int 기본값이라 JPA가 "미지정"으로 인식해 오류 없이 넘어간다는 점을 주석이 짚음.

> 참고: `Employee`는 `IDENTITY`/`AUTO`/`SEQUENCE`/`TABLE`/`UUID` 전략을 주석으로 함께 담고 있어, 어노테이션을 바꿔가며 전략별 동작을 비교하도록 구성돼 있다.

---

## Test2.java - 복합키 매핑

### #1. @IdClass 복합키 - Product 생성
```java
Product p = new Product();
p.setCode("uplus"); p.setNumber(1); p.setColor("pink");
em.persist(p);
```
- `@IdClass(ProductKey.class)` + `code`/`number` 각각 `@Id` → (code, number) 복합 PK로 INSERT.

### #2. @IdClass 복합키 - Product 조회
```java
ProductKey key = new ProductKey();
key.setCode("uplus"); key.setNumber(1);
Product p = em.find(Product.class, key);   // find의 key 자리에 복합키 클래스 인스턴스
```
- 복합키 조회는 키 클래스 인스턴스를 `find` 2번째 인자로 전달. `equals`/`hashCode` 구현이 동일성 판단에 사용됨.
- **선행 조건**: (uplus, 1) 데이터 존재.

### #3. @EmbeddedId 복합키 - Student 생성
```java
StudentKey key = new StudentKey();
key.setCode("uplus"); key.setNumber(1);
Student s = new Student();
s.setId(key); s.setName("홍길동");
em.persist(s);
```
- `@Embeddable` 키(`StudentKey`)를 만들어 엔티티 `@EmbeddedId` 필드에 세팅 후 persist.

### #4. @EmbeddedId 복합키 - Student 조회  (현재 활성)
```java
StudentKey key = new StudentKey();
key.setCode("uplus"); key.setNumber(1);
Student s = em.find(Student.class, key);
```
- `@EmbeddedId` 방식도 조회는 동일하게 키 클래스 인스턴스를 `find`에 전달.
- **선행 조건**: (uplus, 1) Student 데이터 존재.

## 정리
- **생성 전략**(Test.java): `IDENTITY`는 id 미지정 시 DB가 부여. 자동 생성 전략과 수동 id 지정은 충돌할 수 있고, int `0`은 미지정으로 취급된다.
- **복합키**(Test2.java): 두 방식 모두 별도 키 클래스가 필요하고, 조회 시 키 클래스 인스턴스를 `find`에 넘긴다.
  - `@IdClass`: 엔티티 필드에 `@Id`, 키 클래스는 어노테이션 없음.
  - `@EmbeddedId`: 키 클래스가 `@Embeddable`, 엔티티는 필드 하나로 PK 표현.
- 키 클래스는 public/기본 생성자/`equals`·`hashCode`/`Serializable`를 갖춰야 한다.
