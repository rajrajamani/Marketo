
package com.marketo.mktows.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ResultGetImportToListStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultGetImportToListStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="status" type="{http://www.marketo.com/mktows/}ImportToListStatusEnum"/>
 *         &lt;element name="startedTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="endedTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="estimatedTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="estimatedRows" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="rowsImported" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="rowsFailed" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="rowsIgnored" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="importSummary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultGetImportToListStatus", propOrder = {
    "status",
    "startedTime",
    "endedTime",
    "estimatedTime",
    "estimatedRows",
    "rowsImported",
    "rowsFailed",
    "rowsIgnored",
    "importSummary"
})
public class ResultGetImportToListStatus {

    @XmlElement(required = true)
    protected ImportToListStatusEnum status;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startedTime;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endedTime;
    @XmlElement(nillable = true)
    protected Integer estimatedTime;
    @XmlElement(nillable = true)
    protected Integer estimatedRows;
    @XmlElement(nillable = true)
    protected Integer rowsImported;
    @XmlElement(nillable = true)
    protected Integer rowsFailed;
    @XmlElement(nillable = true)
    protected Integer rowsIgnored;
    @XmlElement(nillable = true)
    protected String importSummary;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ImportToListStatusEnum }
     *     
     */
    public ImportToListStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportToListStatusEnum }
     *     
     */
    public void setStatus(ImportToListStatusEnum value) {
        this.status = value;
    }

    /**
     * Gets the value of the startedTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartedTime() {
        return startedTime;
    }

    /**
     * Sets the value of the startedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartedTime(XMLGregorianCalendar value) {
        this.startedTime = value;
    }

    /**
     * Gets the value of the endedTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndedTime() {
        return endedTime;
    }

    /**
     * Sets the value of the endedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndedTime(XMLGregorianCalendar value) {
        this.endedTime = value;
    }

    /**
     * Gets the value of the estimatedTime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    /**
     * Sets the value of the estimatedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEstimatedTime(Integer value) {
        this.estimatedTime = value;
    }

    /**
     * Gets the value of the estimatedRows property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEstimatedRows() {
        return estimatedRows;
    }

    /**
     * Sets the value of the estimatedRows property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEstimatedRows(Integer value) {
        this.estimatedRows = value;
    }

    /**
     * Gets the value of the rowsImported property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRowsImported() {
        return rowsImported;
    }

    /**
     * Sets the value of the rowsImported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRowsImported(Integer value) {
        this.rowsImported = value;
    }

    /**
     * Gets the value of the rowsFailed property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRowsFailed() {
        return rowsFailed;
    }

    /**
     * Sets the value of the rowsFailed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRowsFailed(Integer value) {
        this.rowsFailed = value;
    }

    /**
     * Gets the value of the rowsIgnored property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRowsIgnored() {
        return rowsIgnored;
    }

    /**
     * Sets the value of the rowsIgnored property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRowsIgnored(Integer value) {
        this.rowsIgnored = value;
    }

    /**
     * Gets the value of the importSummary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportSummary() {
        return importSummary;
    }

    /**
     * Sets the value of the importSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportSummary(String value) {
        this.importSummary = value;
    }

}
