package com.xx.domain;


public class NetworkAclEntry {
	private String cidrBlock;
	private Boolean egress;
	private Integer icmpType;
	private Integer icmpCode;
	private String networkAclId;
	private Integer portRangeFrom;
	private Integer portRangeTo;
	private String protocol;
	private String ruleAction;
	private Integer ruleNumber;
	
	public String getCidrBlock() {
		return cidrBlock;
	}
	public void setCidrBlock(String cidrBlock) {
		this.cidrBlock = cidrBlock;
	}
	public Boolean getEgress() {
		return egress;
	}
	public void setEgress(Boolean egress) {
		this.egress = egress;
	}
	public Integer getIcmpType() {
		return icmpType;
	}
	public void setIcmpType(Integer icmpType) {
		this.icmpType = icmpType;
	}
	public Integer getIcmpCode() {
		return icmpCode;
	}
	public void setIcmpCode(Integer icmpCode) {
		this.icmpCode = icmpCode;
	}
	public String getNetworkAclId() {
		return networkAclId;
	}
	public void setNetworkAclId(String networkAclId) {
		this.networkAclId = networkAclId;
	}
	public Integer getPortRangeFrom() {
		return portRangeFrom;
	}
	public void setPortRangeFrom(Integer portRangeFrom) {
		this.portRangeFrom = portRangeFrom;
	}
	public Integer getPortRangeTo() {
		return portRangeTo;
	}
	public void setPortRangeTo(Integer portRangeTo) {
		this.portRangeTo = portRangeTo;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getRuleAction() {
		return ruleAction;
	}
	public void setRuleAction(String ruleAction) {
		this.ruleAction = ruleAction;
	}
	public Integer getRuleNumber() {
		return ruleNumber;
	}
	public void setRuleNumber(Integer ruleNumber) {
		this.ruleNumber = ruleNumber;
	}
	
}

