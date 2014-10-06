package contact.service.jpa;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import contact.service.ContactDao;
import contact.service.DaoFactory;

public class JpaDaoFactory implements DaoFactory{

	private static final String PERSISTENCE_UNIT = "contacts";
	/** instance of the entity DAO */
	private ContactDao contactDao;
	private final EntityManagerFactory emf;
	private EntityManager em;
	private static Logger logger;
	private static JpaDaoFactory factory;
	
	static {
		logger = Logger.getLogger(JpaDaoFactory.class.getName());
	}
	
	public JpaDaoFactory() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		em = emf.createEntityManager();
		contactDao = new JpaContactDao( em );
	}

// getInstance() should be in the abstract factory and not repeated here
	public static JpaDaoFactory getInstance() {
		if ( factory == null ) {
			factory = new JpaDaoFactory();
		}
		return factory;
	}
	
	@Override
	public ContactDao getContactDao() {
		return contactDao;
	}
	
	@Override
	public void shutdown() {
		try {
			if (em != null && em.isOpen()) em.close();
			if (emf != null && emf.isOpen()) emf.close();
		} catch (IllegalStateException ex) {
			logger.warning(ex.getMessage());
		}
	}

}
