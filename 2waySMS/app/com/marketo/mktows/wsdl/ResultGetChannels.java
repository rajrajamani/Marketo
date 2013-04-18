
package com.marketo.mktows.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultGetChannels complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultGetChannels">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tags" type="{http://www.marketo.com/mktows/}ArrayOfTagStatus"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultGetChannels", propOrder = {
    "tags"
})
public class ResultGetChannels {

    @XmlElement(required = true)
    protected ArrayOfTagStatus tags;

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTagStatus }
     *     
     */
    public ArrayOfTagStatus getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTagStatus }
     *     
     */
    public void setTags(ArrayOfTagStatus value) {
        this.tags = value;
    }

}
