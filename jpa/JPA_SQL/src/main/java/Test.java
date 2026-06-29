import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// JPA_SQL 과제 [문제] : 스터디카페 예약 스키마 기반 JPQL 5문제
// 엔티티 : User(1) - RoomHistory(N) - Room(1)
//        User(1) - PaymentHistory(N) - Room(1) / RoomHistory(1)
public class Test {

	public static void main(String[] args) throws Exception {
		// persistence.xml 의 my-pu 로 EntityManager 생성
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-pu");
		EntityManager em = emf.createEntityManager();

		// #1. 내부 조인 (3 테이블, 명시적 inner join)
		//     RoomHistory + User + Room 을 이용해서
		//     모든 예약을 [예약자명, 회의실 id, 정원, 예약 시작시간] 으로 조회한다.
		{
			// TODO: JPQL 작성
		}

		// #2. 조인 + where 필터
		//     PaymentHistory + User 를 이용해서
		//     결제금액(price)이 50000 이상인 결제를 [사용자명, 결제금액, 결제일] 조건으로 조회한다.
//		{
//			// TODO: JPQL 작성
//		}

		// #3. 외부 조인 (left outer join)
		//     Room 을 기준으로 RoomHistory 를 left join 해서
		//     예약이 없는 회의실까지 포함해 [회의실 id, 회의실 가격, 예약 시작시간] 을 조회한다.
		//     (예약이 없는 회의실은 시작시간이 null 로 표시)
//		{
//			// TODO: JPQL 작성
//		}

		// #4. 서브쿼리 + where (비교 연산)
		//     PaymentHistory 를 이용해서
		//     결제금액(price)이 전체 결제 평균금액보다 큰 결제를 [결제 id, 금액] 조건으로 조회한다.
		//     평균금액은 서브쿼리( select avg(price) from PaymentHistory ) 로 구한다.
//		{
//			// TODO: JPQL 작성
//		}

		// #5. 상관 서브쿼리 (select 절 scalar) + EXISTS
		//     User 를 이용해서
		//     예약 이력이 있는( EXISTS ) 사용자만 [사용자명, 그 사용자의 총 결제금액] 으로 조회한다.
		//     총 결제금액은 상관 서브쿼리( select sum(price) ... where ph.user = u ) 로 구한다.
//		{
//			// TODO: JPQL 작성
//		}

		em.close();
		emf.close();
	}

}
