package com.mycom.myapp.board.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.mycom.myapp.board.dao.BoardDao;
import com.mycom.myapp.board.dto.BoardDto;
import com.mycom.myapp.board.dto.BoardParamDto;
import com.mycom.myapp.board.dto.BoardResultDto;

@Service
public class BoardServiceImpl implements BoardService{

	// 생성자 DI
	private final BoardDao boardDao;
	
	public BoardServiceImpl(BoardDao boardDao) {
		this.boardDao = boardDao;
	}
	
	// 비정상적인 실패 상황은 ?????
	@Override
	public BoardResultDto listBoard(BoardParamDto boardParamDto) {
		BoardResultDto boardResultDto = new BoardResultDto();
		
		List<BoardDto> list = boardDao.listBoard(boardParamDto); // 목록
		int count = boardDao.listBoardTotalCount(); // 전체 건수
		boardResultDto.setList(list);
		boardResultDto.setCount(count);
		boardResultDto.setResult("success");
		
		return boardResultDto;
	}

	// 검색어가 있는 경우
	@Override
	public BoardResultDto listBoardSearchWord(BoardParamDto boardParamDto) {
		BoardResultDto boardResultDto = new BoardResultDto();
		
		List<BoardDto> list = boardDao.listBoardSearchWord(boardParamDto); // 목록
		int count = boardDao.listBoardSearchWordTotalCount(boardParamDto); // 전체 건수
		boardResultDto.setList(list);
		boardResultDto.setCount(count);
		boardResultDto.setResult("success");
		
		return boardResultDto;
	}

	// 상세 + 조회수 처리
	// insert, delete, update 가 여러 건 수행되는 Service 메소드는 transaction 관리가 필요. <= Spring 이 제공하는 @Transactional 사용
	// DB 연동의 원천 기술인 JDBC 는 con.setAutoCommit(false) ----- 작업 진행 -- 예외 발생 con.rollback(), 예외 발생 X con.commit();
	// Spring Framework 이 @Transactional 이 포함된 메소드를 scan 하고 그 대상 메소드를 대상으로 AOP pointcut 구성
	// pointcut 에 해당되는 메소드를 가진 클래스의 Proxy 를 구성하고, Proxy Handler 에서 pointcut 메소드 호출 전에 con 의 setAutocommit(false)
	//  -> pointcut 메소드 호출 -> Proxy Handler 에서 con.commit(), 문제가 발생하면 con.rollback() 호출
	
	// @Transactional 은 초소, 꼭 필요한 메소드에게만 적용 <= AOP Proxy 비용이 크다
	// try - catch X <= 단순하게 @Transactional 사용, 미사용 선택
	
	// 프론트와의 예외 응답 체계
	// 1. SF 에서 기본 제공하는 예외 응답 활용
	/*
	 {
	    "timestamp": "2026-06-22T04:16:28.509Z",
	    "status": 500,
	    "error": "Internal Server Error",
	    "trace": "java.lang.NullPointerException: Cannot invoke ..."
	    "message": "Cannot invoke ...",
	    "path": "/boards/detail/3475"
	}	
	 */
	// 2. 사용자 정의 예외 Dto <= BoardResultDto <= 코드가 직접 구현  <= try - catch
	
	// try-catch 있는 버전
	@Override
	@Transactional
	public BoardResultDto detailBoard(BoardParamDto boardParamDto) {
		BoardResultDto boardResultDto = new BoardResultDto();
		
		try {
			// 조회수 처리
			int userReadCount = boardDao.countBoardUserRead(boardParamDto);

			// 현재 사용자가 현재 게시글을 처음 조회하는 경우
			if( userReadCount == 0 ) {
				// 현재 사용자가 현재 게시글을 조회했음을 표시
				boardDao.insertBoardUserRead(boardParamDto);
				
//				String str = null;
//				str.length();  // NullPointerException 발생
				
				// 현재 게시글의 조회수 컬럼값 증가
				boardDao.updateBoardUserRead(boardParamDto.getBoardId());
			}

			// 게시글 상세
			BoardDto boardDto = boardDao.detailBoard(boardParamDto);
			
			// same user
			// boardDto.getUserSeq() <= 현재 게시글의 글 작성자 userSeq
			// boardParamDto.getUserSeq() <= 현재 게시글을 조회자 userSeq
			if(boardDto.getUserSeq() == boardParamDto.getUserSeq()) { // 내가 쓴 게시글을 내가 조회
				boardDto.setSameUser(true);
			}else {
				boardDto.setSameUser(false); // 원래 DTO 생성할 때 초긱값이 false;
			}
			
			boardResultDto.setDto(boardDto);
			boardResultDto.setResult("success");			
		}catch(Exception e) {
			e.printStackTrace();
			boardResultDto.setResult("fail");
			
//			throw new RuntimeException("~~~"); // rollback 은 된다. 메세지는 사용자 정의 응답 X
			
			// Spring 이 제안하는 방법
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();  // 우리 코드의 진행을 방해 X, 이 코드의 AOP 에게 정책 전달 (rollback 하라)
		}

		return boardResultDto;
	}

