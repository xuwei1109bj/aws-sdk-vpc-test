package com.xx.implement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AssociateRouteTableRequest;
import com.amazonaws.services.ec2.model.AttachInternetGatewayRequest;
import com.amazonaws.services.ec2.model.CreateNetworkAclEntryRequest;
import com.amazonaws.services.ec2.model.CreateNetworkAclRequest;
import com.amazonaws.services.ec2.model.CreateRouteRequest;
import com.amazonaws.services.ec2.model.CreateRouteTableRequest;
import com.amazonaws.services.ec2.model.CreateSubnetRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateVpcRequest;
import com.amazonaws.services.ec2.model.DeleteInternetGatewayRequest;
import com.amazonaws.services.ec2.model.DeleteNetworkAclEntryRequest;
import com.amazonaws.services.ec2.model.DeleteNetworkAclRequest;
import com.amazonaws.services.ec2.model.DeleteRouteRequest;
import com.amazonaws.services.ec2.model.DeleteRouteTableRequest;
import com.amazonaws.services.ec2.model.DeleteSubnetRequest;
import com.amazonaws.services.ec2.model.DeleteTagsRequest;
import com.amazonaws.services.ec2.model.DeleteVpcRequest;
import com.amazonaws.services.ec2.model.DescribeInternetGatewaysRequest;
import com.amazonaws.services.ec2.model.DescribeNetworkAclsRequest;
import com.amazonaws.services.ec2.model.DescribeRouteTablesRequest;
import com.amazonaws.services.ec2.model.DescribeRouteTablesResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DetachInternetGatewayRequest;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.NetworkAclEntry;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Vpc;


public class AWSVPCClient {

	private AWSCredentials awsCredentials;
	private AmazonEC2Client amazonEC2Client;
	
	public Vpc createVPC(String cidrBlock, String instanceTenancy) {
		CreateVpcRequest createVpcRequest = new CreateVpcRequest();
		createVpcRequest.setCidrBlock(cidrBlock);
		createVpcRequest.setInstanceTenancy(instanceTenancy);
		return amazonEC2Client.createVpc(createVpcRequest).getVpc();
	}
	
	public void deleteVPC(String vpcId) {
		DeleteVpcRequest deleteVpcRequest = new DeleteVpcRequest(vpcId);
		amazonEC2Client.deleteVpc(deleteVpcRequest);
	}
	
//	public AmazonEC2Client getEC2Client() {
//		return amazonEC2Client;
//	}
	
	/**
	 * @param resouceId	such as vpcId, subnetId ...
	 * @param tags
	 */
	public void addTags(String resouceId, List<Tag> tags) {
		List<String> resources = new ArrayList<String>();
		resources.add(resouceId);
		CreateTagsRequest createTagsRequest = new CreateTagsRequest(resources, tags);
		amazonEC2Client.createTags(createTagsRequest);
	}
	
	/**
	 * @param resouceId	such as vpcId, subnetId ...
	 * @param tags
	 */
	public void deleteTags(String resouceId, List<Tag> tags) {
		DeleteTagsRequest deleteTagsRequest = new DeleteTagsRequest();
		Collection<String> resources = new ArrayList<String>();
		resources.add(resouceId);
		deleteTagsRequest.setResources(resources);
		deleteTagsRequest.setTags(tags);
		amazonEC2Client.deleteTags(deleteTagsRequest);
	}
	
	public Vpc getVPC(String vpcId) {
		if(isValidString(vpcId)) {
			List<String> vpcIds = new ArrayList<String>();
			vpcIds.add(vpcId);
			List<Vpc> vpcs = getVPCs(vpcIds);
			if(vpcs != null && vpcs.size() > 0) {
				return vpcs.get(0);
			}
		}
		return null;
	}
	
	public List<Vpc> getVPCs(Collection<String> vpcIds) {
		DescribeVpcsRequest describeVpcsRequest = new DescribeVpcsRequest();
		describeVpcsRequest.setVpcIds(vpcIds);
		return amazonEC2Client.describeVpcs(describeVpcsRequest).getVpcs();
	}
	
	public List<Vpc> getVPCs() {
		return amazonEC2Client.describeVpcs().getVpcs();
	}
	
	public Subnet createSubnet(String vpcId, String cidrBlock) {
		CreateSubnetRequest createSubnetRequest = new CreateSubnetRequest(vpcId, cidrBlock);
		return amazonEC2Client.createSubnet(createSubnetRequest).getSubnet();
	}
	
