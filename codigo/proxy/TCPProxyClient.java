import java.net.*;
import java.io.*;

public class TCPProxyClient {
	DataInputStream in;
	DataOutputStream out;
	Socket s;
	String mensagem = "";
	String data = "EOF:null";


	public TCPProxyClient() {
		
		// args give message contents and destination hostname
		Socket s = null;
		try{
			int serverPort = 6000;
			int clientPort = 5300;
			String localhost = "localhost";
			InetAddress aclient = InetAddress.getByName("127.0.0.2");
			this.s = new Socket(localhost, serverPort,aclient,clientPort);   
			this.s.setSoTimeout(20000);
			//Scanner scan = new Scanner(System.in);
			System.out.println("Connected with Python server");

			this.in = new DataInputStream( new BufferedInputStream(this.s.getInputStream()));
			this.out = new DataOutputStream( this.s.getOutputStream());	                        
			//byte[] buffer = new byte[1000];
			

		//	System.out.println("Received: " + data);	
		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage()); System.exit(0);
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());System.exit(0);
		}catch (IOException e){System.out.println("readline:"+e.getMessage());System.exit(0);
		}//finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
	}		      	
	

	
	public void sendRequest(byte[] msg) {
		try{
			this.out.write(msg);
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());}
		
	}


	public byte[] getResponse() {
		try{

			int i = this.in.available();
			while(true) {
				i = this.in.available();
				if(i > 100 ) break;
			}

	
			byte[] bs = new byte[i];
	
				
			this.in.read(bs);
            
			return bs;
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());return null;}
		 catch(Exception e) {System.out.println("error:"+e.getMessage());return null;}

	}
	public void close(){
		try{
			this.s.close();
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());}
		

	}
}
