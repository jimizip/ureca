import entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// 스프링 또는 별도의 서버 없이 로컬 메인에서 JQA Hibernate 실행 환경 구
public class Test {

	public static void main(String[] args) {
		// persistence.xml 의 my-pu로 EntityManager를 EntityManagerFactory 로부터 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin(); // transaction 준비, 영속성 컨텍스트(1차 캐시)
		
		// #1. 1건 persist
//		{
//			Product p = new Product();
//			p.setId(1);
//			p.setName("Book"); // p는 현재 일반 자바 객체 (new)
//			
//			em.persist(p); // p가 영속성 컨텍스트에 등록 <= 이 시점에 DB insert X (쓰기 지연)
//		}
		
		// #2. 여러 건 persist
		{
			Product p2 = new Product();
			p2.setId(2);
			p2.setName("Phone");
			
			Product p3 = new Product();
			p3.setId(3);
			p3.setName("Car");
			
			em.persist(p2);
			em.persist(p3);
		}
		
		// persist() : New 객체를 영속성 컨텍스트에 추가 (snapshot 등록 - 관리), SQL 관련 X
		// flush() : 영속성 컨텍스트에 관리되는 객체 -> DB 반영, 새로운 객체 - insert, 기존 객체 - update.. SQL 수행
		// commit() : DB 반영 확정
		em.getTransaction().commit(); // Transaction 완료, 확정 -> DB 반영 ( 내부적으로 flush() 호출) -> 없으면 SQL에 등록 확정이 안됨
		
		em.close();
		emf.close();

	}

}