	// try-catch 없는 버전
//	@Override
//	@Transactional
//	public BoardResultDto detailBoard(BoardParamDto boardParamDto) {
//		BoardResultDto boardResultDto = new BoardResultDto();
//		
//		// 조회수 처리
//		int userReadCount = boardDao.countBoardUserRead(boardParamDto);
//
//		// 현재 사용자가 현재 게시글을 처음 조회하는 경우
//		if( userReadCount == 0 ) {
//			// 현재 사용자가 현재 게시글을 조회했음을 표시
//			boardDao.insertBoardUserRead(boardParamDto);
//			
//			String str = null;
//			str.length();  // NullPointerException 발생
//			
//			// 현재 게시글의 조회수 컬럼값 증가
//			boardDao.updateBoardUserRead(boardParamDto.getBoardId());
//		}
//		
//		// 게시글 상세
//		BoardDto boardDto = boardDao.detailBoard(boardParamDto);
//		
//		// same user
//		// boardDto.getUserSeq() <= 현재 게시글의 글 작성자 userSeq
//		// boardParamDto.getUserSeq() <= 현재 게시글을 조회자 userSeq
//		if(boardDto.getUserSeq() == boardParamDto.getUserSeq()) { // 내가 쓴 게시글을 내가 조회
//			boardDto.setSameUser(true);
//		}else {
//			boardDto.setSameUser(false); // 원래 DTO 생성할 때 초긱값이 false;
//		}
//		
//		boardResultDto.setDto(boardDto);
//		boardResultDto.setResult("success");
//		
//		return boardResultDto;
//	}


	@Override
	public BoardResultDto insertBoard(BoardDto boardDto) {
		BoardResultDto boardResultDto = new BoardResultDto();
		
		try {
			int ret = boardDao.insertBoard(boardDto);
			if( ret  == 1 ) boardResultDto.setResult("success");
			else boardResultDto.setResult("fail");
		}catch(Exception e) {
			e.printStackTrace();
			boardResultDto.setResult("fail");
		}
		
		return boardResultDto;
	}

	@Override
	public BoardResultDto updateBoard(BoardDto boardDto) {
		BoardResultDto boardResultDto = new BoardResultDto();
		
		try {
			int ret = boardDao.updateBoard(boardDto);
			if ( ret == 1 ) boardResultDto.setResult("success");
			else boardResultDto.setResult("fail");
		}catch(Exception e) {
			e.printStackTrace();
			boardResultDto.setResult("fail");
		}

		return boardResultDto;
	}

	// board_user_read 삭제 후, board 삭제
	@Override
	@Transactional
	public BoardResultDto deleteBoard(int boardId) {
		BoardResultDto boardResultDto = new BoardResultDto();

		try {
			// board_user_read 삭제 <= return 이 0 건일 수도 있다.
			boardDao.deleteBoardUserRead(boardId);
			
			int ret = boardDao.deleteBoard(boardId);
			if( ret == 1) boardResultDto.setResult("success");
			else boardResultDto.setResult("fail");
		}catch(Exception e) {
			e.printStackTrace();
			boardResultDto.setResult("fail");
			
			// Spring 이 제안하는 방법
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();  // 우리 코드의 진행을 방해 X, 이 코드의 AOP 에게 정책 전달 (rollback 하라)			
		}		
		
		return boardResultDto;
	}	
}















