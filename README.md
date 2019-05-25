# SpringBoot
## Task
+ auth  
+ load、 read、 timeout processor of redis  

# Netty
## Task
+ p2p chat  
+ refresh(chat state) redis     

# Redis
## Design
hash chat stuid seniorid expire_time  

# Protocol - json

## server message
{type:txt, mess: content}
{type:/img/voc mess:url}  
{type: sys, code: 500(server error)/200(binary upload success)more..., mess: message}
## client message  
{from: uid, type: sys, mess: register/...}
{from: uid, to: uid, type: txt/img/voc/,mess: message}
## binary message
### stage 1
client binary message: 32byte(voc:1, img:2) + binary content   
server: {type: sys, code: 200, mess: url}  
### stage 2
client {from: uid, to: uid, type:txt/img/voc/,mess: message}  
server {type:txt, mess: content} or {type:/img/voc mess:url}  
 
# auth Work Flow
handshake -> add self -> send/recv message
  
