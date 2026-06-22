package com.mycom.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mycom.myapp.common.DBManager;
import com.mycom.myapp.dto.BookDto;

@Repository
public class BookDaoImpl implements BookDao{

	@Autowired
	DataSource dataSource; // Connection Pool, 생성자 DI 가 추전되는 방법이지만, 의도적으로 필드 DI 사용
	
	@Override
	public List<BookDto> listBook(){
		List<BookDto> list = new ArrayList<>();
		String sql = "select * from book; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				BookDto bookDto = new BookDto();
				bookDto.setBookId(rs.getInt("bookid"));
				bookDto.setBookName(rs.getString("bookname"));
				bookDto.setPublisher(rs.getString("publisher"));
				bookDto.setPrice(rs.getInt("price"));
				list.add(bookDto);
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(rs, pstmt, con);
		}
		
		return list;
	}

	@Override
	public BookDto detailBook(int bookId){
		BookDto bookDto = null;
		String sql = "select * from book where bookid = ?; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, bookId);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				bookDto = new BookDto();
				bookDto.setBookId(rs.getInt("bookid"));
				bookDto.setBookName(rs.getString("bookname"));
				bookDto.setPublisher(rs.getString("publisher"));
				bookDto.setPrice(rs.getInt("price"));
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(rs, pstmt, con);
		}
		
		return bookDto;
	}

	@Override
	public int insertBook(BookDto bookDto){
		int ret = -1;
		String sql = "insert into book values (?, ?, ?, ?); ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, bookDto.getBookId());
			pstmt.setString(2, bookDto.getBookName());
			pstmt.setString(3, bookDto.getPublisher());
			pstmt.setInt(4, bookDto.getPrice());
			ret = pstmt.executeUpdate();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return ret;
	}

	@Override
	public int updateBook(BookDto bookDto){
		int ret = -1;
		String sql = "update book set bookname = ?, publisher = ?, price = ? where bookid = ?; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(sql);			
			pstmt.setString(1, bookDto.getBookName());
			pstmt.setString(2, bookDto.getPublisher());
			pstmt.setInt(3, bookDto.getPrice());
			pstmt.setInt(4, bookDto.getBookId());
			ret = pstmt.executeUpdate();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return ret;
	}

	@Override
	public int deleteBook(int bookId){
		int ret = -1;
		String sql = "delete from book where bookid = ?; ";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(sql);			
			pstmt.setInt(1, bookId);
			ret = pstmt.executeUpdate();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.releaseConnection(pstmt, con);
		}
		
		return ret;
	}

}
