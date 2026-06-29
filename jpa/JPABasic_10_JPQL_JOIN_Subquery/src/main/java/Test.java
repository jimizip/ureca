import java.time.LocalDate;

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
		
		// #1. Orders + Product 선택 조인
		//     orders 와 product 를 조인, 주문, 상품을 함께 조회
		//     where 절에 조인 조건 <= 암묵적 조인
//		{
//			String jpql = """
//					select o, p from Orders o, Product p where o.product = p
//					""";
//			
//			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1]));
//		}

		// #2. Orders + Product 선택 조인
		//     orders 와 product 를 조인, 주문, 상품을 함께 조회
		//     #1 과 동일한 처리, 명시적 조인
//		{
//			String jpql = """
//					select o, p from Orders o inner join o.product p
//					""";
//			
//			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1]));
//		}		
		
		// #3. 3 테이블 모두 조인 - 묵시적 조인
		//     orders, product, customer 함께 조회
//		{
//			String jpql = """
//					select o, p, c
//					  from Orders o, Product p, Customer c 
//					 where o.product = p
//					   and o.customer = c
//					""";
//			
//			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1]+ ", " + a[2]));
//		}		
		
		// #4. 3 테이블 모두 조인 - 명시적 조인
		//     orders, product, customer 함께 조회
//		{
//			String jpql = """
//					select o, p, c
//					  from Orders o inner join o.product p
//					                inner join o.customer c
//					""";
//			
//			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1]+ ", " + a[2]));
//		}
				
		// #5. 필요한 필드만 선택 
		//     orders, product, customer 조인, 주문 id, 수량, 상품 id, 가격, 고객 이름만 projection 
//		{
//			String jpql = """
//					select o.id, o.orderQuantity, p.id, p.price, c.name
//					  from Orders o inner join o.product p
//					                inner join o.customer c
//					""";
//			
//			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1] + ", " + a[2] + ", " + a[3] + ", " + a[4]));
//		}	
		
		// #6. left (outer) join
		//     product 기준, 상품id, 상품명, 주문 수량, 주문 일자 <= 주문이 없는 상품까지 포함
//		{
//			String jpql = """
//					select p.id, p.name, o.orderQuantity, o.orderDate
//					  from Product p left join p.orders o
//					""";
//			
//			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1] + ", " + a[2] + ", " + a[3] ));
//		}	
		
		
		// #7. join + where and
		//     product 기준, 상품명, 상품가격, 주문 수량, 주문 일자 <= 상품가격 1000 초과, 주문 수량 3 인 건
//		{
//			String jpql = """
//					select p.name, p.price, o.orderQuantity, o.orderDate
//					  from Product p join p.orders o
//					 where p.price > 1000
//					   and o.orderQuantity = 3
//					""";
//			
//			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1] + ", " + a[2] + ", " + a[3] ));
//		}
		
		// #8. subquery + in
		//     orders 에서 상품 가격이 4000 미만인 주문 전체, 상품가격 <= subquery 이용
		//     select o, o.product.price <= o.product.price 에 의해 left join 수행
		//     @ManyToOne 관계에서 defualt optional=true <= join, left join 상관없고, 관계 컬럼의 값이 null 허용 또는 not null 선택과 관련
		{
			String jpql = """
					select o, o.product.price
					  from Orders o
					 where o.product in ( select p from Product p where p.price < 4000 )
					""";
			
			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1] ));
		}
		
		// #9. subquery - select
		//     orders 전체 필드와, 고객명 조회
//		{
//			String jpql = """
//					select o, ( select c.name from Customer c where o.customer = c) customerName
//					  from Orders o
//					""";
//			
//			em.createQuery(jpql, Object[].class).getResultList().forEach( a -> System.out.println( a[0] + ", " + a[1] ));
//		}
		
		em.close();
		emf.close();
	}

}