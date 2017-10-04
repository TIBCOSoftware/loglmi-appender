/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging.forwarders.uldp;

import com.tibco.loglogic.logging.forwarders.shared.LogForwarderSettings;
import com.tibco.loglogic.logging.uldpclient.UldpConnectionSettings;

/**
 * Holds the parameters for the ULDP connection
 *
 * @author Tibco LogLogic
 *
 */
public class UldpForwarderSettings
    extends LogForwarderSettings {

    private final UldpConnectionSettings uldpConnectionSettings;

    public UldpForwarderSettings( UldpConnectionSettings uldpConnectionSettings ) {
        this.uldpConnectionSettings = uldpConnectionSettings;
    }

    public UldpConnectionSettings getConnectionSettings() {
        return uldpConnectionSettings;
    }
}
