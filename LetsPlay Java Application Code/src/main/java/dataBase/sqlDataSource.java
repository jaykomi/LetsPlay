package dataBase;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;


public class sqlDataSource {
	

	private static final BasicDataSource ds = new BasicDataSource ();

	static {
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/letsplaydb?allowPublicKeyRetrieval=true&useSSL=false");
		ds.setUsername("root");
		ds.setPassword("8HjK87%v5HT!");
		ds.setMinIdle(5);
		ds.setMaxIdle(10);
		ds.setMaxOpenPreparedStatements(100);
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	private sqlDataSource() {

	}

}



