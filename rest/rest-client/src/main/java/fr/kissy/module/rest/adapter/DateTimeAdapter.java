package fr.kissy.module.rest.adapter;

import org.apache.cxf.xjc.runtime.DataTypeAdapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * @author Guillaume Le Biller
 */
public class DateTimeAdapter extends XmlAdapter<String, Date> {
    /**
     * @inheritDoc
     */
    @Override
    public Date unmarshal(String value) {
        return DataTypeAdapter.parseDateTime(value);
    }
    /**
     * @inheritDoc
     */
    @Override
    public String marshal(Date value) {
        return DataTypeAdapter.printDateTime(value);
    }
}