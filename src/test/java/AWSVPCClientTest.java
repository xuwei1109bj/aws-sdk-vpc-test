import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Vpc;
import com.xx.implement.AWSVPCClient;


public class AWSVPCClientTest {
	private static final String AWS_ACCESS_KEY = "";
	private static final String AWS_SECRET_KEY = "";
	private static final String REGION = "ap-northeast-1";
	private AWSVPCClient client;
	
	@Before
	public void setUp() throws Exception {
		client = new AWSVPCClient();
		client.setCredential(AWS_ACCESS_KEY, AWS_SECRET_KEY);
		client.setRegion(REGION);
	}

	@Test
	public void createVPCTest() {
		String instanceTenancy = "default";
		String cidrBlock = "109.0.0.0/16";
		Vpc vpc = client.createVPC(cidrBlock, instanceTenancy);
		System.out.println("create vpc >>> "+vpc);
	}

	@Test
	public void deleteVPCTest() {
		String vpcId = "vpc-e6e1ff84";
		client.deleteVPC(vpcId);
		System.out.println("-----------end-----------");
	}
	
	@Test
	public void getVPCTest() {
		String vpcId = "vpc-e6e1ff84";
		Vpc vpc = client.getVPC(vpcId);
		System.out.println("getVPCTest vpc >> "+vpc);
	}
	
	@Test
	public void addTagsTest() {
		String vpcId = "vpc-e6e1ff84";
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("testkey", "testvalue"));
		tags.add(new Tag("testkey2", "testvalue2"));
		client.addTags(vpcId, tags);
		
