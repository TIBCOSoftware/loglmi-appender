/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lpautet on 6/26/17.
 */
public class MiniSyslogTcpServer
    extends Thread {

    private final ServerSocket serverSocket;

    private final List<String> messages = new ArrayList<>();

    private final int port;

    public MiniSyslogTcpServer()
        throws IOException {
        serverSocket = new ServerSocket( 0 );
        port = serverSocket.getLocalPort();
        System.out.println( "port: " + port );
        this.start();
    }

    @Override
    public void run() {
        System.out.println( "Starting mini syslog tcp server" );
        for ( ;; ) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println( "Accepted connection" );
                StringBuffer stringBuffer = new StringBuffer();
                for ( ;; ) {
                    int r = socket.getInputStream().read();
                    if ( r == -1 )
                        break;
                    if ( r == '\n' ) {
                        System.out.println( messages.size() + 1 + ">" + stringBuffer.toString() );
                        messages.add( stringBuffer.toString() );
                        stringBuffer = new StringBuffer();
                    }
                    else {
                        stringBuffer.append( (char) r );
                    }
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    public int getPort() {
        return port;
    }

    public List<String> getMessages() {
        return messages;
    }
}
