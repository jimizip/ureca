package com.mycom.myapp.user.dto;

import java.util.Date;

public class UserDto {
	private int userSeq;
	private String userName;
	private String userPassword;
	private String userEmail;
	private String userProfileImage;
	private Date userRegisterDate;
	
	public UserDto() {}
	public UserDto(int userSeq, String userName, String userPassword, String userEmail, String userProfileImage,
			Date userRegisterDate) {
		super();
		this.userSeq = userSeq;
		this.userName = userName;
		this.userPassword = userPassword;
		this.userEmail = userEmail;
		this.userProfileImage = userProfileImage;
		this.userRegisterDate = userRegisterDate;
	}

	public int getUserSeq() {
		return userSeq;
	}
	public void setUserSeq(int userSeq) {
		this.userSeq = userSeq;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserProfileImage() {
		return userProfileImage;
	}
	public void setUserProfileImage(String userProfileImage) {
		this.userProfileImage = userProfileImage;
	}
	public Date getUserRegisterDate() {
		return userRegisterDate;
	}
	public void setUserRegisterDate(Date userRegisterDate) {
		this.userRegisterDate = userRegisterDate;
	}
	
	// toString() 민감한 데이터 출력 X
	@Override
//	public String toString() {
//		return "UserDto [userSeq=" + userSeq + ", userName=" + userName + ", userPassword=" + userPassword
//				+ ", userProfileImage=" + userProfileImage + ", userRegisterDate=" + userRegisterDate + "]";
//	}
	public String toString() {
		return "UserDto [userSeq=" + userSeq + ", userName=" + userName + ", userProfileImage=" + userProfileImage + ", userRegisterDate=" + userRegisterDate + "]";
	}
}
