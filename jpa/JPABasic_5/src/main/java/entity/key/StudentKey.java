package entity.key;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

//복합키를 표현하는 클래스가 가져야 할 조건
//1. public 2. 기본 생성자 3. equals & hashCode 4. Seriallizable

@Embeddable // 부모 엔티티 클래스의 필드에 직접 넣어서 사용
public class StudentKey implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String code;
	private int number;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(code, number);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentKey other = (StudentKey) obj;
		return Objects.equals(code, other.code) && number == other.number;
	}
	
	@Override
	public String toString() {
		return "StudentKey [code=" + code + ", number=" + number + "]";
	}
	
	
}
