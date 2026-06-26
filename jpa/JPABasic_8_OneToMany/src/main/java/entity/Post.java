package entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

// OneToMany 연관 관계를 가진다. Owing Entity
// Post 1개에 대해 여러 개의 Comment 가 연결 => Post 객체 하나가 여러 개의 Comment 를 필드로 가진다. List<Comment>
@Entity
public class Post {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String titlel;
	private String content;
	
//	@OneToMany
//	private List<Comment> comments;
	
//	@OneToMany(cascade=CascadeType.PERSIST)
//	private List<Comment> comments;
	
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	private List<Comment> comments;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitlel() {
		return titlel;
	}
	
	public void setTitlel(String titlel) {
		this.titlel = titlel;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
//	@Override
//	public String toString() {
//		return "Post [id=" + id + ", titlel=" + titlel + ", content=" + content + ", comments=" + comments + "]";
//	}
	
	@Override
	public String toString() {
		return "Post [id=" + id + ", titlel=" + titlel + ", content=" + content + "]";
	}
	
	
}
