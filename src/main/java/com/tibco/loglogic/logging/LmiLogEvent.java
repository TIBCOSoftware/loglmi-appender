/*
 * Copyright © 2017. TIBCO Software Inc.
 * This file is subject to the license terms contained
 * in the license file that is distributed with this file.
 */
package com.tibco.loglogic.logging;

import java.net.InetAddress;

/**
 * <pre>
 * This class helps to produce log messages that follows LogLogic's best practices
 * It is based on the standard set of fields that LogLogic is providing.
 * It can be extended by using any other field name as needed
 * <br/>
 * You will most likely use this class to format a message to pass to your logging framework (log4j, java.util logging, logback etc..).
 *
 * <code>
 * Logger logger = LoggerFactory.getLogger(...);
 * LmiLogEvent event = new LmiLogEvent("Failed Login","sshd:failure");
 * event.setTargetUser("joe");
 * event.addKVP("myOwnFieldName", "some value");
 * logger.info(event.toString());
 * </code>
 *
 * The underlying log framework will also have a grammar for declaring a log message pattern.
 * Therefore you can either just log the LmiLogEvent string as is, or augment it with other log pattern pattern variables when configuring your logger appenders/handlers.
 *
 * </pre>
 *
 * @author TIBCO LogLogic
 *
 */
public class LmiLogEvent {

    private StringBuffer eventMessage;

    private static final String KVDELIM = "=";

    private static final String PAIRDELIM = " ";

    private static final char QUOTE = '"';

    /**
     * Fields for exceptions
     */
    private static final String THROWABLE_CLASS = "exceptionClass";

    private static final String THROWABLE_MESSAGE = "exceptionMessage";

    private static final String THROWABLE_STACKTRACE_ELEMENTS = "stackTraceElement";

    private static final String EXCEPTION_STACK_SEPARATOR = "," ;


    /**
     * Constructor.
     *
     * @param eventName the event name
     * @param eventID the event id
     */
    public LmiLogEvent( String eventName, String eventID ) {

        this.eventMessage = new StringBuffer();

        addKVP( EVENT_NAME, eventName );
        addKVP( EVENT_ID, eventID );
    }

    /**
     * Default constructor
     */
    public LmiLogEvent() {

        this.eventMessage = new StringBuffer();
    }

    /**
     * Add a key value pair for char value
     *
     * @param key
     * @param value
     */
    public void addKVP( String key, char value ) {
        addKVP( key, String.valueOf( value ) );
    }

    /**
     * Add a key value pair for boolean value
     *
     * @param key
     * @param value
     */
    public void addKVP( String key, boolean value ) {
        addKVP( key, String.valueOf( value ) );
    }

    /**
     * Add a key value pair for double value
     *
     * @param key
     * @param value
     */
    public void addKVP( String key, double value ) {
        addKVP( key, String.valueOf( value ) );
    }

    /**
     * Add a key value pair for long value
     *
     * @param key
     * @param value
     */
    public void addKVP( String key, long value ) {
        addKVP( key, String.valueOf( value ) );
    }

    /**
     * Add a key value pair for int value
     *
     * @param key
     * @param value
     */
    public void addKVP( String key, int value ) {
        addKVP( key, String.valueOf( value ) );
    }

    /**
     * Add a key value pair for InetAddress value
     *
     * @param key
     * @param value
     */
    public void addKVP( String key, InetAddress value ) {
        addKVP( key, value.getHostAddress() );
    }

    /**
     * Add a key value pair for generic object
     *
     * @param key
     * @param value
     */
    public void addKVP( String key, Object value ) {
        addKVP( key, value.toString() );
    }

    /**
     * Utility method for formatting Throwable,Error,Exception objects in a more linear and Splunk friendly manner than
     * printStackTrace
     *
     * @param throwable the Throwable object to add to the event
     */
    public void addThrowable( Throwable throwable ) {
        addThrowableObject( throwable, -1 );
    }

    /**
     * Utility method for formatting Throwable,Error,Exception objects in a more linear and Splunk friendly manner than
     * printStackTrace
     *
     * @param throwable the Throwable object to add to the event
     * @param stackTraceDepth maximum number of stacktrace elements to log
     */
    public void addThrowable( Throwable throwable, int stackTraceDepth ) {
        addThrowableObject( throwable, stackTraceDepth );
    }

    private void addThrowableObject( Throwable throwable, int stackTraceDepth ) {
        addKVP( THROWABLE_CLASS, throwable.getClass().getCanonicalName() );
        addKVP( THROWABLE_MESSAGE, throwable.getMessage() );
        StackTraceElement[] elements = throwable.getStackTrace();
        StringBuffer sb = new StringBuffer();
        int depth = 0;
        for ( StackTraceElement element : elements ) {
            depth++;
            if ( stackTraceDepth == -1 || stackTraceDepth >= depth )
                sb.append( element.toString() ).append( EXCEPTION_STACK_SEPARATOR );
            else
                break;
        }
        addKVP( THROWABLE_STACKTRACE_ELEMENTS, sb.toString() );
    }

    /**
     * Add a key value pair
     *
     * @param key
     * @param value
     */
    public void addKVP( String key, String value ) {
        if ( eventMessage.length() != 0 )
                        eventMessage.append( PAIRDELIM );
        
        value = value.replace("\\", "\\\\");
        value = value.replace( QUOTE + "", "\\" + QUOTE );
        eventMessage.append( key ).append( KVDELIM ).append( QUOTE ).append( value ).append( QUOTE );
    }

    @Override
    /**
     * return the completed event message
     */
    public String toString() {
        return eventMessage.toString();
    }

    /**
     * application involved in the event
     */
    public static String APPLICATION = "ll_application";

    /**
     * 
     */
    public static String BYTES_RECEIVED = "ll_bytesReceived";

    /**
     * 
     */
    public static String BYTES_SENT = "ll_bytesSent";

    /**
     * general code field.(error code/return code)
     */
    public static String CODE = "ll_code";

    /**
     * TIBCO Business Event Customer identifier
     */
    public static String CUSTOMER_ID = "ll_customerID";

    /**
     * General details field (the catch all defaut container)
     */
    public static String DETAILS = "ll_details";

    /**
     * Device Category for SEM use(UTM/Firewall/ IPS/IDS)
     */
    public static String DEVICE_CATEGORY = "ll_deviceCategory";

    /**
     * 
     */
    public static String ERROR = "ll_error";

    /**
     * General Category field as it relates to the events from expert system point of view and provided by expert system
     */
    public static String EVENT_CATEGORY = "ll_eventCategory";

    /**
     * time event completed
     */
    public static String EVENT_END_TIME = "ll_eventEndTime";

    /**
     * 
     */
    public static String EVENT_ID = "ll_eventID";

    /**
     * human readable version of EventID or in place of
     */
    public static String EVENT_NAME = "ll_eventName";

    /**
     *  Event priority (either numeric or string value)
     */
    public static String EVENT_PRIORITY = "ll_eventPriority";

    /**
     * sensor name that generated the event
     */
    public static String EVENT_ORIGINATOR = "ll_eventOriginator";

    /**
     * time event started or occured
     */
    public static String EVENT_START_TIME = "ll_eventStartTime";

    /**
     * General Group/userGroup/securityGroup of entity impacted by the action described by the event
     */
    public static String GROUP = "ll_group";

