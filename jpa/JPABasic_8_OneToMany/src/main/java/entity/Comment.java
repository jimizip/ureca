package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// OneToMany 에서 연관 관계 X
@Entity
public class Comment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String contetent;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContetent() {
		return contetent;
	}

	public void setContetent(String contetent) {
		this.contetent = contetent;
	}
	
	// post 제외
	@Override
	public String toString() {
		return "Comment [id=" + id + ", contetent=" + contetent + "]";
	}
}
