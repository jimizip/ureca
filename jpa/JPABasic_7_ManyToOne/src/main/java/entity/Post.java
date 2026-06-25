package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// 게시글 - 연관 관계 없음 ( N:1 에서 Many 쪽이 FK 가진다.)
@Entity
public class Post {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String titlel;
	private String content;
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
	
	@Override
	public String toString() {
		return "Post [id=" + id + ", titlel=" + titlel + ", content=" + content + "]";
	}
	
	
}
