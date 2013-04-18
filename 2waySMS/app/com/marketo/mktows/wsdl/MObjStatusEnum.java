
package com.marketo.mktows.wsdl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MObjStatusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MObjStatusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CREATED"/>
 *     &lt;enumeration value="UPDATED"/>
 *     &lt;enumeration value="DELETED"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="UNCHANGED"/>
 *     &lt;enumeration value="SKIPPED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MObjStatusEnum")
@XmlEnum
public enum MObjStatusEnum {

    CREATED,
    UPDATED,
    DELETED,
    FAILED,
    UNCHANGED,
    SKIPPED;

    public String value() {
        return name();
    }

    public static MObjStatusEnum fromValue(String v) {
        return valueOf(v);
    }

}
