package com.huixiang.xzb.chatserver.manager;

/**
 * When send messages to a offline user, I call the offline user first,
 * then save the messages in redis set.
 *
 * When the user go online, send these messages saved previous to him first
 */
public class MessageManager {

}