	public Subnet getSubnet(String subnetId) {
		if(isValidString(subnetId)) {
			DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest();
			Collection<String> subnetIds = new ArrayList<String>();
			subnetIds.add(subnetId);
			describeSubnetsRequest.setSubnetIds(subnetIds);
			DescribeSubnetsResult result = amazonEC2Client.describeSubnets(describeSubnetsRequest);
			if(result != null && result.getSubnets() != null && result.getSubnets().size() > 0) {
				return result.getSubnets().get(0);
			}
		}
		return null;
	}
	
	public List<Subnet> getSubnets() {
		return amazonEC2Client.describeSubnets().getSubnets();
	}
	
	public List<Subnet> getSubnets(Collection<String> subnetIds) {
		DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest();
		describeSubnetsRequest.setSubnetIds(subnetIds);
		return amazonEC2Client.describeSubnets(describeSubnetsRequest).getSubnets();
	}
	
	public void deleteSubnet(String subnetId) {
		DeleteSubnetRequest deleteSubnetRequest = new DeleteSubnetRequest(subnetId);
		amazonEC2Client.deleteSubnet(deleteSubnetRequest);
	}
	
	public RouteTable createRouteTable(String vpcId) {
		CreateRouteTableRequest createRouteTableRequest = new CreateRouteTableRequest();
		createRouteTableRequest.setVpcId(vpcId);
		return amazonEC2Client.createRouteTable(createRouteTableRequest).getRouteTable();
	}
	
	public void deleteRouteTable(String routeTableId) {
		DeleteRouteTableRequest deleteRouteTableRequest = new DeleteRouteTableRequest();
		deleteRouteTableRequest.setRouteTableId(routeTableId);
		amazonEC2Client.deleteRouteTable(deleteRouteTableRequest);
	}
	
	public RouteTable getRouteTable(String routeTableId) {
		if(isValidString(routeTableId)) {
			DescribeRouteTablesRequest describeRouteTablesRequest = new DescribeRouteTablesRequest();
			Collection<String> routeTableIds = new ArrayList<String>();
			routeTableIds.add(routeTableId);
			describeRouteTablesRequest.setRouteTableIds(routeTableIds);
			DescribeRouteTablesResult result = amazonEC2Client.describeRouteTables(describeRouteTablesRequest);
			List<RouteTable> routeTables = result.getRouteTables();
			if(routeTables != null && routeTables.size() > 0) {
				return routeTables.get(0);
			}
		}
		return null;
	}
	
	public List<RouteTable> getRouteTables(Collection<String> routeTableIds) {
		DescribeRouteTablesRequest describeRouteTablesRequest = new DescribeRouteTablesRequest();
		describeRouteTablesRequest.setRouteTableIds(routeTableIds);
		return amazonEC2Client.describeRouteTables(describeRouteTablesRequest).getRouteTables();
	}
	
	public List<RouteTable> getRouteTables() {
		return amazonEC2Client.describeRouteTables().getRouteTables();
	}
	
	public void associateRouteTable(String routeTableId, String subnetId) {
		AssociateRouteTableRequest associateRouteTableRequest = new AssociateRouteTableRequest();
		associateRouteTableRequest.setRouteTableId(routeTableId);
		associateRouteTableRequest.setSubnetId(subnetId);
		amazonEC2Client.associateRouteTable(associateRouteTableRequest );
	}
	
	/**
	 * The request must contain exactly one of gatewayId, networkInterfaceId or instanceId
	 * 
	 * @param destinationCidrBlock
	 * @param gatewayId	expecting "igw-..."
	 * @param instanceId
	 * @param networkInterfaceId	expecting "eni-..."
	 * @param routeTableId
	 */
	public void createRoute(String destinationCidrBlock, String gatewayId, String instanceId, String networkInterfaceId, String routeTableId) {
		CreateRouteRequest createRouteRequest = new CreateRouteRequest();
		createRouteRequest.setDestinationCidrBlock(destinationCidrBlock);
		createRouteRequest.setRouteTableId(routeTableId);
		if(isValidString(gatewayId)) {
			createRouteRequest.setGatewayId(gatewayId);
		}
		if(isValidString(instanceId)) {
			createRouteRequest.setInstanceId(instanceId);
		}
		if(isValidString(networkInterfaceId)) {
			createRouteRequest.setNetworkInterfaceId(networkInterfaceId);
		}
		amazonEC2Client.createRoute(createRouteRequest);
	}
	
