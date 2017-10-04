/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.appenders.logback;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.shared.LogForwarderSettings;
import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarder;
import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarderSettings;

/**
 * LogBack Appender for sending logs using Syslog/TCP to LMI
 *
 * @author lpautet@tibco.com
 *
 */
public class SyslogTcpAppender
    extends LogAppender {

    private SyslogTcpForwarderSettings settings = new SyslogTcpForwarderSettings();

    @Override
    protected SyslogTcpForwarderSettings getSettings() {
        return settings;
    }

    @Override
    protected LogForwarder createForwarder() {
        return new SyslogTcpForwarder( settings );
    }

    public String getHost() {
        return settings.getHost();
    }

    public void setHost( String host ) {
        settings.setHost( host );
    }

    public int getPort() {
        return settings.getPort();
    }

    public void setPort( int port ) {
        settings.setPort( port );
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

    public String getMaxQueueSize() {
        return Long.toString( getSettings().getMaxQueueSize() );
    }

    public void setMaxQueueSize( String maxQueueSize ) {
        getSettings().setMaxQueueSize( maxQueueSize );
    }

    public boolean getUseTls() {
        return getSettings().isUseTls();
    }

    public void setUseTls( boolean useAuthentication ) {
        getSettings().setUseTls( useAuthentication );
    }

    public String getKeystorePath() {
        return getSettings().getKeystorePath();
    }

    public void setKeystorePath( String keystorePath ) {
        getSettings().setKeystorePath( keystorePath );
    }

    public String getKeystorePassword() {
        return getSettings().getKeystorePassword();
    }

    public void setKeystorePassword( String keystorePass ) {
        getSettings().setKeystorePassword( keystorePass );
    }

    public String getTlsProtocolName() {
        return getSettings().getTlsProtocolName();
    }

    public void setTlsProtocolName( String tlsProtocolName ) {
        getSettings().setTlsProtocolName( tlsProtocolName );
    }

    public String getCipherSuite() {
        return getSettings().getCipherSuite();
    }

    public void setCipherSuite( String cipherName ) {
        getSettings().setCipherSuite( cipherName );
    }

    public boolean getNoServerAuth() {
        return getSettings().isNoServerAuthentication();
    }

    public void setNoServerAuth( boolean noServerAuth ) {
        getSettings().setNoServerAuthentication( noServerAuth );
    }

    public boolean isIgnoreHostnameValidation() {
        return getSettings().isIgnoreHostnameValidation();
    }

    public void setIgnoreHostnameValidation( boolean val ) {
        getSettings().setIgnoreHostnameValidation( val );
    }

    public void setAcceptedCertificateFingerprints( String acceptedCertificateFingerprints ) {
        String[] parts = acceptedCertificateFingerprints.split(",");
        for ( String s : parts) {
            getSettings().addAcceptedCertificateFingerprints( s );
        }
    }

    public boolean isUseOctetCounting() {
        return getSettings().isUseOctetCounting();
    }

    public void setUseOctetCounting( boolean useOctetCounting ) {
        getSettings().setUseOctetCounting( useOctetCounting );
    }

    public int getSoTimeout() {
        return getSettings().getSoTimeout();
    }

    public void setSoTimeout( int soTimeout ) {
        getSettings().setSoTimeout( soTimeout );
    }

}