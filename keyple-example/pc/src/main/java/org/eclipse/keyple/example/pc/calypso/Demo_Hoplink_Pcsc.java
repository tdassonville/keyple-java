/*
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License version 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 */

package org.eclipse.keyple.example.pc.calypso;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import org.eclipse.keyple.example.common.Demo_HoplinkTransactionEngine;
import org.eclipse.keyple.plugin.pcsc.PcscPlugin;
import org.eclipse.keyple.plugin.pcsc.PcscProtocolSetting;
import org.eclipse.keyple.plugin.pcsc.PcscReader;
import org.eclipse.keyple.seproxy.*;
import org.eclipse.keyple.seproxy.event.ObservableReader;
import org.eclipse.keyple.seproxy.exception.KeypleBaseException;
import org.eclipse.keyple.seproxy.protocol.SeProtocolSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Demo_Hoplink_Pcsc {
    private ProxyReader poReader, csmReader;

    /**
     * This object is used to freeze the main thread while card operations are handle through the
     * observers callbacks. A call to the notify() method would end the program (not demonstrated
     * here).
     */
    private static final Object waitForEnd = new Object();

    /**
     * main program entry
     *
     * @param args the program arguments
     * @throws IllegalArgumentException,KeypleBaseException setParameter exception
     * @throws InterruptedException thread exception
     */
    public static void main(String[] args)
            throws IllegalArgumentException, KeypleBaseException, InterruptedException {
        final Logger logger = LoggerFactory.getLogger(Demo_Hoplink_Pcsc.class);

        /* Get the instance of the SeProxyService (Singleton pattern) */
        SeProxyService seProxyService = SeProxyService.getInstance();

        SortedSet<ReaderPlugin> pluginsSet = new ConcurrentSkipListSet<ReaderPlugin>();

        /* Get the instance of the PcscPlugin (Singleton pattern) */
        pluginsSet.add(PcscPlugin.getInstance());

        /* Assign PcscPlugin to the SeProxyService */
        seProxyService.setPlugins(pluginsSet);

        /* Setting up the transaction engine (implements Observer) */
        Demo_HoplinkTransactionEngine transactionEngine = new Demo_HoplinkTransactionEngine();

        /*
         * Get PO and CSM readers. Apply regulars expressions to reader names to select PO / CSM
         * readers. Use the getReader helper method from the transaction engine.
         */
        ProxyReader poReader = transactionEngine.getReader(seProxyService,
                PcscReadersSettings.PO_READER_NAME_REGEX);
        ProxyReader csmReader = transactionEngine.getReader(seProxyService,
                PcscReadersSettings.CSM_READER_NAME_REGEX);

        /* Both readers are expected not null */
        if (poReader == csmReader || poReader == null || csmReader == null) {
            throw new IllegalStateException("Bad PO/CSM setup");
        }

        logger.info("PO Reader  NAME = {}", poReader.getName());
        logger.info("CSM Reader  NAME = {}", csmReader.getName());

        /* Set PcSc settings per reader */
        poReader.setParameter(PcscReader.SETTING_KEY_LOGGING, "true");
        poReader.setParameter(PcscReader.SETTING_KEY_PROTOCOL, PcscReader.SETTING_PROTOCOL_T1);
        csmReader.setParameter(PcscReader.SETTING_KEY_LOGGING, "true");
        csmReader.setParameter(PcscReader.SETTING_KEY_PROTOCOL, PcscReader.SETTING_PROTOCOL_T0);

        /* Set the PO reader protocol flag */
        poReader.addSeProtocolSetting(
                new SeProtocolSetting(PcscProtocolSetting.SETTING_PROTOCOL_ISO14443_4));

        /* Assign readers to Hoplink transaction engine */
        transactionEngine.setReaders(poReader, csmReader);

        /* check if the expected CSM is available. */
        if (!transactionEngine.checkCsm()) {
            throw new IllegalStateException("No CSM available! Exit program.");
        }

        /* Set terminal as Observer of the first reader */
        ((ObservableReader) poReader).addObserver(transactionEngine);

        /* Wait for ever (exit with CTRL-C) */
        synchronized (waitForEnd) {
            waitForEnd.wait();
        }
    }
}
