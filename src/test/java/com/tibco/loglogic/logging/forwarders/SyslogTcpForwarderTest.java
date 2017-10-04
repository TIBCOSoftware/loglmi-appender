/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.junit.Test;

import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage;
import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarder;
import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarderSettings;
import com.tibco.loglogic.logging.uldpclient.MicroPki;

/**
 * Created by lpautet on 5/31/17.
 */
public class SyslogTcpForwarderTest {

    @Test
    public void test1()
        throws Exception {
        final ServerSocket serverSocket = new ServerSocket( 0 );
        int port = serverSocket.getLocalPort();
        System.out.println( "port: " + port );

        SyslogTcpForwarderSettings syslogTcpForwarderSettings = new SyslogTcpForwarderSettings();
        syslogTcpForwarderSettings.setHost( "localhost" );
        syslogTcpForwarderSettings.setPort( port );

        syslogTcpForwarderSettings.setRawMode( true );
        SyslogTcpForwarder syslogTcpForwarder = new SyslogTcpForwarder( syslogTcpForwarderSettings );

        final Socket[] socket = new Socket[1];

        Executors.newSingleThreadExecutor().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket.setSoTimeout( 10000 );
                    socket[0] = serverSocket.accept();
                    socket[0].setSoTimeout( 10000 );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        syslogTcpForwarder.connect();

        String logLine = "My first message";

        SyslogMessage syslogMessage = new SyslogMessage();

        syslogMessage.setMessage( logLine );

        syslogTcpForwarder.forwardEvent( syslogMessage, true );

        InputStream in = socket[0].getInputStream();
        byte[] buf = new byte[4096];
        int r = in.read( buf );
        String msg = new String( buf, 0, r, Charset.forName( "UTF8" ) );
        assertEquals( logLine + "\n", msg );

        System.out.println( new String( buf ) );
    }

    @Test
    public void testTlsNoAuth()
        throws Exception {
        MicroPki microPki = new MicroPki();
        microPki.createCa();
        microPki.createServerKeystore();
        SSLContext sslContext = SSLContext.getInstance( "TLSv1.2" );
        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509", "SunJSSE" );
        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        keyStore.load( new FileInputStream( microPki.getServerKeystore() ), microPki.getPassword().toCharArray() );
        kmf.init( keyStore, microPki.getPassword().toCharArray() );
        sslContext.init( kmf.getKeyManagers(), new TrustManager[0], null );
        final SSLServerSocket serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory()
            .createServerSocket( 0 );
        int port = serverSocket.getLocalPort();
        System.out.println( "port(TLS): " + port );
        final Socket[] socket = new Socket[1];

        Executors.newSingleThreadExecutor().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket.setSoTimeout( 10000 );
                    socket[0] = serverSocket.accept();
                    SSLSocket sslSocket = (SSLSocket) socket[0];
                    socket[0].setSoTimeout( 10000 );
                    sslSocket.startHandshake();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        SyslogTcpForwarderSettings syslogTcpForwarderSettings = new SyslogTcpForwarderSettings();
        syslogTcpForwarderSettings.setHost( "localhost" );
        syslogTcpForwarderSettings.setPort( port );
        syslogTcpForwarderSettings.setUseTls( true );
        syslogTcpForwarderSettings.setNoServerAuthentication( true );
        syslogTcpForwarderSettings.setSoTimeout( 5000 );

        syslogTcpForwarderSettings.setRawMode( true );
        SyslogTcpForwarder syslogTcpForwarder = new SyslogTcpForwarder( syslogTcpForwarderSettings );

        syslogTcpForwarder.connect();

        String logLine = "My first message";

        SyslogMessage syslogMessage = new SyslogMessage();

        syslogMessage.setMessage( logLine );

        syslogTcpForwarder.forwardEvent( syslogMessage, true );

        InputStream in = socket[0].getInputStream();
        System.out.println( in.available() );
        byte[] buf = new byte[4096];
        int r = in.read( buf );
        String msg = new String( buf, 0, r, Charset.forName( "UTF8" ) );
        assertEquals( logLine.getBytes().length + " " + logLine, msg );
        System.out.println( msg );
    }

