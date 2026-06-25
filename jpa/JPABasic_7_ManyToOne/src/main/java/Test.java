import entity.Comment;
import entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Test {

	public static void main(String[] args) throws Exception{
		// persistence.xml 의 my-pu로 EntityManager를 EntityManagerFactory 로부터 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin(); // transaction 준비, 영속성 컨텍스트(1차 캐시)
		
		// 게시글
		Post p = new Post();
		p.setTitlel("제목 1");
		p.setContent("내용 1");
		
		// 댓글
		Comment c1 = new Comment();
		c1.setContetent("코멘트 1");
		
		Comment c2 = new Comment();
		c2.setContetent("코멘트 2");
		
		// #1. 연결 없이 Post 만 persist
//		{
//			em.persist(p);
//		}
		
		// #2. 연결 없이 Comment 만 persist
		// 	   comment 의 post_id null
//		{
//			em.persist(c1);
//		}
		
		// #3. 연결 후, Post 만 persist
//		{
//			c1.setPost(p);
//			c2.setPost(p);
//			em.persist(p);
//		}
		
		// #4. 연결 후, Comment 만 persist
		// 	   org.hibernate.TransientPropertyValueException 발생
		//     ManyToOne 의 Ownership 을 가진 Comment 엔티티 객체가 영속화 될 때 연결된 Post 객체가 영속화 되지 않아서 오류 발생
//		{
//			c1.setPost(p);
//			c2.setPost(p);
//			em.persist(c1); // c1 하나만
//		}
//		
		// #5. 연결 후, c1 -> c2 -> p 순으로 persist
		// comment insert 2건, post insert 1건, comment update 2건
		// comment 가 insert 될 때 post_id 가 null
		// post 가 insert 후, comment post_id update 수행
//		{
//			c1.setPost(p);
//			c2.setPost(p);
//			em.persist(c1);
//			em.persist(c2);
//			em.persist(p);
//		}
		
		// #6. 연결 후, p -> c1 -> c2 순으로 persist
		// post insert 1건, comment insert 2건, update X
		// comment 가 insert 되기 전에 post 가 먼저 insert 되어서 별도의 update 필요 X
//		{
//			c1.setPost(p);
//			c2.setPost(p);
//			em.persist(p);
//			em.persist(c1);
//			em.persist(c2);
//		}
		
		// #7. cascade PERSIST 적용, comment 만 persist
		// post insert 1건, comment insert 2건, update X
		{
			c1.setPost(p);
			c2.setPost(p);

			em.persist(c1);
			em.persist(c2);
		}
		
	
		em.getTransaction().commit(); // Transaction 완료, 확정 -> DB 반영 ( 내부적으로 flush() 호출) -> 없으면 SQL에 등록 확정이 안됨
		
		em.close();
		emf.close();

	}

}
