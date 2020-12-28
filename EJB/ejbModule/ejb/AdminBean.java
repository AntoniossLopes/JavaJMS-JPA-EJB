package ejb;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jpa.AdminMessages;
import jpa.PublicationMeta3;
import jpa.Users;

/**
 * Session Bean implementation class AdminBean
 */
@Stateless
@LocalBean
public class AdminBean implements AdminBeanRemote {

	@Inject
	private JMSContext context;
	
	@Resource(mappedName = "java:jboss/exported/jms/topic/playTopic")
	private Destination destination;
	
	@PersistenceContext(name = "Persistence")
	private EntityManager em;
	
    /**
     * Default constructor. 
     */
    public AdminBean() {
        // TODO Auto-generated constructor stub
    }
    
    public void RegisterUser(String nome, String password) {
    	Users novo = new Users(nome,password);
    	em.persist(novo);
    	
    }
    
    public void addPublication(String name, String type, String date, String user) {
    	String jpqlU = "SELECT DISTINCT r FROM Users r WHERE r.name = :name";
		
		TypedQuery<Users> typedQueryU = em.createQuery(jpqlU, Users.class);
		typedQueryU.setParameter("name", user);
		Users mylistU = typedQueryU.getSingleResult();
    	
		name = name.replace("_", " ");
		
    	PublicationMeta3 nova = new PublicationMeta3(name, date, type);
    	nova.setAuthor(mylistU);
    	mylistU.addPublication(nova);

    	em.persist(nova);
    	em.persist(mylistU);  	
    }
    
    public void updatePublication(String name, String type, String date, String user) {	
		String jpqlP = "SELECT DISTINCT p FROM PublicationMeta3 p WHERE p.name = :name";
		
		TypedQuery<PublicationMeta3> typedQueryP = em.createQuery(jpqlP, PublicationMeta3.class);
		
		name = name.replace("_", " ");
		
		typedQueryP.setParameter("name", name);
		PublicationMeta3 mylistP = typedQueryP.getSingleResult();
    	
		mylistP.setDate(date);
		mylistP.setType(type);

    	em.persist(mylistP);
    }
    
    public void removePublication(String name) {
    	String jpqlP = "SELECT DISTINCT p FROM PublicationMeta3 p WHERE p.name = :name";
		
		TypedQuery<PublicationMeta3> typedQueryP = em.createQuery(jpqlP, PublicationMeta3.class);
		
		name = name.replace("_", " ");
		
		typedQueryP.setParameter("name", name);
		PublicationMeta3 mylistP = typedQueryP.getSingleResult();

		em.remove(mylistP);
    }
    
    public String getAllUsers() {
    	List<Users> users;
    	
    	String jpqlU = "SELECT DISTINCT r FROM Users r LEFT JOIN FETCH r.publications p";

		TypedQuery<Users> typedQueryU = em.createQuery(jpqlU, Users.class);
		List<Users> mylistU = typedQueryU.getResultList();
						
		users = mylistU;
		String x = "";
		for(int i = 0 ; i < users.size(); i++) {
			x = x + users.get(i).getName() + " " + users.get(i).getActive() + "\n";
		}
		return x;
    }
    
    public String getAllPubs() {
    	List<PublicationMeta3> publications;
    	  
    	String jpqlP = "SELECT DISTINCT s FROM PublicationMeta3 s";
				
		TypedQuery<PublicationMeta3> typedQueryP = em.createQuery(jpqlP, PublicationMeta3.class);
		List<PublicationMeta3> mylistP = typedQueryP.getResultList();
				
		publications = mylistP;
		
		String x = "";
		for(int i = 0 ; i < mylistP.size(); i++) {
			x =  x + mylistP.get(i).getName() + "\n";
		}
		
		return x;
    }
    
