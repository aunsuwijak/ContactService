package contact.service;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.ContactMain;
/**
 * Web service test - test ContactDao through HTTP request.
 * 
 * @author Suwijak Chaipipat
 * @version 7.10.2014
 */
public class WebServiceTest {
	private static String serviceUrl;
	private HttpClient client;
	
	@BeforeClass
	public static void doFirst( ) throws Exception {
		serviceUrl = ContactMain.startServer( 8080 );
		System.out.println( serviceUrl );
	} 
	 
	@Before
	public void beforeTest() {
		client = new HttpClient();
		
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void afterTest() {
		try {
			client.stop();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void doLast( ) throws Exception {
		ContactMain.stopServer();
	}
	
	@Test
	public void testGetSuccess() {
		ContentResponse response = null;
		
		try {
			response = client.GET(serviceUrl + "/1000");
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		
		// Get success
		assertEquals(Response.Status.OK.getStatusCode() , response.getStatus() );	
	}
	
	@Test
	public void testGetFailed() {
		ContentResponse response = null;
		
		// Assume that number of contact is less than Long.MAX_VALUE
		try {
			response = client.GET(serviceUrl + "/" + -100);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		
		// Get failed
		assertEquals(Response.Status.NOT_FOUND.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testPostSuccess() {
		ContentResponse response = null;
		
		StringContentProvider content = new StringContentProvider("<contact>" +
				"<title>contact nickname or title</title>" +
				"<name>contact's full name</name>" +
				"<email>contact's email address</email>" +
				"<phoneNum>contact's telephone number</phoneNum>"+
				"</contact>");
		
		Request request = client.newRequest(serviceUrl);
		request = request.content(content, "application/xml");
		request = request.method(HttpMethod.POST);
		
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}	
		
		// Post success
		assertEquals(Response.Status.CREATED.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testPostFailed() {
		ContentResponse response = null;
		
		StringContentProvider content = new StringContentProvider("<contact id=\"1234\">" +
				"<title>contact nickname or title</title>" +
				"<name>contact's full name</name>" +
				"<email>contact's email address</email>" +
				"<phoneNum>contact's telephone number</phoneNum>"+
				"</contact>");
		
		Request request = client.newRequest(serviceUrl);
		request = request.content(content, "application/xml");
		request = request.method(HttpMethod.POST);
		
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		
		content = new StringContentProvider("<contact id=\"1234\">" +
				"<title>contact nickname or title</title>" +
				"<name>contact's full name</name>" +
				"<email>contact's email address</email>" +
				"<phoneNum>contact's telephone number</phoneNum>"+
				"</contact>");
		
		request = client.newRequest(serviceUrl);
		request = request.content(content, "application/xml");
		request = request.method(HttpMethod.POST);
		
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		
		// Post failed
		assertEquals( Response.Status.CONFLICT.getStatusCode() , response.getStatus() );
	}
	
	@Test 
	public void testPutSuccess() {
		ContentResponse response = null;
		
		StringContentProvider content = new StringContentProvider("<contact id=\"1234\">" +
				"<title>contact nickname</title>" +
				"<name>contact's full name</name>" +
				"<email>contact's email address</email>" +
				"<phoneNum>contact's telephone number</phoneNum>"+
				"</contact>");
		
		
		Request request = client.newRequest(serviceUrl + "/1234");
		request = request.content(content, "application/xml");
		request = request.method(HttpMethod.PUT);
		
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		
		// Put success
		assertEquals( Response.Status.OK.getStatusCode() , response.getStatus() );
	}
	
	@Test 
	public void testPutFailed() {
		ContentResponse response = null;
		
		
		StringContentProvider content = new StringContentProvider("<contact id=\"" + -100 + "\">" +
				"<title>contact nickname or title</title>" +
				"<name>contact's full name</name>" +
				"<email>contact's email address</email>" +
				"<phoneNum>contact's telephone number</phoneNum>"+
				"</contact>");
		
		Request request = client.newRequest(serviceUrl + "/1");
		request = request.content(content, "application/xml");
		request = request.method(HttpMethod.PUT);
		
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		
		// Put failed
		assertEquals( Response.Status.NOT_FOUND.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testDeleteSuccess() {
		ContentResponse response = null;
		
		StringContentProvider content = new StringContentProvider("<contact id=\"1800\">" +
				"<title>contact nickname or title</title>" +
				"<name>contact's full name</name>" +
				"<email>contact's email address</email>" +
				"<phoneNum>contact's telephone number</phoneNum>"+
				"</contact>");
		
		Request request = client.newRequest(serviceUrl);
		request = request.content(content, "application/xml");
		request = request.method(HttpMethod.POST);
		
		try {
			request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		
		request = client.newRequest(serviceUrl + "/1800");
		request = request.method(HttpMethod.DELETE);
		
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		
		// Delete success
		assertEquals( Response.Status.OK.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testDeleteFailed() {
		ContentResponse response = null;

		Request request = client.newRequest(serviceUrl + "/1");
		request = request.method(HttpMethod.DELETE);
		
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		
		// Delete failed
		assertEquals( Response.Status.NOT_FOUND.getStatusCode() , response.getStatus() );
	}
}