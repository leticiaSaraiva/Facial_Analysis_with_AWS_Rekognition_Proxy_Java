import java.net.*;
import java.io.*;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.sql.*;

public class Proxy {
	
	
	public static void main (String args[]) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;	
		connection = new ConnectionFactory().getConnection();;
		System.out.println("\nConnected to the database");
		Gson gson = new Gson();


		TCPProxyServer tcpserver  = new TCPProxyServer();
		TCPProxyClient tcpclient = new TCPProxyClient();


		while(true){
			tcpserver.accept();	
			String resposta = new String(" ");
			InetAddress inetA = tcpserver.getInetAddress();
			int port_client = tcpserver.getPort();
			while(true){
				byte [] requestPython = tcpserver.getRequest();
				resposta = new String(requestPython);
				if(resposta.equals(""))
					break;

				JsonReader reader =  new JsonReader(new StringReader(resposta));

				reader.setLenient(true);

				Msg msg = 	gson.fromJson(reader, Msg.class);

				if(msg.getmethodId() == 2){

					
					JsonReader re =  new JsonReader(new StringReader(msg.getarguments()));
					re.setLenient(true);

					User user = gson.fromJson(re, User.class);
					System.out.println("User: "+ user.getusername() + " " + inetA.toString() +" "+ port_client );
					String nome;
					String password;	
					try{
						String sql = "SELECT * FROM PEAPLE WHERE USERNAME = ?";
						PreparedStatement stmt = connection.prepareStatement(sql);
						stmt.setString(1, user.getusername());
						
						ResultSet ra = stmt.executeQuery();
						
						ra.next();
											
						nome = ra.getString("username");			
						password = ra.getString("password");						
						stmt.close();
					}catch(SQLException e) {
						nome = "";
						password = "";
					}

						Msg msg_send;
						if(nome.equals("")){
							String arguments = "{\"result\":-1}";

							msg_send = new Msg(1,msg.getrequestId(),msg.getobjectReference(),msg.getmethodId(),arguments);
						}
						
						else if(!password.equals(user.getpassword())){
							String arguments = "{\"result\":-2}";
							
							msg_send = new Msg(1,msg.getrequestId(),msg.getobjectReference(),msg.getmethodId(),arguments);

						}
						else {
							String arguments = "{\"result\":2}";

							msg_send = new Msg(1,msg.getrequestId(),msg.getobjectReference(),msg.getmethodId(),arguments);
						}

						String msg_reply = gson.toJson(msg_send);

						tcpserver.sendReply(msg_reply.getBytes(),inetA, port_client);
						
					



				}
				else if(msg.getmethodId() == 3){
					JsonReader re =  new JsonReader(new StringReader(msg.getarguments()));
					re.setLenient(true);

					User user = gson.fromJson(re, User.class);
					System.out.println("User: "+ user.getusername()+ " " + inetA.toString() +" "+ port_client);
					String nome= user.getusername();
					String password = user.getpassword();	
					String sql = "INSERT INTO PEAPLE (USERNAME, PASSWORD) VALUES (?, ?)";
					Msg msg_send;

					try{
					    ps = connection.prepareStatement(sql);
						
						ps.setString(1, nome);
						ps.setString(2, password);
				

						int rowsAffected = ps.executeUpdate();
						ps.close();


						if(rowsAffected > 0){
							String arguments = "{\"result\":3}";
							msg_send = new Msg(1,msg.getrequestId(),msg.getobjectReference(),msg.getmethodId(),arguments);
						}
						else {
							String arguments = "{\"result\":-1}";

							msg_send = new Msg(1,msg.getrequestId(),msg.getobjectReference(),msg.getmethodId(),arguments);
						}

					}
					catch(SQLException e){
						System.err.println(e.getMessage());
						String arguments = "{\"result\":-1}";

						msg_send = new Msg(1,msg.getrequestId(),msg.getobjectReference(),msg.getmethodId(),arguments);

					}

		

					String msg_reply = gson.toJson(msg_send);

					tcpserver.sendReply(msg_reply.getBytes(),inetA,port_client);
						
					

				}
			else if(msg.getmethodId() == 1 || msg.getmethodId() == 4){

				String msg_reply = gson.toJson(msg);

				tcpclient.sendRequest(msg_reply.getBytes());
						


				byte[] response_Server = tcpclient.getResponse();
	


				tcpserver.sendReply(response_Server,inetA,port_client);


			}



		}
			
			
		}
	}
}

		
