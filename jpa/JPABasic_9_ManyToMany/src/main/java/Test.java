import java.util.List;

import entity.Team;
import entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Test {

	public static void main(String[] args) throws Exception{
		// persistence.xml 의 my-pu 로 EntityManager 를 EntityManagerFactory 로부터 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin(); // transaction 준비, 영속성 컨텍스트(1차 캐시) 준비
		
		User u1 = new User();
		u1.setName("회원 1");
		
		User u2 = new User();
		u2.setName("회원 2");	
		
		Team t1 = new Team();
		t1.setName("팀 1");
		
		Team t2 = new Team();
		t2.setName("팀 2");
		
		
		// #1. 테이블 생성 확인
//		{
//			
//		}

		// #2. User 2 건 persist
		//     users insert 2건
//		{
//			em.persist(u1);
//			em.persist(u2);
//		}		
		
		// #3. Team 2 건 persist
		//     teams insert 2건
//		{
//			em.persist(t1);
//			em.persist(t2);
//		}		
		
		// #4. User 2 건, Team 2 건 persist
		//     users, teams 각각 insert 2건
//		{
//			em.persist(u1);
//			em.persist(u2);			
//			em.persist(t1);
//			em.persist(t2);
//		}
		
		// #5. Team 에  User 연결 후 Team 만 persist
		//     org.hibernate.TransientObjectException <= team 객체가 영속화 될 때, 연관관계의 users 영속화 X
//		{
//			t1.setUsers(List.of(u1, u2));
//			t2.setUsers(List.of(u2));
//			
//			em.persist(t1);
//			em.persist(t2);
//		}
		
		// #6. Team, User 연결 후 Team, user persist
		//     7 건 insert
//		{
//			t1.setUsers(List.of(u1, u2));
//			t2.setUsers(List.of(u2));
//			
//			em.persist(t1);
//			em.persist(t2);
//			em.persist(u1);
//			em.persist(u2);
//		}
		
		
		// #7. User 에  Team 연결 후 User 만 persist
		//     users 2 건 insert, teams_users 0건 
		//     <= Non-Owing Entity 는 연결이 되어도 그 연결에 의한 관계 테이블의 persist 와 관련 X
//		{
//			u1.setTeams(List.of(t1, t2));
//			u2.setTeams(List.of(t2));
//			em.persist(u1);
//			em.persist(u2);
//		}
		
		// #8. User 에  Team 연결 후 User, Team persist
		//     users 2 건, teams 2 건 insert, teams_users insert X
		//     <= Non-Owing Entity 는 연결이 되어도 그 연결에 의한 관계 테이블의 persist 와 관련 X
//		{
//			u1.setTeams(List.of(t1, t2));
//			u2.setTeams(List.of(t2));
//			em.persist(u1);
//			em.persist(u2);
//			em.persist(t1);
//			em.persist(t2);
//		}		
		
		// #9. Team 에 CascadeType.PERSIST, Team, User 연결 후 Team 만 persist
		//     7 건 insert
		{
			t1.setUsers(List.of(u1, u2));
			t2.setUsers(List.of(u2));
			
			em.persist(t1);
			em.persist(t2);
		}
		
		em.getTransaction().commit(); // transaction 완료, 확정 -> DB 반영 ( 내부적으로 flush() 호출 )
		
		em.close();
		emf.close();
	}

}






















