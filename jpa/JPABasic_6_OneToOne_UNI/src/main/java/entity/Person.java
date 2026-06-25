package entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

// 1:1 단방향 => Person -> Passport (반대는 안됨)
// OneToOne 단방향 관계의 Ownership 은 Person
@Entity
public class Person {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
//	@OneToOne
//	@JoinColumn(name="passport")
//	private Passport passport;
	
	// # Test #5
//	@OneToOne(cascade=CascadeType.PERSIST) // Person 이 persist 될 때 연결된 passport 도 함께 persist 된다.
//	@JoinColumn(name="passport")
//	private Passport passport;
	
	// # Test2 #3 ~
	@OneToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY) // Person 이 persist 될 때 연결된 passport 도 함께 persist 된다.
	@JoinColumn(name="passport")
	private Passport passport;

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

	public Passport getPassport() {
		return passport;
	}

	public void setPassport(Passport passport) {
		this.passport = passport;
	}

//	@Override
//	public String toString() {
//		return "Person [id=" + id + ", name=" + name + ", passport=" + passport + "]";
//	}
	
	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + "]";
	}
	
	

}
