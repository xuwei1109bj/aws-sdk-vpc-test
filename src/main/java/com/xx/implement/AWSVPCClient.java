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
	
	/**
	 * Creates a VPC with the specified CIDR block.
	 * 
	 * The smallest VPC you can create uses a /28 netmask (16 IP addresses), and
	 * the largest uses a /16 netmask (65,536 IP addresses). To help you decide
	 * how big to make your VPC, see Your VPC and Subnets in the Amazon Virtual
	 * Private Cloud User Guide .
	 * 
	 * By default, each instance you launch in the VPC has the default DHCP
	 * options, which includes only a default DNS server that Amazon provide
	 * (AmazonProvidedDNS).
	 * 
	 * @param cidrBlock
	 * @param instanceTenancy
	 * @return
	 */
	public Vpc createVPC(String cidrBlock, String instanceTenancy) {
		CreateVpcRequest createVpcRequest = new CreateVpcRequest();
		createVpcRequest.setCidrBlock(cidrBlock);
		createVpcRequest.setInstanceTenancy(instanceTenancy);
		return amazonEC2Client.createVpc(createVpcRequest).getVpc();
	}
	
	/**
	 * Deletes the specified VPC. You must detach or delete all gateways and
	 * resources that are associated with the VPC before you can delete it. For
	 * example, you must terminate all instances running in the VPC, delete all
	 * security groups associated with the VPC (except the default one), delete
	 * all route tables associated with the VPC (except the default one), and so
	 * on.
	 * 
	 * @param vpcId
	 */
	public void deleteVPC(String vpcId) {
		DeleteVpcRequest deleteVpcRequest = new DeleteVpcRequest(vpcId);
		amazonEC2Client.deleteVpc(deleteVpcRequest);
	}
	