    public void ApproveMessages(String name) {
    	String jpqlP = "SELECT DISTINCT p FROM AdminMessages p WHERE p.message = :message";
		
		TypedQuery<AdminMessages> typedQueryP = em.createQuery(jpqlP, AdminMessages.class);
		typedQueryP.setParameter("message", name);
		AdminMessages mylistP = typedQueryP.getSingleResult();
		
		List<Users> lista;
    	
    	String jpqlU = "SELECT DISTINCT r FROM Users r LEFT JOIN FETCH r.publications p";

		TypedQuery<Users> typedQueryU = em.createQuery(jpqlU, Users.class);
		List<Users> mylistU = typedQueryU.getResultList();
						
		lista = mylistU;
		
		String[] parser = mylistP.getMessage().split(" ");
		if(parser[0].equals("REGISTER")) {
    		RegisterUser(parser[1],parser[2]);
    		//send message to async queue
    		try {
				sendMessages("USER ACCEPTED BY ADMIN",parser[1]);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else {
    		
    		try {
    			
    			if(parser[0].equals("ADD_PUBLICATION")) {
    				addPublication(parser[1],parser[2],parser[3],parser[4]);
            	}
            	else if(parser[0].equals("UPDATE_PUBLICATION")) {
            		updatePublication(parser[1],parser[2],parser[3],parser[4]);
            	}
            	else if(parser[0].equals("REMOVE_PUBLICATION")) {
            		removePublication(parser[1]);
            	}
    			
    			for(int i = 0 ; i < lista.size(); i++) {
    				sendMessages("DATABASE INFO WAS UPDATED",lista.get(i).getName());
    			}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		//send message to async queue
    	}

		em.remove(mylistP);
    }
    
    public void RejectMessages(String name) {
    	String jpqlP = "SELECT DISTINCT p FROM AdminMessages p WHERE p.message = :message";
		
		TypedQuery<AdminMessages> typedQueryP = em.createQuery(jpqlP, AdminMessages.class);
		typedQueryP.setParameter("message", name);
		AdminMessages mylistP = typedQueryP.getSingleResult();
		
		//send message to async queue saying that registration wasnt accepted
		
		try {
			String[] parser = mylistP.getMessage().split(" ");
			if(parser[0].equals("REGISTER")) {
				sendMessages("USER DENIED BY ADMIN",parser[1]);
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		em.remove(mylistP);
    }
    
    public String GetPub(String name) {
    	List<Users> users;
    	List<PublicationMeta3> publications;
    	
    	name = name.replace("_", " ");
    	
    	String jpqlU = "SELECT DISTINCT r FROM Users r LEFT JOIN FETCH r.publications p";
		String jpqlP = "SELECT DISTINCT s FROM PublicationMeta3 s WHERE s.name = :name";
		
		TypedQuery<Users> typedQueryU = em.createQuery(jpqlU, Users.class);
		List<Users> mylistU = typedQueryU.getResultList();
		
		TypedQuery<PublicationMeta3> typedQueryP = em.createQuery(jpqlP, PublicationMeta3.class);
		typedQueryP.setParameter("name", name);
		List<PublicationMeta3> mylistP = typedQueryP.getResultList();
				
		users = mylistU;
		publications = mylistP;
		
		String x = "";
		for(int i = 0 ; i < mylistP.size();i++) {
			x = x + mylistP.get(i).getName() + "\n";
			x = x + mylistP.get(i).getType() + "\n";
			x = x + mylistP.get(i).getDate() + "\n";
			x = x + "Author:" + mylistP.get(i).getAuthor().getName() + "\n";
		}
		return x;
    }
    
    private void sendMessages(String message, String user) throws JMSException {
    	JMSProducer prod = context.createProducer();
    	TextMessage reply =context.createTextMessage();
    	reply.setText(message);
    	
    	reply.setStringProperty("user", user);
    	
    	prod.send(destination, reply);
    	System.out.println("Sent reply to: "+destination + " "+reply.getStringProperty("user"));
    }
    
    public void DisableUser(String username) {
    	List<Users> users;
    	
    	String jpqlU = "SELECT DISTINCT r FROM Users r WHERE r.name = :name";

		TypedQuery<Users> typedQueryU = em.createQuery(jpqlU, Users.class);
		typedQueryU.setParameter("name", username);
		Users mylistU = typedQueryU.getSingleResult();
		if(mylistU != null) {
			mylistU.setActive(false);
			em.persist(mylistU);	
		}
		
		try {
			sendMessages("USER PERMISSION DENIED BY ADMIN", username);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		//envia mensagem para a async queue para a pessoa em questao a dizer que foi desativado
		
    }
    
    public List<AdminMessages> getAllTaks() {
    	List<AdminMessages> messages;
  	  
    	String jpqlP = "SELECT DISTINCT s FROM AdminMessages s";
				
		TypedQuery<AdminMessages> typedQueryP = em.createQuery(jpqlP, AdminMessages.class);
		List<AdminMessages> mylistP = typedQueryP.getResultList();
				
		messages = mylistP;
			
		return messages;
    }
}
