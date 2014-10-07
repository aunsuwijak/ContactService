package contact.service.mem;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.entity.Contacts;
import contact.service.DaoFactory;



/**
 * Manage instances of Data Access Objects (DAO) used in the app.
 * This enables you to change the implementation of the actual ContactDao
 * without changing the rest of your application.
 * 
 * @author jim
 */
public class MemDaoFactory extends DaoFactory {
	private MemContactDao daoInstance;
	
	public MemDaoFactory() {
		daoInstance = new MemContactDao();
		try {
			loadFile( "/tmp/ContactsSevicePersistence.xml" );
		} catch (Exception e) {}
	}
	
	public MemContactDao getContactDao() {
		return daoInstance;
	}

	@Override
	public void shutdown() {
		List<Contact> contacts = daoInstance.findAll();
		Contacts exportContacts = new Contacts();
		exportContacts.setContacts( contacts );

		try {
			JAXBContext context = JAXBContext.newInstance( Contacts.class );
			File outputFile = new File( "/tmp/ContactsSevicePersistence.xml" );
			Marshaller marshaller = context.createMarshaller();	
			marshaller.marshal( exportContacts, outputFile );
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
	}
	
	public void loadFile(String path) {
		File file = null;
		
		file = new File(path) ;
			
		JAXBContext context;
		
		Unmarshaller umx;
		
		Contacts contacts = null;
		
		try {
			context = JAXBContext.newInstance( Contacts.class );
			umx = context.createUnmarshaller();
			contacts = (Contacts)umx.unmarshal(file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		List<Contact> list = contacts.getContacts();
		
		for ( Contact c : list )
			daoInstance.save(c);
	}
}
