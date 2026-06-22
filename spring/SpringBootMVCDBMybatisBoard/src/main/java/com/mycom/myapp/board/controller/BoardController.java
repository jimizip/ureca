package com.mycom.myapp.board.controller;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycom.myapp.board.dto.BoardDto;
import com.mycom.myapp.board.dto.BoardParamDto;
import com.mycom.myapp.board.dto.BoardResultDto;
import com.mycom.myapp.board.service.BoardService;
import com.mycom.myapp.user.dto.UserDto;

import jakarta.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/boards")
public class BoardController {

	// 생성자 DI
	private final BoardService boardService;
	
	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}
	
	// 목록
	@GetMapping("/list")
	public BoardResultDto listBoard(BoardParamDto boardParamDto) {
		BoardResultDto boardResultDto = null;
		
		// 검색어 포함여부에 따른 분리처리 Controller -> Service 에서 구현도 가능
		if( Strings.isEmpty(boardParamDto.getSearchWord())) {
			boardResultDto = boardService.listBoard(boardParamDto);
		}else {
			boardResultDto = boardService.listBoardSearchWord(boardParamDto);
		}
		
		return boardResultDto;
	}
	
	// 상세
	@GetMapping("/detail/{boardId}")
	public BoardResultDto detailBoard(@PathVariable Integer boardId, HttpSession session) {
		BoardResultDto boardResultDto = null;
		// 메소드의 파라미터에 BoardParamDto 를 사용하지 않는 이유.
		// 게시글 상세 처리 외, 조회수처리를 위해서 현재 글을 조회하는 사용자의 userSeq 가 필요.
		// 이 userSeq 는 프론트 javascript 로 전달 X
		// session 에서 (백엔드에서 ) 처리하는 게 옮다.
		BoardParamDto boardParamDto = new BoardParamDto();
		boardParamDto.setBoardId(boardId);
		int userSeq = ((UserDto) session.getAttribute("userDto")).getUserSeq();
		boardParamDto.setUserSeq(userSeq);
		
		boardResultDto = boardService.detailBoard(boardParamDto);
		return boardResultDto;
	}	
	
	// 등록
	@PostMapping("/insert")
	public BoardResultDto insertBoard(BoardDto boardDto, HttpSession session) {
		// insert 에 필요한 userSeq 는 프론트에서 파라미터로 받는 게 아니라, 백엔드 세션에서 가져와야 한다.
		int userSeq = ((UserDto) session.getAttribute("userDto")).getUserSeq();
		boardDto.setUserSeq(userSeq);
		return boardService.insertBoard(boardDto);
	}
	
	// 수정
	@PostMapping("/update")
	public BoardResultDto updateBoard(BoardDto boardDto) {
		return boardService.updateBoard(boardDto);
	}
	
	// 삭제
	@GetMapping("/delete/{boardId}")
	public BoardResultDto deleteBoard(@PathVariable Integer boardId) {
		return boardService.deleteBoard(boardId);
	}
}









