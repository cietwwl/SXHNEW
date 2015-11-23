package com.joyveb.tlol.pay.util;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

public class HttpRequest {

	
	public static String request(String url) throws Exception {
		 HttpClient httpClient = new HttpClient();  
		 httpClient.setHttpConnectionFactoryTimeout(10*100000);
		 httpClient.setConnectionTimeout(10*100000);
		 httpClient.setTimeout(10*100000);
		 GetMethod getMethod = new GetMethod(url);
		 getMethod.setFollowRedirects(true);
		 String body = null;
		 getMethod.setRequestHeader(new Header("User-Agent","Mozilla/4.47 (compatible; MSIE 6.0; Windows 98)"));
		 int statusCode = httpClient.executeMethod(getMethod);
		 if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
			 body = getMethod.getResponseBodyAsString();   
		 }else {
			 throw new Exception("���������վ��ʱ�޷���");
			 //System.out.println("���������վ��ʱ�޷���");
		 } 
		return body;
	}
	
	public static void main(String[] args) {
		
		String url = "http://pay.sxmobi.com/pmc/charge.aspx?P_MerId=1&P_Order=1&P_BNum=10000&P_Amt=300&P_VerifyAmt=0&P_MP=&P_CardAmt=300&P_CardNo=111&P_CardPwd=222&P_FrpId=SZX&Cert=02b4c076d1a8c86542b4c076d1a8c1w3";
		try {
			String result = HttpRequest.request(url);
			System.out.println("+++++++++++++++++++++++"+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