	public void deleteRoute(String routeTableId, String destinationCidrBlock) {
		DeleteRouteRequest deleteRouteRequest = new DeleteRouteRequest();
		deleteRouteRequest.setDestinationCidrBlock(destinationCidrBlock);
		deleteRouteRequest.setRouteTableId(routeTableId);
		amazonEC2Client.deleteRoute(deleteRouteRequest);
	}
	
	public InternetGateway createInternetGateway() {
		return amazonEC2Client.createInternetGateway().getInternetGateway();
	}
	
	public InternetGateway getInternetGateway(String internetGatewayId) {
		if(isValidString(internetGatewayId)) {
			DescribeInternetGatewaysRequest describeInternetGatewaysRequest = new DescribeInternetGatewaysRequest();
			Collection<String> internetGatewayIds = new ArrayList<String>();
			internetGatewayIds.add(internetGatewayId);
			describeInternetGatewaysRequest.setInternetGatewayIds(internetGatewayIds);
			List<InternetGateway> internetGateways = amazonEC2Client.describeInternetGateways(describeInternetGatewaysRequest).getInternetGateways();
			if(internetGateways != null && internetGateways.size() > 0) {
				return internetGateways.get(0);
			}
		}
		return null;
	}
	
	public List<InternetGateway> getInternetGateways() {
		return amazonEC2Client.describeInternetGateways().getInternetGateways();
	}
	
	public List<InternetGateway> getInternetGateways(Collection<String> internetGatewayIds) {
		DescribeInternetGatewaysRequest describeInternetGatewaysRequest = new DescribeInternetGatewaysRequest();
		describeInternetGatewaysRequest.setInternetGatewayIds(internetGatewayIds);
		return amazonEC2Client.describeInternetGateways(describeInternetGatewaysRequest).getInternetGateways();
	}
	
	public void attachInternetGateway(String vpcId, String internetGatewayId) {
		AttachInternetGatewayRequest attachInternetGatewayRequest = new AttachInternetGatewayRequest();
		attachInternetGatewayRequest.setInternetGatewayId(internetGatewayId);
		attachInternetGatewayRequest.setVpcId(vpcId);
		amazonEC2Client.attachInternetGateway(attachInternetGatewayRequest);
	}
	
	public void detachInternetGateway(String internetGatewayId, String vpcId) {
		DetachInternetGatewayRequest detachInternetGatewayRequest = new DetachInternetGatewayRequest();
		detachInternetGatewayRequest.setInternetGatewayId(internetGatewayId);
		detachInternetGatewayRequest.setVpcId(vpcId);
		amazonEC2Client.detachInternetGateway(detachInternetGatewayRequest);
	}
	
	public void deleteInternetGateway(String internetGatewayId) {
		DeleteInternetGatewayRequest deleteInternetGatewayRequest = new DeleteInternetGatewayRequest();
		deleteInternetGatewayRequest.setInternetGatewayId(internetGatewayId);
		amazonEC2Client.deleteInternetGateway(deleteInternetGatewayRequest);
	}
	
	public NetworkAcl createNetworkACL(String vpcId) {
		CreateNetworkAclRequest createNetworkAclRequest = new CreateNetworkAclRequest();
		createNetworkAclRequest.setVpcId(vpcId);
		return amazonEC2Client.createNetworkAcl(createNetworkAclRequest).getNetworkAcl();
	}

	public void deleteNetwordACL(String networkAclId) {
		DeleteNetworkAclRequest deleteNetworkAclRequest = new DeleteNetworkAclRequest();
		deleteNetworkAclRequest.setNetworkAclId(networkAclId);
		amazonEC2Client.deleteNetworkAcl(deleteNetworkAclRequest);
	}
	
	public NetworkAcl getNetworkACL(String networkAclId) {
		if(isValidString(networkAclId)) {
			DescribeNetworkAclsRequest describeNetworkAclsRequest = new DescribeNetworkAclsRequest();
			Collection<String> networkAclIds = new ArrayList<String>();
			networkAclIds.add(networkAclId);
			describeNetworkAclsRequest.setNetworkAclIds(networkAclIds);
			List<NetworkAcl> acls = amazonEC2Client.describeNetworkAcls(describeNetworkAclsRequest).getNetworkAcls();
			if(acls != null && acls.size() > 0) {
				return acls.get(0);
			}
		}
		return null;
	}
	
