package skybiz.com.posoffline.m_Ipay88.Service;


import skybiz.com.posoffline.m_Ipay88.WebServiceServer.Client.GatewayService_SOAPClient;

/**
 * The Number Conversion Web Service, implemented with Visual DataFlex, 
 * provides functions that convert numbers into words or dollar amounts.
 * 
 * http://www.dataaccess.com/webservicesserver/numberconversion.wso
 * 
 * @author bulldog
 *
 */
public class GatewayServiceClient {
	
	// target endpoint
	public static String clientServiceURL = "http://www.mobile88.com/ePayment/WebService/MHGatewayService/GatewayService.svc";
	
	private static volatile GatewayService_SOAPClient client = null;
	
	// get a shared client
	public static GatewayService_SOAPClient getSharedClient() {
		if (client == null) {
			synchronized(GatewayService_SOAPClient.class) {
				if (client == null) {
					client = new GatewayService_SOAPClient();
					//client.addUrlParam("SOAPAction","https://www.mobile88.com/IGatewayService/EntryPageFunctionality");
					client.setEndpointUrl(clientServiceURL);
				}
			}
		}
		return client;
	}

}
