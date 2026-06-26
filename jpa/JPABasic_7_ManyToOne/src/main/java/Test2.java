import entity.Comment;
import entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.Persistence;

public class Test2 {

	public static void main(String[] args) throws Exception{
		// persistence.xml 의 my-pu로 EntityManager를 EntityManagerFactory 로부터 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin(); // transaction 준비, 영속성 컨텍스트(1차 캐시)
	
		// #1. Post find => Post만 select
//		{
//			Post p = em.find(Post.class, 1);
//			System.out.println(p);
//		}
		
		// #2. Comment find => Comment 와 Post join select <= FetchType.EAGER
//		{
//			Comment c1 = em.find(Comment.class, 1);
//			System.out.println(c1);
//		}
		
		// #3. Comment find, fetch=FetchType.LAZY
		//     => Comment 만 select, toString에 post 가 포함되면 그것 때문에 추가 Post select 진행
		//     => toString 에서 post 제외하면 comment 만 select
//		{
//			Comment c1 = em.find(Comment.class, 1);
//			System.out.println(c1);
//		}
		
		// #4. Comment find, fetch=FetchType.LAZY, getPost()
//		{
//			Comment c1 = em.find(Comment.class, 1);
//			Post p = c1.getPost(); // select post X
//			System.out.println(p); // select post O
//		}
		
		// #5. Comment -> Post -> Post 의 새로운 Comment 연걸(FK)
		//     =>
		{
			Comment c1 = em.find(Comment.class, 1);
			Post p = c1.getPost();
			
//			System.out.println(p); // select post O
			
			Comment c3 = new Comment();
			c3.setContetent("코멘트 3");
			c3.setPost(p);
			
			em.persist(c3);
		}
		
	
		em.getTransaction().commit(); // Transaction 완료, 확정 -> DB 반영 ( 내부적으로 flush() 호출) -> 없으면 SQL에 등록 확정이 안됨
		
		em.close();
		emf.close();

	}

}
