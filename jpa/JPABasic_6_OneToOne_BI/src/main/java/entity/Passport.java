package entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

// 1:1 양방향 => Person -> Passport, Passport -> Person
// Person 과 연관된 필드가 없음
@Entity
public class Passport {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String number;
	
	// 1:1 양방향의 Ownership 이 없는 엔티티가, owner 인 Person 을 찾아가는 필드는 Person 의 passport 를 통해서
//	@OneToOne(mappedBy="passport")
//	private Person person;
	
//	@OneToOne(mappedBy="passport", cascade=CascadeType.PERSIST)
//	private Person person;
	
	@OneToOne(mappedBy="passport", cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private Person person;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "Passport [id=" + id + ", number=" + number + "]";
	}
	
}