    @Test
    public void testTlsServerAuth()
        throws Exception {
        MicroPki microPki = new MicroPki();
        microPki.createCa();
        microPki.createServerKeystore();
        microPki.createClientKeystore();
        SSLContext sslContext = SSLContext.getInstance( "TLSv1.2" );
        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509", "SunJSSE" );
        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        keyStore.load( new FileInputStream( microPki.getServerKeystore() ), microPki.getPassword().toCharArray() );
        System.out.println( keyStore.getCertificate( "ULDP_server" ) );
        kmf.init( keyStore, microPki.getPassword().toCharArray() );
        sslContext.init( kmf.getKeyManagers(), new TrustManager[0], null );
        final SSLServerSocket serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory()
            .createServerSocket( 0 );
        int port = serverSocket.getLocalPort();
        System.out.println( "port(TLS): " + port );
        final Socket[] socket = new Socket[1];

        Executors.newSingleThreadExecutor().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket.setSoTimeout( 10000 );
                    socket[0] = serverSocket.accept();
                    SSLSocket sslSocket = (SSLSocket) socket[0];
                    socket[0].setSoTimeout( 10000 );
                    sslSocket.getSession();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        SyslogTcpForwarderSettings syslogTcpForwarderSettings = new SyslogTcpForwarderSettings();
        syslogTcpForwarderSettings.setHost( "localhost" );
        syslogTcpForwarderSettings.setPort( port );
        syslogTcpForwarderSettings.setUseTls( true );
        syslogTcpForwarderSettings.setKeystorePath( microPki.getClientKeystore().getCanonicalPath() );
        syslogTcpForwarderSettings.setKeystorePassword( microPki.getPassword() );
        syslogTcpForwarderSettings.setSoTimeout( 5000 );
        syslogTcpForwarderSettings.setIgnoreHostnameValidation( false );

        syslogTcpForwarderSettings.setRawMode( true );
        SyslogTcpForwarder syslogTcpForwarder = new SyslogTcpForwarder( syslogTcpForwarderSettings );

        syslogTcpForwarder.connect();

        String logLine = "My first message";

        SyslogMessage syslogMessage = new SyslogMessage();

        syslogMessage.setMessage( logLine );

        syslogTcpForwarder.forwardEvent( syslogMessage, true );

        InputStream in = socket[0].getInputStream();
        System.out.println( in.available() );
        byte[] buf = new byte[4096];
        int r = in.read( buf );
        String msg = new String( buf, 0, r, Charset.forName( "UTF8" ) );
        assertEquals( logLine.getBytes().length + " " + logLine, msg );
        System.out.println( msg );
    }

    @Test
    public void testTlsServerAndClientAuth()
        throws Exception {
        MicroPki microPki = new MicroPki();
        microPki.createCa();
        microPki.createServerKeystore();
        microPki.createClientKeystore();
        SSLContext sslContext = SSLContext.getInstance( "TLSv1.2" );
        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509", "SunJSSE" );
        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        keyStore.load( new FileInputStream( microPki.getServerKeystore() ), microPki.getPassword().toCharArray() );
        kmf.init( keyStore, microPki.getPassword().toCharArray() );
        TrustManagerFactory tmf = TrustManagerFactory.getInstance( "PKIX", "SunJSSE" );
        tmf.init( keyStore );
        sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
        final SSLServerSocket serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory()
            .createServerSocket( 0 );
        serverSocket.setNeedClientAuth( true );
        int port = serverSocket.getLocalPort();
        System.out.println( "port(TLS): " + port );
        final Socket[] socket = new Socket[1];

        Executors.newSingleThreadExecutor().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket.setSoTimeout( 10000 );
                    socket[0] = serverSocket.accept();
                    SSLSocket sslSocket = (SSLSocket) socket[0];
                    socket[0].setSoTimeout( 10000 );
                    sslSocket.startHandshake();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        SyslogTcpForwarderSettings syslogTcpForwarderSettings = new SyslogTcpForwarderSettings();
        syslogTcpForwarderSettings.setHost( "localhost" );
        syslogTcpForwarderSettings.setPort( port );
        syslogTcpForwarderSettings.setUseTls( true );
        syslogTcpForwarderSettings.setKeystorePath( microPki.getClientKeystore().getCanonicalPath() );
        syslogTcpForwarderSettings.setKeystorePassword( microPki.getPassword() );
        syslogTcpForwarderSettings.setSoTimeout( 5000 );

        syslogTcpForwarderSettings.setRawMode( true );
        SyslogTcpForwarder syslogTcpForwarder = new SyslogTcpForwarder( syslogTcpForwarderSettings );

        syslogTcpForwarder.connect();

        String logLine = "My first message";

        SyslogMessage syslogMessage = new SyslogMessage();

        syslogMessage.setMessage( logLine );

        syslogTcpForwarder.forwardEvent( syslogMessage, true );

        InputStream in = socket[0].getInputStream();
        System.out.println( in.available() );
        byte[] buf = new byte[4096];
        int r = in.read( buf );
        String msg = new String( buf, 0, r, Charset.forName( "UTF8" ) );
        assertEquals( logLine.getBytes().length + " " + logLine, msg );
        System.out.println( msg );
    }

