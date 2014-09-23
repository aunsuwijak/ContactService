package contact.service;

public interface DaoFactory {
	
	public abstract ContactDao getContactDao();
	
	public abstract void shutdown();
}