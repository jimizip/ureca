import java.util.List;

import entity.Team;
import entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.Persistence;

public class Test2 {

	public static void main(String[] args) throws Exception{
		// persistence.xml 의 my-pu 로 EntityManager 를 EntityManagerFactory 로부터 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin(); // transaction 준비, 영속성 컨텍스트(1차 캐시) 준비
		
		// #1. Team find
		//     team 만 select <= FetchType.LAZY
//		{
//			Team t1 = em.find(Team.class, 1);
//			System.out.println(t1);
//		}
		
		// #2. User find
		//     user 만 select <= FetchType.LAZY
//		{
//			User u1 = em.find(User.class, 1);
//			System.out.println(u1);
//		}		
		
		// #3. Team find, getUsers(), 출력
		//     team select 1 건, teams_users 와 users join 1 건
//		{
//			Team t1 = em.find(Team.class, 1);
//			t1.getUsers().forEach( u -> System.out.println(u));
//		}
		
		// #4. Team 의 FetchType.EAGER, Team find, getUsers(), 출력
		//     team, teams_users, users join 1 건
		{
			Team t1 = em.find(Team.class, 1);
			t1.getUsers().forEach( u -> System.out.println(u));
		}
		
		em.getTransaction().commit(); // transaction 완료, 확정 -> DB 반영 ( 내부적으로 flush() 호출 )
		
		em.close();
		emf.close();
	}

}






















