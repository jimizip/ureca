package com.mycom.myapp.board.dto;

import java.time.LocalDateTime; // java 8 이후
import java.time.ZoneId;
import java.util.Date;

// 프론트에서 필요로 하는 ( 화면에 보이는 ) 게시글 관련 전체 내용은 게시글 테이블의 컬럼만으로 부족, 사용자의 추가 정보 (이름...) 가 필요하다.
// 단순 CRUD 에서는 한 테이블에 대한 모든 정보로 끝.
// 백엔드에서 게시판 테이블과 사용자 테이블의 조인을 통한 통합 데이터가 필요 => BoardDto 에 사용자 테이블의 조인 결과 추가되는 사용자 정보 함께 관리
public class BoardDto {
	private int boardId;
	private int userSeq;
	private String userName; // users join
	private String userProfileImage; // users join
	private String title;
	private String content;
	private LocalDateTime regDt;
	private int readCount;
	
	private boolean sameUser; // 로그인 사용자와 현재 조회하는 글 작성자가 동일한지 표현 ( 수정, 삭제 가 가능해야 함 )

	public BoardDto() {}
	// 게시글 생성에 필요한 생성자
	public BoardDto(int userSeq, String title, String content) {
		super();
		this.userSeq = userSeq;
		this.title = title;
		this.content = content;
	}
	
	public String getUserProfileImage() {
		return userProfileImage;
	}
	public void setUserProfileImage(String userProfileImage) {
		this.userProfileImage = userProfileImage;
	}
	public int getBoardId() {
		return boardId;
	}
	public void setBoardId(int boardId) {
		this.boardId = boardId;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getRegDt() {
		return regDt;
	}
	// mybatis Date 매핑 오류
//	public void setRegDt(LocalDateTime regDt) {
//		this.regDt = regDt;
//	}
	public void setRegDt(Date regDt) {
		this.regDt = LocalDateTime.ofInstant(regDt.toInstant(), ZoneId.systemDefault());
	}
	public int getReadCount() {
		return readCount;
	}
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	public boolean isSameUser() {
		return sameUser;
	}
	public void setSameUser(boolean sameUser) {
		this.sameUser = sameUser;
	}
	
	@Override
	public String toString() {
		return "BoardDto [boardId=" + boardId + ", userSeq=" + userSeq + ", userName=" + userName
				+ ", userProfileImage=" + userProfileImage + ", title=" + title + ", content=" + content + ", regDt="
				+ regDt + ", readCount=" + readCount + ", sameUser=" + sameUser + "]";
	}

}
