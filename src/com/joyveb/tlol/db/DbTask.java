package com.joyveb.tlol.db;

import java.sql.ResultSet;

import com.joyveb.tlol.db.parser.DbConst;

public class DbTask {

	private DbConst event_id;
	private DbParser parser = null;
	private PreSql sql = null;
	private DataHandler dataHandler = null;
	private ResultSet resultSet = null;
	private boolean sucess = false;
	private DataStruct inputData = null;
	private boolean isAsync = false;

	DbTask(final DbConst event_id, final DbParser parser, final PreSql sql,
			final DataHandler dataHandler) {
		this.event_id = event_id;
		this.parser = parser;
		this.sql = sql;
		this.dataHandler = dataHandler;
	}

	public final DbConst getEvent_id() {
		return event_id;
	}

	public final void setEvent_id(final DbConst event_id) {
		this.event_id = event_id;
	}

	public final DbParser getParser() {
		return parser;
	}

	public final void setParser(final DbParser parser) {
		this.parser = parser;
	}

	public final PreSql getSql() {
		return sql;
	}

	public void setSql(PreSql sql) {
		this.sql = sql;
	}

	public DataHandler getDataHandler() {
		return dataHandler;
	}

	public void setDataHandler(DataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public boolean isSucess() {
		return sucess;
	}

	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}

	public DataStruct getInputData() {
		return inputData;
	}

	public void setInputData(DataStruct inputData) {
		this.inputData = inputData;
	}

	public boolean isAsync() {
		return isAsync;
	}

	public void setAsync(boolean isAsync) {
		this.isAsync = isAsync;
	}

}
