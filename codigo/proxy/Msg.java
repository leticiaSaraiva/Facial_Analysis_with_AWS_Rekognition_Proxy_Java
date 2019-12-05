import com.google.gson.Gson;
/*
public class teste {
	public static void main(String[] args) {
        Gson gson = new Gson();
		eita vi = new eita();
		String saida = gson.toJson(vi);
		System.out.println(saida);


       
	}
}

*/

class Msg{
	int messageType;
	int requestId;
	String objectReference;
	int methodId;
	String arguments;
	

public Msg(int messageType,int requestId,String objectReference,int methodId,String arguments){
		this.messageType = messageType;
		this.requestId = requestId;
		this.objectReference = objectReference;
		this.methodId = methodId;
		this.arguments = arguments;

	}
	public int getmessageType(){
		return this.messageType;

	}
	public void setmessageType(int messageType){
		this.messageType = messageType;

	}

	public int getrequestId(){
		return this.requestId;
	}
	public void setrequestId(int requestId){
		this.requestId = requestId;

	}


	public String getobjectReference(){
		return this.objectReference;
	}

	public void setobjectReference(String objectReference){
		this.objectReference = objectReference;


	}

	public int getmethodId(){
		return this.methodId;
	}

	public void setmethodId(int methodId){
		this.methodId = methodId;



	}


	public String getarguments(){
		return this.arguments;
	}

	public void setarguments(String arguments){
		this.arguments = arguments;



	}




	



}
