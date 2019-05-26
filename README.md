# SpringBoot
## Task
+ auth  
+ load、 read、 timeout processor of redis  

# Netty
## Task
+ p2p chat  
+ refresh(chat state) redis     

# Redis
## unresolved message: list
to[from:time mess]
## chat relationship: hash
 chat stuid seniorid expire_time  

# Protocol - json

## server message
{type:txt, mess: content}
{type:/img/voc mess:url}  
{type: sys, code: 500(server error)/200(binary upload success)more..., mess: message}
## client message  
{from: uid, type: sys, mess: register/exit/cerror}
{from: uid, to: uid, type: txt/img/voc/,mess: message}
## binary message
### stage 1
client binary message: 32byte(voc:1, img:2) + binary content   
server: {type: sys, code: 200, mess: url}  
### stage 2
client {from: uid, to: uid, type:txt/img/voc/,mess: message}  
server {type:txt, mess: content} or {type:/img/voc mess:url}  
 
# Websocket Message Flow
## login
[c]connect -> [s]connect established -> [c]send uid
## logout

# Guarantee 
## delivery to end
### client 
save messages in local storage
attach message with id(from_to_nth)
respond server when message received
### server
save each message in redis
when receiving response, delete the message
re-send unconfirmed message periodically

  
