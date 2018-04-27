/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import static org.junit.Assert.assertEquals;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.tibco.loglogic.logging.LmiLogEvent;

/**
 * Created by lpautet on 6/27/17.
 */
public class LmiLogEventTest {

    @Test
    public void test()
        throws UnknownHostException {
        LmiLogEvent lmiLogEvent = new LmiLogEvent( "TestEvent", "E001" );

        System.out.println( lmiLogEvent.toString() );
        assertEquals( "ll_eventName=\"TestEvent\" ll_eventID=\"E001\"", lmiLogEvent.toString() );

        lmiLogEvent.setBytesReceived( 456 );
        lmiLogEvent.setSourceIP( InetAddress.getByName( "10.11.12.13" ) );
        lmiLogEvent.setTargetIP( InetAddress.getByName( "fe80::f816:3eff:fe56:c623" ) );

        assertEquals( "ll_eventName=\"TestEvent\" ll_eventID=\"E001\" ll_bytesReceived=\"456\" ll_sourceIP=\"10.11.12.13\" ll_targetIP=\"fe80:0:0:0:f816:3eff:fe56:c623\"",
                      lmiLogEvent.toString() );

        System.out.println( lmiLogEvent.toString() );

        lmiLogEvent = new LmiLogEvent( "TestEvent", "E002" );

        lmiLogEvent.addKVP( "BooleanField", true );
        lmiLogEvent.addKVP( "CharField", '@' );
        lmiLogEvent.addKVP( "IntField", 12345 );
        lmiLogEvent.addKVP( "LongField", 0x1000000000000000l );
        lmiLogEvent.addKVP( "DoubleField", 1234567890.0987654321 );
        lmiLogEvent.addKVP( "StringField", "Contains \" quotes and \\ slashes" );
        lmiLogEvent.addKVP( "InetValueField", Inet6Address.getLoopbackAddress() );

        assertEquals( "ll_eventName=\"TestEvent\" ll_eventID=\"E002\" BooleanField=\"true\" CharField=\"@\" IntField=\"12345\" LongField=\"1152921504606846976\" DoubleField=\"1.2345678900987654E9\" StringField=\"Contains \\\" quotes and \\\\ slashes\" InetValueField=\"127.0.0.1\"",
                      lmiLogEvent.toString() );

        System.out.println( lmiLogEvent.toString() );

        lmiLogEvent = new LmiLogEvent( "TestEvent", "E003" );
        lmiLogEvent.addThrowable( new RuntimeException( "This is a dummy one" ) );

        String expected = "ll_eventName=\"TestEvent\" ll_eventID=\"E003\" exceptionClass=\"java.lang.RuntimeException\" exceptionMessage=\"This is a dummy one\" stackTraceElement=\"com.tibco.loglogic.logging.forwarders.LmiLogEventTest.test(LmiLogEventTest.java:56)";

        assertEquals( expected, lmiLogEvent.toString().substring( 0, expected.length() ) );

        System.out.println( lmiLogEvent.toString() );

    }

}
