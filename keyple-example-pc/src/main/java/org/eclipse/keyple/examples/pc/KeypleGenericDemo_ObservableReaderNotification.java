/*
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License version 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 */

package org.eclipse.keyple.examples.pc;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.keyple.plugin.pcsc.PcscPlugin;
import org.eclipse.keyple.seproxy.*;
import org.eclipse.keyple.seproxy.exceptions.IOReaderException;
import org.eclipse.keyple.util.Observable;


public class KeypleGenericDemo_ObservableReaderNotification {

    private SpecificReaderObserver readerObserver;
    private SpecificPluginObserver pluginObserver;

    private KeypleGenericDemo_ObservableReaderNotification() {
        readerObserver = new SpecificReaderObserver();
        pluginObserver = new SpecificPluginObserver(readerObserver);
    }

    private static void listReaders() throws IOReaderException {

        int pluginIndex = 0;
        for (ReadersPlugin plugin : SeProxyService.getInstance().getPlugins()) {
            pluginIndex++;
            int readerIndex = 0;
            for (ProxyReader reader : plugin.getReaders()) {
                System.out.println(pluginIndex + "\t" + plugin.getName() + "\t" + readerIndex++
                        + "\t" + reader.getName() + "\t"
                        + ((reader.isSePresent()) ? "card_present" : "card_absent") + "\t"
                        + ((((AbstractReader) reader).countObservers() > 0) ? "observed_reader"
                                : "not_observed_reader"));
            }
        }
    }

    private void setObservers() throws IOReaderException {

        for (ReadersPlugin plugin : SeProxyService.getInstance().getPlugins()) {

            if (plugin instanceof AbstractPlugin) {
                System.out.println("Add observer on the plugin :  " + plugin.getName());
                ((AbstractPlugin) plugin).addObserver(this.pluginObserver);
            } else {
                System.out.println("Plugin " + plugin.getName() + " isn't observable");
            }

            for (ProxyReader reader : plugin.getReaders()) {
                if (reader instanceof AbstractReader) {
                    System.out.println("Add observer on the reader :  " + reader.getName());
                    ((AbstractReader) reader).addObserver(this.readerObserver);
                } else {
                    System.out.println("Reader " + reader.getName() + " isn't observable");
                }
            }
        }
    }

    public class SpecificReaderObserver implements AbstractReader.ReaderObserver {

        SpecificReaderObserver() {
            super();
        }

        public void update(ReaderEvent event) {
            if (event.getEventType().equals(ReaderEvent.EventType.SE_INSERTED)) {
                System.out.println("Card inserted on: " + event.getReader().getName());
                analyseCard(event.getReader());
            } else if (event.getEventType().equals(ReaderEvent.EventType.SE_REMOVAL)) {
                System.out.println("Card removed on: " + event.getReader().getName());
            }
            try {
                listReaders();
            } catch (IOReaderException e) {
                e.printStackTrace();
            }
        }

        private void analyseCard(AbstractReader reader) {
            try {
                System.out.println("Card present = " + reader.isSePresent());
            } catch (IOReaderException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    public class SpecificPluginObserver implements AbstractPlugin.Observer<PluginEvent> {

        SpecificReaderObserver readerObserver;

        SpecificPluginObserver(SpecificReaderObserver readerObserver) {
            this.readerObserver = readerObserver;
        }

        @Override
        public void update(PluginEvent event) {
            if (event instanceof ReaderPresencePluginEvent) {
                ReaderPresencePluginEvent presence = (ReaderPresencePluginEvent) event;
                ProxyReader reader = presence.getReader();
                if (presence.isAdded()) {
                    System.out.println("New reader: " + reader.getName());

                    if (reader instanceof AbstractReader) {

                        if (readerObserver != null) {
                            ((AbstractReader) reader).addObserver(readerObserver);
                            System.out.println(
                                    "Add observer on the plugged reader :  " + reader.getName());
                        } else {
                            System.out.println("No observer to add to the plugged reader :  "
                                    + reader.getName());
                        }
                    }
                } else {
                    System.out.println("Reader removed: " + reader.getName());

                    if (reader instanceof AbstractReader) {

                        if (readerObserver != null) {
                            ((AbstractReader) reader).removeObserver(readerObserver);
                            System.out.println("Remove observer on the unplugged reader :  "
                                    + reader.getName());
                        } else {
                            System.out.println(
                                    "Unplugged reader " + reader.getName() + " wasn't observed");
                        }
                    }
                }

                try {
                    listReaders();
                    if (((AbstractPlugin) observable).getReaders().isEmpty()) {
                        System.out.println("EXIT - no more reader");
                        synchronized (waitBeforeEnd) {
                            waitBeforeEnd.notify();
                        }
                    }
                } catch (IOReaderException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private final static Object waitBeforeEnd = new Object();

    public static void main(String[] args) throws Exception {

        KeypleGenericDemo_ObservableReaderNotification testObserver =
                new KeypleGenericDemo_ObservableReaderNotification();

        // Set PCSC plugin
        SeProxyService seProxyService = SeProxyService.getInstance();
        List<ReadersPlugin> pluginsSet = new ArrayList<ReadersPlugin>();
        pluginsSet.add(PcscPlugin.getInstance().setLogging(false));
        seProxyService.setPlugins(pluginsSet);

        // Print reader configuration
        listReaders();

        // Set observer
        testObserver.setObservers();

        // Print reader configuration
        listReaders();

        synchronized (waitBeforeEnd) {
            waitBeforeEnd.wait();
        }
    }
}