    @Test
    public void testTlsServerAuthSha1()
        throws Exception {
        MicroPki microPki = new MicroPki();
        microPki.createCa();
        microPki.createServerKeystore();
        SSLContext sslContext = SSLContext.getInstance( "TLSv1.2" );
        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509", "SunJSSE" );
        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        keyStore.load( new FileInputStream( microPki.getServerKeystore() ), microPki.getPassword().toCharArray() );
        String certificateFingerprint = "sha-1:" + SyslogTcpForwarder.hexifyFingerprint( SyslogTcpForwarder
            .getFingerprint( "sha-1", (X509Certificate) keyStore.getCertificate( "ULDP_server" ) ) );
        kmf.init( keyStore, microPki.getPassword().toCharArray() );
        sslContext.init( kmf.getKeyManagers(), new TrustManager[0], null );
        final SSLServerSocket serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory()
            .createServerSocket( 0 );
        int port = serverSocket.getLocalPort();
        System.out.println( "port(TLS): " + port );
        final Socket[] socket = new Socket[1];

        Executors.newSingleThreadExecutor().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket.setSoTimeout( 10000 );
                    socket[0] = serverSocket.accept();
                    SSLSocket sslSocket = (SSLSocket) socket[0];
                    socket[0].setSoTimeout( 10000 );
                    sslSocket.startHandshake();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        SyslogTcpForwarderSettings syslogTcpForwarderSettings = new SyslogTcpForwarderSettings();
        syslogTcpForwarderSettings.setHost( "localhost" );
        syslogTcpForwarderSettings.setPort( port );
        syslogTcpForwarderSettings.setUseTls( true );
        syslogTcpForwarderSettings.addAcceptedCertificateFingerprints( certificateFingerprint );
        syslogTcpForwarderSettings.setSoTimeout( 5000 );

        syslogTcpForwarderSettings.setRawMode( true );
        SyslogTcpForwarder syslogTcpForwarder = new SyslogTcpForwarder( syslogTcpForwarderSettings );

        syslogTcpForwarder.connect();

        String logLine = "My first message";

        SyslogMessage syslogMessage = new SyslogMessage();

        syslogMessage.setMessage( logLine );

        syslogTcpForwarder.forwardEvent( syslogMessage, true );

        InputStream in = socket[0].getInputStream();
        System.out.println( in.available() );
        byte[] buf = new byte[4096];
        int r = in.read( buf );
        String msg = new String( buf, 0, r, Charset.forName( "UTF8" ) );
        assertEquals( logLine.getBytes().length + " " + logLine, msg );
        System.out.println( msg );
    }

    @Test
    public void testTlsServerAuthFail()
        throws Exception {
        MicroPki microPki = new MicroPki();
        microPki.createCa();
        microPki.createServerKeystore();
        MicroPki microPki2 = new MicroPki();
        microPki2.createCa();
        microPki2.createClientKeystore();
        SSLContext sslContext = SSLContext.getInstance( "TLSv1.2" );
        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509", "SunJSSE" );
        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        keyStore.load( new FileInputStream( microPki.getServerKeystore() ), microPki.getPassword().toCharArray() );
        kmf.init( keyStore, microPki.getPassword().toCharArray() );
        sslContext.init( kmf.getKeyManagers(), new TrustManager[0], null );
        final SSLServerSocket serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory()
            .createServerSocket( 0 );
        int port = serverSocket.getLocalPort();
        System.out.println( "port(TLS): " + port );
        final Socket[] socket = new Socket[1];

        Executors.newSingleThreadExecutor().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket.setSoTimeout( 10000 );
                    socket[0] = serverSocket.accept();
                    SSLSocket sslSocket = (SSLSocket) socket[0];
                    socket[0].setSoTimeout( 10000 );
                    try {
                        sslSocket.startHandshake();
                        assertTrue( "Should not pass", false );
                    }
                    catch ( Exception she ) {
                        System.out.println( "EXCEPTION !" + she.getClass() );
                        return;
                    }
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        SyslogTcpForwarderSettings syslogTcpForwarderSettings = new SyslogTcpForwarderSettings();
        syslogTcpForwarderSettings.setHost( "localhost" );
        syslogTcpForwarderSettings.setPort( port );
        syslogTcpForwarderSettings.setUseTls( true );
        syslogTcpForwarderSettings.setKeystorePath( microPki2.getClientKeystore().getCanonicalPath() );
        syslogTcpForwarderSettings.setKeystorePassword( microPki.getPassword() );
        syslogTcpForwarderSettings.setSoTimeout( 5000 );
        syslogTcpForwarderSettings.setIgnoreHostnameValidation( true );

        syslogTcpForwarderSettings.setRawMode( true );
        SyslogTcpForwarder syslogTcpForwarder = new SyslogTcpForwarder( syslogTcpForwarderSettings );

        try {
            syslogTcpForwarder.connect();
        }
        catch ( SSLHandshakeException she ) {
            System.out.println( "GOOD!" );
            return;
        }

        assertTrue( "Should not pass", false );
    }

}
