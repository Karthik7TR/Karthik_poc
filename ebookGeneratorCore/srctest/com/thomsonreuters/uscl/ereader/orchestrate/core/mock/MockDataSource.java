package com.thomsonreuters.uscl.ereader.orchestrate.core.mock;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class MockDataSource implements DataSource {

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter paramPrintWriter) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int paramInt) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public <T> T unwrap(Class<T> paramClass) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return null;
	}

	@Override
	public Connection getConnection(String paramString1, String paramString2)
			throws SQLException {
		return null;
	}

}
