package com.mycom.myapp.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

// DataSource 로부터 Connection 객체를 얻고 사용 후 반납
public class DBManager {
	// Connection 객체 획득, 전달
//	public static Connection getConnection() {
//		
//		Connection con = null;
//		try {
//			
//			Context context = new InitialContext(); // empty context 저장소
//			DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/madang");
//			con = ds.getConnection();
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		return con;
//	}
	
	// DataSource 가 관리하는 Connection 객체는 close() overriding (재정의) 되어 있다.
	// 등록, 수정, 삭제 처리 후 반납
	public static void releaseConnection(PreparedStatement pstmt, Connection con) {
		try {
			if(pstmt != null ) pstmt.close();
			if(con != null ) con.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	// 조회, 상세 조회
	public static void releaseConnection(ResultSet rs, PreparedStatement pstmt, Connection con) {
		try {
			if(rs != null ) rs.close();
			if(pstmt != null ) pstmt.close();
			if(con != null ) con.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void releaseConnecction(AutoCloseable...autoCloseables ) {
		for (AutoCloseable c : autoCloseables) {
			if( c != null ) {
				try {
					c.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
