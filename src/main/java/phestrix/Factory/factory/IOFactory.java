package phestrix.Factory.factory;

import phestrix.Factory.GUI.Util.IndentingXMLStreamWriter;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

public class IOFactory {
    public static Factory readFactory(File file) {
        XMLStreamReader reader = null;
        try {
            int accessorySupplierCount = -1;
            int workerCount = -1;
            int dealerCount = -1;
            int supplierDelay = -1;
            int workerDelay = -1;
            int dealerDelay = -1;
            int accessoryStockLimit = -1;
            int engineStockLimit = -1;
            int bodyStockLimit = -1;
            int carStockLimit = -1;
            boolean logBoolIsInitialized = false;
            boolean loggingEnabled = false;

            reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(file));
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    String forSwitch = reader.getLocalName();
                    switch (forSwitch) {
                        case "FactoryConfiguration" -> {
                            continue;
                        }
                        case "AccessorySupplierCount" ->
                                accessorySupplierCount = Integer.parseInt(reader.getElementText());
                        case "WorkerCount" -> workerCount = Integer.parseInt(reader.getElementText());
                        case "DealerCount" -> dealerCount = Integer.parseInt(reader.getElementText());
                        case "SupplierDelay" -> supplierDelay = Integer.parseInt(reader.getElementText());
                        case "WorkerDelay" -> workerDelay = Integer.parseInt(reader.getElementText());
                        case "DealerDelay" -> dealerDelay = Integer.parseInt(reader.getElementText());
                        case "AccessoryStockLimit" -> accessoryStockLimit = Integer.parseInt(reader.getElementText());
                        case "EngineStockLimit" -> engineStockLimit = Integer.parseInt(reader.getElementText());
                        case "BodyStockLimit" -> bodyStockLimit = Integer.parseInt(reader.getElementText());
                        case "CarStockLimit" -> carStockLimit = Integer.parseInt(reader.getElementText());
                        case "LoggingEnabled" -> {
                            loggingEnabled = Boolean.parseBoolean(reader.getElementText());
                            logBoolIsInitialized = true;
                        }
                        default -> {
                        }
                    }

                }
            }
            reader.close();
            if (accessorySupplierCount == 1 || workerCount == -1 || dealerCount == -1 || supplierDelay == -1 ||
                    workerDelay == -1 || dealerDelay == -1 || accessoryStockLimit == -1 || engineStockLimit == -1 ||
                    bodyStockLimit == -1 || carStockLimit == -1 || !logBoolIsInitialized) {
                throw new IllegalArgumentException("Bad config");
            }
            return new Factory(accessoryStockLimit, accessorySupplierCount, workerCount, dealerCount, supplierDelay,
                    workerDelay, dealerDelay, engineStockLimit, bodyStockLimit, carStockLimit, loggingEnabled,
                    null);
        } catch (IOException | XMLStreamException | IllegalArgumentException ex) {
            ex.printStackTrace();
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean saveFactory(File file, Factory factory) {
        XMLStreamWriter writer = null;
        try {
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            writer = new IndentingXMLStreamWriter(output.createXMLStreamWriter(new FileOutputStream(file)), "    ");

            // Open XML-doc and write FactoryConfiguration
            writer.writeStartDocument("1.0");
            writer.writeStartElement("FactoryConfiguration");

            writer.writeStartElement("AccessorySupplierCount");
            writer.writeCharacters("" + factory.getAccessorySupplierCount());
            writer.writeEndElement();

            writer.writeStartElement("WorkerCount");
            writer.writeCharacters("" + factory.getWorkerCount());
            writer.writeEndElement();

            writer.writeStartElement("DealerCount");
            writer.writeCharacters("" + factory.getDealerCount());
            writer.writeEndElement();

            writer.writeStartElement("SupplierDelay");
            writer.writeCharacters("" + factory.getSupplierDelay());
            writer.writeEndElement();

            writer.writeStartElement("WorkerDelay");
            writer.writeCharacters("" + factory.getWorkerDelay());
            writer.writeEndElement();

            writer.writeStartElement("DealerDelay");
            writer.writeCharacters("" + factory.getDealerDelay());
            writer.writeEndElement();

            writer.writeStartElement("AccessoryStockLimit");
            writer.writeCharacters("" + factory.getAccessoryStock().getLimit());
            writer.writeEndElement();

            writer.writeStartElement("EngineStockLimit");
            writer.writeCharacters("" + factory.getEngineStock().getLimit());
            writer.writeEndElement();

            writer.writeStartElement("BodyStockLimit");
            writer.writeCharacters("" + factory.getBodyStock().getLimit());
            writer.writeEndElement();

            writer.writeStartElement("CarStockLimit");
            writer.writeCharacters("" + factory.getCarStock().getLimit());
            writer.writeEndElement();

            writer.writeStartElement("LoggingEnabled");
            writer.writeCharacters("" + factory.isLoggingEnabled());
            writer.writeEndElement();

            // Close root node
            writer.writeEndElement();
            // Close XML doc
            writer.writeEndDocument();
            writer.flush();
            writer.close();


            return true;
        } catch (XMLStreamException | IOException ex) {
            ex.printStackTrace();
            if (writer != null)
                try {
                    writer.close();
                } catch (XMLStreamException ignored) {
                }
        }
        return false;
    }
}
