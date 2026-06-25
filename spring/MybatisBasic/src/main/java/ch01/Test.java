package ch01;

import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

// SqlSessionFactoryBuilder -> SqlSessionFactory -> SqlSession
public class Test {

	public static void main(String[] args) throws Exception{
		// Mybatis 설정 내용을 SqlSessionFactoryBuilder 에게 전달하면서 시작
		Reader reader = Resources.getResourceAsReader("config/mybatis-config.xml");
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		SqlSession session = sqlSessionFactory.openSession(); // DB Access, autocommit 이 false 로 설정 default
		
		// BookDao.class : Mybatis 에게 전달하는 BookDao 클래스의 정보
		// 리턴되는 bookDao : 추상클래스에 있는 메소드를 모두 구현한 구현 클래스의 객체 변수
		BookDao bookDao = session.getMapper(BookDao.class);
		
		// 우리는 BookDao 의 구현 클래스를 작성 X
		
		// 목록
		List<BookDto> bookList = bookDao.listBook();
		for (BookDto bookDto : bookList) {
			System.out.println(bookDto);
		}
		
		
		session.close();
	}

}
