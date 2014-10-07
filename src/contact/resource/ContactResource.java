package contact.resource;

import java.net.URI;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;
import contact.service.mem.MemDaoFactory;

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
	
	private DaoFactory mdf;
	
	private CacheControl cc;
	
	public ContactResource() {
		DaoFactory.setInstance( new MemDaoFactory() );
		mdf = DaoFactory.getInstance();
		cc = new CacheControl();
		cc.setMaxAge(2345);
		cc.setPrivate(true);
	}
	
	@GET
	@Produces( MediaType.APPLICATION_XML )
	public Response getContacts(@QueryParam("title") String title) {
		
		ContactDao cd = mdf.getContactDao();
		
		List<Contact> contacts = null;
		
		if ( title == null ) 
			contacts = cd.findAll();
		else
			contacts = cd.findByTitle(title);
		
		GenericEntity<List<Contact>> entity = new GenericEntity<List<Contact>>( contacts ) {};
		
		if ( contacts == null ) return Response.status(Response.Status.NOT_FOUND).build();
		
		return Response.ok(entity).build();
	}
	
	@GET
	@Path("{id}")
	@Produces( MediaType.APPLICATION_XML )
	public Response getContact(@PathParam("id") long id) {
		
		ContactDao cd = mdf.getContactDao();
		
		Contact contact = cd.find( id );

		if ( contact == null ) return Response.status(Response.Status.NOT_FOUND).build();
		
		return Response.ok(contact).build();
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_XML )
	public Response postContact( JAXBElement<Contact> element , @Context UriInfo uriInfo ) {
		
		ContactDao cd = mdf.getContactDao();
		
		Contact contact = element.getValue();
		

		if ( cd.find( contact.getId() ) != null )
			return Response.status(Response.Status.CONFLICT).build();
		
		if ( cd.save(contact) ) {
			URI uri = uriInfo.getAbsolutePathBuilder().path(""+contact.getId()).build();
			
			contact = cd.find(contact.getId());

			EntityTag etag = new EntityTag( contact.hashCode() + "" );
			
			return Response.created(uri).cacheControl(cc).tag(etag).build();
		}
		
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes( MediaType.APPLICATION_XML )
	public Response putContact(@HeaderParam("If-Match") String match ,
			@HeaderParam("If-None-Match") String noneMatch , @PathParam("id") long id , JAXBElement<Contact> element , @Context UriInfo uriInfo  ) {
		
		ContactDao cd = mdf.getContactDao();
		
		Contact contact = cd.find( id );
		
		if ( contact == null ) 
			return Response.status( Response.Status.NOT_FOUND )
					.build();
			
		EntityTag etag = new EntityTag( contact.hashCode() + "" );
	
		if ( match != null && noneMatch != null ) {
			return Response.status(Response.Status.PRECONDITION_FAILED)
					.build();	
		}
		else {
			if ( match != null ) {
				match = match.replace("\"", "");
				if ( !match.equals( etag.getValue() ) )
					return Response.status(Response.Status.PRECONDITION_FAILED)
							.build();
			}
			else if ( noneMatch != null ) {
				noneMatch = noneMatch.replace("\"", "");
				if ( noneMatch.equals( etag.getValue() ) )
					return Response.status(Response.Status.PRECONDITION_FAILED)
							.build();
			}
		}

		contact = element.getValue();
		
		contact.setId( id );
			
		if ( cd.update(contact) )
			return Response.ok(contact).cacheControl(cc).tag(etag).build();
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response putContact(@HeaderParam("If-Match") String match ,
			@HeaderParam("If-None-Match") String noneMatch , @PathParam("id") long id){
		
		ContactDao cd = mdf.getContactDao();
		
		Contact contact = cd.find( id );
		
		if ( contact == null ) return Response.status(Response.Status.NOT_FOUND)
				.build();
		
		EntityTag etag = new EntityTag( contact.hashCode() + "" );
		
		if ( match != null && noneMatch != null ) {
			return Response.status(Response.Status.PRECONDITION_FAILED)
					.build();	
		}
		else {
			if ( match != null ) {
				match = match.replace("\"", "");
				if ( !match.equals( etag.getValue() ) )
					return Response.status(Response.Status.PRECONDITION_FAILED)
							.build();
			}
			else if ( noneMatch != null ) {
				noneMatch = noneMatch.replace("\"", "");
				if ( noneMatch.equals( etag.getValue() ) )
					return Response.status(Response.Status.PRECONDITION_FAILED)
							.build();
			}
		}
		
		if ( cd.delete( id ) )
			return Response.ok(contact).cacheControl(cc).tag(etag).build();
		return Response.status(Response.Status.BAD_REQUEST).build();
		
	}
}
