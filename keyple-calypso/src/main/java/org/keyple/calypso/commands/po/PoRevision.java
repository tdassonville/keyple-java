/*
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License version 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 */

package org.keyple.calypso.commands.po;

import org.keyple.calypso.commands.CalypsoCommands;

/**
 * Calypso revisions
 */
public enum PoRevision {

    /**
     * Calypso Revision 2.4 (CLA 0x94)
     */
    REV2_4,

    /**
     * Calypso Revision 3.1 (CLA 0x00)
     */
    REV3_1,

    /**
     * Calypso Revision 3.2 (CLA 0x00)
     */
    REV3_2;


    public CalypsoCommands toOpenSessionCommand() {
        switch (this) {
            case REV2_4:
                return CalypsoCommands.PO_OPEN_SESSION_24;
            case REV3_1:
                return CalypsoCommands.PO_OPEN_SESSION_31;
            case REV3_2:
                return CalypsoCommands.PO_OPEN_SESSION_32;
            default:
                throw new IllegalStateException("Any revision should have a matching command");
        }
    }
}
