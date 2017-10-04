/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.handlers.jdk;

import java.util.logging.LogManager;

import com.tibco.loglogic.logging.forwarders.uldp.UldpForwarder;
import com.tibco.loglogic.logging.forwarders.uldp.UldpForwarderSettings;
import com.tibco.loglogic.logging.uldpclient.UldpConnectionSettings;

/**
 * java.util.logging handler for sending logs using Syslog/ULDP
 *
 * @author lpautet@tibco.com
 *
 */
public class UldpHandler
    extends LogHandler {

    private UldpConnectionSettings uldpConnectionSettings;

    @Override
    protected UldpForwarderSettings getSettings() {
        return (UldpForwarderSettings) super.getSettings();
    }

    @Override
    protected UldpForwarderSettings createSettings() {
        uldpConnectionSettings = new UldpConnectionSettings( "" );
        return new UldpForwarderSettings( uldpConnectionSettings );
    }

    @Override
    protected UldpForwarder createForwarder() {
        return new UldpForwarder( getSettings() );
    }

    /**
     * Read in the handler properties from the config file
     */
    @Override
    protected void configure() {
        super.configure();

        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();

        String hostProperty = cname + ".host";
        if ( manager.getProperty( hostProperty ) == null ) {
            throw new RuntimeException( "Cannot find logging property: " + hostProperty );
        }
        setHost( manager.getProperty( hostProperty ) );
        String portProperty = cname + ".port";
        if ( manager.getProperty( portProperty ) != null ) {
            setPort( Integer.parseInt( manager.getProperty( portProperty ) ) );
        }
        String propertyName = cname + ".useCompression";
        if ( manager.getProperty( propertyName ) != null ) {
            setUseCompression( Boolean.parseBoolean( manager.getProperty( propertyName ) ) );
        }
        propertyName = cname + ".useTls";
        if ( manager.getProperty( propertyName ) != null ) {
            setUseTls( Boolean.parseBoolean( manager.getProperty( propertyName ) ) );
        }
        propertyName = cname + ".useEncryption";
        if ( manager.getProperty( propertyName ) != null ) {
            setUseEncryption( Boolean.parseBoolean( manager.getProperty( propertyName ) ) );
        }
        propertyName = cname + ".keystorePath";
        if ( manager.getProperty( propertyName ) != null ) {
            setKeystorePath( manager.getProperty( propertyName ) );
        }
        propertyName = cname + ".keystorePassword";
        if ( manager.getProperty( propertyName ) != null ) {
            setKeystorePassword( manager.getProperty( propertyName ) );
        }
        propertyName = cname + ".tlsProtocolName";
        if ( manager.getProperty( propertyName ) != null ) {
            setTlsProtocolName( manager.getProperty( propertyName ) );
        }
        propertyName = cname + ".cipherName";
        if ( manager.getProperty( propertyName ) != null ) {
            setCipherSuite( manager.getProperty( propertyName ) );
        }
        propertyName = cname + ".noServerAuth";
        if ( manager.getProperty( propertyName ) != null ) {
            setNoServerAuth( Boolean.parseBoolean( manager.getProperty( propertyName ) ) );
        }
        propertyName = cname + ".ignoreHostnameValidation";
        if ( manager.getProperty( propertyName ) != null ) {
            setIgnoreHostnameValidaiton( Boolean.parseBoolean( manager.getProperty( propertyName ) ) );
        }
        propertyName = cname + ".acceptedCertificateFingerprints";
        if ( manager.getProperty( propertyName ) != null ) {
            setAcceptedCertificateFingerprints( manager.getProperty( propertyName ) );
        }
    }

    public void setHost( String host ) {
        uldpConnectionSettings.setDestination( host );
    }

    public void setPort( int port ) {
        uldpConnectionSettings.setPort( port );
    }

    public void setUseCompression( boolean useCompression ) {
        uldpConnectionSettings.setUseCompression( useCompression );
    }

    public void setUseTls( boolean useAuthentication ) {
        uldpConnectionSettings.setUseTls( useAuthentication );
    }

    public void setUseEncryption( boolean useEncryption ) {
        uldpConnectionSettings.setUseEncryption( useEncryption );
    }

    public void setNoServerAuth( boolean noServerAuth ) {
        uldpConnectionSettings.setNoServerAuthentication( noServerAuth );
    }

    public void setKeystorePath( String keystorePath ) {
        uldpConnectionSettings.setKeystorePath( keystorePath );
    }

    public void setKeystorePassword( String keystorePass ) {
        uldpConnectionSettings.setKeystorePassword( keystorePass );
    }

    public void setTlsProtocolName( String tlsProtocolName ) {
        uldpConnectionSettings.setTlsProtocolName( tlsProtocolName );
    }

    public void setCipherSuite( String cipherName ) {
        uldpConnectionSettings.setCipherSuite( cipherName );
    }

    public void setIgnoreHostnameValidaiton( boolean ignoreHostnameValidaiton ) {
        uldpConnectionSettings.setIgnoreHostnameValidation( ignoreHostnameValidaiton );
    }

    public void setAcceptedCertificateFingerprints( String acceptedCertificateFingerprints ) {
        String[] parts = acceptedCertificateFingerprints.split( "," );
        for ( String s : parts ) {
            uldpConnectionSettings.addAcceptedCertificateFingerprints( s );
        }
    }

}
