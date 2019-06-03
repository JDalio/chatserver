# Redis
## session - string
+ key[session:sessionkey] &emsp; value[uid] &emsp; expired[session duration]
+ _save and check session_
## chat
### chat - string
+ key[chat:from:to] &emsp; value[chat relation duration] &emsp; expired[chat relation duration]
+ _cache chat user id, when timeout, triggered in SpringBoot to refresh \<authotity> [TODO]_
+ **SpringBoot load \<chat>, incr value in \<authority>. Deal with timeout, decr \<authority>.**
+ **NETTY ONLY HAVE READ AUTHORITY IN \<chat> AND \<authority>**
### authority - hash
+ key['authority'] &emsp; field[uid] &emsp; value[can chat number]
+ _check whether a user session have authority to chat with others_
+ **NOTE** when add a note in \<chat>, we need to refresh both from and to in \<authority>
### cache - zset
+ key[cache:toUid] &emsp; member[fromUid:type:datetime mess(when voc 'length+url')] &emsp; score[message datetime]

# Protocol

## server message
{type:txt, mess: content}  //not used
{type:/img/voc mess:url}  //not used
{type: sys, code: 5000(user no authority)/100(init get number of unresolved message)/500(server error)/200(shake hand success)/1000(pong)/2000(ack), [mess: message]}
## client message  
{from: uid, type: sys, mess: ping}
{from: uid, type: sys, mess: unread, sessionkey: key}
{from: uid, to: uid, type: txt/img/voc/, mess: message, sessionkey: key}


{from: uid, type: ack, mess: msg_code, sessionkey: key}

### Ps. datetime is always optional, and long, convert in client to display
## binary message
### stage 1
client binary message: 32byte(voc:1, img:2) + binary content   
server: {type: sys, code: 200, mess: url}  
### stage 2
client {from: uid, to: uid, type:txt/img/voc/,mess: message}  
server {type:txt, mess: content} or {type:/img/voc mess:url}  
 

# Message Detail
+ user register  
C{ from: uid, type: "sys", mess: "register" }  
+ server send number of unresolved messages
S{ type: "sys", code: 100, mess: unresolved number}
+ Ping  
C{ from: uid, type: "sys", mess: "ping" }  
+ Pong  
S{ type: "sys", code: 1000 }  
+ user to user message
C{ from: uid, to: uid, type: txt/voc/img, mess: content[when voc, 'length+url']
+ check user authority fail
S{ type: "sys", code: 5000 }

# Websocket Message Flow
## login
[c]connect -> [s]connect established -> [c]send uid
## logout
trigger inactive -> del user

# Client Guarantee
## Active Detection
cPing/sPong(15) + ReadIdleDuration(35)


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
  
