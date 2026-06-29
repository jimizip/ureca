package entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// PaymentHistory(결제) : User        : ManyToOne <= PaymentHistory 가 Ownership
// PaymentHistory(결제) : Room        : ManyToOne <= PaymentHistory 가 Ownership
// PaymentHistory(결제) : RoomHistory : ManyToOne <= PaymentHistory 가 Ownership (예약과 1:1 대응)
@Entity
@Table(name = "payment_history")
public class PaymentHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int price;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_history_id")
	private RoomHistory roomHistory;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public RoomHistory getRoomHistory() {
		return roomHistory;
	}

	public void setRoomHistory(RoomHistory roomHistory) {
		this.roomHistory = roomHistory;
	}

	@Override
	public String toString() {
		return "PaymentHistory [id=" + id + ", price=" + price + ", paymentDate=" + paymentDate + "]";
	}

}
