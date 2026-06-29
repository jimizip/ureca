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
		
		// #1. group by
		//     orders 에서 주문일(order_date)별로 묶어서, 주문일별 고객수 count 하는 jpql
		//     count(o.customer) >= Orders 와 Customer 의 연관관계에서 파악
//		{
//			String jpql = """
//				select o.orderDate, count(o.customer)
//				  from Orders o
//				 group by o.orderDate
//					""";
//			em.createQuery(jpql, Object[].class).getResultList()
//				.forEach( o -> System.out.println( o[0]+ ", " + o[1]));
//		}
		
		// #2. having
		//     #1 의 결과 중 고객 수가 2건 초과인 주문일만 조회
		//     alias : select 절은 가능, having X
//		{
//			String jpql = """
//				select o.orderDate, count(o.customer) as customerCount
//				  from Orders o
//				 group by o.orderDate
//				 having count(o.customer) > 2
//						""";
//			em.createQuery(jpql, Object[].class).getResultList()
//			.forEach( o -> System.out.println( o[0]+ ", " + o[1]));			
//		}
		
		// #3. where + having
		//     2025-04-13 주문일 이후 주문만 대상, 고객 수가 2건 초과인 건 조회, 주문일은 파라미터 (named)
//		{
//			String jpql = """
//				select o.orderDate, count(o.customer)
//				  from Orders o
//				 where o.orderDate > :orderDate
//				 group by o.orderDate
//				 having count(o.customer) > 2
//						""";
//			em.createQuery(jpql, Object[].class)
//				.setParameter("orderDate", LocalDate.of(2025, 4, 13))
//				.getResultList()
//				.forEach( o -> System.out.println( o[0]+ ", " + o[1]));			
//		}
		
		// #4. group by + order by + alias
		//     주문 테이블에서 주문일별 고객 수를 내림차순 정렬해서 조회, alias 사용 확인
		//     having 은 alias X, order by 는 O
//		{
//			String jpql = """
//				select o.orderDate, count(o.customer) as customerCount
//				  from Orders o
//				 group by o.orderDate
//				 order by customerCount desc
//						""";
//			em.createQuery(jpql, Object[].class)
//				.getResultList()
//				.forEach( o -> System.out.println( o[0]+ ", " + o[1]));			
//		}	
		
		// #5. join + group by + order by + alias
		//     주문, 고객, 상품 테이블에서 여성(female) 고객이 주문한 상품 중 가격이 2000 이상인 주문 건을 
		//     일자별로 합계(sum) 조회, sum() 으로 desc 정렬
//		{
//			String jpql = """
//				select o.orderDate, sum(p.price) as femaleOrderSum
//				  from Orders o join o.product p
//				                join o.customer c
//	             where c.gender = 'f'
//				   and p.price >= 2000
//				 group by o.orderDate
//				 order by femaleOrderSum desc
//						""";
//			em.createQuery(jpql, Object[].class)
//				.getResultList()
//				.forEach( o -> System.out.println( o[0]+ ", " + o[1]));			
//		}	
		
		// #6. subquery + group by + order by + alias
		//     주문, 고객, 상품 테이블에서 여성(female) 고객이 주문한 상품 중 가격이 2000 이상인 주문 건을 
		//     일자별로 합계(sum) 조회, sum() 으로 desc 정렬
		//     o.product.price <= join 발생
		{
			String jpql = """
				select o.orderDate, sum(o.product.price) as femaleOrderSum
				  from Orders o
	             where o.customer in ( select c from Customer c where c.gender = 'f' )
				   and o.product in ( select p from Product p where p.price >= 2000 )
				 group by o.orderDate
				 order by femaleOrderSum desc
						""";
			em.createQuery(jpql, Object[].class)
				.getResultList()
				.forEach( o -> System.out.println( o[0]+ ", " + o[1]));			
		}
		
		// #7. 위 #6 의 select o.proudct.price 로 인한 join 수행 대신 완전히 subquery 로 대체
		//     where, and 의 subquery 는 대상 filtering 역할
		//     select sum() 안의 subquery 는 () 필요.
		{
			String jpql = """
				select o.orderDate, 
				sum(
					( select p.price from Product p where p = o.product and p.price >= 2000 )
				) as femaleOrderSum
				  from Orders o
	             where o.customer in ( select c from Customer c where c.gender = 'f' )
				   and o.product in ( select p from Product p where p.price >= 2000 )
				 group by o.orderDate
				 order by femaleOrderSum desc
						""";
			em.createQuery(jpql, Object[].class)
				.getResultList()
				.forEach( o -> System.out.println( o[0]+ ", " + o[1]));			
		}
		
		em.close();
		emf.close();
	}

}