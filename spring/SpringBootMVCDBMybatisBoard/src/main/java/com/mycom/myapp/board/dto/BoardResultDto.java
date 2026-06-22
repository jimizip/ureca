package com.mycom.myapp.board.dto;

import java.util.List;

// 게시판 프론트 <= 백 결과를 전달하는 다양한 결과 데이터 표준화
public class BoardResultDto {
	private String result; // 작업 요청 결과 성공, 실패
	private List<BoardDto> list; // 게시글 목록
	private BoardDto dto; // 게시글 상세
	private int count; // 게시글 전체 건수
	
	// 기본 생성자
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public List<BoardDto> getList() {
		return list;
	}
	public void setList(List<BoardDto> list) {
		this.list = list;
	}
	public BoardDto getDto() {
		return dto;
	}
	public void setDto(BoardDto dto) {
		this.dto = dto;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public String toString() {
		return "BoardResultDto [result=" + result + ", list=" + list + ", dto=" + dto + ", count=" + count + "]";
	}
	
	
	
	
}
