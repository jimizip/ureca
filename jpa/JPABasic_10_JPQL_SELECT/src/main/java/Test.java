import entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class Test {

	public static void main(String[] args) throws Exception{
		// persistence.xml 의 my-pu 로 EntityManager 를 EntityManagerFactory 로부터 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();
		
		// #1. find() - id 로 단건 조회
//		{
//			Product p = em.find(Product.class, 7);
//			System.out.println(p);
//		}
		
		// JPQL
		
		// #2. Product 전체 조회
//		{
//			String jpql = "select p from Product p";
//			TypedQuery<Product> query = em.createQuery(jpql, Product.class);
////			query.getResultList().forEach( p -> System.out.println(p));
//			query.getResultList().forEach( System.out::println);
//		}
		
		// #3. Product id, name, price 만 조회
//		{
//			String jpql = "select p.id, p.name, p.price from Product p";
//			em.createQuery(jpql, Object[].class).getResultList()
//					.forEach( o -> System.out.println( o[0] + ", " + o[1] + ", " + o[2]) );
//		}		
		
		// #4. Product 전체 조회, 단, 가격 > 2000
//		{
//			String jpql = "select p from Product p where p.price > 2000";
//			em.createQuery(jpql, Product.class).getResultList()
//					.forEach( p -> System.out.println(p) );
//		}			
		
		// #5. Parameter - named parameter, Product 전체 조회, 단, 가격 > 2000 이고 수량 >= 40 
//		{
//			String jpql = """
//					select p from Product p
//					 where p.price > :price
//					   and p.quantity >= :quantity
//					""";
//			em.createQuery(jpql, Product.class)
//					.setParameter("price", 2000)
//					.setParameter("quantity", 40)
//					.getResultList()
//					.forEach( p -> System.out.println(p) );
//		}	
		
		// #6. Parameter - positional parameter, Product 전체 조회, 단, 가격 > 2000 이고 수량 >= 40 
//		{
//			String jpql = """
//					select p from Product p
//					 where p.price > ?1
//					   and p.quantity >= ?2
//					""";
//			em.createQuery(jpql, Product.class)
//					.setParameter(1, 2000)
//					.setParameter(2, 40)
//					.getResultList()
//					.forEach( p -> System.out.println(p) );
//		}
		
		// #7. like - Product 전체 조회, 단, 가격 > 2000 이고 country 에 'k' 가 포함된 상품 
//		{
//			String jpql = """
//					select p from Product p
//					 where p.price > :price
//					   and p.country like :country
//					""";
//			em.createQuery(jpql, Product.class)
//					.setParameter("price", 2000)
//					.setParameter("country", "%k%")
//					.getResultList()
//					.forEach( p -> System.out.println(p) );
//		}
		
		// #8. count - Product 의 country 전체 건수
//		{
//			String jpql = """
//					select count(p.country) from Product p
//					""";
//			Long cnt = em.createQuery(jpql, Long.class).getSingleResult();
//			System.out.println(cnt);
//		}
		
		// #9. avg - Product 의 평균 가격 (pirce)
//		{
//			String jpql = """
//					select avg(p.price) from Product p
//					""";
//			Double avg = em.createQuery(jpql, Double.class).getSingleResult();
//			System.out.println(avg);
//		}
		
		// #10. sum, min, max - Product 의 수량 (quantity)
//		{
//			String jpql = """
//					select sum(p.quantity), min(p.quantity), max(p.quantity) from Product p
//					""";
//			Object[] row = em.createQuery(jpql, Object[].class).getSingleResult();
//			System.out.println(row[0] + ", " + row[1] + ", " + row[2]);
//		}
				
		// #11. sum, min, max - 국가별 Product 의 수량 (quantity)
//		{
//			String jpql = """
//					select p.country, sum(p.quantity), min(p.quantity), max(p.quantity) 
//					  from Product p
//					 group by p.country
//					""";
//			em.createQuery(jpql, Object[].class).getResultList()
//					.forEach( a -> System.out.println(a[0] + ", " + a[1] + ", " + a[2] + ", " + a[3]));
//		}
		
		// #12. DB 의 내장 함수 <= 추천 X ( JPA 는 DB 독립적 ) - country - ifnull
		{
			String jpql = """
					select p.name, p.price, ifnull(p.country, 'unknown')
					  from Product p
					""";
			em.createQuery(jpql, Object[].class).getResultList()
					.forEach( a -> System.out.println(a[0] + ", " + a[1] + ", " + a[2] ));
		}
		
		em.close();
		emf.close();
	}

}






















