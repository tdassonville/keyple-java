/*
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License version 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 */

package org.keyple.commands;

import java.util.HashMap;
import java.util.Map;
import org.keyple.seproxy.ApduResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class ApduResponseParser. This abstract class has to be extended by all PO and CSM response
 * parser classes, it provides the generic getters to manage response’s status.
 *
 * @author Ixxi
 *
 */
public abstract class ApduResponseParser {

    /** the byte array APDU response. */
    protected ApduResponse response;

    protected static final Map<Integer, StatusProperties> STATUS_TABLE;
    static {
        HashMap<Integer, StatusProperties> m = new HashMap<Integer, StatusProperties>();
        m.put(0x9000, new StatusProperties(true, "Success"));
        STATUS_TABLE = m;
    }

    // Note: The conversion of all commands was done with:
    // Input regex: new byte\[\] \{\(byte\) 0x([0-9A-Za-z]{2})\, \(byte\) 0x([0-9A-Za-z]{2})\}
    // Output regex: 0x$1$2

    /**
     * Get the internal status table
     * 
     * @return Status table
     */
    Map<Integer, StatusProperties> getStatusTable() {
        return STATUS_TABLE;
    }

    /**
     * the generic abstract constructor to build a parser of the APDU response.
     *
     * @param response response to parse
     */
    public ApduResponseParser(ApduResponse response) {
        this.response = response;
    }

    /**
     * Gets the apdu response.
     *
     * @return the ApduResponse instance.
     */
    public final ApduResponse getApduResponse() {
        return response;
    }

    private int getStatusCodeV2() {
        return response.getStatusCodeV2();
    }

    private StatusProperties getPropertiesForStatusCode() {
        return getStatusTable().get(getStatusCodeV2());
    }

    /**
     * Checks if is successful.
     *
     * @return if the status is successful from the statusTable according to the current status
     *         code.
     */
    public boolean isSuccessful() {
        StatusProperties props = getPropertiesForStatusCode();
        return props != null && props.isSuccessful();
    }

    /**
     * Gets the status information.
     *
     * @return the ASCII message from the statusTable for the current status code.
     */
    public final String getStatusInformation() {
        StatusProperties props = getPropertiesForStatusCode();
        return props != null ? props.getInformation() : null;
    }


    /**
     * Map of statuses
     */
    protected static class StatusProperties {

        /** The successful. */
        private boolean successful;

        /** The information. */
        private String information;

        /**
         * A map with the double byte of a status as key, and the successful property and ASCII text
         * information as data.
         *
         * @param successful set successful status
         * @param information additional information
         */
        public StatusProperties(boolean successful, String information) {
            this.successful = successful;
            this.information = information;
        }

        /**
         * Gets the successful.
         *
         * @return the successful
         */
        public boolean isSuccessful() {
            return successful;
        }

        /**
         * Gets the information.
         *
         * @return the information
         */
        String getInformation() {
            return information;
        }

    }
}