    /**
     * manufacturer of device/source (Microsoft/Oracle/etc)
     */
    public static String MANUFACTURER = "ll_manufacturer";

    /**
     * unqie ID from mail
     */
    public static String MESSAGE_ID = "ll_messageID";

    /**
     * NONE!
     */
    public static String MESSAGE_TYPE = "ll_messageType";

    /**
     * Name of the object in the event.
     */
    public static String OBJECT_NAME = "ll_objectName";

    /**
     * The Value representing the type of the object involved in the event
     */
    public static String OBJECT_TYPE = "ll_objectType";

    /**
     * 
     */
    public static String PERMISSIONS = "ll_permissions";

    /**
     * policy is a made up of a set of permissions or rules (to level container)
     */
    public static String POLICY = "ll_policy";

    /**
     * General Privilege Used
     */
    public static String PRIVILEGE_USED = "ll_privilegeUsed";

    /**
     * ip protocal
     */
    public static String PROTOCOL_NAME = "ll_protocolName";

    /**
     * ip protocal
     */
    public static String PROTOCOL_NUMBER = "ll_protocolNumber";

    /**
     * name or ID of Realm (security)
     */
    public static String _REALM = "ll_Realm";

    /**
     * reason of an error or action
     */
    public static String REASON = "ll_reason";

    /**
     * 
     */
    public static String RECEIVED_PACKET = "ll_receivedPacket";

    /**
     * name or ID of role (security)
     */
    public static String ROLE = "ll_role";

    /**
     * name or ID of rule
     */
    public static String RULE = "ll_rule";

    /**
     * IPS/IDS/AV/HIPS sensor
     */
    public static String SENSOR_IP = "ll_sensorIP";

    /**
     * IPS/IDS/AV/HIPS sensor
     */
    public static String SENSOR_NAME = "ll_sensorName";

    /**
     * 
     */
    public static String SENT_PACKET = "ll_sentPacket";

    /**
     * service or deamon Name
     */
    public static String SERVICE = "ll_service";

    /**
     * event severity
     */
    public static String SEVERITY = "ll_severity";

    /**
     * domain of the source system (from where)
     */
    public static String SOURCE_DOMAIN = "ll_sourceDomain";

    /**
     * (from where)
     */
    public static String SOURCE_GID = "ll_sourceGID";

    /**
     * hostname or dns of the source system (from where)
     */
    public static String SOURCE_HOST = "ll_sourceHost";

    /**
     * IPv4 or IPv6 address of the source system (from where)
     */
    public static String SOURCE_IP = "ll_sourceIP";

    /**
     * IPS/IDS/AV/HIPS
     */
    public static String SOURCE_LOCATION = "ll_sourceLocation";

    /**
     * of the source system (from where)
     */
    public static String SOURCE_MAC = "ll_sourceMAC";

    /**
     * Port Number (from where)
     */
    public static String SOURCE_PORT = "ll_sourcePort";

    /**
     * (from where)
     */
    public static String SOURCE_PORT_NAME = "ll_sourcePortName";

    /**
     * (from where) the process/thread/actor processing the event
     */
    public static String SOURCE_PROCESS_NAME = "ll_sourceProcessName";

    /**
     * (from where)
     */
    public static String SOURCE_PROGRAM = "ll_sourceProgram";

    /**
     * (from where)
     */
    public static String SOURCE_UID = "ll_sourceUID";

    /**
     * (from where)
     */
    public static String SOURCE_USER = "ll_sourceUser";

    /**
     * domain of the target system (to where)
     */
    public static String TARGET_DOMAIN = "ll_targetDomain";

    /**
     *  (to where)
     */
    public static String TARGET_GID = "ll_targetGID";

    /**
     * hostname or dns of the target system (to where)
     */
    public static String TARGET_HOST = "ll_targetHost";

    /**
     * IPv4 or IPv6 address of the target system (to where)
     */
    public static String TARGET_IP = "ll_targetIP";

    /**
     * IPS/IDS/AV/HIPS
     */
    public static String TARGET_LOCATION = "ll_targetLocation";

    /**
     * of the target system (to where)
     */
    public static String TARGET_MAC = "ll_targetMAC";

    /**
     * Port Number (to where)
     */
    public static String TARGET_PORT = "ll_targetPort";

    /**
     * (to where)
     */
    public static String TARGET_PORT_NAME = "ll_targetPortName";

    /**
     * (to where)
     */
    public static String TARGET_PROCESS_NAME = "ll_targetProcessName";

    /**
     * (to where)
     */
    public static String TARGET_PROGRAM = "ll_targetProgram";

    /**
     * (to where)
     */
    public static String TARGET_UID = "ll_targetUID";

    /**
     * (to where)
     */
    public static String TARGET_USER = "ll_targetUser";

    /**
     * General Type can be Event/Configuration/user activity
     */
    public static String TYPE = "ll_type";

    /**
     * server name (db)
     */
    public static String SYSTEM_NAME = "ll_systemName";

    /**
     * db
     */
    public static String TARGET_LOGIN_USER = "ll_targetLoginUser";

    /**
     * RACF
     */
    public static String ACCESS_RULE_KEY = "ll_accessRuleKey";

    /**
     * FLOW
     */
    public static String ACTIVE_TIMEOUT = "ll_activeTimeout";

    /**
     * FW/IPS/IDS
     */
    public static String ATTACK_IP = "ll_attackIP";

    /**
     * i5OS
     */
    public static String ATTRIBUTE_DESCRIPTION = "ll_attributeDescription";

    /**
     * i5OS
     */
    public static String ATTRIBUTE_NAME = "ll_attributeName";

    /**
     * NonStop
     */
    public static String AUDIT_NUMBER = "ll_auditNumber";

    /**
     * NONE!
     */
    public static String AUTH_PACKAGE = "ll_authPackage";

    /**
     * BotNet Value
     */
    public static String BOT = "ll_bot";

    /**
     * i5OS
     */
    public static String CHANGE_PASSWORD = "ll_changePassword";

    /**
     * i5OS/IPS/IDS
     */
    public static String CLASSIFICATION = "ll_classification";

    /**
     * w3c
     */
    public static String CLIENT_OS = "ll_clientOS";

    /**
     * i5OS
     */
    public static String CMD = "ll_cmd";

    /**
     * w3c
     */
    public static String CONTENT_DURATION = "ll_contentDuration";

    /**
     * Dashboard stats
     */
    public static String COUNT = "ll_count";

    /**
     * NonStop
     */
    public static String CREATOR_USER_NAME = "ll_creatorUserName";

    /**
     * db
     */
    public static String DATABASE_ID = "ll_databaseID";

    /**
     * db
     */
    public static String DATABASE_NAME = "ll_databaseName";

    /**
     * db
     */
    public static String DATABASE_USER = "ll_databaseUser";

    /**
     * Hypervisor
     */
    public static String DATA_CENTER = "ll_dataCenter";

    /**
     * mail
     */
    public static String DELAY = "ll_delay";

    /**
     * w3c
     */
    public static String DEVICE_ACTION = "ll_deviceAction";

    /**
     * 
     */
    public static String DIRECTION = "ll_direction";

    /**
     * NONE!
     */
    public static String DISCONECT_REASON = "ll_disconectReason";

    /**
     * NONE!
     */
    public static String DISCONNECT_DETAILS = "ll_disconnectDetails";

