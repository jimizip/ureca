package com.mycom.myapp.board.dto;

// 목록, 상세 요청 <= front -> back 전달 파라미터 표준화
// 등록, 수정은 BoardDto
public class BoardParamDto {

	// 목록
	private int limit; // pagination
	private int offset; // pagination
	private String searchWord; // 검색어
	
	// 상세
	private int boardId; // 게시글 id
	private int userSeq; // 게시글을 보는 사용자의 userSeq (작성자와 보는 사용자 확인)
	
	// 기본생성자만 사용
	
	
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public String getSearchWord() {
		return searchWord;
	}
	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
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
	
	@Override
	public String toString() {
		return "BoardParamDto [limit=" + limit + ", offset=" + offset + ", searchWord=" + searchWord + ", boardId="
				+ boardId + ", userSeq=" + userSeq + "]";
	}
}
