
package com.marketo.mktows.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParamsRequestCampaign complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParamsRequestCampaign">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="source" type="{http://www.marketo.com/mktows/}ReqCampSourceType"/>
 *         &lt;element name="campaignId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="leadList" type="{http://www.marketo.com/mktows/}ArrayOfLeadKey" minOccurs="0"/>
 *         &lt;element name="programName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="campaignName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programTokenList" type="{http://www.marketo.com/mktows/}ArrayOfAttrib" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParamsRequestCampaign", propOrder = {
    "source",
    "campaignId",
    "leadList",
    "programName",
    "campaignName",
    "programTokenList"
})
public class ParamsRequestCampaign {

    @XmlElement(required = true)
    protected ReqCampSourceType source;
    @XmlElement(nillable = true)
    protected Integer campaignId;
    @XmlElement(nillable = true)
    protected ArrayOfLeadKey leadList;
    @XmlElement(nillable = true)
    protected String programName;
    @XmlElement(nillable = true)
    protected String campaignName;
    @XmlElement(nillable = true)
    protected ArrayOfAttrib programTokenList;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link ReqCampSourceType }
     *     
     */
    public ReqCampSourceType getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReqCampSourceType }
     *     
     */
    public void setSource(ReqCampSourceType value) {
        this.source = value;
    }

    /**
     * Gets the value of the campaignId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCampaignId() {
        return campaignId;
    }

    /**
     * Sets the value of the campaignId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCampaignId(Integer value) {
        this.campaignId = value;
    }

    /**
     * Gets the value of the leadList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLeadKey }
     *     
     */
    public ArrayOfLeadKey getLeadList() {
        return leadList;
    }

    /**
     * Sets the value of the leadList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLeadKey }
     *     
     */
    public void setLeadList(ArrayOfLeadKey value) {
        this.leadList = value;
    }

    /**
     * Gets the value of the programName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * Sets the value of the programName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramName(String value) {
        this.programName = value;
    }

    /**
     * Gets the value of the campaignName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampaignName() {
        return campaignName;
    }

    /**
     * Sets the value of the campaignName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampaignName(String value) {
        this.campaignName = value;
    }

    /**
     * Gets the value of the programTokenList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAttrib }
     *     
     */
    public ArrayOfAttrib getProgramTokenList() {
        return programTokenList;
    }

    /**
     * Sets the value of the programTokenList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAttrib }
     *     
     */
    public void setProgramTokenList(ArrayOfAttrib value) {
        this.programTokenList = value;
    }

}