	public List<NetworkAcl> getNetworkACLs() {
		return amazonEC2Client.describeNetworkAcls().getNetworkAcls();
	}
	
	public List<NetworkAcl> getNetworkACLs(Collection<String> networkAclIds) {
		DescribeNetworkAclsRequest describeNetworkAclsRequest = new DescribeNetworkAclsRequest();
		describeNetworkAclsRequest.setNetworkAclIds(networkAclIds);
		return amazonEC2Client.describeNetworkAcls(describeNetworkAclsRequest).getNetworkAcls();
	}

	// {NetworkAclId: acl-59f9e03b,VpcId: vpc-e6e1ff84,IsDefault: false,Entries:
	// [{RuleNumber: 32767,Protocol: -1,RuleAction: deny,Egress: true,CidrBlock:
	// 0.0.0.0/0,}, {RuleNumber: 32767,Protocol: -1,RuleAction: deny,Egress:
	// false,CidrBlock: 0.0.0.0/0,}],Associations: [],Tags: []}
	/**
	 * Creates an entry (a rule) in a network ACL with the specified rule
	 * number. Each network ACL has a set of numbered ingress rules and a
	 * separate set of numbered egress rules. When determining whether a packet
	 * should be allowed in or out of a subnet associated with the ACL, we
	 * process the entries in the ACL according to the rule numbers, in
	 * ascending order. Each network ACL has a set of ingress rules and a
	 * separate set of egress rules. 
	 * 
	 * We recommend that you leave room between
	 * the rule numbers (for example, 100, 110, 120, ...), and not number them
	 * one right after the other (for example, 101, 102, 103, ...). This makes
	 * it easier to add a rule between existing ones without having to renumber
	 * the rules. 
	 * 
	 * After you add an entry, you can't modify it; you must either
	 * replace it, or create an entry and delete the old one.
	 * 
	 * @param networkAclId
	 * @param aclEntry
	 */
	public void createNetworkACLEntry(String networkAclId, NetworkAclEntry aclEntry) {
		CreateNetworkAclEntryRequest createNetworkAclEntryRequest = new CreateNetworkAclEntryRequest();
		createNetworkAclEntryRequest.setCidrBlock(aclEntry.getCidrBlock());
		createNetworkAclEntryRequest.setEgress(aclEntry.getEgress());
		createNetworkAclEntryRequest.setIcmpTypeCode(aclEntry.getIcmpTypeCode());
		createNetworkAclEntryRequest.setNetworkAclId(networkAclId);
		createNetworkAclEntryRequest.setPortRange(aclEntry.getPortRange());
		createNetworkAclEntryRequest.setProtocol(aclEntry.getProtocol());
		createNetworkAclEntryRequest.setRuleAction(aclEntry.getRuleAction());
		createNetworkAclEntryRequest.setRuleNumber(aclEntry.getRuleNumber());
		amazonEC2Client.createNetworkAclEntry(createNetworkAclEntryRequest);
	}
	
	/**
	 * Deletes the specified ingress or egress entry (rule) from the specified network ACL.
	 * 
	 * @param egress
	 * @param networkAclId
	 * @param ruleNumber
	 */
	public void deleteNetworkACLEntry(Boolean egress, String networkAclId, Integer ruleNumber) {
		DeleteNetworkAclEntryRequest deleteNetworkAclEntryRequest = new DeleteNetworkAclEntryRequest();
		deleteNetworkAclEntryRequest.setEgress(egress);
		deleteNetworkAclEntryRequest.setNetworkAclId(networkAclId);
		deleteNetworkAclEntryRequest.setRuleNumber(ruleNumber);
		amazonEC2Client.deleteNetworkAclEntry(deleteNetworkAclEntryRequest);
	}
	
	
	
	
	
	
	
	
	public void setCredential(String accessKey, String secretKey) {
		awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		amazonEC2Client = new AmazonEC2Client(awsCredentials);
	}
	
	public void setRegion(String region) {
		if (region != null && region.trim().length() > 0) {
			Region r = RegionUtils.getRegion(region);
			if (r != null) {
				amazonEC2Client.setRegion(r);
			}
		}
	}
	
	
	private boolean isValidString(String str) {
		if(str != null && str.trim().length() > 0) {
			return true;
		}
		return false;
	}
}
