package entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

// 1:1 양방향 => Person -> Passport, Passport -> Person
// OneToOne 양방향 관계의 Ownership 은 Person
@Entity
public class Person {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
//	@OneToOne
//	@JoinColumn(name="passport")
//	private Passport passport;
	
//	@OneToOne(cascade=CascadeType.PERSIST)
//	@JoinColumn(name="passport")
//	private Passport passport;
	
	@OneToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
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
