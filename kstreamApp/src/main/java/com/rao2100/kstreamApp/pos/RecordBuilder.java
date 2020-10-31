package com.rao2100.kstreamApp.pos;

import com.rao2100.kstreamApp.pos.types.HadoopRecord;
import com.rao2100.kstreamApp.pos.types.LineItem;
import com.rao2100.kstreamApp.pos.types.Notification;
import com.rao2100.kstreamApp.pos.types.PosInvoice;

import java.util.ArrayList;
import java.util.List;



class RecordBuilder {

    final static Double LOYALTY_FACTOR = 0.02;

    /**
     * Create a flattened List of HadoopRecords from Invoice Each Line Item is
     * denormalized into an independent record
     *
     * @param invoice PosInvoice object
     * @return List of HadoopRecord
     */
    static List<HadoopRecord> getHadoopRecords(PosInvoice invoice) {
        List<HadoopRecord> records = new ArrayList<>();
        for (LineItem i : invoice.getInvoiceLineItems()) {
            HadoopRecord record = new HadoopRecord().withInvoiceNumber(invoice.getInvoiceNumber())
                    .withCreatedTime(invoice.getCreatedTime()).withStoreID(invoice.getStoreID())
                    .withPosID(invoice.getPosID()).withCustomerType(invoice.getCustomerType())
                    .withPaymentMethod(invoice.getPaymentMethod()).withDeliveryType(invoice.getDeliveryType())
                    .withItemCode(i.getItemCode()).withItemDescription(i.getItemDescription())
                    .withItemPrice(i.getItemPrice()).withItemQty(i.getItemQty()).withTotalValue(i.getTotalValue());
            if (invoice.getDeliveryType().equalsIgnoreCase(PosConfig.DELIVERY_TYPE_HOME_DELIVERY)) {
                record.setCity(invoice.getDeliveryAddress().getCity());
                record.setState(invoice.getDeliveryAddress().getState());
                record.setPinCode(invoice.getDeliveryAddress().getPinCode());
            }
            records.add(record);
        }
        return records;
    }

    /**
     * Set personally identifiable values to null
     *
     * @param invoice PosInvoice object
     * @return masked PosInvoice object
     */
    static PosInvoice getMaskedInvoice(PosInvoice invoice) {
        invoice.setCustomerCardNo(null);
        if (invoice.getDeliveryType().equalsIgnoreCase(PosConfig.DELIVERY_TYPE_HOME_DELIVERY)) {
            invoice.getDeliveryAddress().setAddressLine(null);
            invoice.getDeliveryAddress().setContactNumber(null);
        }
        return invoice;
    }

    /**
     * Transform PosInvoice to Notification
     *
     * @param invoice PosInvoice Object
     * @return Notification Object
     */
    static Notification getNotification(PosInvoice invoice) {
        return new Notification().withInvoiceNumber(invoice.getInvoiceNumber())
                .withCustomerCardNo(invoice.getCustomerCardNo()).withTotalAmount(invoice.getTotalAmount())
                .withEarnedLoyaltyPoints(invoice.getTotalAmount() * PosConfig.LOYALTY_FACTOR);
    }
}
