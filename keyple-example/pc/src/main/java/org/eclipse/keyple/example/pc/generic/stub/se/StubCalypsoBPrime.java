/*
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License version 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 */

package org.eclipse.keyple.example.pc.generic.stub.se;

import java.nio.ByteBuffer;
import org.eclipse.keyple.plugin.stub.StubSecureElement;
import org.eclipse.keyple.util.ByteBufferUtils;

/**
 * Simple contact stub SE (no command)
 */
public class StubCalypsoBPrime extends StubSecureElement {

    final static String seProtocol = "PROTOCOL_B_PRIME";
    final String ATR_HEX = "3B8F8001805A0A01032003111122334482900082";

    public StubCalypsoBPrime() {
        /* Get data */
        addHexCommand("FFCA 000000", "CA7195009000");
    }

    @Override
    public ByteBuffer getATR() {
        return ByteBufferUtils.fromHex(ATR_HEX);
    }

    @Override
    public String getSeProcotol() {
        return seProtocol;
    }


}