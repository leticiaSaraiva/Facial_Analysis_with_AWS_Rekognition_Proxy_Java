import socket
import boto3

import psycopg2
import json
import TCPServer
              # Endereco IP do Servidor

reko_client = boto3.client('rekognition','us-east-1')
rds_client = boto3.client('rds','sa-east-1')
rds = rds_client.describe_db_instances()

port = rds['DBInstances'][0]['Endpoint']['Port']
adress = rds['DBInstances'][0]['Endpoint']['Address']
Username = rds['DBInstances'][0]['MasterUsername']
DBName = rds['DBInstances'][0]['DBName']
print(DBName, Username, adress,port)
postgre = psycopg2.connect(dbname=DBName, user=Username,password=Username,host=adress,port=port)
cur = postgre.cursor()
#cur.execute("CREATE TABLE PEAPLE (USERNAME VARCHAR(50) PRIMARY KEY, PASSWORD VARCHAR(50));");
#cur.execute("CREATE TABLE INFO (USERNAME VARCHAR(50), IDINFO SERIAL, SENTIMENTO VARCHAR(40), SMILE BOOL, PRIMARY KEY(USERNAME,IDINFO), FOREIGN KEY(USERNAME) REFERENCES PEAPLE(USERNAME));")
#postgre.commit()


tcp = TCPServer.TCPServer()

        

print("Conected in database")
s3_bucket = 'ufcquixada'

while True:
    tcp.accept()
    
    
    while True:
        msg = tcp.getRequest()
        if not msg:
            
            break
        msg = msg.decode('utf-8')
      
        
        print(msg)
    

        

        msg = json.loads(msg)
        arg = json.loads(msg['arguments'])
    
        
        #Analisar Imagem
        if(msg['methodId'] == 1):
            file_name = arg['image']
            username= arg['username']

            img = {
                    'S3Object':{
                    'Bucket': s3_bucket, 'Name': file_name
                    }
                }
            response = reko_client.detect_faces(Image=img, Attributes=['ALL'])
            if(len(response['FaceDetails']) == 1):
                sex = response['FaceDetails'][0]['Gender']['Value']
                confidenceSex = response['FaceDetails'][0]['Gender']['Confidence']
            
                AgeL = response['FaceDetails'][0]['AgeRange']['Low']
                AgeH = response['FaceDetails'][0]['AgeRange']['High']
                best_sentimento = 0
                indice = 0
                name_sentimento = ''                
                for i in range(len(response['FaceDetails'][0]['Emotions'])):
                    if(response['FaceDetails'][0]['Emotions'][i]['Confidence'] > best_sentimento):
                        indice = i
                        best_sentimento = response['FaceDetails'][0]['Emotions'][i]['Confidence']

                sentimento = response['FaceDetails'][0]['Emotions'][indice]['Type']
                
                confidenceSmile = response['FaceDetails'][0]['Smile']['Confidence']
                #eyeg = response['FaceDetails'][0]['Eyeglasses']['Value']
                eyegConf = response['FaceDetails'][0]['Eyeglasses']['Confidence']
                #mustache = response['FaceDetails'][0]['Mustache']['Mustache']
                mustacheConf = response['FaceDetails'][0]['Mustache']['Confidence']
                #eyeOpen = response['FaceDetails'][0]['EyesOpen']['Value']
                eyeOpenConf = response['FaceDetails'][0]['EyesOpen']['Confidence']
               
                
                sorrindo = ''
                if(response['FaceDetails'][0]['Smile']['Value']):
                    sorrindo = 'Yes'
                else:
                    sorrindo = 'No'
                
                eyeg = ''
                if(response['FaceDetails'][0]['Eyeglasses']['Value']):
                    eyeg = 'Yes'
                else:
                    eyeg = 'No'
                
                
                mustache = ''
                if(response['FaceDetails'][0]['Mustache']['Value']):
                    mustache = 'Yes'
                else:
                    mustache = 'No'
                    
                eyeOpen = ''
                if(response['FaceDetails'][0]['EyesOpen']['Value']):
                    eyeOpen = 'Yes'
                else:
                    eyeOpen = 'No'
                
                
                
                
                
                width = response['FaceDetails'][0]['BoundingBox']['Width']
                height = response['FaceDetails'][0]['BoundingBox']['Height']
                top = response['FaceDetails'][0]['BoundingBox']['Top']
                left = response['FaceDetails'][0]['BoundingBox']['Left']
                
                
                send_value = {'result': 1,'Eyeglasses':eyeg,'EyeglassesConf':eyegConf,'Mustache':mustache,'MustacheConf':mustacheConf,'EyeOpen':eyeOpen,'EyeOpenConf':eyeOpenConf,'Sex': sex,'ConfS':confidenceSex, 'AgeL': AgeL, 'AgeH': AgeH,'Sent':sentimento,'ConfSe':best_sentimento,'Smile':sorrindo,'ConfSmile':confidenceSmile,'Width':width,'Height':height,'Top':top,'Left':left}

                messageType = 1
                requestId = msg['requestId']
                objectReference = "Image Analisys"
                methodId = 1 
                arguments = json.dumps(send_value)         
                msg_send = {"messageType": messageType,"requestId": requestId,"objectReference": objectReference, "methodId": methodId, "arguments": arguments}
                send = json.dumps(msg_send).encode('utf-8')
                tcp.sendReply(send)
                
                

                                
                if(sorrindo == 'Yes'):    
                    cur.execute("INSERT INTO INFO VALUES(%s,DEFAULT,%s,TRUE)",(username,sentimento))
                else:
                    cur.execute("INSERT INTO INFO VALUES(%s,DEFAULT,%s,FALSE)",(username,sentimento))
                print("save in DB")
                postgre.commit()
                
                
                
                     
             
                
                
                
                
            else:
                
                messageType = 1
                requestId = msg['requestId']
                objectReference = "Image Analisys"
                methodId = 1 
                arguments = json.dumps({'result':-1})         
                msg_send = {"messageType": messageType,"requestId": requestId,"objectReference": objectReference, "methodId": methodId, "arguments": arguments}
                print(msg_send)
                send = json.dumps(msg_send).encode('utf-8')
            
                tcp.sendReply(send)
        
        #Login
       
        elif(msg['methodId'] == 4):
            username = arg['username']
            cur.execute("SELECT * FROM INFO WHERE USERNAME = %s",(username,))
            result_info = cur.fetchall()
            
            if(len(result_info) != 0):
                messageType = 1
                requestId = msg['requestId']
                objectReference = "Image Analisys"
                methodId = 4 
                arguments = json.dumps({'data':result_info,'result': 4})         
                msg_send = {"messageType": messageType,"requestId": requestId,"objectReference": objectReference, "methodId": methodId, "arguments": arguments}
                print(msg_send)
            else:
                messageType = 1
                requestId = msg['requestId']
                objectReference = "Image Analisys"
                methodId = 4
                arguments = json.dumps({'result':-1})         
                msg_send = {"messageType": messageType,"requestId": requestId,"objectReference": objectReference, "methodId": methodId, "arguments": arguments}
           
        
            print(msg_send)
            send = json.dumps(msg_send).encode('utf-8')
            tcp.sendReply(send)
            
            #tcpserver.sendReply(pickle.dumps(result_info))


            
    #tcpserver.close()
