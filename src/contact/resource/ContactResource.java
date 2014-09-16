package contact.resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import contact.entity.Contact;
import contact.service.ContactDao;

/**
 * ContactResource provides RESTful web resources using JAX-RS
 * annotations to map requests to request handling code,
 * and to inject resources into code.
 * 
 * @author suwijak chaipipat
 * @version 16.9.2014
 */
@Singleton
@Path("/")
public class ContactResource {

	@Context
	UriInfo uriInfo;
	
	ContactDao cd = new ContactDao();
	
	public ContactResource() {
		
	}
	
	
	@GET
	@Produces( MediaType.APPLICATION_XML )
	public Response getContacts(@QueryParam("q") String q) {
		
		List<Contact> contacts = cd.findAll();
		
		if ( q == null ) 
			return getContacts();
	
		List<Contact> query_contacts = new ArrayList<Contact>();
			
		for ( Contact contact : contacts ) {
			if ( contact.getEmail().contains(q) ) query_contacts.add( contact );
		}
		
		GenericEntity<List<Contact>> entity = new GenericEntity<List<Contact>>(query_contacts){};
		
		return Response.ok(entity).build();
	}
	
	public Response getContacts() {
		
		List<Contact> contacts = cd.findAll();
		
		GenericEntity<List<Contact>> entity = new GenericEntity<List<Contact>>(contacts){};
		
		return Response.ok(entity).build();
	}
	
	@GET
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML )
	public Response getContact(@PathParam("id") String id) {
		
		Contact contact = cd.find( Integer.parseInt(id) );

		return Response.ok(contact).build();
	}
	
	@POST
	@Produces( MediaType.APPLICATION_XML )
	public Response postContact(@FormParam("title") String title, @FormParam("name") String name, @FormParam("email") String email) {
		Contact contact = new Contact(title, name, email);
		if ( cd.save(contact) )
			return Response.ok( contact ).build();
		return Response.notModified("Can't create contact.").build();
	}
	
	@PUT
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML )
	public Response putContact(@PathParam("id") String id, @FormParam("title") String title, @FormParam("name") String name, @FormParam("email") String email) {
		Contact contact = new Contact(title, name, email);
		contact.setId( Integer.parseInt(id) );
		if ( cd.update(contact) )
			return Response.ok( contact ).build();
		return Response.notModified("Can't update contact.").build();
	}
	
	@DELETE
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML )
	public Response putContact(@PathParam("id") String id){
		if ( cd.delete( Integer.parseInt(id) ) )
			return Response.ok("Successful delete contact.").build();
		return Response.notModified("Can't delte contact.").build();
	}
}
