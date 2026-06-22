package com.mycom.myapp.board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.board.dto.BoardDto;
import com.mycom.myapp.board.dto.BoardParamDto;

@Mapper
public interface BoardDao {

	// 목록, 목록별 전체 건수
	List<BoardDto> listBoard(BoardParamDto boardParamDto);
	int listBoardTotalCount();
	
	// 검색어 목록, 검색어 목록별 전체 건수
	List<BoardDto> listBoardSearchWord(BoardParamDto boardParamDto);
	int listBoardSearchWordTotalCount(BoardParamDto boardParamDto);
	
	// 상세
	BoardDto detailBoard(BoardParamDto boardParamDto);
	
	// 조회수
	// 현재 사용자 (글 조회하는) 가 현재 게시글을 이전에 조회했는지 확인, 판단
	int countBoardUserRead(BoardParamDto boardParamDto);
	// 현재 사용자 (글 조회하는) 가 현재 게시글을 조회했음을 기록 (insert)
	int insertBoardUserRead(BoardParamDto boardParamDto);
	// 현재 게시글의 조회수 증가
	int updateBoardUserRead(int boardId);
	
	// 등록, 수정, 삭제
	int insertBoard(BoardDto boardDto);
	int updateBoard(BoardDto boardDto);
	int deleteBoard(int boardId);
	// 원 게시글 삭제 시 FK 인 board_user_read 테이블의 데이터도 함께 삭제, 게시글 삭제 보다 먼저 삭제되어야 한다. 회원 삭제시도 동일한 처리 필요
	int deleteBoardUserRead(int boardId);
	
	// 삭제 처리
	// 1. hard delete <= 삭제 시점에 delete 수행
	// 2. soft delete <= 테이블에서 삭제하지 않고, 삭제여부, 사용여부 컬럼의 값으로 삭제 표현 del_yn, select 할 때 del_yn = 'Y' 인 것만 처리
	// 3. batch delete <= 삭제 mark 후 일정 기간, 시간 지나면 배치로 삭제 <= 법령...
	// 4. hard delete + 별도 delete 테이블 이관
}