    /**
     * i5OS
     */
    public static String DLO_USER = "ll_dloUser";

    /**
     * 
     */
    public static String DURATION = "ll_duration";

    /**
     * i5OS
     */
    public static String ENTRY_DESCRIPTION = "ll_entryDescription";

    /**
     * i5OS
     */
    public static String ENTRY_TYPE = "ll_entryType";

    /**
     * HIPS
     */
    public static String EVENT_RESPONSE = "ll_eventResponse";

    /**
     * HIPS
     */
    public static String EVENT_RESPONSE_STATUS = "ll_eventResponseStatus";

    /**
     * ECM
     */
    public static String EVENT_SOURCE = "ll_eventSource";

    /**
     * MSSQL
     */
    public static String EVENT_SUB_CLASS = "ll_eventSubClass";

    /**
     * Flow
     */
    public static String FIRST_SWITCH_TIME = "ll_firstSwitchTime";

    /**
     * Flow
     */
    public static String FLAG_COUNT = "ll_flagCount";

    /**
     * Flow
     */
    public static String FLOW_COUNT = "ll_flowCount";

    /**
     * i5OS
     */
    public static String FOLDER = "ll_folder";

    /**
     * web
     */
    public static String HTTP_STATUS_CODE = "ll_httpStatusCode";

    /**
     * w3c
     */
    public static String IM_USER = "ll_imUser";

    /**
     * Flow
     */
    public static String INACTIVE_TIMEOUT = "ll_inactiveTimeout";

    /**
     * Flow
     */
    public static String INTERFACE_DESCRIPTION = "ll_interfaceDescription";

    /**
     * Flow
     */
    public static String INTERFACE_NAME = "ll_interfaceName";

    /**
     * Tibco EMS JMS
     */
    public static String JC_ID = "ll_jcID";

    /**
     * Tibco EMS JMS
     */
    public static String JDEST = "ll_jdest";

    /**
     * Tibco EMS JMS
     */
    public static String JD_M = "ll_jdM";

    /**
     * Tibco EMS JMS
     */
    public static String JEXP = "ll_jexp";

    /**
     * Tibco EMS JMS
     */
    public static String JM_ID = "ll_jmID";

    /**
     * i5OS
     */
    public static String JOB = "ll_job";

    /**
     * RACF
     */
    public static String JOB_ID = "ll_jobID";

    /**
     * i5OS
     */
    public static String JOB_NAME = "ll_jobName";

    /**
     * i5OS
     */
    public static String JOB_NUMBER = "ll_jobNumber";

    /**
     * i5OS
     */
    public static String JOB_USER = "ll_jobUser";

    /**
     * i5OS
     */
    public static String JOURNAL_DESCRIPTION = "ll_journalDescription";

    /**
     * i5OS
     */
    public static String JOURNAL_LIBRARY = "ll_journalLibrary";

    /**
     * i5OS
     */
    public static String JOURNAL_NUMBER = "ll_journalNumber";

    /**
     * i5OS
     */
    public static String JOURNAL_PROGRAM = "ll_journalProgram";

    /**
     * i5OS
     */
    public static String JOURNAL_SYS_NAME = "ll_journalSysName";

    /**
     * i5OS
     */
    public static String JOURNAL_TYPE = "ll_journalType";

    /**
     * i5OS
     */
    public static String JOURNAL_USER = "ll_journalUser";

    /**
     * Tibco EMS JMS
     */
    public static String JPRI = "ll_jpri";

    /**
     * Tibco EMS JMS
     */
    public static String JRE_DEL = "ll_jreDel";

    /**
     * Tibco EMS JMS
     */
    public static String JR_TO = "ll_jrTO";

    /**
     * Tibco EMS JMS
     */
    public static String JT_CMP = "ll_jtCMP";

    /**
     * Tibco EMS JMS
     */
    public static String JT_CMS = "ll_jtCMS";

    /**
     * Tibco EMS JMS
     */
    public static String JT_COMP = "ll_jtComp";

    /**
     * Tibco EMS JMS
     */
    public static String JT_DS = "ll_jtDS";

    /**
     * Tibco EMS JMS
     */
    public static String JT_IMP = "ll_jtImp";

    /**
     * Tibco EMS JMS
     */
    public static String JT_ME = "ll_jtME";

    /**
     * Tibco EMS JMS
     */
    public static String JT_MT = "ll_jtMT";

    /**
     * Tibco EMS JMS
     */
    public static String JT_PU = "ll_jtPU";

    /**
     * Tibco EMS JMS
     */
    public static String JT_S = "ll_jtS";

    /**
     * Tibco EMS JMS
     */
    public static String JT_SEND = "ll_jtSend";

    /**
     * Tibco EMS JMS
     */
    public static String JT_SSS = "ll_jtSSS";

    /**
     * Tibco EMS JMS
     */
    public static String JTYPE = "ll_jtype";

    /**
     * Flow
     */
    public static String LAST_SWITCH_TIME = "ll_lastSwitchTime";

    /**
     * db
     */
    public static String LINKED_SERVER_NAME = "ll_linkedServerName";

    /**
     * Physical location of logsource
     */
    public static String LOCATION = "ll_location";

    /**
     * Flow
     */
    public static String MAX_PACKET_LENGTH = "ll_maxPacketLength";

    /**
     * mail
     */
    public static String MAX_SIZE = "ll_maxSize";

    /**
     * w3c
     */
    public static String METHOD = "ll_method";

    /**
     * Flow
     */
    public static String MIN_PACKET_LENGTH = "ll_minPacketLength";

    /**
     * Flow
     */
    public static String MULTICAST_BYTES = "ll_multicastBytes";

    /**
     * Flow
     */
    public static String MULTICAST_PACKETS = "ll_multicastPackets";

    /**
     * (from where)
     */
    public static String NAT_SOURCE_IP = "ll_natSourceIP";

    /**
     * (to where)
     */
    public static String NAT_SOURCE_PORT = "ll_natSourcePort";

    /**
     * (from where)
     */
    public static String NAT_TARGET_IP = "ll_natTargetIP";

    /**
     * (to where)
     */
    public static String NAT_TARGET_PORT = "ll_natTargetPort";

    /**
     * Type of NAT
     */
    public static String NAT_TYPE = "ll_natType";

    /**
     * distributed system node name
     */
    public static String NODE = "ll_node";

    /**
     * used for Mail
     */
    public static String NUMBER_OF_RECIPIENTS = "ll_numberOfRecipients";

    /**
     * i5OS
     */
    public static String OBJECT_LIBRARY = "ll_objectLibrary";

    /**
     * ECM
     */
    public static String OBJECT_LOCATION = "ll_objectLocation";

    /**
     * db
     */
    public static String OBJECT_PRIVILEGE = "ll_objectPrivilege";

    /**
     * db
     */
    public static String OBJECT_SCHEMA = "ll_objectSchema";

    /**
     * vCloud
     */
    public static String ORGANIZATION = "ll_organization";

    /**
     * vCloud
     */
    public static String ORGANIZATION_ID = "ll_organizationID";

    /**
     * 
     */
    public static String ORG_EVENT_ACTION = "ll_orgEventAction";

    /**
     * 
     */
    public static String ORG_EVENT_STATUS = "ll_orgEventStatus";

