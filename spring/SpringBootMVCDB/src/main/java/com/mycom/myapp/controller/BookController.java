package com.mycom.myapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycom.myapp.dto.BookDto;
import com.mycom.myapp.service.BookService;

@Controller
public class BookController {

	@Autowired
	BookService bookService;
	
	// books.jsp 페이지 이동
	@GetMapping("/books")
	public String bookMain() {
		return "books";
	}
	
	// 목록, 상세, 등록, 수정, 삭제는 json 데이터 응답
	// json 으로 변환할 자료구조 (model) 를 그대로 return 하는 메소드를 만들고
	// 그 메소드 위에 @ResponseBody 를 붙여준다. -> Spring Framework 이 자동으로 json 응답을 만들어서 Client 에게 전달
	
	// 목록
	@GetMapping("/books/list")
	@ResponseBody
	public List<BookDto> listBook(){
		List<BookDto> bookList = bookService.listBook();
		return bookList;
	}
	
	// 상세
	// @PathVariable
	@GetMapping("/books/detail/{bookId}")
	@ResponseBody
	public BookDto detailBook(@PathVariable Integer bookId){
		BookDto bookDto = bookService.detailBook(bookId);
		return bookDto;
	}
	
	// 등록
	@PostMapping("/books/insert")
	@ResponseBody
	public Map<String, String> insertBook(BookDto bookDto){
		int ret = bookService.insertBook(bookDto);
		Map<String, String> map = new HashMap<>();
		if( ret == 1 ) {
			map.put("result", "success");
		}else {
			map.put("result", "fail");
		}
		return map;
	}
	
	// 수정
	@PostMapping("/books/update")
	@ResponseBody
	public Map<String, String> updateBook(BookDto bookDto){
		int ret = bookService.updateBook(bookDto);
		Map<String, String> map = new HashMap<>();
		if( ret == 1 ) {
			map.put("result", "success");
		}else {
			map.put("result", "fail");
		}
		return map;
	}
	
	// 삭제
	@GetMapping("/books/delete/{bookId}")
	@ResponseBody
	public Map<String, String> deleteBook(@PathVariable Integer bookId){
		int ret = bookService.deleteBook(bookId);
		Map<String, String> map = new HashMap<>();
		if( ret == 1 ) {
			map.put("result", "success");
		}else {
			map.put("result", "fail");
		}
		return map;
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
