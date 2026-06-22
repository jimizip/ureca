package com.mycom.myapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycom.myapp.dao.BookDao;
import com.mycom.myapp.dto.BookDto;

// 단순 CRUD 로서 Service Layer 의 별도의 Business Logic 이 없다.
@Service
public class BookServiceImpl implements BookService{

	@Autowired
	BookDao bookDao; // interface

	@Override
	public List<BookDto> listBook() {
		return bookDao.listBook();
	}

	@Override
	public BookDto detailBook(int bookId) {
		return bookDao.detailBook(bookId);
	}

	@Override
	public int insertBook(BookDto bookDto) {
		return bookDao.insertBook(bookDto);
	}

	@Override
	public int updateBook(BookDto bookDto) {
		return bookDao.updateBook(bookDto);
	}

	@Override
	public int deleteBook(int bookId) {
		return bookDao.deleteBook(bookId);
	}

}