    /**
     * db
     */
    public static String OS_PRIVILEGE = "ll_osPrivilege";

    /**
     * NONE!
     */
    public static String OS_TYPE = "ll_osType";

    /**
     * db
     */
    public static String OWNER = "ll_owner";

    /**
     * 
     */
    public static String PACKET_COUNT = "ll_packetCount";

    /**
     * ECM
     */
    public static String PARENT_NAME = "ll_parentName";

    /**
     * w3c
     */
    public static String PEER_HOST = "ll_peerHost";

    /**
     * w3c
     */
    public static String PEER_IP = "ll_peerIP";

    /**
     * hardware related info
     */
    public static String PLATFORM = "ll_platform";

    /**
     * db
     */
    public static String PROVIDER_NAME = "ll_providerName";

    /**
     * RACF
     */
    public static String REASON_CODE = "ll_reasonCode";

    /**
     * RACF
     */
    public static String REASON_CODE_DESC = "ll_reasonCodeDesc";

    /**
     * mail
     */
    public static String RECIPIENT_ADDRESS = "ll_recipientAddress";

    /**
     * mail
     */
    public static String RECIPIENT_STATUS = "ll_recipientStatus";

    /**
     * The type of request
     */
    public static String RECORD_TYPE = "ll_recordType";

    /**
     * RACF
     */
    public static String RECORD_TYPE_DESC = "ll_recordTypeDesc";

    /**
     * web
     */
    public static String REFERER_BY = "ll_refererBy";

    /**
     * w3c
     */
    public static String REFERER = "ll_referer";

    /**
     * mail
     */
    public static String RELATED_RECIPIENT_ADDRESS = "ll_relatedRecipientAddress";

    /**
     * Reputation Value
     */
    public static String REPUTATION = "ll_reputation";

    /**
     * RACF
     */
    public static String REQUESTING_COMPONENT = "ll_requestingComponent";

    /**
     * The type of request. Also used for Veracity values in NonStop
     */
    public static String REQUEST_TYPE = "ll_requestType";

    /**
     * RACF
     */
    public static String RESOURCE_SECURITY_LABEL = "ll_resourceSecurityLabel";

    /**
     * RACF
     */
    public static String RETURN_CODE_DESC = "ll_returnCodeDesc";

    /**
     * mail
     */
    public static String RETURN_PATH = "ll_returnPath";

    /**
     * Risk Rating Value
     */
    public static String RISK_RATING = "ll_riskRating";

    /**
     * Flow
     */
    public static String SAMPLING_ALGORITHM = "ll_samplingAlgorithm";

    /**
     * Flow
     */
    public static String SAMPLING_INTERVAL = "ll_samplingInterval";

    /**
     * mail
     */
    public static String SENDER_ADDRESS = "ll_senderAddress";

    /**
     * DB (oracle/mssql/sybase)
     */
    public static String SERVER_TYPE = "ll_serverType";

    /**
     * w3c/vpn
     */
    public static String SESSION = "ll_session";

    /**
     * ips/ids/hips
     */
    public static String SIGNATURE = "ll_signature";

    /**
     * ips/ids/hips
     */
    public static String SIGNATURE_ID = "ll_signatureID";

    /**
     * NONE!
     */
    public static String SIGNATURE_VERSION = "ll_signatureVersion";

    /**
     * mail
     */
    public static String SIZE = "ll_size";

    /**
     * fw/ips/ids/vpn (from where)
     */
    public static String SOURCE_INTERFACE = "ll_sourceInterface";

    /**
     * distributed system node name
     */
    public static String SOURCE_NODE = "ll_sourceNode";

    /**
     * name of the object in the event
     */
    public static String SOURCE_OBJECT_NAME = "ll_sourceObjectName";

    /**
     * the value representing the type of the object involved in the event. may or may not be from the event
     */
    public static String SOURCE_OBJECT_TYPE = "ll_sourceObjectType";

    /**
     * 
     */
    public static String SOURCE_TENANT = "ll_sourceTenant";

    /**
     * fw/router/switch (from where)
     */
    public static String SOURCE_VLAN = "ll_sourceVlan";

    /**
     * NONE!
     */
    public static String SOURCE_VOLUME_NAME = "ll_sourceVolumeName";

    /**
     * fw/router/switch (from where)
     */
    public static String SOURCE_ZONE = "ll_sourceZone";

    /**
     * db
     */
    public static String SQL_TEXT = "ll_sqlText";

    /**
     * Flow
     */
    public static String SRC_AUTONOMOUS_SYS = "ll_srcAutonomousSys";

    /**
     * NONE!
     */
    public static String STATE = "ll_state";

    /**
     * mail
     */
    public static String SUBJECT = "ll_subject";

    /**
     * RACF
     */
    public static String SUBMITTOR_LOGON_ID = "ll_submittorLogonID";

    /**
     * Additional sub field for ll_type
     */
    public static String SUB_TYPE = "ll_subType";

    /**
     * RACF
     */
    public static String SYSTEM_ID = "ll_systemID";

    /**
     * db
     */
    public static String SYSTEM_PRIVILEGE = "ll_systemPrivilege";

    /**
     * Flow
     */
    public static String SYSTEM_UP_TIME = "ll_systemUpTime";

    /**
     * db
     */
    public static String SYSTEM_USER = "ll_systemUser";

    /**
     * fw/ips/ids/vpn (to where)
     */
    public static String TARGET_INTERFACE = "ll_targetInterface";

    /**
     * distributed system node name
     */
    public static String TARGET_NODE = "ll_targetNode";

    /**
     * name of the object in the event
     */
    public static String TARGET_OBJECT_NAME = "ll_targetObjectName";

    /**
     * the value representing the type of the object involved in the event. may or may not be from the event
     */
    public static String TARGET_OBJECT_TYPE = "ll_targetObjectType";

    /**
     * 
     */
    public static String TARGET_TENANT = "ll_targetTenant";

    /**
     * fw/router/switch (to where)
     */
    public static String TARGET_VLAN = "ll_targetVlan";

    /**
     * RACF
     */
    public static String TARGET_VOLUME_NAME = "ll_targetVolumeName";

    /**
     * fw/router/switch (to where)
     */
    public static String TARGET_ZONE = "ll_targetZone";

    /**
     * Flow
     */
    public static String TARGET_AUTONOMOUS_SYS = "ll_targetAutonomousSys";

    /**
     * RACF
     */
    public static String TERMINAL_NAME = "ll_terminalName";

    /**
     * embedded messages within an event - Tibco EMS JMS
     */
    public static String TEXT_BODY = "ll_textBody";

    /**
     * fw/ips/ids/hips/av
     */
    public static String THREAT_NAME = "ll_threatName";

    /**
     * Threat Rating Value (no duplicate of riskrating)
     */
    public static String THREAT_RATING = "ll_threatRating";

    /**
     * fw/ips/ids/hips/av
     */
    public static String THREAT_TYPE = "ll_threatType";

    /**
     * Flow
     */
    public static String TOTAL_EXPECTED_BYTES = "ll_totalExpectedBytes";

    /**
     * Flow
     */
    public static String TOTAL_EXPECTED_FLOWS = "ll_totalExpectedFlows";

    /**
     * Flow
     */
    public static String TOTAL_EXPECTED_PACKETS = "ll_totalExpectedPackets";

