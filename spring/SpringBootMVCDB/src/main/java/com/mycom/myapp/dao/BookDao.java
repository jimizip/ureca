package com.mycom.myapp.dao;

import java.util.List;

import com.mycom.myapp.dto.BookDto;

public interface BookDao {
	List<BookDto> listBook();
	BookDto detailBook(int bookId);
	int insertBook(BookDto bookDto);
	int updateBook(BookDto bookDto);
	int deleteBook(int bookId);
}
