
package com.marketo.mktows.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImportToListModeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ImportToListModeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UPSERTLEADS"/>
 *     &lt;enumeration value="LISTONLY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ImportToListModeEnum")
@XmlEnum
public enum ImportToListModeEnum {

    UPSERTLEADS,
    LISTONLY;

    public String value() {
        return name();
    }

    public static ImportToListModeEnum fromValue(String v) {
        return valueOf(v);
    }

}