    /**
     * mail
     */
    public static String TOTAL_SIZE = "ll_totalSize";

    /**
     * db
     */
    public static String TRANSACTION_ID = "ll_transactionID";

    /**
     * f5
     */
    public static String TRANSLATED_IP = "ll_translatedIP";

    /**
     * f5
     */
    public static String TRANSLATED_PORT = "ll_translatedPort";

    /**
     * w3c
     */
    public static String URI_PATH = "ll_uriPath";

    /**
     * w3c
     */
    public static String URI_QUERY = "ll_uriQuery";

    /**
     * w3c
     */
    public static String URL = "ll_url";

    /**
     * w3c
     */
    public static String USER_AGENT = "ll_userAgent";

    /**
     * RACF
     */
    public static String USER_SECURITY_LABEL = "ll_userSecurityLabel";

    /**
     * w3c
     */
    public static String VERSION = "ll_version";

    /**
     * RACF
     */
    public static String VIOLATION_OCCURRED = "ll_violationOccurred";

    /**
     * hypervisor
     */
    public static String VM_NAME = "ll_vmName";

    /**
     * hypervisor
     */
    public static String VIRTUAL_MACHINE = "ll_virtualMachine";

    /**
     * FW/VPN/Router
     */
    public static String VP_N = "ll_vpN";

    /**
     * mail
     */
    public static String XMIT_DELAY = "ll_xmitDelay";

    public void setApplication( String application ) {
        addKVP( APPLICATION, application );
    }

    public void setBytesReceived( int bytesReceived ) {
        addKVP( BYTES_RECEIVED, bytesReceived );
    }

    public void setBytesSent( int bytesSent ) {
        addKVP( BYTES_SENT, bytesSent );
    }

    public void setCode( String code ) {
        addKVP( CODE, code );
    }

    public void setCustomerID( String customerID ) {
        addKVP( CUSTOMER_ID, customerID );
    }

    public void setDetails( String details ) {
        addKVP( DETAILS, details );
    }

    public void setDeviceCategory( String deviceCategory ) {
        addKVP( DEVICE_CATEGORY, deviceCategory );
    }

    public void setError( String error ) {
        addKVP( ERROR, error );
    }

    public void setEventCategory( String eventCategory ) {
        addKVP( EVENT_CATEGORY, eventCategory );
    }

    public void setEventEndTime( long eventEndTime ) {
        addKVP( EVENT_END_TIME, eventEndTime );
    }

    public void setEventID( String eventID ) {
        addKVP( EVENT_ID, eventID );
    }

    public void setEventName( String eventName ) {
        addKVP( EVENT_NAME, eventName );
    }

    public void setEventPriority( String eventPriority ) {
        addKVP( EVENT_PRIORITY, eventPriority );
    }

    public void setEventOriginator( String eventOriginator ) {
        addKVP( EVENT_ORIGINATOR, eventOriginator );
    }

    public void setEventStartTime( long eventStartTime ) {
        addKVP( EVENT_START_TIME, eventStartTime );
    }

    public void setGroup( String group ) {
        addKVP( GROUP, group );
    }

    public void setManufacturer( String manufacturer ) {
        addKVP( MANUFACTURER, manufacturer );
    }

    public void setMessageID( String messageID ) {
        addKVP( MESSAGE_ID, messageID );
    }

    public void setMessageType( String messageType ) {
        addKVP( MESSAGE_TYPE, messageType );
    }

    public void setObjectName( String objectName ) {
        addKVP( OBJECT_NAME, objectName );
    }

    public void setObjectType( String objectType ) {
        addKVP( OBJECT_TYPE, objectType );
    }

    public void setPermissions( String permissions ) {
        addKVP( PERMISSIONS, permissions );
    }

    public void setPolicy( String policy ) {
        addKVP( POLICY, policy );
    }

    public void setPrivilegeUsed( String privilegeUsed ) {
        addKVP( PRIVILEGE_USED, privilegeUsed );
    }

    public void setProtocolName( String protocolName ) {
        addKVP( PROTOCOL_NAME, protocolName );
    }

    public void setProtocolNumber( String protocolNumber ) {
        addKVP( PROTOCOL_NUMBER, protocolNumber );
    }

    public void setRealm( String Realm ) {
        addKVP( _REALM, Realm );
    }

    public void setReason( String reason ) {
        addKVP( REASON, reason );
    }

    public void setReceivedPacket( int receivedPacket ) {
        addKVP( RECEIVED_PACKET, receivedPacket );
    }

    public void setRole( String role ) {
        addKVP( ROLE, role );
    }

    public void setRule( String rule ) {
        addKVP( RULE, rule );
    }

    public void setSensorIP( InetAddress sensorIP ) {
        addKVP( SENSOR_IP, sensorIP );
    }

    public void setSensorName( String sensorName ) {
        addKVP( SENSOR_NAME, sensorName );
    }

    public void setSentPacket( int sentPacket ) {
        addKVP( SENT_PACKET, sentPacket );
    }

    public void setService( String service ) {
        addKVP( SERVICE, service );
    }

    public void setSeverity( String severity ) {
        addKVP( SEVERITY, severity );
    }

    public void setSourceDomain( String sourceDomain ) {
        addKVP( SOURCE_DOMAIN, sourceDomain );
    }

    public void setSourceGID( String sourceGID ) {
        addKVP( SOURCE_GID, sourceGID );
    }

    public void setSourceHost( String sourceHost ) {
        addKVP( SOURCE_HOST, sourceHost );
    }

    public void setSourceIP( InetAddress sourceIP ) {
        addKVP( SOURCE_IP, sourceIP );
    }

    public void setSourceLocation( String sourceLocation ) {
        addKVP( SOURCE_LOCATION, sourceLocation );
    }

    public void setSourceMAC( String sourceMAC ) {
        addKVP( SOURCE_MAC, sourceMAC );
    }

    public void setSourcePort( String sourcePort ) {
        addKVP( SOURCE_PORT, sourcePort );
    }

    public void setSourcePortName( String sourcePortName ) {
        addKVP( SOURCE_PORT_NAME, sourcePortName );
    }

    public void setSourceProcessName( String sourceProcessName ) {
        addKVP( SOURCE_PROCESS_NAME, sourceProcessName );
    }

    public void setSourceProgram( String sourceProgram ) {
        addKVP( SOURCE_PROGRAM, sourceProgram );
    }

    public void setSourceUID( String sourceUID ) {
        addKVP( SOURCE_UID, sourceUID );
    }

    public void setSourceUser( String sourceUser ) {
        addKVP( SOURCE_USER, sourceUser );
    }

    public void setTargetDomain( String targetDomain ) {
        addKVP( TARGET_DOMAIN, targetDomain );
    }

    public void setTargetGID( String targetGID ) {
        addKVP( TARGET_GID, targetGID );
    }

    public void setTargetHost( String targetHost ) {
        addKVP( TARGET_HOST, targetHost );
    }

    public void setTargetIP( InetAddress targetIP ) {
        addKVP( TARGET_IP, targetIP );
    }

    public void setTargetLocation( String targetLocation ) {
        addKVP( TARGET_LOCATION, targetLocation );
    }

    public void setTargetMAC( String targetMAC ) {
        addKVP( TARGET_MAC, targetMAC );
    }

    public void setTargetPort( String targetPort ) {
        addKVP( TARGET_PORT, targetPort );
    }

