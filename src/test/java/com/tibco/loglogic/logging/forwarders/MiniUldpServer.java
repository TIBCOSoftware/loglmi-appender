/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tibco.loglogic.logging.uldpclient.DummyUldpServer;

/**
 * Created by lpautet on 6/26/17.
 */
public class MiniUldpServer
    extends DummyUldpServer {

    public MiniUldpServer()
        throws IOException {
        super();
    }

    int msgCount = 0;

    static class ReceivedUldpMessage {
        final List<KVP> kvpList;

        final int msgType;

        final int logSourceType;

        final String domainName;

        public ReceivedUldpMessage( List<KVP> kvpList, int msgType, int logSourceType, String domainName ) {
            this.kvpList = kvpList;
            this.msgType = msgType;
            this.logSourceType = logSourceType;
            this.domainName = domainName;
        }
    }

    List<ReceivedUldpMessage> messages = new ArrayList<>();

    @Override
    public void run() {
        for ( ;; ) {
            try {
                acceptConnection();
                negotiate();
                for ( ;; ) {
                    List<KVP> message = this.readOneMessage();
                    System.out.println( "ULDP" + ++msgCount + ">" );
                    messages.add( new ReceivedUldpMessage( message, this.getMsgType(), this.getLogSourceType(),
                                                           this.getDomainName() ) );
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
                return;
            }
        }
    }

    public List<ReceivedUldpMessage> getMessages() {
        return messages;
    }

}
