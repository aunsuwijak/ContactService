package contact.service;

import java.util.List;

import contact.entity.Contact;
/**
 * Interface defines the operations required by 
 * a DAO for Contacts.
 * 
 * @author Suwijack Chaipipat
 */
public interface ContactDao {

	public abstract Contact find(long id);

	public abstract List<Contact> findAll();
	
	public abstract List<Contact> findByTitle(String match);

	public abstract boolean delete(long id);

	public abstract boolean save(Contact contact);

	public abstract boolean update(Contact update);

}