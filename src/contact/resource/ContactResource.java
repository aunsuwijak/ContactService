package contact.resource;

import java.net.URI;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;
import contact.service.jpa.JpaDaoFactory;
import contact.service.mem.MemDaoFactory;
import contact.service.mem.MemContactDao;

/**
 * ContactResource provides RESTful web resources using JAX-RS
 * annotations to map requests to request handling code,
 * and to inject resources into code.
 * 
 * @author Suwijak Chaipipat 5510545046
 * @version 16.9.2014
 */
@Singleton
@Path("/contacts")
public class ContactResource {
	
	private DaoFactory mdf = MemDaoFactory.getInstance();
	
	public ContactResource() {
		
	}
	
	@GET
	@Produces( MediaType.APPLICATION_XML )
	public Response getContacts(@QueryParam("title") String title) {
		
		ContactDao cd = mdf.getContactDao();
		
		if ( title == null ) 
			return getContacts();
		
		List<Contact> contacts = cd.findByTitle(title);
		
		GenericEntity<List<Contact>> entity = new GenericEntity<List<Contact>>(contacts){};
		
		return Response.ok(entity).build();
	}
	
	public Response getContacts() {
		
		ContactDao cd = mdf.getContactDao();
		
		List<Contact> contacts = cd.findAll();
		
		GenericEntity<List<Contact>> entity = new GenericEntity<List<Contact>>(contacts){};
		
		return Response.ok(entity).build();
	}
	
	@GET
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML )
	public Response getContact(@PathParam("id") String id) {
		
		ContactDao cd = mdf.getContactDao();
		
		Contact contact = cd.find( Integer.parseInt(id) );

		if ( contact == null ) return Response.status(Response.Status.NO_CONTENT).build();
		return Response.ok(contact).build();
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_XML )
	public Response postContact( JAXBElement<Contact> element , @Context UriInfo uriInfo ) {
		
		ContactDao cd = mdf.getContactDao();
		
		Contact contact = element.getValue();
		
		if ( cd.save(contact) ) {
			URI uri = uriInfo.getAbsolutePathBuilder().path(""+contact.getId()).build();
			return Response.created(uri).build();
		}
		
		if ( cd.find( contact.getId() ) != null )
			return Response.status(Response.Status.CONFLICT).build();
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes( MediaType.APPLICATION_XML )
	public Response putContact(@PathParam("id") String id , JAXBElement<Contact> element , @Context UriInfo uriInfo  ) {
		
		ContactDao cd = mdf.getContactDao();
		
		Contact contact = element.getValue();
		
		contact.setId( Integer.parseInt(id) );
		
		if ( cd.update(contact) )
			return Response.ok( contact ).build();
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response putContact(@PathParam("id") String id){
		
		ContactDao cd = mdf.getContactDao();
		
		if ( cd.delete( Integer.parseInt(id) ) )
			return Response.ok().build();
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
