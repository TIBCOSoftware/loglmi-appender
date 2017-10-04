package com.tibco.loglogic.logging.forwarders.syslogtcp;
/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
import java.util.ArrayList;
import java.util.List;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarderSettings;

/**
 * Settings for forwarding logs using syslog TCP protocol
 *
 * Default port is 514 for regular connection and 6514 for TLS connection
 *
 * RFC 5424, 5425
 *
 * @author lpautet@tibco.com
 *
 */
public class SyslogTcpForwarderSettings
    extends LogForwarderSettings {

    private final static int DEFAULT_SYSLOG_PORT = 514;

    private final static int DEFAULT_SYSLOG_TLS_PORT = 6514;

    private static final String DEFAULT_CIPHER_SUITE = "TLS_RSA_WITH_AES_128_CBC_SHA";

    private boolean useOctetCounting = false;

    private boolean useTls = false;

    private boolean noServerAuthentication = false;

    private String keystorePath;

    private String tlsProtocolName = "TLSv1.2";

    private String cipherSuite = DEFAULT_CIPHER_SUITE;

    private String keystorePassword;

    private String host = "";

    private int port = DEFAULT_SYSLOG_PORT;

    private int soTimeout = 0;

    private boolean ignoreHostnameValidation = false;

    private List<CertificateFingerprint> acceptedCertificateFingerprints = new ArrayList<>();

    static final class CertificateFingerprint {
        final String algorithm;

        final byte[] signature;

        CertificateFingerprint( String textFingerprint ) {
            String[] parts = textFingerprint.split( ":" );
            algorithm = parts[0];
            signature = new byte[parts.length - 1];
            for ( int i = 0; i < signature.length; i++ ) {
                signature[i] = (byte) ( Integer.parseInt( parts[i + 1], 16 ) & 0xff );
            }
        }
    }

    /**
     * Get the host to connect to
     *
     * @return the destination host
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the destination host
     *
     * @param host the destination host
     */
    public void setHost( String host ) {
        this.host = host;
    }

    /**
     * Get the port to use
     *
     * @return the port number to use
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port to use for the connection.
     *
     * Has no effect if the port number is 0;
     *
     * @param port the port to use.
     */
    public void setPort( int port ) {
        if ( port == 0 )
            return;
        this.port = port;
    }

    /**
     * Get whether to use TLS connection for contacting the destination or not.
     *
     * @return true if using a TLS connection
     */
    public boolean isUseTls() {
        return useTls;
    }

    /**
     * Set whether to use TLS connection for contacting the destination or not.
     *
     * If true, this sets useOctetCounting to true and changes the port to 6514 if the port is set to the default port
     * (514).
     *
     * If false, this sets useOctetCount to false and changes the port to 514 if the port is set to the TLS default
     * (6514).
     *
     * @param useTls
     */
    public void setUseTls( boolean useTls ) {
        this.useTls = useTls;
        if ( useTls ) {
            setUseOctetCounting( true );
            if ( port == DEFAULT_SYSLOG_PORT )
                port = DEFAULT_SYSLOG_TLS_PORT;
        }
        else {
            useOctetCounting = false;
            if ( port == DEFAULT_SYSLOG_TLS_PORT )
                port = DEFAULT_SYSLOG_PORT;
        }
    }

    /**
     * Get whether the server should not be authenticated or not
     *
     * default: false
     *
     * @return true if the server should not be authenticated
     */
    public boolean isNoServerAuthentication() {
        return noServerAuthentication;
    }

    /**
     * Set whether the server should not be authenticated or not
     *
     * @param noServerAuthentication true if the server should not be authenticated
     */
    public void setNoServerAuthentication( boolean noServerAuthentication ) {
        this.noServerAuthentication = noServerAuthentication;
    }

    /**
     * Get the path for the keystore to use. The keystore is used to locate the anchor of trust (CA) to authenticate the
     * server, unless noServerAuthentication is set to true.
     *
     * The keystore is also used to locate the certificate to present to the server, unless the server is configured not
     * to authenticate clients.
     *
     * @return the path of the keystore to use
     */
    public String getKeystorePath() {
        return keystorePath;
    }

    /**
     * Set the path for the keystore to use. The keystore is used to locate the anchor of trust (CA) to authenticate the
     * server, unless noServerAuthentication is set to true.
     *
     * The keystore is also used to locate the certificate to present to the server, unless the server is configured not
     * to authenticate clients.
     *
     * @param keystorePath the path of the keystore to use
     */
    public void setKeystorePath( String keystorePath ) {
        this.keystorePath = keystorePath;
    }

    /**
     * Get the name of the TLS protocol to use
     *
     * default is: TLSv1.2
     *
     * @return the name of the TLS protocol to use
     */
    public String getTlsProtocolName() {
        return tlsProtocolName;
    }

    /**
     * Set the name of the TLS protocol to use
     *
     * default is: TLSv1.2
     *
     * @param tlsProtocolName the name of the TLS protocol to use
     */
    public void setTlsProtocolName( String tlsProtocolName ) {
        this.tlsProtocolName = tlsProtocolName;
    }

    /**
     * Get the cipher suite to use
     *
     * default is: TLS_RSA_WITH_AES_128_CBC_SHA
     *
     * @return the cipher suite to use
     */
    public String getCipherSuite() {
        return cipherSuite;
    }

    /**
     * Set the cipher suite to use
     *
     * default is: TLS_RSA_WITH_AES_128_CBC_SHA
     *
     * @param cipherSuite the cipher suite to use
     */
    public void setCipherSuite( String cipherSuite ) {
        this.cipherSuite = cipherSuite;
    }

    /**
     * Get the password of the keystore
     *
     * @return the password of the keystore
     */
    public String getKeystorePassword() {
        return keystorePassword;
    }

    /**
     * Set the password of the keystore
     *
     * @return the password of the keystore
     */
    public void setKeystorePassword( String keystorePassword ) {
        this.keystorePassword = keystorePassword;
    }

    /**
     * Get whether to use octet counting framing
     *
     * default: false for non-TLS, true for TLS (as per RFC 5425)
     *
     * @return true if using octet counting framing
     */
    public boolean isUseOctetCounting() {
        return useOctetCounting;
    }

    /**
     * Set whether to use octet counting framing
     *
     * default: false for non-TLS, true for TLS (as per RFC 5425)
     *
     * @param useOctetCounting true if using octet counting framing
     */
    public void setUseOctetCounting( boolean useOctetCounting ) {
        this.useOctetCounting = useOctetCounting;
    }

    /**
     * Get the socket timeout
     *
     * default: 0 means wait indefinitely
     *
     * @return the socket timeout (in ms)
     */
    public int getSoTimeout() {
        return soTimeout;
    }

    /**
     * Set the socket timeout
     *
     * default: 0 means wait indefinitely
     *
     * @param soTimeout the socket timeout
     */
    public void setSoTimeout( int soTimeout ) {
        this.soTimeout = soTimeout;
    }

    /**
     * Adds a new certificate fingerprint to the list of accepted certificates
     *
     * As per RFC 5425, the format is:
     *
     * the fingerprint is prepended with an ASCII label identifying the hash function followed by a colon.
     * Implementations MUST support SHA-1 as the hash algorithm and use the ASCII label "sha-1" to identify the SHA-1
     * algorithm. The length of a SHA-1 hash is 20 bytes and the length of the corresponding fingerprint string is 65
     * characters. An example certificate fingerprint is:
     * sha-1:E1:2D:53:2B:7C:6B:8A:29:A2:76:C8:64:36:0B:08:4B:7A:F1:9E:9D
     *
     * @param certificateFingerprint the certificate fingerprint to add
     */
    public void addAcceptedCertificateFingerprints( String certificateFingerprint ) {
        acceptedCertificateFingerprints.add( new CertificateFingerprint( certificateFingerprint ) );
    }

    List<CertificateFingerprint> getAcceptedCertificateFingerprints() {
        return acceptedCertificateFingerprints;
    }

    /**
     * Get whether or not the hostname of the destination should be verified
     *
     * default: true
     *
     * @return true if the destination hostname should be verified
     */
    public boolean isIgnoreHostnameValidation() {
        return ignoreHostnameValidation;
    }

    /**
     * Set whether or not the hostname of the destination should be verified
     *
     * default: true
     *
     * @param ignoreHostnameValidation true if the destination hostname should be verified
     */
    public void setIgnoreHostnameValidation( boolean ignoreHostnameValidation ) {
        this.ignoreHostnameValidation = ignoreHostnameValidation;
    }
}