		System.out.println(" check result >>>>> ");
		Vpc vpc = client.getVPC(vpcId);
		System.out.println("getVPCTest vpc >> "+vpc);
	}
	
	@Test
	public void deleteTagsTest() {
		String vpcId = "vpc-e6e1ff84";
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("testkey", "testvalue"));
		tags.add(new Tag("testkey2", "testvalue2"));
		client.deleteTags(vpcId, tags);
		
		System.out.println(" check result >>>>> ");
		Vpc vpc = client.getVPC(vpcId);
		System.out.println("getVPCTest vpc >> "+vpc);
	}
	
	@Test
	public void getVPCs1Test() {
		List<Vpc> vpcs = client.getVPCs();
		System.out.println("getVPCs1Test vpcs >> ");
		for(Vpc v : vpcs) {
			System.out.println(v);
		}
		System.out.println("-----------end----------");
	}
	
	@Test
	public void getVPCs2Test() {
		List<String> vpcIds = new ArrayList<String>();
		vpcIds.add("vpc-42fce220");
		vpcIds.add("vpc-073db56e");
		vpcIds.add("dfdfdfdfdf");
		System.out.println("getVPCs2Test vpcs >> ");
		List<Vpc> vpcs = client.getVPCs(vpcIds);
		for(Vpc v : vpcs) {
			System.out.println(v);
		}
		System.out.println("-----------end----------");
	}
	
	@Test
	public void createSubnetTest() {
		String vpcId = "vpc-e6e1ff84";
		String cidrBlock = "109.0.1.0/24";
		Subnet subnet = client.createSubnet(vpcId, cidrBlock);
		System.out.println("create subnet >>> "+subnet);
	}
	
	@Test
	public void getSubnets1Test() {
		List<Subnet> subnets = client.getSubnets();
		for(Subnet s : subnets) {
			System.out.println(s);
		}
		System.out.println("-------------end-----------");
	}
	
	@Test
	public void getSubnets2Test() {
		Collection<String> subnetIds = new ArrayList<String>();
		subnetIds.add("subnet-dddcc9b4");
		subnetIds.add("subnet-19838a7b");
		List<Subnet> subnets = client.getSubnets(subnetIds);
		for(Subnet s : subnets) {
			System.out.println(s);
		}
		System.out.println("-------------end-----------");
	}
	
	@Test
	public void deleteSubnetTest() {
		String subnetId = "subnet-987855de";
		client.deleteSubnet(subnetId);
		
		System.out.println(" check delete subnet result >>> ");
		List<Subnet> subnets = client.getSubnets();
		for(Subnet s : subnets) {
			System.out.println(s);
		}
	}
	
	@Test
	public void createRouteTableTest() {
		String vpcId = "vpc-e6e1ff84";
		RouteTable rt = client.createRouteTable(vpcId);
		System.out.println("create route table >>> "+rt);
	}
	
	@Test
	public void getRouteTables1Test() {
		List<RouteTable> routeTables = client.getRouteTables();
		for(RouteTable rt : routeTables) {
			System.out.println(rt);
		}
	}
	
	@Test
	public void getRouteTables2Test() {
		List<String> routeTableIds = new ArrayList<String>();
		routeTableIds.add("rtb-10f1e872");
		routeTableIds.add("rtb-6c9e870e");
		List<RouteTable> list = client.getRouteTables(routeTableIds);
		for(RouteTable rt : list) {
			System.out.println(rt);
		}
	}
	
	@Test
	public void getRouteTableTest() {
		String routeTableId = "rtb-6c9e870e";
		RouteTable rt = client.getRouteTable(routeTableId);
		System.out.println(rt);
	}
	
	@Test
	public void associateRouteTableTest() {
		String subnetId = "subnet-25414d51";
		String routeTableId = "rtb-328b9250";
		client.associateRouteTable(routeTableId, subnetId);
		System.out.println("associateRouteTable ------------end");
	}

	@Test
	public void createInternetGatewayTest() {
		InternetGateway ig = client.createInternetGateway();
		System.out.println("createInternetGateway >>>> "+ig);
	}
	
	@Test
	public void getInternetGatewayTest() {
		String internetGatewayId = "igw-f2d7c29b";
		InternetGateway ig = client.getInternetGateway(internetGatewayId);
		System.out.println(ig);
	}
	
	@Test
	public void getInternetGateways1Test() {
		List<InternetGateway> igs = client.getInternetGateways();
		for(InternetGateway ig : igs) {
			System.out.println(ig);
		}
	}
	
	@Test
	public void getInternetGateways2Test() {
		Collection<String> internetGatewayIds = new ArrayList<String>();
		internetGatewayIds.add("igw-f2d7c29b");
		List<InternetGateway> igs = client.getInternetGateways(internetGatewayIds);
		for(InternetGateway ig : igs) {
			System.out.println(ig);
		}
	}
	
	@Test
	public void attachInternetGatewayTest() {
		String internetGatewayId = "igw-0e1d0b6c";
		String vpcId = "vpc-e6e1ff84";
		client.attachInternetGateway(vpcId, internetGatewayId);
	}
	
	@Test
	public void detachInternetGatewayTest() {
		String internetGatewayId = "igw-0e1d0b6c";
		String vpcId = "vpc-e6e1ff84";
		client.detachInternetGateway(internetGatewayId, vpcId);
	}
	
	@Test
	public void deleteInternetGatewayTest() {
		String internetGatewayId = "igw-0e1d0b6c";
		client.deleteInternetGateway(internetGatewayId);
	}
	
	@Test
	public void createRouteTest() {
		String routeTableId = "rtb-328b9250";
		String destinationCidrBlock = "0.0.0.0/0";
		String networkInterfaceId = null;
		String instanceId = null;
		String gatewayId = "igw-0e1d0b6c";
		client.createRoute(destinationCidrBlock, gatewayId, instanceId, networkInterfaceId, routeTableId);
	}
	
	@Test
	public void deleteRouteTest() {
		String routeTableId = "rtb-328b9250";
		String destinationCidrBlock = "0.0.0.0/0";
		client.deleteRoute(routeTableId, destinationCidrBlock);
	}
	
	@Test
	public void getNetworkACLTest() {
		String networkAclId = "acl-0dd6cf6f";
		NetworkAcl acl = client.getNetworkACL(networkAclId);
		System.out.println(acl);
	}
	
	@Test
	public void getNetworkACLs1Test() {
		List<NetworkAcl> acls = client.getNetworkACLs();
		for(NetworkAcl acl : acls) {
			System.out.println(acl);
		}
	}
	
	@Test
	public void getNetworkACLs2Test() {
		Collection<String> networkAclIds = new ArrayList<String>();
		networkAclIds.add("acl-063db56f");
		networkAclIds.add("acl-0dd6cf6f");
		List<NetworkAcl> acls = client.getNetworkACLs(networkAclIds);;
		for(NetworkAcl acl : acls) {
			System.out.println(acl);
		}
	}
	
	@Test
	public void createNetworkACLTest() {
		String vpcId = "vpc-e6e1ff84";
		NetworkAcl acl = client.createNetworkACL(vpcId);
		System.out.println(acl);
	}
	
}
