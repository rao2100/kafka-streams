package com.rao2100.kstreamApp.pos.datagenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rao2100.kstreamApp.AppStreamsUtil;
import com.rao2100.kstreamApp.pos.types.DeliveryAddress;
import com.rao2100.kstreamApp.pos.types.PosInvoice;
import com.rao2100.kstreamApp.pos.types.LineItem;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InvoiceGeneratorAvro {
    private static final Logger logger = LogManager.getLogger();
    private static InvoiceGeneratorAvro ourInstance = new InvoiceGeneratorAvro();
    private final Random invoiceIndex;
    private final Random invoiceNumber;
    private final Random numberOfItems;
    private final PosInvoice[] invoices;


    public static InvoiceGeneratorAvro getInstance() {
        return ourInstance;
    }

    private InvoiceGeneratorAvro() {
        String DATAFILE = "src/main/resources/data/Invoice.json";
        ObjectMapper mapper;
        invoiceIndex = new Random();
        invoiceNumber = new Random();
        numberOfItems = new Random();
        mapper = new ObjectMapper();
        try {
            invoices = mapper.readValue(new File(DATAFILE), PosInvoice[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getIndex() {
        return invoiceIndex.nextInt(100);
    }

    private int getNewInvoiceNumber() {
        return invoiceNumber.nextInt(99999999) + 99999;
    }

    private int getNoOfItems() {
        return numberOfItems.nextInt(4) + 1;
    }

    public GenericRecord getNextInvoice(Schema schema) {   
        
        PosInvoice invoice = invoices[getIndex()];
        invoice.setInvoiceNumber(Integer.toString(getNewInvoiceNumber()));
        invoice.setCreatedTime(System.currentTimeMillis());
        
        int itemCount = getNoOfItems();
        Double totalAmount = 0.0;
        List<LineItem> items = new ArrayList<>();
        ProductGenerator productGenerator = ProductGenerator.getInstance();
        for (int i = 0; i < itemCount; i++) {
            LineItem item = productGenerator.getNextProduct();
            totalAmount = totalAmount + item.getTotalValue();
            items.add(item);
        }
        invoice.setNumberOfItems(itemCount);
        invoice.setInvoiceLineItems(items);
        invoice.setTotalAmount(totalAmount);
        invoice.setTaxableAmount(totalAmount);
        invoice.setCGST(totalAmount * 0.025);
        invoice.setSGST(totalAmount * 0.025);
        invoice.setCESS(totalAmount * 0.00125);

        DeliveryAddress deliveryAddress = null;
        if ("HOME-DELIVERY".equalsIgnoreCase(invoice.getDeliveryType())) {
            deliveryAddress = AddressGenerator.getInstance().getNextAddress();
            invoice.setDeliveryAddress(deliveryAddress);
        }
        logger.debug(invoice);

        GenericRecord invoiceRecord = AppStreamsUtil.mapObjectToRecord(invoice, schema);
        if ("HOME-DELIVERY".equalsIgnoreCase(invoice.getDeliveryType())) {            
            GenericRecord deliveryAddressRecord = AppStreamsUtil.mapObjectToRecord(deliveryAddress);
            invoiceRecord.put("deliveryAddress", deliveryAddressRecord);
        }
        List<GenericRecord> lineItemAddressRecord = new ArrayList<>();
        for (LineItem lineItem : items) {
            GenericRecord itemRecord = AppStreamsUtil.mapObjectToRecord(lineItem);
            lineItemAddressRecord.add(itemRecord);
        }

        // deliveryAddress
        invoiceRecord.put("invoiceLineItems", lineItemAddressRecord);

        return invoiceRecord;
    }

}
