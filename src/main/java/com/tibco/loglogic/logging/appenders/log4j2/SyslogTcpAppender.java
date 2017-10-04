/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.appenders.log4j2;

import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarder;
import com.tibco.loglogic.logging.forwarders.syslogtcp.SyslogTcpForwarderSettings;

/**
 * Log4j 2.x Appender for sending logs using Syslog/TCP to LMI
 *
 * @author lpautet@tibco.com
 *
 */
@Plugin(name = "SyslogTcpAppender", category = "Core", elementType = "appender", printObject = true)
public final class SyslogTcpAppender
    extends LogAppender {

    protected SyslogTcpAppender( String name, Filter filter, Layout<? extends Serializable> layout,
                                 final boolean ignoreExceptions, SyslogTcpForwarderSettings settings )
        throws Exception {
        super( name, filter, layout, ignoreExceptions, settings, new SyslogTcpForwarder( settings ) );
    }

    @PluginFactory
    public static SyslogTcpAppender createAppender( @PluginAttribute("name") String name,
                                                    @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                    @PluginElement("Filter") final Filter filter,
                                                    @PluginAttribute("host") String host,
                                                    @PluginAttribute("port") int port,
                                                    @PluginAttribute("appName") String appName,
                                                    @PluginAttribute("source") String source,
                                                    @PluginAttribute("facility") int facility,
                                                    @PluginAttribute("useTls") boolean useTls,
                                                    @PluginAttribute("keystorePath") String keystorePath,
                                                    @PluginAttribute("keystorePassword") String keystorePassword,
                                                    @PluginAttribute("tlsProtocolName") String tlsProtocolName,
                                                    @PluginAttribute("cipherSuite") String cipherSuite,
                                                    @PluginAttribute("noServerAuth") boolean noServerAuth,
                                                    @PluginAttribute("ignoreHostnameValidation") boolean ignoreHostnameValidation,
                                                    @PluginAttribute("acceptedCertificateFingerprints") String acceptedCertificateFingerprints,
                                                    @PluginAttribute("useOctetCounting") boolean useOctetCounting,
                                                    @PluginAttribute("soTimeout") int soTimeout)

        throws Exception {
        if ( name == null ) {
            LOGGER.error( "No name provided for SyslogTcpAppender" );
            return null;
        }
        if ( layout == null ) {
            layout = PatternLayout.createDefaultLayout();
        }
        SyslogTcpForwarderSettings settings = new SyslogTcpForwarderSettings();
        settings.setPort( port );
        settings.setAppName( appName );
        settings.setSource( source );
        if ( facility != 0 ) {
            settings.setFacility( (byte) facility );
        }
        settings.setSoTimeout( soTimeout );
        settings.setUseOctetCounting( useOctetCounting );
        settings.setUseTls( useTls );
        settings.setKeystorePath( keystorePath );
        settings.setKeystorePassword( keystorePassword );
        settings.setTlsProtocolName( tlsProtocolName );
        settings.setCipherSuite( cipherSuite );
        settings.setNoServerAuthentication( noServerAuth );
        settings.setIgnoreHostnameValidation( ignoreHostnameValidation );
        if ( acceptedCertificateFingerprints != null ) {
            String[] parts = acceptedCertificateFingerprints.split(",");
            for (String s : parts) {
                settings.addAcceptedCertificateFingerprints(s);
            }
        }
        return new SyslogTcpAppender( name, filter, layout, true, settings );
    }
}