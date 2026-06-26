package entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

// ManyToMany 연관관계의 Owing 엔티티 - @JoinTable
@Entity
@Table(name="teams")
public class Team {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
//	@ManyToMany
//	@JoinTable(
//		name="teams_users", // team 과 user 관계 테이블
//		joinColumns=@JoinColumn(name="team_id"), // owner 인 team 의 FK 컬럼
//		inverseJoinColumns=@JoinColumn(name="user_id") // owner 가 아닌 user 의 FK 컬럼
//	)
//	private List<User> users;

//	@ManyToMany(cascade=CascadeType.PERSIST)
//	@JoinTable(
//		name="teams_users", // team 과 user 관계 테이블
//		joinColumns=@JoinColumn(name="team_id"), // owner 인 team 의 FK 컬럼
//		inverseJoinColumns=@JoinColumn(name="user_id") // owner 가 아닌 user 의 FK 컬럼
//	)
//	private List<User> users;
	
	@ManyToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinTable(
		name="teams_users", // team 과 user 관계 테이블
		joinColumns=@JoinColumn(name="team_id"), // owner 인 team 의 FK 컬럼
		inverseJoinColumns=@JoinColumn(name="user_id") // owner 가 아닌 user 의 FK 컬럼
	)
	private List<User> users;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Team [id=" + id + ", name=" + name + "]";
	}
	
	// team, user 객체 toString 상호 참조 로 StackOverFlowError 발생	
//	@Override
//	public String toString() {
//		return "Team [id=" + id + ", name=" + name + ", users=" + users + "]";
//	}
}
