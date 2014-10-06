package contact.service;
//No Javadoc. Next time you do this your entire assignment gets score 0.
// this should be abstract class and contain static getInstance() method.
public interface DaoFactory {
	
	public abstract ContactDao getContactDao();
	
	public abstract void shutdown();
}