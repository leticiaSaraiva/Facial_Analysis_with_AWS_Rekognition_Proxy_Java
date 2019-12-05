import socket
import json
import TCPServer

class TCPServer:
    def __init__(self):
        PORT = 6000         # Porta que o Servidor esta
        HOST = '0.0.0.0'
        self.tcp = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.orig = (HOST, PORT)
        self.tcp.bind(self.orig)
        self.tcp.listen(1)    
       
    def accept(self):
        self.con, client = self.tcp.accept()
        print ('Concetado por', client)
       
    def sendReply(self, msg):
        self.con.send(msg)
    def getRequest(self):
        return self.con.recv(4096)


