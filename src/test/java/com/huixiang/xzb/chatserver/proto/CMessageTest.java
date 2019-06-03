package com.huixiang.xzb.chatserver.proto;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

/**
 * CMessage Tester.
 *
 * @author Dalio
 */
public class CMessageTest {

    private String recvTxtmsg = "{\"to\":\"5\",\"mess\":\"自己给自己发\",\"type\":\"txt\",\"sessionkey\":\"asdfqwer12345678\",\"from\":4,\"datetime\":1559482212000}";
    private String sendTxtmsg = "{\"mess\":\"自己给自己发\",\"type\":\"txt\",\"from\":4,\"datetime\":1559482212000}";

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        CMessage message = new CMessage(recvTxtmsg);
        message.setTo(null);
        message.setSessionkey(null);
        assertEquals(message.toString(), new CMessage(sendTxtmsg).toString());

    }

} 
