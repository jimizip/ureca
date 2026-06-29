package entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// User : RoomHistory     : OneToMany (mappedBy="user")   <= RoomHistory 가 소유
// User : PaymentHistory  : OneToMany (mappedBy="user")   <= PaymentHistory 가 소유
@Entity
@Table(name = "User")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private String tel;

	private String email;

	private String password;

	@OneToMany(mappedBy = "user")
	private List<RoomHistory> roomHistories;

	@OneToMany(mappedBy = "user")
	private List<PaymentHistory> paymentHistories;

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

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", tel=" + tel + ", email=" + email + "]";
	}

}
