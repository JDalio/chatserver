package com.huixiang.xzb.chatserver.manager;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

/**
 * UserManager Tester.
 *
 * @author Dalio
 */
public class UserManagerTest {

    @Before
    public void before() throws Exception {

    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: checkConnectAuthority(String sessionkey)
     */
    @Test
    public void testCheckConnectAuthority() throws Exception {
        assert !UserManager.checkConnectAuthority("aaaaaa");
        assert UserManager.checkConnectAuthority("555555");
    }

    @Test
    public void testGetChannel() throws Exception{
        assertEquals(UserManager.getChannel("10"),null);
    }

} 
