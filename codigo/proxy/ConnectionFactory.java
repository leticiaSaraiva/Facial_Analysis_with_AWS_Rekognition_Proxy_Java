import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//ip = 'sistemadistr.c8hjg7bwthtd.sa-east-1.rds.amazonaws.com'
//port = 5432
//username = sd19
//password = sd2019lj

public class ConnectionFactory {
	
	private final String ip = "ufcquixada.c8hjg7bwthtd.sa-east-1.rds.amazonaws.com";
	private final Integer port = 5432;
	private final String user = "nuvemufc";
	private final String password = "nuvemufc";
	private final String database = "ufcquixada";
	



	public Connection getConnection() {
		try{
			Class.forName("org.postgresql.Driver");
			System.out.println(	this.ip);

			return (Connection) DriverManager.getConnection("jdbc:postgresql://"+ip+":"+port+"/"+database, user, password);
			
		}catch (ClassNotFoundException ex){ System.err.print(ex.getMessage());
        }catch(SQLException e) { System.err.print(e.getMessage());}

		return null;
	}
}
