package entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

// 댓글 - ManyToOne 연관 관계 가진다. ( N:1 에서 Many 쪽이 FK 가진다.)
// Ownership 가지는 Entity
@Entity
public class Comment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String contetent;
	
//	@ManyToOne
//	private Post post;

	@ManyToOne(cascade=CascadeType.PERSIST)
	private Post post;

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

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", contetent=" + contetent + ", post=" + post + "]";
	}
}