//	public AmazonEC2Client getEC2Client() {
//		return amazonEC2Client;
//	}
	
	/**
	 * Adds or overwrites one or more tags for the specified EC2 resource or
	 * resources. Each resource can have a maximum of 10 tags. Each tag consists
	 * of a key and optional value. Tag keys must be unique per resource.
	 * 
	 * @param resouceId
	 *            such as vpcId, subnetId ...
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
	
	/**
	 * Get vpc.
	 * 
	 * @param vpcId
	 * @return
	 */
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
	
	/**
	 * Get vpcs.
	 * 
	 * @param vpcIds
	 * @return
	 */
	public List<Vpc> getVPCs(Collection<String> vpcIds) {
		DescribeVpcsRequest describeVpcsRequest = new DescribeVpcsRequest();
		describeVpcsRequest.setVpcIds(vpcIds);
		return amazonEC2Client.describeVpcs(describeVpcsRequest).getVpcs();
	}
	
	/**
	 * Get vpcs.
	 * 
	 * @return
	 */
	public List<Vpc> getVPCs() {
		return amazonEC2Client.describeVpcs().getVpcs();
	}
	
	/**
	 * Creates a subnet in an existing VPC.
	 * 
	 * When you create each subnet, you provide the VPC ID and the CIDR block
	 * you want for the subnet. After you create a subnet, you can't change its
	 * CIDR block. The subnet's CIDR block can be the same as the VPC's CIDR
	 * block (assuming you want only a single subnet in the VPC), or a subset of
	 * the VPC's CIDR block. If you create more than one subnet in a VPC, the
	 * subnets' CIDR blocks must not overlap. The smallest subnet (and VPC) you
	 * can create uses a /28 netmask (16 IP addresses), and the largest uses a
	 * /16 netmask (65,536 IP addresses).
	 * 
	 * IMPORTANT: AWS reserves both the first four and the last IP address in
	 * each subnet's CIDR block. They're not available for use.
	 * 
	 * If you add more than one subnet to a VPC, they're set up in a star
	 * topology with a logical router in the middle.
	 * 
	 * @param vpcId
	 * @param cidrBlock
	 * @return
	 */
	public Subnet createSubnet(String vpcId, String cidrBlock) {
		CreateSubnetRequest createSubnetRequest = new CreateSubnetRequest(vpcId, cidrBlock);
		return amazonEC2Client.createSubnet(createSubnetRequest).getSubnet();
	}
	
	/**
	 * Get subnet.
	 * 
	 * @param subnetId
	 * @return
	 */
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
	
	/**
	 * Get subnets.
	 * 
	 * @return
	 */
	public List<Subnet> getSubnets() {
		return amazonEC2Client.describeSubnets().getSubnets();
	}
	
	/**
	 * Get subnets.
	 * 
	 * @param subnetIds
	 * @return
	 */
	public List<Subnet> getSubnets(Collection<String> subnetIds) {
		DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest();
		describeSubnetsRequest.setSubnetIds(subnetIds);
		return amazonEC2Client.describeSubnets(describeSubnetsRequest).getSubnets();
	}
	
	/**
	 * Deletes the specified subnet. You must terminate all running instances in
	 * the subnet before you can delete the subnet.
	 * 
	 * @param subnetId
	 */
	public void deleteSubnet(String subnetId) {
		DeleteSubnetRequest deleteSubnetRequest = new DeleteSubnetRequest(subnetId);
		amazonEC2Client.deleteSubnet(deleteSubnetRequest);
	}
	
	/**
	 * Creates a route table for the specified VPC. After you create a route
	 * table, you can add routes and associate the table with a subnet.
	 * 
	 * @param vpcId
	 * @return
	 */
	public RouteTable createRouteTable(String vpcId) {
		CreateRouteTableRequest createRouteTableRequest = new CreateRouteTableRequest();
		createRouteTableRequest.setVpcId(vpcId);
		return amazonEC2Client.createRouteTable(createRouteTableRequest).getRouteTable();
	}
	
	/**
	 * Deletes the specified route table. You must disassociate the route table
	 * from any subnets before you can delete it. You can't delete the main
	 * route table.
	 * 
	 * @param routeTableId
	 */
	public void deleteRouteTable(String routeTableId) {
		DeleteRouteTableRequest deleteRouteTableRequest = new DeleteRouteTableRequest();
		deleteRouteTableRequest.setRouteTableId(routeTableId);
		amazonEC2Client.deleteRouteTable(deleteRouteTableRequest);
	}
	
	/**
	 * Get the specified routeTable.
	 * 
	 * @param routeTableId
	 * @return
	 */
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
	
	/**
	 * Get the specified routeTables.
	 * 
	 * @param routeTableIds
	 * @return
	 */
	public List<RouteTable> getRouteTables(Collection<String> routeTableIds) {
		DescribeRouteTablesRequest describeRouteTablesRequest = new DescribeRouteTablesRequest();
		describeRouteTablesRequest.setRouteTableIds(routeTableIds);
		return amazonEC2Client.describeRouteTables(describeRouteTablesRequest).getRouteTables();
	}
	
	/**
	 * Get routeTables.
	 * 
	 * @return
	 */
	public List<RouteTable> getRouteTables() {
		return amazonEC2Client.describeRouteTables().getRouteTables();
	}
	
	/**
	 * Associates a subnet with a route table. The subnet and route table must
	 * be in the same VPC. This association causes traffic originating from the
	 * subnet to be routed according to the routes in the route table. The
	 * action returns an association ID, which you need in order to disassociate
	 * the route table from the subnet later. A route table can be associated
	 * with multiple subnets.
	 * 
	 * @param routeTableId
	 * @param subnetId
	 */
	public void associateRouteTable(String routeTableId, String subnetId) {
		AssociateRouteTableRequest associateRouteTableRequest = new AssociateRouteTableRequest();
		associateRouteTableRequest.setRouteTableId(routeTableId);
		associateRouteTableRequest.setSubnetId(subnetId);
		amazonEC2Client.associateRouteTable(associateRouteTableRequest );
	}
	
	/**
	 * The request must contain exactly one of gatewayId, networkInterfaceId or
	 * instanceId
	 * 
	 * @param destinationCidrBlock
	 * @param gatewayId
	 *            expecting "igw-..."
	 * @param instanceId
	 *            expecting "i-..."
	 * @param networkInterfaceId
	 *            expecting "eni-..."
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
	
	/**
	 * Delete the specified route.
	 * 
	 * @param routeTableId
	 * @param destinationCidrBlock
	 */
	public void deleteRoute(String routeTableId, String destinationCidrBlock) {
		DeleteRouteRequest deleteRouteRequest = new DeleteRouteRequest();
		deleteRouteRequest.setDestinationCidrBlock(destinationCidrBlock);
		deleteRouteRequest.setRouteTableId(routeTableId);
		amazonEC2Client.deleteRoute(deleteRouteRequest);
	}
	
	/**
	 * Creates an Internet gateway for use with a VPC. After creating the
	 * Internet gateway, you attach it to a VPC using AttachInternetGateway.
	 * 
	 * @return
	 */
	public InternetGateway createInternetGateway() {
		return amazonEC2Client.createInternetGateway().getInternetGateway();
	}
	
	/**
	 * Get the specified internetGateway.
	 * 
	 * @param internetGatewayId
	 * @return
	 */
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
	
	/**
	 * Get internetGateways.
	 * 
	 * @return
	 */
	public List<InternetGateway> getInternetGateways() {
		return amazonEC2Client.describeInternetGateways().getInternetGateways();
	}
	
	/**
	 * Get the specified internetGateways.
	 * 
	 * @param internetGatewayIds
	 * @return
	 */
	public List<InternetGateway> getInternetGateways(Collection<String> internetGatewayIds) {
		DescribeInternetGatewaysRequest describeInternetGatewaysRequest = new DescribeInternetGatewaysRequest();
		describeInternetGatewaysRequest.setInternetGatewayIds(internetGatewayIds);
		return amazonEC2Client.describeInternetGateways(describeInternetGatewaysRequest).getInternetGateways();
	}
	
	/**
	 * Attaches an Internet gateway to a VPC, enabling connectivity between the
	 * Internet and the VPC.
	 * 
	 * @param vpcId
	 * @param internetGatewayId
	 */
	public void attachInternetGateway(String vpcId, String internetGatewayId) {
		AttachInternetGatewayRequest attachInternetGatewayRequest = new AttachInternetGatewayRequest();
		attachInternetGatewayRequest.setInternetGatewayId(internetGatewayId);
		attachInternetGatewayRequest.setVpcId(vpcId);
		amazonEC2Client.attachInternetGateway(attachInternetGatewayRequest);
	}
	
	/**
	 * Detaches an Internet gateway from a VPC, disabling connectivity between
	 * the Internet and the VPC. The VPC must not contain any running instances
	 * with Elastic IP addresses.
	 * 
	 * @param internetGatewayId
	 * @param vpcId
	 */
	public void detachInternetGateway(String internetGatewayId, String vpcId) {
		DetachInternetGatewayRequest detachInternetGatewayRequest = new DetachInternetGatewayRequest();
		detachInternetGatewayRequest.setInternetGatewayId(internetGatewayId);
		detachInternetGatewayRequest.setVpcId(vpcId);
		amazonEC2Client.detachInternetGateway(detachInternetGatewayRequest);
	}
	
	/**
	 * Deletes the specified Internet gateway. You must detach the Internet
	 * gateway from the VPC before you can delete it.
	 * 
	 * @param internetGatewayId
	 */
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
