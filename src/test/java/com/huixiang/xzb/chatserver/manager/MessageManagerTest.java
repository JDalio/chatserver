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
    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getMsgs(String uid)
     */
    @Test
    public void testGetMsgs() throws Exception {
        assertNull(MessageManager.getMsgs("bn"));
        List<CMessage>msgs=MessageManager.getMsgs("ZmTVjw");
        assertNotNull(msgs);
        for(CMessage cMessage:msgs){
            System.out.println(cMessage);
        }
    }
}
