package com.lwan.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * A wrapper for statement for use with MS Access DB drivers.
 * This is necessary as the Access engine cannot store stored procs,
 * and cannot execute more then one statement at a time. This object is to
 * get around that by handling the multi-statement and parameter assigning
 * part to javacode.
 * 
 * @author Brutalbarbarian
 *
 */
public class StoredProc {
	HashMap<String, Parameter> parameters;
	List<List<Parameter>> paramMap;
	List<String> statements;
	private ResultSet result;
	
	/**
	 * Simpler constructor for a stored proc which assumes there are no parameters
	 * 
	 * @param _statements
	 */
	public StoredProc (String... _statements) {
		statements = Arrays.asList(_statements);
	}
	
	/**
	 * Make sure all param names start with '@' in order for the parser to work, and only
	 * contain alphanumeric characters or underscores. Any other characters will break the parser. 
	 * 
	 * Be wary with temp tables as they cannot be dropped from the same stored proc where values are selected from and
	 * and returned within a result set. This is due to the result set remaining open, which stops
	 * the table from being dropped. Make sure to clean up any instances of temp tables after they are created 
	 * in a seperate stored proc after the result set has been closed.
	 * 
	 * @param parameters
	 * @param statements
	 * @throws SQLException 
	 */
	public StoredProc (String[] params, int[] paramTypes, String[] _statements) {
		parameters = new HashMap<>();
		// setup params
		for (int i = 0; i < params.length; i++) {
			parameters.put(params[i], new Parameter(params[i], paramTypes[i]));
		}
		// hook up params to each statement
		statements = new Vector<>(_statements.length);
		paramMap = new Vector<>(_statements.length);
		for (String origStatement : _statements) {
			List<Parameter> paramList = new Vector<>();
			char[] statement = origStatement.toCharArray();
			StringBuilder actualStatement = new StringBuilder(statement.length);
			StringBuilder param = new StringBuilder();

			boolean paramFound = false;
			
			for (int i = 0; i < statement.length; i++) {
				if (paramFound) {
					if (!(Character.isLetterOrDigit(statement[i]) || statement[i] == '_')) {
						Parameter par = parameters.get(param.toString());
						if (par == null) {
							throw new RuntimeException("Parameter '" + param.toString() + "' not found in paramList " +
									"for storedproc " + getClass().getName());
						}
						paramList.add(par);
						param.setLength(0);	// reset back to 0
						paramFound = false;	
					} else {
						param.append(statement[i]);
					}
				} else if (statement[i] == '@') {
					paramFound = true;
					actualStatement.append('?');
					param.append('@');
				}
				
				if (!paramFound) {
					actualStatement.append(statement[i]);
				}
			}
			// if reached here with paramFound = true... the param must
			// have been the last item in the statement
			if (paramFound) {
				paramList.add(parameters.get(param.toString()));
			}
			
			statements.add(actualStatement.toString());
			paramMap.add(paramList);
			
		}
	}
	
	/**
	 * Note any result set returned by this must have its originating statement closed manually.
	 * This means calling resultSet.getStatement().close();</br>
	 * If nothing is closed, then all statements created from this stored proc is closed normally.
	 * 
	 * Can override this if need extra logic. Make sure to call result = null prior to calling
	 * anything else. After that, call doExecute() on the statement lines as needed with extra logic
	 * in between such as if/else statements.
	 * 
	 * @param con
	 * @throws SQLException
	 */
	public void execute(Connection con) throws SQLException {
		result = null;	// clear previous result set
		
//		printParameters();

		doExecute(con, 0, statements.size());
	}
	
	protected final void doExecute(Connection con, int line) throws SQLException {
		doExecute(con, line, line+1);
	}
	
	public void printParameters() {
		for (Parameter param : parameters.values()){
			System.out.println(param.name() + "(" + param.getType() + "):" + param.get());
		}
	}
	
	protected final void doExecute(Connection con, int startIndex, int endIndex) throws SQLException {
		if (con == null || con.isClosed()) {
			throw new SQLException("Cannot execute stored procedure without an active connection");
		}
		
		for (int i = startIndex; i < endIndex; i++) {
			PreparedStatement statement = con.prepareStatement(statements.get(i));
			
			
			// just ignore params if its null. Likely this stored proc has no parameters
			if (paramMap != null) { 
				List<Parameter> params = paramMap.get(i);
				for (int j = 1; j <= params.size(); j++) {
					Parameter param = params.get(j - 1);
					setParam(param.get(), param.getType(), statement, j);
				}
			}
			try {
				statement.execute();
			} catch (SQLException e) {
				if (getClass() == StoredProc.class) {
					System.err.println("SQLError: " + statements.get(i));
				} else {
					System.err.println("SQLError: " + getClass().getName());
				}
				throw e;
			}
					
			ResultSet res = statement.getResultSet();
			
			if (res != null) {
				if (result != null) {
					throw new SQLException("Cannot return more then one result set in storedproc " +
							getClass().getName());
				}
				// if there are multiple statements within this stored proc that can return resultsets,
				// only the one executed last will be kept.
				result = res;	
			} else {
				statement.close();
			}
		}
	}
	
	protected void setParam(Object value, int type, PreparedStatement statement, int statementIndex) throws SQLException {
		if (value == null) {
			statement.setNull(statementIndex, type);
		} else {
			if (type == Types.DATE) {
				value = new Date(((java.util.Date)value).getTime());
			}
			statement.setObject(statementIndex, value);
		}
	}
	
	/**
	 * Clear all parameters.
	 * It is recommended to always call this prior to assigning any new parameters.
	 * 
	 */
	public void clearParameters() {
		if (parameters != null) {
			for (Parameter param : parameters.values()) {
				param.set(null);
			}
		}
	}
	
	/**
	 * Get the parameter object by name. Will return
	 * null if parameter dosen't exist.
	 * 
	 * @param name
	 * @return
	 */
	public Parameter getParamByName(String name) {
		if (parameters == null) return null;
		return parameters.get(name);
	}
	
	/**
	 * Get a collection of all the parameter objects.
	 * 
	 * @return
	 */
	public Collection<Parameter> getAllParameters() {
		if (parameters == null) return new ArrayList<Parameter>(0);
		return parameters.values();
	}
	

	/**
	 * Check if this stored proc currently have a result assigned.
	 * Calling this will not clear the result as getResult() would.
	 * 
	 * @return
	 */
	public boolean hasResult() {
		return result == null;
	}
	
	/**
	 * Get the result from the last execution of this stored proc.
	 * Will be null if this stored proc either has never been executed,  
	 * dosen't actually return anything with any of its statements,
	 * or if getResult() has already been called on this StoredProc once since
	 * its last execution. This is to prevent multiple uses of the same result
	 * set from the same execution instance.
	 * 
	 * @return
	 */
	public ResultSet getResult() {
		ResultSet res = result;
		result = null;
		return res;
	}
	
	public String getName() {
		if (getClass() == StoredProc.class) {
			return statements.get(0);
		} else {
			return getClass().getName();
		}
	}
}