    public void setTargetPortName( String targetPortName ) {
        addKVP( TARGET_PORT_NAME, targetPortName );
    }

    public void setTargetProcessName( String targetProcessName ) {
        addKVP( TARGET_PROCESS_NAME, targetProcessName );
    }

    public void setTargetProgram( String targetProgram ) {
        addKVP( TARGET_PROGRAM, targetProgram );
    }

    public void setTargetUID( String targetUID ) {
        addKVP( TARGET_UID, targetUID );
    }

    public void setTargetUser( String targetUser ) {
        addKVP( TARGET_USER, targetUser );
    }

    public void setType( String type ) {
        addKVP( TYPE, type );
    }

    public void setSystemName( String systemName ) {
        addKVP( SYSTEM_NAME, systemName );
    }

    public void setTargetLoginUser( String targetLoginUser ) {
        addKVP( TARGET_LOGIN_USER, targetLoginUser );
    }

    public void setAccessRuleKey( String accessRuleKey ) {
        addKVP( ACCESS_RULE_KEY, accessRuleKey );
    }

    public void setActiveTimeout( String activeTimeout ) {
        addKVP( ACTIVE_TIMEOUT, activeTimeout );
    }

    public void setAttackIP( InetAddress attackIP ) {
        addKVP( ATTACK_IP, attackIP );
    }

    public void setAttributeDescription( String attributeDescription ) {
        addKVP( ATTRIBUTE_DESCRIPTION, attributeDescription );
    }

    public void setAttributeName( String attributeName ) {
        addKVP( ATTRIBUTE_NAME, attributeName );
    }

    public void setAuditNumber( String auditNumber ) {
        addKVP( AUDIT_NUMBER, auditNumber );
    }

    public void setAuthPackage( String authPackage ) {
        addKVP( AUTH_PACKAGE, authPackage );
    }

    public void setBot( String bot ) {
        addKVP( BOT, bot );
    }

    public void setChangePassword( String changePassword ) {
        addKVP( CHANGE_PASSWORD, changePassword );
    }

    public void setClassification( String classification ) {
        addKVP( CLASSIFICATION, classification );
    }

    public void setClientOS( String clientOS ) {
        addKVP( CLIENT_OS, clientOS );
    }

    public void setCmd( String cmd ) {
        addKVP( CMD, cmd );
    }

    public void setContentDuration( String contentDuration ) {
        addKVP( CONTENT_DURATION, contentDuration );
    }

    public void setCount( int count ) {
        addKVP( COUNT, count );
    }

    public void setCreatorUserName( String creatorUserName ) {
        addKVP( CREATOR_USER_NAME, creatorUserName );
    }

    public void setDatabaseID( String databaseID ) {
        addKVP( DATABASE_ID, databaseID );
    }

    public void setDatabaseName( String databaseName ) {
        addKVP( DATABASE_NAME, databaseName );
    }

    public void setDatabaseUser( String databaseUser ) {
        addKVP( DATABASE_USER, databaseUser );
    }

    public void setDataCenter( String dataCenter ) {
        addKVP( DATA_CENTER, dataCenter );
    }

    public void setDelay( int delay ) {
        addKVP( DELAY, delay );
    }

    public void setDeviceAction( String deviceAction ) {
        addKVP( DEVICE_ACTION, deviceAction );
    }

    public void setDirection( String direction ) {
        addKVP( DIRECTION, direction );
    }

    public void setDisconectReason( String disconectReason ) {
        addKVP( DISCONECT_REASON, disconectReason );
    }

    public void setDisconnectDetails( String disconnectDetails ) {
        addKVP( DISCONNECT_DETAILS, disconnectDetails );
    }

    public void setDloUser( String dloUser ) {
        addKVP( DLO_USER, dloUser );
    }

    public void setDuration( int duration ) {
        addKVP( DURATION, duration );
    }

    public void setEntryDescription( String entryDescription ) {
        addKVP( ENTRY_DESCRIPTION, entryDescription );
    }

    public void setEntryType( String entryType ) {
        addKVP( ENTRY_TYPE, entryType );
    }

    public void setEventResponse( String eventResponse ) {
        addKVP( EVENT_RESPONSE, eventResponse );
    }

    public void setEventResponseStatus( String eventResponseStatus ) {
        addKVP( EVENT_RESPONSE_STATUS, eventResponseStatus );
    }

    public void setEventSource( String eventSource ) {
        addKVP( EVENT_SOURCE, eventSource );
    }

    public void setEventSubClass( String eventSubClass ) {
        addKVP( EVENT_SUB_CLASS, eventSubClass );
    }

    public void setFirstSwitchTime( long firstSwitchTime ) {
        addKVP( FIRST_SWITCH_TIME, firstSwitchTime );
    }

    public void setFlagCount( int flagCount ) {
        addKVP( FLAG_COUNT, flagCount );
    }

    public void setFlowCount( int flowCount ) {
        addKVP( FLOW_COUNT, flowCount );
    }

    public void setFolder( String folder ) {
        addKVP( FOLDER, folder );
    }

    public void setHttpStatusCode( String httpStatusCode ) {
        addKVP( HTTP_STATUS_CODE, httpStatusCode );
    }

    public void setImUser( String imUser ) {
        addKVP( IM_USER, imUser );
    }

    public void setInactiveTimeout( long inactiveTimeout ) {
        addKVP( INACTIVE_TIMEOUT, inactiveTimeout );
    }

    public void setInterfaceDescription( String interfaceDescription ) {
        addKVP( INTERFACE_DESCRIPTION, interfaceDescription );
    }

    public void setInterfaceName( String interfaceName ) {
        addKVP( INTERFACE_NAME, interfaceName );
    }

    public void setJcID( String jcID ) {
        addKVP( JC_ID, jcID );
    }

    public void setJdest( String jdest ) {
        addKVP( JDEST, jdest );
    }

    public void setJdM( String jdM ) {
        addKVP( JD_M, jdM );
    }

    public void setJexp( String jexp ) {
        addKVP( JEXP, jexp );
    }

    public void setJmID( String jmID ) {
        addKVP( JM_ID, jmID );
    }

    public void setJob( String job ) {
        addKVP( JOB, job );
    }

    public void setJobID( String jobID ) {
        addKVP( JOB_ID, jobID );
    }

    public void setJobName( String jobName ) {
        addKVP( JOB_NAME, jobName );
    }

    public void setJobNumber( String jobNumber ) {
        addKVP( JOB_NUMBER, jobNumber );
    }

    public void setJobUser( String jobUser ) {
        addKVP( JOB_USER, jobUser );
    }

    public void setJournalDescription( String journalDescription ) {
        addKVP( JOURNAL_DESCRIPTION, journalDescription );
    }

    public void setJournalLibrary( String journalLibrary ) {
        addKVP( JOURNAL_LIBRARY, journalLibrary );
    }

    public void setJournalNumber( String journalNumber ) {
        addKVP( JOURNAL_NUMBER, journalNumber );
    }

    public void setJournalProgram( String journalProgram ) {
        addKVP( JOURNAL_PROGRAM, journalProgram );
    }

    public void setJournalSysName( String journalSysName ) {
        addKVP( JOURNAL_SYS_NAME, journalSysName );
    }

