/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.appenders.log4j;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.uldp.UldpForwarder;
import com.tibco.loglogic.logging.forwarders.uldp.UldpForwarderSettings;
import com.tibco.loglogic.logging.uldpclient.UldpConnectionSettings;

/**
 * Log4j Appender for sending logs using Syslog/ULDP to LMI
 *
 * @author lpautet@tibco.com
 *
 */
public class UldpAppender
    extends LogAppender {

    private UldpConnectionSettings uldpConnectionSettings;

    private UldpForwarderSettings uldpForwarderSettings;

    public UldpAppender() {
        super();
        this.uldpConnectionSettings = new UldpConnectionSettings( "" );
        this.uldpForwarderSettings = new UldpForwarderSettings( uldpConnectionSettings );
    }

    @Override
    protected UldpForwarderSettings getSettings() {
        return uldpForwarderSettings;
    }

    @Override
    protected LogForwarder createForwarder() {
        return new UldpForwarder( uldpForwarderSettings );
    }

    public boolean getUseCompression() {
        return uldpConnectionSettings.isUseCompression();
    }

    public void setUseCompression( boolean useCompression ) {
        uldpConnectionSettings.setUseCompression( useCompression );
    }

    public boolean getUseTls() {
        return uldpConnectionSettings.isUseTls();
    }

    public void setUseTls( boolean useAuthentication ) {
        uldpConnectionSettings.setUseTls( useAuthentication );
    }

    public boolean getUseEncyrption() {
        return uldpConnectionSettings.isUseEncryption();
    }

    public void setUseEncryption( boolean useEncryption ) {
        uldpConnectionSettings.setUseEncryption( useEncryption );
    }

    public String getKeystorePath() {
        return uldpConnectionSettings.getKeystorePath();
    }

    public void setKeystorePath( String keystorePath ) {
        uldpConnectionSettings.setKeystorePath( keystorePath );
    }

    public String getKeystorePassword() {
        return uldpConnectionSettings.getKeystorePassword();
    }

    public void setKeystorePassword( String keystorePass ) {
        uldpConnectionSettings.setKeystorePassword( keystorePass );
    }

    public String getTlsProtocolName() {
        return uldpConnectionSettings.getTlsProtocolName();
    }

    public void setTlsProtocolName( String tlsProtocolName ) {
        uldpConnectionSettings.setTlsProtocolName( tlsProtocolName );
    }

    public String getCipherSuite() {
        return uldpConnectionSettings.getCipherSuite();
    }

    public void setCipherSuite( String cipherName ) {
        uldpConnectionSettings.setCipherSuite( cipherName );
    }

    public String getDomainName() {
        return uldpConnectionSettings.getDomainName();
    }

    public void setDomainName( String domainName ) {
        uldpConnectionSettings.setDomainName( domainName );
    }

    public boolean getNoServerAuth() {
        return uldpConnectionSettings.isNoServerAuthentication();
    }

    public void setNoServerAuth( boolean noServerAuth ) {
        uldpConnectionSettings.setNoServerAuthentication( noServerAuth );
    }

    public String getHost() {
        return uldpConnectionSettings.getDestination();
    }

    public void setHost( String host ) {
        uldpConnectionSettings.setDestination( host );
    }

    public int getPort() {
        return uldpConnectionSettings.getPort();
    }

    public void setPort( int port ) {
        uldpConnectionSettings.setPort( port );
    }

    public String getAppName() {
        return getSettings().getAppName();
    }

    public void setAppName( String appName ) {
        getSettings().setAppName( appName );
    }

    public String getSource() {
        return getSettings().getSource();
    }

    public void setSource( String source ) {
        getSettings().setSource( source );
    }

    public void setRawMode( boolean rawMode ) {
        getSettings().setRawMode( rawMode );
    }

    public boolean getRawMode() {
        return getSettings().isRawMode();
    }

    public void setMaxQueueSize( String maxQueueSize ) {
        getSettings().setMaxQueueSize( maxQueueSize );
    }

    public boolean isIgnoreHostnameValidation() {
        return uldpConnectionSettings.isIgnoreHostnameValidation();
    }

    public void setIgnoreHostnameValidation( boolean val ) {
        uldpConnectionSettings.setIgnoreHostnameValidation( val );
    }

    public void setAcceptedCertificateFingerprints( String acceptedCertificateFingerprints ) {
        String[] parts = acceptedCertificateFingerprints.split( "," );
        for ( String s : parts ) {
            uldpConnectionSettings.addAcceptedCertificateFingerprints( s );
        }
    }
}
