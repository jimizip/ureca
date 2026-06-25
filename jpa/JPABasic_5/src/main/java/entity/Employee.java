package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Employee {
	// 순차적인 int 타입 id 생성 전략
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // 권장. Auto Increment
	// @GeneratedValue(strategy=GenerationType.AUTO) // Hibernate 가 DB에 맞게 알아서 선택 ( MYSQL -> SEQUENCE 유사)
	// GeneratedValue(strategy=GenerationType.SEQUENCE) // DB 의 SEQUENCE 이용. 지원하지 않는 DB 경우 Hibernate 가 DB 에 맞게 알아서 처리( MySQL -> SEQUENCE 유사 
	// @GeneratedValue(strategy=GenerationType.TABLE) // Hibernate 에게 TABLE 생성해서 id 관리해달라고 요청
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
//    @Id
//    @GeneratedValue(strategy=GenerationType.UUID)
//	private String id;
//	
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
	
	private String name;
	private String address;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", address=" + address + "]";
	}
	
}