    public void setJournalType( String journalType ) {
        addKVP( JOURNAL_TYPE, journalType );
    }

    public void setJournalUser( String journalUser ) {
        addKVP( JOURNAL_USER, journalUser );
    }

    public void setJpri( String jpri ) {
        addKVP( JPRI, jpri );
    }

    public void setJreDel( String jreDel ) {
        addKVP( JRE_DEL, jreDel );
    }

    public void setJrTO( String jrTO ) {
        addKVP( JR_TO, jrTO );
    }

    public void setJtCMP( String jtCMP ) {
        addKVP( JT_CMP, jtCMP );
    }

    public void setJtCMS( String jtCMS ) {
        addKVP( JT_CMS, jtCMS );
    }

    public void setJtComp( String jtComp ) {
        addKVP( JT_COMP, jtComp );
    }

    public void setJtDS( String jtDS ) {
        addKVP( JT_DS, jtDS );
    }

    public void setJtImp( String jtImp ) {
        addKVP( JT_IMP, jtImp );
    }

    public void setJtME( String jtME ) {
        addKVP( JT_ME, jtME );
    }

    public void setJtMT( String jtMT ) {
        addKVP( JT_MT, jtMT );
    }

    public void setJtPU( String jtPU ) {
        addKVP( JT_PU, jtPU );
    }

    public void setJtS( long jtS ) {
        addKVP( JT_S, jtS );
    }

    public void setJtSend( String jtSend ) {
        addKVP( JT_SEND, jtSend );
    }

    public void setJtSSS( String jtSSS ) {
        addKVP( JT_SSS, jtSSS );
    }

    public void setJtype( String jtype ) {
        addKVP( JTYPE, jtype );
    }

    public void setLastSwitchTime( long lastSwitchTime ) {
        addKVP( LAST_SWITCH_TIME, lastSwitchTime );
    }

    public void setLinkedServerName( String linkedServerName ) {
        addKVP( LINKED_SERVER_NAME, linkedServerName );
    }

    public void setLocation( String location ) {
        addKVP( LOCATION, location );
    }

    public void setMaxPacketLength( int maxPacketLength ) {
        addKVP( MAX_PACKET_LENGTH, maxPacketLength );
    }

    public void setMaxSize( int maxSize ) {
        addKVP( MAX_SIZE, maxSize );
    }

    public void setMethod( String method ) {
        addKVP( METHOD, method );
    }

    public void setMinPacketLength( int minPacketLength ) {
        addKVP( MIN_PACKET_LENGTH, minPacketLength );
    }

    public void setMulticastBytes( int multicastBytes ) {
        addKVP( MULTICAST_BYTES, multicastBytes );
    }

    public void setMulticastPackets( int multicastPackets ) {
        addKVP( MULTICAST_PACKETS, multicastPackets );
    }

    public void setNatSourceIP( InetAddress natSourceIP ) {
        addKVP( NAT_SOURCE_IP, natSourceIP );
    }

    public void setNatSourcePort( String natSourcePort ) {
        addKVP( NAT_SOURCE_PORT, natSourcePort );
    }

    public void setNatTargetIP( InetAddress natTargetIP ) {
        addKVP( NAT_TARGET_IP, natTargetIP );
    }

    public void setNatTargetPort( String natTargetPort ) {
        addKVP( NAT_TARGET_PORT, natTargetPort );
    }

    public void setNatType( String natType ) {
        addKVP( NAT_TYPE, natType );
    }

    public void setNode( String node ) {
        addKVP( NODE, node );
    }

    public void setNumberOfRecipients( int numberOfRecipients ) {
        addKVP( NUMBER_OF_RECIPIENTS, numberOfRecipients );
    }

    public void setObjectLibrary( String objectLibrary ) {
        addKVP( OBJECT_LIBRARY, objectLibrary );
    }

    public void setObjectLocation( String objectLocation ) {
        addKVP( OBJECT_LOCATION, objectLocation );
    }

    public void setObjectPrivilege( String objectPrivilege ) {
        addKVP( OBJECT_PRIVILEGE, objectPrivilege );
    }

    public void setObjectSchema( String objectSchema ) {
        addKVP( OBJECT_SCHEMA, objectSchema );
    }

    public void setOrganization( String organization ) {
        addKVP( ORGANIZATION, organization );
    }

    public void setOrganizationID( String organizationID ) {
        addKVP( ORGANIZATION_ID, organizationID );
    }

    public void setOrgEventAction( String orgEventAction ) {
        addKVP( ORG_EVENT_ACTION, orgEventAction );
    }

    public void setOrgEventStatus( String orgEventStatus ) {
        addKVP( ORG_EVENT_STATUS, orgEventStatus );
    }

    public void setOsPrivilege( String osPrivilege ) {
        addKVP( OS_PRIVILEGE, osPrivilege );
    }

    public void setOsType( String osType ) {
        addKVP( OS_TYPE, osType );
    }

    public void setOwner( String owner ) {
        addKVP( OWNER, owner );
    }

    public void setPacketCount( int packetCount ) {
        addKVP( PACKET_COUNT, packetCount );
    }

    public void setParentName( String parentName ) {
        addKVP( PARENT_NAME, parentName );
    }

    public void setPeerHost( String peerHost ) {
        addKVP( PEER_HOST, peerHost );
    }

    public void setPeerIP( InetAddress peerIP ) {
        addKVP( PEER_IP, peerIP );
    }

    public void setPlatform( String platform ) {
        addKVP( PLATFORM, platform );
    }

    public void setProviderName( String providerName ) {
        addKVP( PROVIDER_NAME, providerName );
    }

    public void setReasonCode( String reasonCode ) {
        addKVP( REASON_CODE, reasonCode );
    }

    public void setReasonCodeDesc( String reasonCodeDesc ) {
        addKVP( REASON_CODE_DESC, reasonCodeDesc );
    }

    public void setRecipientAddress( String recipientAddress ) {
        addKVP( RECIPIENT_ADDRESS, recipientAddress );
    }

    public void setRecipientStatus( String recipientStatus ) {
        addKVP( RECIPIENT_STATUS, recipientStatus );
    }

    public void setRecordType( String recordType ) {
        addKVP( RECORD_TYPE, recordType );
    }

    public void setRecordTypeDesc( String recordTypeDesc ) {
        addKVP( RECORD_TYPE_DESC, recordTypeDesc );
    }

    public void setRefererBy( String refererBy ) {
        addKVP( REFERER_BY, refererBy );
    }

    public void setReferer( String referer ) {
        addKVP( REFERER, referer );
    }

    public void setRelatedRecipientAddress( String relatedRecipientAddress ) {
        addKVP( RELATED_RECIPIENT_ADDRESS, relatedRecipientAddress );
    }

    public void setReputation( String reputation ) {
        addKVP( REPUTATION, reputation );
    }

    public void setRequestingComponent( String requestingComponent ) {
        addKVP( REQUESTING_COMPONENT, requestingComponent );
    }

    public void setRequestType( String requestType ) {
        addKVP( REQUEST_TYPE, requestType );
    }

    public void setResourceSecurityLabel( String resourceSecurityLabel ) {
        addKVP( RESOURCE_SECURITY_LABEL, resourceSecurityLabel );
    }

    public void setReturnCodeDesc( String returnCodeDesc ) {
        addKVP( RETURN_CODE_DESC, returnCodeDesc );
    }

