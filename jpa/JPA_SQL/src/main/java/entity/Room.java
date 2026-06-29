package entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// Room : RoomHistory : OneToMany (mappedBy="room") <= RoomHistory 가 소유
// 스키마상 Room.id 는 수동 PK (AUTO_INCREMENT 아님) => @GeneratedValue 없음
@Entity
@Table(name = "Room")
public class Room {

	@Id
	private int id;

	@Column(name = "room_size")
	private int roomSize;

	private int price;

	@OneToMany(mappedBy = "room")
	private List<RoomHistory> roomHistories;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRoomSize() {
		return roomSize;
	}

	public void setRoomSize(int roomSize) {
		this.roomSize = roomSize;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", roomSize=" + roomSize + ", price=" + price + "]";
	}

}
