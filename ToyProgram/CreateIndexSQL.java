import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateIndexSQL {

	Connection conn = null;
	ResultSet rs = null;

	String dbUserName = "";

	ArrayList<String> columnNames = null;
	HashMap<String, String> columnTypes = null;
	HashMap<String, String> columnNullables = null;
	ArrayList<String> tablePrimaryKeys = null;
	HashMap<String, String> tablePrimaryKeysHaspMap = null;
	
	
	/* Get Database Connection */
	public void getDatabaseConnection(String databaseType, String databaseURL, String userName, String password) {
		this.conn = null;
		try {
			if (databaseType.equals("oracle")) {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				this.conn = DriverManager.getConnection(databaseURL, userName, password);
				this.dbUserName = userName;
			} else {
				throw new Exception(
						"DatabaseType Invalid. please check DatabaseType. This program is only supported by oracle. Please set database type 'oracle'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* 쿼리 실행 */
	public void executeSqlStatement(String sql) throws SQLException {
		PreparedStatement psmt = conn.prepareStatement(sql);
		psmt.executeUpdate();
	}

	/* Close Database Connection */
	public void closeDatabaseConnection() {
		try {
			if (rs != null)
				rs.close();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	public String get_index_sql(String sourceUser, String orgtableName, String subScript) throws SQLException {
		StringBuffer tbSchemaSql = new StringBuffer();
		Formatter fm = new Formatter(tbSchemaSql);
		Connection connection = this.conn;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// GET_DDL(INDEX)
		fm.format("SELECT DBMS_METADATA.GET_DDL('INDEX', u.index_name, u.owner) createSql \n" + 
				"from dba_indexes u where index_name like '%s%s' and table_owner = '%s'", orgtableName, '%', sourceUser);	
		Statement tbStatement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = tbStatement.executeQuery(tbSchemaSql.toString());		

		// PK 찾기
		Statement tbStatement2 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		String sql = "select CONSTRAINT_NAME from dba_constraints where OWNER = '" + sourceUser + "' AND TABLE_NAME = '" + orgtableName + "' AND CONSTRAINT_TYPE = 'P'";
		ResultSet rsPK = tbStatement2.executeQuery(sql);	
		rsPK.next();
		String pkName = rsPK.getString("CONSTRAINT_NAME");
		rsPK.close();
		
		// return 값 담을 StringBuilder
		StringBuilder query = new StringBuilder();
		Formatter fmQ = new Formatter(query);

		// 정규식
	    String regEx = "CREATE ([A-Z ]*)INDEX (\"[1-9A-Z\\w]*\").\"([1-9A-Z\\w]*)\" ON \""+sourceUser+"\".\"([1-9A-Z\\w]*)\" (.*)";
	    Pattern pat = Pattern.compile(regEx);
	    Matcher match = null;
	    String irTabNm;
	    String coulmStr;
	    char[] ch;
	    int quote = 0;
	    int pkCnt;
	    
	    while (rs.next()) {
			String createSql = rs.getString("createSql");
			String[] sql1 = createSql.split("\\n");//.replaceAll("\"", "")
			pkCnt = 0;
			
			for (int i = 0; i < sql1.length; i++) {
				if ( sql1[i].toUpperCase().indexOf("CREATE") != -1 ) {
					match = pat.matcher(sql1[i]);

					if(match.find()) {
						// IR 인터페이스 테이블 명
						irTabNm = "IR_" + match.group(4) + subScript + "_INT";
						
						// 컬럼에서 따옴표("") 발라내기
						// 함수 안의 따옴표는 나두고 그 외 컬럼명을 감싼 따옴표는 제거
						// {EX} ("A", "B", SUBSTR("C", 1, 1)) => (A, B, SUBSTR("C", 1, 1)) 
						coulmStr = "";
						ch = match.group(5).toCharArray();
						for(int k = 0; k<ch.length; k++) {
							if(ch[k] == '(') {
								quote++;
							} else if (ch[k] == ')') {
								quote--;	
							} 
							
							if (quote == 1) {
								if(ch[k] != '"') {
									coulmStr += ch[k];	
								}
							} else {
								coulmStr += ch[k];	
							}
						}

//						if(match.group(3).matches("[A-Z0-9_\\w\"]*_PK")) {
						if(match.group(3).equals(pkName)) {
//							query.append("--");
							pkCnt++;
							if(pkCnt>1) {
								System.err.println("--" + match.group(4));
							}
						} else {
							// CREATE INDEX 문 생성 
							if(match.group(4).replaceAll("\"", "").equals(orgtableName)) {
								fmQ.format("CREATE %sINDEX %s.%s ON %s.%s %s;\n",
										match.group(1), 
										this.dbUserName, 
										match.group(3), 
										this.dbUserName, 
										irTabNm, 
										coulmStr);
							}
						}
					}
					i = sql1.length;
				}
			}
	    }
		
		rs.close();

		return query.toString();
	}
	
	
	public void runProcess(String databaseURL, String userName, String password, String sourceUser, String filePath, String subScript) { 
		CreateIndexSQL cis =  null;
		
		if (subScript.length() > 0 & !subScript.substring(0, 0).equals("_")) {
			subScript = "_" + subScript;
		}
		
		try {
			/* read File */
    		File file = new File(filePath);
    		FileReader filereader = new FileReader(file);
    		BufferedReader bufReader = new BufferedReader(filereader);
    		String line = "";
    		String result;
    		
    		getDatabaseConnection("oracle", databaseURL, userName, password);

			/* sql create & run */
    		while((line = bufReader.readLine()) != null){
    			try {
        			if(!line.contains("--")) { //주석 "--"
        				result = get_index_sql(sourceUser, line, subScript);
        				if(result.length() > 0) {
        					System.out.println("--" + line);
        					System.out.println(result);
        				}
        			}
				} catch (Exception e) {
					System.out.println("--" + line);
					e.printStackTrace();
				}
    		}
    		
    		filereader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!conn.isClosed()) {
					closeDatabaseConnection();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        
		
		System.out.println("--END");
	}
}
