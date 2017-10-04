/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.handlers.jdk;

import java.util.logging.LogManager;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarder;
import com.tibco.loglogic.logging.forwarders.shared.LogForwarderSettings;
import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarder;
import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarderSettings;

/**
 * java.util.logging Handler for sending logs using Syslog/TCP to LMI
 *
 * @author lpautet@tibco.com
 *
 */
public class SyslogTcpHandler
    extends LogHandler {

    @Override
    protected LogForwarderSettings createSettings() {
        return new SyslogTcpForwarderSettings();
    }

    @Override
    protected SyslogTcpForwarderSettings getSettings() {
        return (SyslogTcpForwarderSettings) super.getSettings();
    }

    @Override
    protected LogForwarder createForwarder() {
        return new SyslogTcpForwarder( getSettings() );
    }

    /**
     * Read in the handler properties from the config file
     */
    @Override
    protected void configure() {
        super.configure();

        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();

        String property = cname + ".host";
        if (manager.getProperty(property) == null) {
            throw new RuntimeException("Cannot find logging property: " + property);
        }
        setHost(manager.getProperty(property));
        property = cname + ".port";
        if (manager.getProperty(property) != null) {
            setPort(Integer.parseInt(manager.getProperty(property)));
        }
        property = cname + ".maxQueueSize";
        if (manager.getProperty(property) != null) {
            setMaxQueueSize(manager.getProperty(property));
        }
        property = cname + ".useOctetCounting";
        if (manager.getProperty(property) != null) {
            setMaxQueueSize(manager.getProperty(property));
        }
        property = cname + ".soTimeout";
        if (manager.getProperty(property) != null) {
            setSoTimeout(Integer.parseInt(manager.getProperty(property)));
        }
        property = cname + ".useTls";
        if (manager.getProperty(property) != null) {
            setUseTls(Boolean.parseBoolean(manager.getProperty(property)));
        }
        property = cname + ".keystorePath";
        if (manager.getProperty(property) != null) {
            setKeystorePath(manager.getProperty(property));
        }
        property = cname + ".keystorePassword";
        if (manager.getProperty(property) != null) {
            setKeystorePassword(manager.getProperty(property));
        }
        property = cname + ".tlsProtocolName";
        if (manager.getProperty(property) != null) {
            setTlsProtocolName(manager.getProperty(property));
        }
        property = cname + ".cipherSuite";
        if (manager.getProperty(property) != null) {
            setCipherSuite(manager.getProperty(property));
        }
        property = cname + ".noServerAuth";
        if (manager.getProperty(property) != null) {
            setNoServerAuth(Boolean.parseBoolean(manager.getProperty(property)));
        }
        property = cname + ".ignoreHostnameValidation";
        if (manager.getProperty(property) != null) {
            setIgnoreHostnameValidaiton(Boolean.parseBoolean(manager.getProperty(property)));
        }
        property = cname + ".acceptedCertificateFingerprints";
        if (manager.getProperty(property) != null) {
            setAcceptedCertificateFingerprints( manager.getProperty( property ));
        }
    }

    private void setHost( String host ) {
        if ( host != null )
            getSettings().setHost( host );
    }

    private void setPort( int port ) {
        if ( port > 0 )
            getSettings().setPort( port );
    }

    private void setMaxQueueSize( String maxQueueSize ) {
        if ( maxQueueSize != null )
            getSettings().setMaxQueueSize( maxQueueSize );
    }

    private void setSoTimeout( int soTimeout ) {
        getSettings().setSoTimeout( soTimeout );
    }

    public void setUseTls( boolean useAuthentication ) {
        getSettings().setUseTls( useAuthentication );
    }

    public void setNoServerAuth( boolean noServerAuth ) {
        getSettings().setNoServerAuthentication( noServerAuth );
    }

    public void setKeystorePath( String keystorePath ) {
        getSettings().setKeystorePath( keystorePath );
    }

    public void setKeystorePassword( String keystorePass ) {
        getSettings().setKeystorePassword( keystorePass );
    }

    public void setTlsProtocolName( String tlsProtocolName ) {
        getSettings().setTlsProtocolName( tlsProtocolName );
    }

    public void setCipherSuite( String cipherName ) {
        getSettings().setCipherSuite( cipherName );
    }

    public void setIgnoreHostnameValidaiton( boolean ignoreHostnameValidaiton ) {
        getSettings().setIgnoreHostnameValidation( ignoreHostnameValidaiton );
    }

    public void setAcceptedCertificateFingerprints(String acceptedCertificateFingerprints) {
        String[] parts = acceptedCertificateFingerprints.split(",");
        for ( String s : parts) {
            getSettings().addAcceptedCertificateFingerprints( s );
        }
    }

}
