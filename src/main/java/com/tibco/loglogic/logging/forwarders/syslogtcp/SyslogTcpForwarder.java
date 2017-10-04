/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders.syslogtcp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.shared.SyslogMessage;
import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarderSettings.CertificateFingerprint;

/**
 * Forwards log using syslog TCP protocol
 *
 * @author lpautet@tibco.com
 *
 */
public class SyslogTcpForwarder
    extends LogForwarder {

    private static int SOCKET_BUFFER_SIZE = 8 * 1024; // Default to 8192

    // streaming objects
    private Socket streamSocket = null;

    private OutputStream ostream;

    private BufferedOutputStream out = null;

    public SyslogTcpForwarder( SyslogTcpForwarderSettings settings ) {
        super( settings );
    }

    public SSLContext sslContext;

    private KeyStore keyStore;

    @Override
    protected SyslogTcpForwarderSettings getSettings() {
        return (SyslogTcpForwarderSettings) super.getSettings();
    }

    private static final class AcceptAllTrustManager
        implements X509TrustManager {

        @Override
        public void checkClientTrusted( X509Certificate[] x509Certificates, String s )
            throws CertificateException {
            throw new RuntimeException( "not implemented" );
        }

        @Override
        public void checkServerTrusted( X509Certificate[] x509Certificates, String s )
            throws CertificateException {
            // do nothing
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static final class AcceptSha1TrustManager
        implements X509TrustManager {

        private final List<CertificateFingerprint> acceptedCertificateFingerprints;

        public AcceptSha1TrustManager( List<CertificateFingerprint> acceptedCertificateFingerprints ) {
            this.acceptedCertificateFingerprints = acceptedCertificateFingerprints;
        }

        @Override
        public void checkClientTrusted( X509Certificate[] x509Certificates, String s )
            throws CertificateException {
            throw new RuntimeException( "not implemented" );
        }

        @Override
        public void checkServerTrusted( X509Certificate[] x509Certificates, String s )
            throws CertificateException {
            x509Certificates[0].checkValidity();
            for ( CertificateFingerprint certificateFingerprint : acceptedCertificateFingerprints ) {
                try {
                    if ( Arrays.equals( certificateFingerprint.signature,
                                        getFingerprint( certificateFingerprint.algorithm, x509Certificates[0] ) ) ) {
                        return;
                    }
                }
                catch ( NoSuchAlgorithmException e ) {
                    throw new CertificateException( "Unknown fingerprint algorithm: "
                        + certificateFingerprint.algorithm );
                }
                return;
            }
            throw new CertificateException( "Unaccepted Certificate Signature" );
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

    /**
     * open the TCP connection, create an UTF8 writer with it
     */
    @Override
    public void initializeConnection()
        throws IOException {

        if ( getSettings().isUseTls() ) {
            try {
                sslContext = SSLContext.getInstance( getSettings().getTlsProtocolName() );
            }
            catch ( NoSuchAlgorithmException e ) {
                throw new IOException( "Cannot find algorithm for TLS provider: " + e.getMessage() );
            }
            KeyManager[] keyManagers = new KeyManager[0];
            if ( getSettings().getKeystorePath() != null && getSettings().getKeystorePassword() != null ) {
                KeyManagerFactory kmf;
                try {
                    keyStore = KeyStore.getInstance( "JKS" );
                }
                catch ( KeyStoreException e ) {
                    throw new IOException( "Cannot create KeyStore instance: " + e.getMessage() );
                }
                File keyStoreFile = new File( getSettings().getKeystorePath() );
                if ( !keyStoreFile.exists() ) {
                    throw new IOException( "Keystore not found: " + keyStoreFile.getAbsolutePath() );
                }
                try {
                    keyStore.load( new FileInputStream( getSettings().getKeystorePath() ),
                                   getSettings().getKeystorePassword().toCharArray() );
                }
                catch ( NoSuchAlgorithmException e ) {
                    throw new IOException( "Unknown algorithm while opening keystore: " + e.getMessage() );
                }
                catch ( CertificateException e ) {
                    throw new IOException( "Certificate exception while opening keystore: " + e.getMessage() );
                }
                try {
                    kmf = KeyManagerFactory.getInstance( "SunX509", "SunJSSE" );
                }
                catch ( NoSuchProviderException e ) {
                    throw new IOException( "Cannot find KeyManagerFactory provider: " + e.getMessage() );
                }
                catch ( NoSuchAlgorithmException e ) {
                    throw new IOException( "Cannot find KeyManagerFactory algorithm: " + e.getMessage() );
                }
                try {
                    kmf.init( keyStore, getSettings().getKeystorePassword().toCharArray() );
                }
                catch ( KeyStoreException e ) {
                    throw new IOException( "Keystore exception while initializing key manager " + e.getMessage() );
                }
                catch ( NoSuchAlgorithmException e ) {
                    throw new IOException( "No such algorithm exception while initializing key manager "
                        + e.getMessage() );
                }
                catch ( UnrecoverableKeyException e ) {
                    throw new IOException( "Unrecoverable key exception while initializing key manager "
                        + e.getMessage() );
                }
                keyManagers = kmf.getKeyManagers();
            }
            TrustManager[] trustManagers;

            if ( getSettings().isNoServerAuthentication() ) {
                trustManagers = new TrustManager[] { new AcceptAllTrustManager() };
            }
            else if ( getSettings().getAcceptedCertificateFingerprints().size() != 0 ) {
                trustManagers = new TrustManager[] {
                    new AcceptSha1TrustManager( getSettings().getAcceptedCertificateFingerprints() ) };
            }
            else {
                TrustManagerFactory tmf;
                if ( keyStore == null ) {
                    throw new IOException( "No keystore/password defined, set useNoServerAuthentication to true if needed (unsecure)" );
                }
                try {
                    tmf = TrustManagerFactory.getInstance( "PKIX", "SunJSSE" );
                }
                catch ( NoSuchAlgorithmException e ) {
                    throw new IOException( "Cannot find TrustManagerFactory provider: " + e.getMessage() );
                }
                catch ( NoSuchProviderException e ) {
                    throw new IOException( "Cannot find TrustManagerFactory algorithm: " + e.getMessage() );
                }
                try {
                    tmf.init( keyStore );
                }
                catch ( KeyStoreException e ) {
                    throw new IOException( "Keystore exception while initializing trust manager " + e.getMessage() );
                }
                trustManagers = tmf.getTrustManagers();
            }

            try {
                sslContext.init( keyManagers, trustManagers, null );
            }
            catch ( KeyManagementException e ) {
                throw new IOException( "Key Management exception while initializing TLS context " + e.getMessage() );
            }

            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) factory.createSocket();
            sslSocket.setEnabledCipherSuites( new String[] { getSettings().getCipherSuite() } );

            if ( !getSettings().isNoServerAuthentication() && !getSettings().isIgnoreHostnameValidation() ) {
                SSLParameters sslParams = new SSLParameters();
                sslParams.setEndpointIdentificationAlgorithm( "HTTPS" );
                sslSocket.setSSLParameters( sslParams );
            }
            streamSocket = sslSocket;
        }
        else {
            streamSocket = new Socket();
        }

        streamSocket.setSoTimeout( getSettings().getSoTimeout() );
        streamSocket.connect( new InetSocketAddress( getSettings().getHost(), getSettings().getPort() ),
                              getSettings().getSoTimeout() );

        if ( streamSocket.isConnected() ) {
            if ( streamSocket instanceof SSLSocket ) {
                ((SSLSocket) streamSocket).startHandshake();
            }
            streamSocket.setSendBufferSize( SOCKET_BUFFER_SIZE );
            streamSocket.setReceiveBufferSize( SOCKET_BUFFER_SIZE );
            ostream = streamSocket.getOutputStream();
            out = new BufferedOutputStream( ostream );
        }
        else {
            throw new IOException( "Connection failed" );
        }
    }

    /**
     * close the connection
     */
    public void close() {
        try {
            if ( out != null ) {
                out.flush();
                out.close();
                if ( streamSocket != null )
                    streamSocket.close();
            }
        }
        catch ( Exception ignored ) {
        }
    }

    protected void sendMessage( SyslogMessage syslogMessage )
        throws IOException {
        String currentMessage = syslogMessage.toString();
        if ( !getSettings().isUseOctetCounting() ) {
            currentMessage = currentMessage.replace( "\r", "\\r" ).replace( "\n", "\\n" );
            currentMessage += "\n";
        }
        byte[] data = currentMessage.getBytes( "UTF8" );
        if ( getSettings().isUseOctetCounting() ) {
            String header = Integer.toString( data.length ) + " ";
            out.write( header.getBytes( "UTF8" ) );
        }
        out.write( data );
    }

    @Override
    public void flush()
        throws IOException {
        out.flush();
    }

    public static byte[] getFingerprint( String algorithm, X509Certificate cert )
        throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance( algorithm );
        byte[] der = cert.getEncoded();
        md.update( der );
        byte[] digest = md.digest();
        return digest;
    }

    public static String hexifyFingerprint( byte bytes[] ) {

        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

        StringBuffer buf = new StringBuffer( bytes.length * 2 );

        for ( int i = 0; i < bytes.length; ++i ) {
            if ( i != 0 )
                buf.append( ':' );
            buf.append( hexDigits[( bytes[i] & 0xf0 ) >> 4] );
            buf.append( hexDigits[bytes[i] & 0x0f] );
        }

        return buf.toString();
    }
}