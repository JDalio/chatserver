package com.huixiang.xzb.chatserver.manager;

import com.huixiang.xzb.chatserver.proto.CMessage;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.List;

import static org.junit.Assert.*;

/**
 * MessageManager Tester.
 *
 * @author Dalio
 */
public class MessageManagerTest {
    private String msg = "{\"from\":5,\"type\":\"txt\",\"to\":4,\"datetime\":1558885388628,\"sessionkey\":\"555555\",\"mess\":\"你好啊\"}";
    private String msg1 = "{\"to\":4,\"from\":6,\"type\":\"img\",\"datetime\":1558885388629,\"sessionkey\":\"666666\",\"mess\":\"http://39.107.65.148/chat/class.jpg\"}";
    private String invalidMsg = "{\"from\":6,\"type\":\"txt\",\"to\":4,\"datetime\":1558885388628,\"sessionkey\":\"asdfqwer12345678\",\"mess\":\"你好啊\"}";
    private String invalidMsg1 = "{\"from\":7,\"type\":\"txt\",\"to\":4,\"datetime\":1558885388628,\"sessionkey\":\"777777\",\"mess\":\"你好啊\"}";
    private String pingmsg = "{\"from\":4,\"type\":\"sys\",\"mess\":\"ping\"}";

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: checkCMessage(CMessage msg)
     */
    @Test
    public void testCheckCMessage() throws Exception {
        assert MessageManager.checkCMessage(new CMessage(msg));
        assert MessageManager.checkCMessage(new CMessage(msg1));
        assert MessageManager.checkCMessage(new CMessage(pingmsg));

        assert !MessageManager.checkCMessage(new CMessage(invalidMsg));
        assert !MessageManager.checkCMessage(new CMessage(invalidMsg1));
    }

    /**
     * Method: cache(CMessage cMessage)
     */
    @Test
    public void testCache() throws Exception {
        MessageManager.cache(new CMessage(msg));
        MessageManager.cache(new CMessage(msg1));
    }

    /**
     * Method: getUnresolvedNum(String uid)
     */
    @Test
    public void testGetUnresolvedNum() throws Exception {
        assertEquals(MessageManager.getUnresolvedNum("4"), 2);
    }

    /**
     * Method: getUnread(String uid)
     */
    @Test
    public void testGetUnresolvedMsg() throws Exception {
        List<CMessage> msgs = MessageManager.getUnresolvedMsg("4");
        assertEquals(msgs.get(1), new CMessage(msg));
        assertEquals(msgs.get(0), new CMessage(msg1));
    }


} 
