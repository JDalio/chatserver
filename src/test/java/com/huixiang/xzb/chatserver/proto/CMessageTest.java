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
    private CMessage cMessage;
    private String pongjson = "{\"from\":\"abcdef\",\"type\":\"sys\",\"mess\":\"pong\"}";
    private String txtjson = "{\"from\":\"abcdef\",\"type\":\"txt\",\"to\":\"wxzy34\",\"datetime\":1558885388628,\"mess\":\"哈喽\"}";
    private String urljson = "{\"from\":\"abcdef\",\"type\":\"txt\",\"to\":\"wxzy34\",\"datetime\":1558885388628,\"mess\":\"https://www.google.com/search?rlz=1C2CHBD_zh-CNUS808US808&safe=strict&source=hp&ei=E5fqXOzbMOGJ0gKT4ZCIDA&q=%E5%BE%AE%E4%BF%A1%E5%B0%8F%E7%A8%8B%E5%BA%8F+%E6%94%B9%E5%8F%98globalData&oq=%E5%BE%AE%E4%BF%A1%E5%B0%8F%E7%A8%8B%E5%BA%8F+%E6%94%B9%E5%8F%98globalData&gs_l=psy-ab.12...1535.13530..13878...3.0..0.565.10893.2-30j5j0j2......0....1..gws-wiz.....6..35i39j0i131j0j0i203j0i12j0i4i30j0i30.09N1PtXNQD4\"}";

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
        cMessage = new CMessage(pongjson);
        assertEquals("abcdef", cMessage.getFrom());
        assertEquals("sys", cMessage.getType());
        assertEquals("pong", cMessage.getMess());
        System.out.println(cMessage);

        cMessage = new CMessage(txtjson);
        assertEquals("abcdef", cMessage.getFrom());
        assertEquals(new Long(1558885388628L), cMessage.getDatetime());
        assertEquals("哈喽", cMessage.getMess());

        cMessage = new CMessage(urljson);
        assertEquals("https://www.google.com/search?rlz=1C2CHBD_zh-CNUS808US808&safe=strict&source=hp&ei=E5fqXOzbMOGJ0gKT4ZCIDA&q=%E5%BE%AE%E4%BF%A1%E5%B0%8F%E7%A8%8B%E5%BA%8F+%E6%94%B9%E5%8F%98globalData&oq=%E5%BE%AE%E4%BF%A1%E5%B0%8F%E7%A8%8B%E5%BA%8F+%E6%94%B9%E5%8F%98globalData&gs_l=psy-ab.12...1535.13530..13878...3.0..0.565.10893.2-30j5j0j2......0....1..gws-wiz.....6..35i39j0i131j0j0i203j0i12j0i4i30j0i30.09N1PtXNQD4"
                , cMessage.getMess());


    }

} 
