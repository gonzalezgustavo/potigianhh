package com.example.potigianhh.model;

import java.util.Date;

public class RequestHeader {
    private Integer documentCode;
    private Integer documentPrefix;
    private Integer documentSuffix;
    private Integer originBranch;
    private Date insertDate;
    private Integer situationCode;
    private Date situationDate;
    private Double utility;
    private Double totalPrice;
    private Integer documentItems;
    private Integer totalPackages;
    private Double totalWeight;
    private Integer clientCode;
    private String clientName;
    private String clientCUIT;
    private Integer clientProvinceCode;
    private String clientAddress;
    private String clientPostalCode;
    private String clientTown;
    private Long pathNumber;
    private Long distributionNumber;
    private String preparerCode;
    private Date preparerDate;
    private Integer printer;

    public Integer getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(Integer documentCode) {
        this.documentCode = documentCode;
    }

    public Integer getDocumentPrefix() {
        return documentPrefix;
    }

    public void setDocumentPrefix(Integer documentPrefix) {
        this.documentPrefix = documentPrefix;
    }

    public Integer getDocumentSuffix() {
        return documentSuffix;
    }

    public void setDocumentSuffix(Integer documentSuffix) {
        this.documentSuffix = documentSuffix;
    }

    public Integer getOriginBranch() {
        return originBranch;
    }

    public void setOriginBranch(Integer originBranch) {
        this.originBranch = originBranch;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Integer getSituationCode() {
        return situationCode;
    }

    public void setSituationCode(Integer situationCode) {
        this.situationCode = situationCode;
    }

    public Date getSituationDate() {
        return situationDate;
    }

    public void setSituationDate(Date situationDate) {
        this.situationDate = situationDate;
    }

    public Double getUtility() {
        return utility;
    }

    public void setUtility(Double utility) {
        this.utility = utility;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getDocumentItems() {
        return documentItems;
    }

    public void setDocumentItems(Integer documentItems) {
        this.documentItems = documentItems;
    }

    public Integer getTotalPackages() {
        return totalPackages;
    }

    public void setTotalPackages(Integer totalPackages) {
        this.totalPackages = totalPackages;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Integer getClientCode() {
        return clientCode;
    }

    public void setClientCode(Integer clientCode) {
        this.clientCode = clientCode;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientCUIT() {
        return clientCUIT;
    }

    public void setClientCUIT(String clientCUIT) {
        this.clientCUIT = clientCUIT;
    }

    public Integer getClientProvinceCode() {
        return clientProvinceCode;
    }

    public void setClientProvinceCode(Integer clientProvinceCode) {
        this.clientProvinceCode = clientProvinceCode;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientPostalCode() {
        return clientPostalCode;
    }

    public void setClientPostalCode(String clientPostalCode) {
        this.clientPostalCode = clientPostalCode;
    }

    public String getClientTown() {
        return clientTown;
    }

    public void setClientTown(String clientTown) {
        this.clientTown = clientTown;
    }

    public Long getPathNumber() {
        return pathNumber;
    }

    public void setPathNumber(Long pathNumber) {
        this.pathNumber = pathNumber;
    }

    public Long getDistributionNumber() {
        return distributionNumber;
    }

    public void setDistributionNumber(Long distributionNumber) {
        this.distributionNumber = distributionNumber;
    }

    public String getPreparerCode() {
        return preparerCode;
    }

    public void setPreparerCode(String preparerCode) {
        this.preparerCode = preparerCode;
    }

    public Date getPreparerDate() {
        return preparerDate;
    }

    public void setPreparerDate(Date preparerDate) {
        this.preparerDate = preparerDate;
    }

    public Integer getPrinter() {
        return printer;
    }

    public void setPrinter(Integer printer) {
        this.printer = printer;
    }

    public String getComposedDoc() {
        return  documentPrefix.toString() + "-" +
                documentCode.toString() + "-" +
                documentSuffix.toString();
    }
}
