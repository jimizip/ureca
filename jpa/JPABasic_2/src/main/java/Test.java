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
		
		// #1. 반복문을 통한 여러 건 등록
//		for (int i = 1; i <= 3; i++) {
//			Product p = new Product();
//			p.setId(i);
//			p.setName("Phone" + i);
//			em.persist(p); // 이 때 insert()가 일어나는게 아니고, 세 건 다 영속성 컨텍스트의 스냅샷으로 들어가는 거임.
//		}
		
		// #2. Dirty Check - 최초 persist 후 변경 처리
		{
			Product p = new Product();
			p.setId(4);
			p.setName("Watch");
			
			em.persist(p); // 영속성 컨텍스트에 p 객체의 snapshot 등록
			
			p.setName("Glasses"); // 변경 된 내용이 들어가 있음 / snapshot과 다른 객체의 변화(dirty)를 자동 감지, 변경, 추적
		}
		
		em.getTransaction().commit(); // Transaction 완료, 확정 -> DB 반영 ( 내부적으로 flush() 호출) -> 없으면 SQL에 등록 확정이 안됨
		
		em.close();
		emf.close();

	}

}
