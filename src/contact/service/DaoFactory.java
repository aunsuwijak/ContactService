package contact.service;

import contact.service.mem.MemDaoFactory;

/**
 * DaoFactory - an abstract class use to create an instance of factory class. 
 * 
 * @author Suwijak Chaipipat 5510545046
 * @version 7.10.2014
 */
public abstract class DaoFactory {
	
	private static DaoFactory instance;
	
	public static DaoFactory getInstance() {
		if ( instance == null ) setInstance( new MemDaoFactory() );
		return instance;
	}
	
	public static void setInstance(DaoFactory factory) {
		instance = factory;
	}
	
	/** Get contact database accs*/
	public abstract ContactDao getContactDao();
	
	public abstract void shutdown();
}