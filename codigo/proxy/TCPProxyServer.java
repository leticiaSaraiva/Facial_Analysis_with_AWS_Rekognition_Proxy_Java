import java.net.*;
import java.io.*;
import java.util.Scanner;

public class TCPProxyServer {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	String mensagem = "";
	Scanner entrada = new Scanner(System.in);
	String data = "EOF:null";
	byte[] bs;
	ServerSocket listenSocket;


	public TCPProxyServer() {
		
		try{
			int serverPort = 8819; // the server port
			InetAddress aserver = InetAddress.getByName("0.0.0.0");
			
			this.listenSocket = new ServerSocket(serverPort,2,aserver);
			
			//while(true) {
		
		}
		catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}

			
			//}
	}
	public InetAddress getInetAddress(){
		return this.clientSocket.getInetAddress();


	}
	
	public int getPort(){

		return this.clientSocket.getPort();
			

		
	}

	public void accept(){
		try{
			this.clientSocket = this.listenSocket.accept();
			System.out.println("Connected with Python client");
					//Connection c = new Connection(clientSocket);
			this.in = new DataInputStream( clientSocket.getInputStream());
			this.out = new DataOutputStream( clientSocket.getOutputStream());
			this.bs = new byte[1000];
		} catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}

	}
	public void sendReply(byte[] msg,InetAddress inetA, int port_client) {
		try{
			if(inetA.toString().equals(this.clientSocket.getInetAddress().toString()) && port_client == this.clientSocket.getPort()){
				this.out.write(msg);
			}
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());}
		
	}


	public byte[] getRequest() {
		try{
			int i = this.in.read(this.bs);
            if( i == -1){
				String send = "";
				this.bs = send.getBytes();
						
			}

			return this.bs;
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());return null;}
		
	}
	public void close(){
		try{
			this.clientSocket.close();
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());}
		

	}
}