    public void setReturnPath( String returnPath ) {
        addKVP( RETURN_PATH, returnPath );
    }

    public void setRiskRating( String riskRating ) {
        addKVP( RISK_RATING, riskRating );
    }

    public void setSamplingAlgorithm( String samplingAlgorithm ) {
        addKVP( SAMPLING_ALGORITHM, samplingAlgorithm );
    }

    public void setSamplingInterval( String samplingInterval ) {
        addKVP( SAMPLING_INTERVAL, samplingInterval );
    }

    public void setSenderAddress( String senderAddress ) {
        addKVP( SENDER_ADDRESS, senderAddress );
    }

    public void setServerType( String serverType ) {
        addKVP( SERVER_TYPE, serverType );
    }

    public void setSession( String session ) {
        addKVP( SESSION, session );
    }

    public void setSignature( String signature ) {
        addKVP( SIGNATURE, signature );
    }

    public void setSignatureID( String signatureID ) {
        addKVP( SIGNATURE_ID, signatureID );
    }

    public void setSignatureVersion( String signatureVersion ) {
        addKVP( SIGNATURE_VERSION, signatureVersion );
    }

    public void setSize( int size ) {
        addKVP( SIZE, size );
    }

    public void setSourceInterface( String sourceInterface ) {
        addKVP( SOURCE_INTERFACE, sourceInterface );
    }

    public void setSourceNode( String sourceNode ) {
        addKVP( SOURCE_NODE, sourceNode );
    }

    public void setSourceObjectName( String sourceObjectName ) {
        addKVP( SOURCE_OBJECT_NAME, sourceObjectName );
    }

    public void setSourceObjectType( String sourceObjectType ) {
        addKVP( SOURCE_OBJECT_TYPE, sourceObjectType );
    }

    public void setSourceTenant( String sourceTenant ) {
        addKVP( SOURCE_TENANT, sourceTenant );
    }

    public void setSourceVlan( String sourceVlan ) {
        addKVP( SOURCE_VLAN, sourceVlan );
    }

    public void setSourceVolumeName( String sourceVolumeName ) {
        addKVP( SOURCE_VOLUME_NAME, sourceVolumeName );
    }

    public void setSourceZone( String sourceZone ) {
        addKVP( SOURCE_ZONE, sourceZone );
    }

    public void setSqlText( String sqlText ) {
        addKVP( SQL_TEXT, sqlText );
    }

    public void setSrcAutonomousSys( String srcAutonomousSys ) {
        addKVP( SRC_AUTONOMOUS_SYS, srcAutonomousSys );
    }

    public void setState( String state ) {
        addKVP( STATE, state );
    }

    public void setSubject( String subject ) {
        addKVP( SUBJECT, subject );
    }

    public void setSubmittorLogonID( String submittorLogonID ) {
        addKVP( SUBMITTOR_LOGON_ID, submittorLogonID );
    }

    public void setSubType( String subType ) {
        addKVP( SUB_TYPE, subType );
    }

    public void setSystemID( String systemID ) {
        addKVP( SYSTEM_ID, systemID );
    }

    public void setSystemPrivilege( String systemPrivilege ) {
        addKVP( SYSTEM_PRIVILEGE, systemPrivilege );
    }

    public void setSystemUpTime( long systemUpTime ) {
        addKVP( SYSTEM_UP_TIME, systemUpTime );
    }

    public void setSystemUser( String systemUser ) {
        addKVP( SYSTEM_USER, systemUser );
    }

    public void setTargetInterface( String targetInterface ) {
        addKVP( TARGET_INTERFACE, targetInterface );
    }

    public void setTargetNode( String targetNode ) {
        addKVP( TARGET_NODE, targetNode );
    }

    public void setTargetObjectName( String targetObjectName ) {
        addKVP( TARGET_OBJECT_NAME, targetObjectName );
    }

    public void setTargetObjectType( String targetObjectType ) {
        addKVP( TARGET_OBJECT_TYPE, targetObjectType );
    }

    public void setTargetTenant( String targetTenant ) {
        addKVP( TARGET_TENANT, targetTenant );
    }

    public void setTargetVlan( String targetVlan ) {
        addKVP( TARGET_VLAN, targetVlan );
    }

    public void setTargetVolumeName( String targetVolumeName ) {
        addKVP( TARGET_VOLUME_NAME, targetVolumeName );
    }

    public void setTargetZone( String targetZone ) {
        addKVP( TARGET_ZONE, targetZone );
    }

    public void setTargetAutonomousSys( String targetAutonomousSys ) {
        addKVP( TARGET_AUTONOMOUS_SYS, targetAutonomousSys );
    }

    public void setTerminalName( String terminalName ) {
        addKVP( TERMINAL_NAME, terminalName );
    }

    public void setTextBody( String textBody ) {
        addKVP( TEXT_BODY, textBody );
    }

    public void setThreatName( String threatName ) {
        addKVP( THREAT_NAME, threatName );
    }

    public void setThreatRating( String threatRating ) {
        addKVP( THREAT_RATING, threatRating );
    }

    public void setThreatType( String threatType ) {
        addKVP( THREAT_TYPE, threatType );
    }

    public void setTotalExpectedBytes( int totalExpectedBytes ) {
        addKVP( TOTAL_EXPECTED_BYTES, totalExpectedBytes );
    }

    public void setTotalExpectedFlows( int totalExpectedFlows ) {
        addKVP( TOTAL_EXPECTED_FLOWS, totalExpectedFlows );
    }

    public void setTotalExpectedPackets( int totalExpectedPackets ) {
        addKVP( TOTAL_EXPECTED_PACKETS, totalExpectedPackets );
    }

    public void setTotalSize( int totalSize ) {
        addKVP( TOTAL_SIZE, totalSize );
    }

    public void setTransactionID( String transactionID ) {
        addKVP( TRANSACTION_ID, transactionID );
    }

    public void setTranslatedIP( InetAddress translatedIP ) {
        addKVP( TRANSLATED_IP, translatedIP );
    }

    public void setTranslatedPort( String translatedPort ) {
        addKVP( TRANSLATED_PORT, translatedPort );
    }

    public void setUriPath( String uriPath ) {
        addKVP( URI_PATH, uriPath );
    }

    public void setUriQuery( String uriQuery ) {
        addKVP( URI_QUERY, uriQuery );
    }

    public void setUrl( String url ) {
        addKVP( URL, url );
    }

    public void setUserAgent( String userAgent ) {
        addKVP( USER_AGENT, userAgent );
    }

    public void setUserSecurityLabel( String userSecurityLabel ) {
        addKVP( USER_SECURITY_LABEL, userSecurityLabel );
    }

    public void setVersion( String version ) {
        addKVP( VERSION, version );
    }

    public void setViolationOccurred( String violationOccurred ) {
        addKVP( VIOLATION_OCCURRED, violationOccurred );
    }

    public void setVmName( String vmName ) {
        addKVP( VM_NAME, vmName );
    }

    public void setVirtualMachine( String virtualMachine ) {
        addKVP( VIRTUAL_MACHINE, virtualMachine );
    }

    public void setVpN( String vpN ) {
        addKVP( VP_N, vpN );
    }

    public void setXmitDelay( int xmitDelay ) {
        addKVP( XMIT_DELAY, xmitDelay );
    }

}
