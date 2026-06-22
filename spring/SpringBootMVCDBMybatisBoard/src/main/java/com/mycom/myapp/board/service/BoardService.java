package com.mycom.myapp.board.service;

import com.mycom.myapp.board.dto.BoardDto;
import com.mycom.myapp.board.dto.BoardParamDto;
import com.mycom.myapp.board.dto.BoardResultDto;

// Dao Layer 가 여러 개의 메소드로 분리되어도 하나의 B.L 처리하는 Service Layer 는 1개의 메소드로 처리
public interface BoardService {
	// 목록
	BoardResultDto listBoard(BoardParamDto boardParamDto); // limit, offset
	BoardResultDto listBoardSearchWord(BoardParamDto boardParamDto); // limit, offset, searchWord
	
	// 상세
	// 글 조회수 관련 부분 포함
	BoardResultDto detailBoard(BoardParamDto boardParamDto);
	
	// 등록, 수정, 삭제
	BoardResultDto insertBoard(BoardDto boardDto);
	BoardResultDto updateBoard(BoardDto boardDto);
	BoardResultDto deleteBoard(int boardId); // board_user_read 삭제 포함
}
