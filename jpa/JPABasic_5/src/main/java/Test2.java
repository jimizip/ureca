import entity.Employee;
import entity.Product;
import entity.Student;
import entity.key.ProductKey;
import entity.key.StudentKey;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Persistence;

// persistence.xml 의 속성들을 추가해 가면서 개발 편의성 확인
public class Test2 {

	public static void main(String[] args) throws Exception{
		// persistence.xml 의 my-pu로 EntityManager를 EntityManagerFactory 로부터 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin(); // transaction 준비, 영속성 컨텍스트(1차 캐시)
		
		// #1. @IdClass 를 통한 복합키 처리 - Product 생성
//		{
//			Product p = new Product();
//			p.setCode("uplus");
//			p.setNumber(1);
//			p.setColor("pink");
//			
//			em.persist(p);
//		}
		
		// #2. @IdClass 를 통한 복합키 처리 - Product 조회
//		{
//			// 복합키 클래스 객체 생성
//			ProductKey key = new ProductKey();
//			key.setCode("uplus");
//			key.setNumber(1);
//			
//			// find 의 key 자리에 복합키 클래스 객체 전달
//			Product p = em.find(Product.class, key);
//			System.out.println(p);
//		}
		
		// #3. @Embeddable, @EmbeddedID 를 통한 복합키 처리 - Student 생성
//		{
//			StudentKey key = new StudentKey();
//			key.setCode("uplus");
//			key.setNumber(1);
//			
//			Student s = new Student();
//			s.setId(key);
//			s.setName("홍길동");
//			
//			em.persist(s);
//		}
		
		// #4. @Embeddable, @EmbeddedID 를 통한 복합키 처리 - Student 조회
		{
			StudentKey key = new StudentKey();
			key.setCode("uplus");
			key.setNumber(1);
			
			Student s = em.find(Student.class, key);
			System.out.println(s);
		}
		
	
		em.getTransaction().commit(); // Transaction 완료, 확정 -> DB 반영 ( 내부적으로 flush() 호출) -> 없으면 SQL에 등록 확정이 안됨
		
		em.close();
		emf.close();

	}

}
