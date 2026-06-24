package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity // JPA가 관리하는 클래스로 DB의 테이블과 매
public class Product {
	@Id
	private int id; // JAP가 관리하는 클래스는 반드시 PK의 필드를 가져야 하고 @ID로 표시한다.
	private String name;
	
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
	
	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + "]";
	}
	
	
}
