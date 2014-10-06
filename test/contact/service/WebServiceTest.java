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
import org.junit.Before;
import org.junit.Test;

import contact.ContactMain;
import contact.entity.Contact;
import contact.service.mem.MemDaoFactory;

//No Javadoc. 
public class WebServiceTest {
	private static String serviceUrl;
	private DaoFactory factory;
	private HttpClient client;
	
	@Before
	public void doFirst( ) throws Exception {
		serviceUrl = ContactMain.startServer( 8080 );

//Don't assume you know the URL. Get it from ContactMain.
		serviceUrl = "http://localhost:8080/contacts";
		
		client = new HttpClient();
		
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	} 
	 
	@After
	public void doLast( ) throws Exception {
		ContactMain.stopServer();
	}
	
	@Test
	public void testGetSuccess() {
		ContentResponse response = null;
// Shouldn't directly access dao here. Test through the web service interface only.
		factory = MemDaoFactory.getInstance();
		ContactDao cd = factory.getContactDao();
		
		cd.save( new Contact("contact1", "Joe Contact", "joe@microsoft.com", "0812345678") );
// this *assumes* the DAO will assign id of 1000.  Too much coupling.		
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
// This *assumes* the DAO doesn't have contact id 12345.		
		try {
			response = client.GET(serviceUrl + "/12345");
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
// Should be NOT_FOUND
		// Get failed
		assertEquals(Response.Status.NO_CONTENT.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testPostSuccess() {
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
		
		StringContentProvider content = new StringContentProvider("<contact id=\"1400\">" +
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
		assertEquals( Response.Status.BAD_REQUEST.getStatusCode() , response.getStatus() );
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
		assertEquals( Response.Status.BAD_REQUEST.getStatusCode() , response.getStatus() );
	}
}