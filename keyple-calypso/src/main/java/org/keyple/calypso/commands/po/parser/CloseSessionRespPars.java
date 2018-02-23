/*
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License version 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 */

package org.keyple.calypso.commands.po.parser;


import java.util.HashMap;
import java.util.Map;
import org.keyple.calypso.commands.utils.ResponseUtils;
import org.keyple.commands.ApduResponseParser;
import org.keyple.seproxy.ApduResponse;

/**
 * Close Secure Session (008E) response parser. See specs: Calypso / page 104 / 9.5.2 Close Secure
 * Session
 */
public class CloseSessionRespPars extends ApduResponseParser {

    private static final Map<Integer, StatusProperties> STATUS_TABLE;

    /**
     * Initializes the status table.
     */
    static {
        Map<Integer, StatusProperties> m =
                new HashMap<Integer, StatusProperties>(ApduResponseParser.STATUS_TABLE);
        m.put(0x6700, new StatusProperties(false,
                "Lc signatureLo not supported (e.g. Lc=4 with a Revision 3.2 mode for Open Secure Session)."));
        m.put(0x6B00, new StatusProperties(false, "P1 or P2 signatureLo not supported."));
        m.put(0x6988, new StatusProperties(false, "incorrect signatureLo."));
        m.put(0x6985, new StatusProperties(false, "No session was opened."));

        STATUS_TABLE = m;
    }

    Map<Integer, StatusProperties> getStatusTable() {
        return STATUS_TABLE;
    }

    /** The signatureLo. */
    private byte[] signatureLo;

    /** The postponed data. */
    private byte[] postponedData;

    /**
     * Instantiates a new CloseSessionRespPars from the response.
     *
     * @param response from CloseSessionCmdBuild
     */
    public CloseSessionRespPars(ApduResponse response) {
        super(response);
        parse(response.getBytes());
    }

    private void parse(byte[] response) {
        // fclairamb(2018-02-14): Removed 2 bytes to the global response length;
        final int size = response.length - 2;

        if (size == 8) {
            signatureLo = ResponseUtils.subArray(response, 4, size);
            postponedData = ResponseUtils.subArray(response, 0, 4);
        } else if (size == 4) {
            signatureLo = ResponseUtils.subArray(response, 0, size);
        }
        // TODO: I can't add this, it breaks compatibility with existing tests
        /*
         * else if ( size != 0 ){ throw new RuntimeException("Size "+size+" is impossible"); }
         */
    }

    // TODO: Switch that to ByteBuffer

    public byte[] getSignatureLo() {
        return signatureLo != null ? signatureLo : new byte[] {};
    }

    public byte[] getPostponedData() {
        return postponedData != null ? postponedData : new byte[] {};
    }
}