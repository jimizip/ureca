import entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// persistence.xml 의 속성들을 추가해 가면서 개발 편의성 확인
public class Test {

	public static void main(String[] args) throws Exception{
		// persistence.xml 의 my-pu로 EntityManager를 EntityManagerFactory 로부터 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin(); // transaction 준비, 영속성 컨텍스트(1차 캐시)
		
		// #1. persist() 로 초기 데이터 적재
//		{
//			Employee e = new Employee();
//			e.setId(1);
//			e.setName("홍길동");
//			e.setAddress("서울");
//			
//			em.persist(e);
//		}
		
		// #2. hibernate.show_sql 추가 후 persist() 로 초기 insert sql 확인
//		{
//			Employee e = new Employee();
//			e.setId(2);
//			e.setName("이길동");
//			e.setAddress("부산");
//			
//			em.persist(e);
//		}
		
		// #3. hibernate.format_sql 추가 후 persist() 로 초기 format 된 insert sql 확인
//		{
//			Employee e = new Employee();
//			e.setId(3);
//			e.setName("삼길동");
//			e.setAddress("여주");
//			
//			em.persist(e);
//		}
		
		// #4. hibernate.hbm2ddl.auto 추가 후 create, update 차이 확인
		// create: 항상 시작 시 drop, create
		// update: 항상 시작 시 변경사항을 확인 후, 있으면 alter 적용
		
		// #5. flush() vs commit()
//		{
//			Employee e = new Employee();
//			e.setId(10);
//			e.setName("BeforeFlush");
//			e.setAddress("BBB");
//			
//			em.persist(e);
//			
//			// flush() 1차 캐쉬에 관리되는 엔티니 객체의 snapshot 부터 dirty check 변화 부분들을 DB에 반
//			// flush() 가 insert-update 자체에 변화를 주지 않는다.
//			// <- em.flush() 가 있어도, 없어도 insert-update로 처리.
//			
//			// em.flush(); // insert sql 수행, commit 은 아직 아니다.
//			
//			System.out.println("After Flush");
//			
//			e.setName("AfterFlush");
//		}
		
		// #6. flush() vs commit()
//		{
//			Employee e = new Employee();
//			e.setId(10);
//			e.setName("BeforeFlush");
//			e.setAddress("BBB");
//			
//			em.persist(e);
//			
//			em.flush(); // insert sql 수행, commit 은 아직 아니다.
//			
//			// 10초 대기
//			Thread.sleep(10000); // select 확인 x <- READ COMMITD. 10초 후, commit 수행 되고 확인 O
//			
//			System.out.println("After Flush");
//			
//			e.setName("AfterFlush");
//		}
		
		// #7. find() - select 수행 확인
//		{
//			Employee e =em.find(Employee.class, 10);
//			System.out.println(e);
//		}
		
		// #8. find() - Dirty Check
		//     select - update ( update 는 dirty check 가 여러번 발생해도 하나의 update 로 처리 )
		//              필드가 다른 dirty 도 동일
//		{
//			Employee e =em.find(Employee.class, 10);
//			System.out.println(e);
//			e.setAddress("제주"); // dirty 1
//			e.setAddress("제주2"); // dirty 2
//			e.setName("홍길동"); // dirty 3
//			System.out.println(e);
//		}
		
		// #9. merge()
//		{
			// 9-1. 테이블에 없는 객체 merge() - select & insert
//			Employee e = new Employee();
//			e.setId(2);
//			e.setName("이길동");
//			e.setAddress("강릉");
//			
//			em.merge(e);
			
			// 9-2. 테이블에 있는 객체 merge() - select only
//			Employee e = new Employee();
//			e.setId(2);
//			e.setName("이길동");
//			e.setAddress("강릉");
//			
//			em.merge(e);
			
			// 9-3. 테이블에 있는 객체 merge() & Dirty check - select & update
//			Employee e = new Employee();
//			e.setId(2);
//			e.setName("이길동");
//			e.setAddress("강릉");
//			
//			Employee e2 = em.merge(e);
//			e2.setAddress("부여");
//		}
		
		// #10. remove find() select & remove() delete
		{
			Employee e =em.find(Employee.class, 10);
			em.remove(e);
		}
		
	
		em.getTransaction().commit(); // Transaction 완료, 확정 -> DB 반영 ( 내부적으로 flush() 호출) -> 없으면 SQL에 등록 확정이 안됨
		
		em.close();
		emf.close();

	}

}
