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

import com.tibco.loglogic.logging.forwarders.uldp.UldpForwarder;
import com.tibco.loglogic.logging.forwarders.uldp.UldpForwarderSettings;
import com.tibco.loglogic.logging.uldpclient.UldpConnectionSettings;

/**
 * Log4j 2.x Appender for sending logs using Syslog/ULDP to LMI
 *
 * @author lpautet@tibco.com
 *
 */
@Plugin(name = "UldpAppender", category = "Core", elementType = "appender", printObject = true)
public final class UldpAppender
    extends LogAppender {

    protected UldpAppender( String name, Filter filter, Layout<? extends Serializable> layout,
                            final boolean ignoreExceptions, UldpForwarderSettings settings )
        throws Exception {
        super( name, filter, layout, ignoreExceptions, settings, new UldpForwarder( settings ) );
    }

    @PluginFactory
    public static UldpAppender createAppender( @PluginAttribute("name") String name,
                                               @PluginElement("Layout") Layout<? extends Serializable> layout,
                                               @PluginElement("Filter") final Filter filter,
                                               @PluginAttribute("host") String host, @PluginAttribute("port") int port,
                                               @PluginAttribute("appName") String appName,
                                               @PluginAttribute("source") String source,
                                               @PluginAttribute("facility") int facility,
                                               @PluginAttribute("useTls") boolean useAuthentication,
                                               @PluginAttribute("useEncryption") boolean useEncryption,
                                               @PluginAttribute("useCompression") boolean useCompression,
                                               @PluginAttribute("keystorePath") String keystorePath,
                                               @PluginAttribute("keystorePassword") String keystorePassword,
                                               @PluginAttribute("tlsProtocolName") String tlsProtocolName,
                                               @PluginAttribute("cipherSuite") String cipherSuite,
                                               @PluginAttribute("domainName") String domainName,
                                               @PluginAttribute("noServerAuth") boolean noServerAuth,
                                               @PluginAttribute("ignoreHostnameValidation") boolean ignoreHostnameValidation,
                                               @PluginAttribute("acceptedCertificateFingerprints") String acceptedCertificateFingerprints )

        throws Exception {
        if ( name == null ) {
            LOGGER.error( "No name provided for UldpAppender" );
            return null;
        }
        if ( layout == null ) {
            layout = PatternLayout.createDefaultLayout();
        }
        UldpConnectionSettings uldpConnectionSettings = new UldpConnectionSettings( host );
        UldpForwarderSettings settings = new UldpForwarderSettings( uldpConnectionSettings );
        uldpConnectionSettings.setPort( port );
        settings.setAppName( appName );
        settings.setSource( source );
        uldpConnectionSettings.setUseTls( useAuthentication );
        uldpConnectionSettings.setUseEncryption( useEncryption );
        uldpConnectionSettings.setUseCompression( useCompression );
        uldpConnectionSettings.setKeystorePath( keystorePath );
        uldpConnectionSettings.setKeystorePassword( keystorePassword );
        if ( tlsProtocolName != null )
            uldpConnectionSettings.setTlsProtocolName( tlsProtocolName );
        if ( cipherSuite != null )
            uldpConnectionSettings.setCipherSuite( cipherSuite );
        if ( domainName != null )
            uldpConnectionSettings.setDomainName( domainName );
        uldpConnectionSettings.setNoServerAuthentication( noServerAuth );

        if ( facility != 0 ) {
            settings.setFacility( (byte) facility );
        }

        if ( acceptedCertificateFingerprints != null ) {
            String[] parts = acceptedCertificateFingerprints.split(",");
            for (String s : parts) {
                uldpConnectionSettings.addAcceptedCertificateFingerprints(s);
            }
        }

        return new UldpAppender( name, filter, layout, true, settings );
    }
}