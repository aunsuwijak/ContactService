package contact.service;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contact.ContactMain;
import contact.service.mem.MemDaoFactory;

public class ETagTest {
	private static String serviceUrl;
	private DaoFactory factory = MemDaoFactory.getInstance();
	private HttpClient client;
	
	@Before
	public void doFirst( ) throws Exception {
		serviceUrl = ContactMain.startServer( 8080 );
		
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
		factory.shutdown();
	}
	
	@Test
	public void testGet() {
			Request request = client.newRequest( serviceUrl );
			request = request.method( HttpMethod.GET );
			
			ContentResponse response = null;
			
			try {
				response = request.send();
			} catch (InterruptedException | TimeoutException
					| ExecutionException e) {
				e.printStackTrace();
			}
			
			assertEquals( Response.Status.OK.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testPost() {
		StringContentProvider content = new StringContentProvider("<contact>" +
			"<title>contact nickname or title</title>" +
			"<name>contact's full name</name>" +
			"<email>contact's email address</email>" +
			"<phoneNum>contact's telephone number</phoneNum>"+
			"</contact>");
	
		Request request = client.newRequest( serviceUrl );
		request = request.method( HttpMethod.POST );
		request = request.content( content , "application/xml" );
			
		ContentResponse response = null;
			
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
			
		assertEquals( Response.Status.CREATED.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testPostEtag() {
		StringContentProvider content = new StringContentProvider("<contact>" +
			"<title>contact nickname or title</title>" +
			"<name>contact's full name</name>" +
			"<email>contact's email address</email>" +
			"<phoneNum>contact's telephone number</phoneNum>"+
			"</contact>");
		
		Request request = client.newRequest( serviceUrl );
		request = request.method( HttpMethod.POST );
		request = request.content( content , "application/xml" );
		
		ContentResponse response = null;
			
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}

		assertNotNull( response.getHeaders().get( HttpHeader.ETAG ) );
	}
	
	@Test
	public void testPutIfMatch() {
		StringContentProvider content = new StringContentProvider("<contact id=\"1235\">" +
			"<title>contact nickname or title</title>" +
			"<name>contact's full name</name>" +
			"<email>contact's email address</email>" +
			"<phoneNum>contact's telephone number</phoneNum>"+
			"</contact>");
	
		Request request = client.newRequest( serviceUrl );
		request = request.method( HttpMethod.POST );
		request = request.content( content , "application/xml" );
			
		ContentResponse response = null;
			
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
		
		String etag = response.getHeaders().get( HttpHeader.ETAG );
		
		assertNotNull( etag );
		
		content = new StringContentProvider("<contact id=\"1235\">" +
				"<title>contact nickname or title</title>" +
				"<name>contact's name</name>" +
				"<email>contact's email address</email>" +
				"<phoneNum>contact's telephone number</phoneNum>"+
				"</contact>");
		
		request = client.newRequest( serviceUrl + "/1235" );
		request = request.header( HttpHeader.IF_MATCH , etag );
		request = request.method( HttpMethod.PUT );
		request = request.content( content , "application/xml" );
				
		response = null;
				
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
			
		assertEquals( Response.Status.OK.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testPutIfNoneMatch() {
		StringContentProvider content = new StringContentProvider("<contact id=\"1236\">" +
			"<title>contact nickname or title</title>" +
			"<name>contact's full name</name>" +
			"<email>contact's email address</email>" +
			"<phoneNum>contact's telephone number</phoneNum>"+
			"</contact>");
	
		Request request = client.newRequest( serviceUrl );
		request = request.method( HttpMethod.POST );
		request = request.content( content , "application/xml" );
			
		ContentResponse response = null;
			
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
		
		String etag = response.getHeaders().get( HttpHeader.ETAG );
		
		assertNotNull( etag );
		
		content = new StringContentProvider("<contact id=\"1236\">" +
				"<title>contact nickname or title</title>" +
				"<name>contact's name</name>" +
				"<email>contact's email address</email>" +
				"<phoneNum>contact's telephone number</phoneNum>"+
				"</contact>");
		
		request = client.newRequest( serviceUrl + "/1236" );
		request = request.header( HttpHeader.IF_NONE_MATCH , "gregeg" );
		request = request.method( HttpMethod.PUT );
		request = request.content( content , "application/xml" );
				
		response = null;
				
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
			
		assertEquals( Response.Status.OK.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testDeleteIfMatch() {
		StringContentProvider content = new StringContentProvider("<contact id=\"1237\">" +
			"<title>contact nickname or title</title>" +
			"<name>contact's full name</name>" +
			"<email>contact's email address</email>" +
			"<phoneNum>contact's telephone number</phoneNum>"+
			"</contact>");
	
		Request request = client.newRequest( serviceUrl );
		request = request.method( HttpMethod.POST );
		request = request.content( content , "application/xml" );
			
		ContentResponse response = null;
			
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
		
		String etag = response.getHeaders().get( HttpHeader.ETAG );
		
		assertNotNull( etag );
		
		request = client.newRequest( serviceUrl + "/1237" );
		request = request.header( HttpHeader.IF_MATCH , etag );
		request = request.method( HttpMethod.DELETE );
				
		response = null;
				
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
			
		assertEquals( Response.Status.OK.getStatusCode() , response.getStatus() );
	}
	
	@Test
	public void testDeleteIfNoneMatch() {
		StringContentProvider content = new StringContentProvider("<contact id=\"1238\">" +
			"<title>contact nickname or title</title>" +
			"<name>contact's full name</name>" +
			"<email>contact's email address</email>" +
			"<phoneNum>contact's telephone number</phoneNum>"+
			"</contact>");
	
		Request request = client.newRequest( serviceUrl );
		request = request.method( HttpMethod.POST );
		request = request.content( content , "application/xml" );
			
		ContentResponse response = null;
			
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
		
		String etag = response.getHeaders().get( HttpHeader.ETAG );
		
		assertNotNull( etag );
		
		request = client.newRequest( serviceUrl + "/1238" );
		request = request.header( HttpHeader.IF_NONE_MATCH , "gregeg" );
		request = request.method( HttpMethod.DELETE );
				
		response = null;
				
		try {
			response = request.send();
		} catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			e.printStackTrace();
		}
			
		assertEquals( Response.Status.OK.getStatusCode() , response.getStatus() );
	}
}