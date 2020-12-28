package ejb;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jpa.AdminMessages;
import jpa.PublicationMeta3;
import jpa.Users;

/**
 * Message-Driven Bean implementation class for: MessageBean
 */
@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				@ActivationConfigProperty(propertyName="destination", propertyValue = "jms/queue/playQueue")
		})
public class MessageBean implements MessageListener {

    /**
     * Default constructor. 
     */
	
	@Inject
	private JMSContext jcontext;
	
	@PersistenceContext(name = "Persistence")
	private EntityManager em;
	
    public MessageBean() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     * separar as strings que receber para ver qual e o tipo de mensagem que envia
     * Mensagens compostas por : Function parametro parametro .....
     */
    public void onMessage(Message message) {
        /// TODO Auto-generated method stub
    	TextMessage textMsg = (TextMessage) message;
        try {
        	System.out.println("Recebi: "+textMsg.getText());
        	String receber = textMsg.getText();
        	String[] parser = receber.split(" ");
        	String aux = "";
        	//falte fazer os ifs iguais para chamar os metodos
        	if(parser[0].equals("LOGIN")) {
        		aux = LoginUser(parser[1],parser[2]);
        	}
        	else if(parser[0].equals("REGISTER")) {
        		aux = "Register sucessful! Waiting for Admin Approval";
        		addMessagetoDB(receber);
        	}
        	else if(parser[0].equals("GetPublication")) {
        		aux = GetPub(parser[1]);
        	}
        	else if(parser[0].equals("GetAllPublication")) {
        		aux = GetAllPubs();
        	}
        	else if(parser[0].equals("ADD_PUBLICATION")) {
        		aux = "Add-Publication sucessful! Waiting for Admin Approval";
        		addMessagetoDB(receber);
        	}
        	else if(parser[0].equals("UPDATE_PUBLICATION")) {
        		aux = "Update-Publication sucessful! Waiting for Admin Approval";
        		addMessagetoDB(receber);
        	}
        	else if(parser[0].equals("REMOVE_PUBLICATION")) {
        		aux = "Remove-Publication sucessful! Waiting for Admin Approval";
        		addMessagetoDB(receber);
        	}
        	
        	send(textMsg.getJMSReplyTo(), aux);
        }catch(JMSException e) {
        	e.printStackTrace();
        }   
    }
    
    public void send(Destination destination, String text) throws JMSException {
    	JMSProducer prod = jcontext.createProducer();
    	TextMessage reply =jcontext.createTextMessage();
    	reply.setText(text);
    	
    	prod.send(destination, reply);
    	System.out.println("Sent reply to: "+destination);
    }

    public void addMessagetoDB(String message) {
    	AdminMessages nova = new AdminMessages(message);
    	em.persist(nova);
    }
    
    public void RegisterUser(String nome, String password) {
    	Users novo = new Users(nome,password);

    	em.persist(novo);	
    }
    
    public String LoginUser(String name, String password) {
    	List<Users> users;
    	
    	String jpqlU = "SELECT DISTINCT r FROM Users r LEFT JOIN FETCH r.publications p WHERE r.name = :name AND r.password = :password";
		
		TypedQuery<Users> typedQueryU = em.createQuery(jpqlU, Users.class);
		typedQueryU.setParameter("name", name);
		typedQueryU.setParameter("password", password);
		List<Users> mylistU = typedQueryU.getResultList();
		
				
		users = mylistU;
		String x = "LOGIN ";
		if(mylistU.size() != 0)
			x = x + mylistU.get(0).getName() + " " + mylistU.get(0).getActive();
		else
			x = x + name + " false";
		return x;
    }
    
    public String GetAllPubs() {
    	List<Users> users;
    	List<PublicationMeta3> publications;
    	
    	String jpqlU = "SELECT DISTINCT r FROM Users r LEFT JOIN FETCH r.publications p";
		String jpqlP = "SELECT DISTINCT s FROM PublicationMeta3 s";
		
		TypedQuery<Users> typedQueryU = em.createQuery(jpqlU, Users.class);
		List<Users> mylistU = typedQueryU.getResultList();
		
		TypedQuery<PublicationMeta3> typedQueryP = em.createQuery(jpqlP, PublicationMeta3.class);
		List<PublicationMeta3> mylistP = typedQueryP.getResultList();
				
		users = mylistU;
		publications = mylistP;
		
		String x = "";
		for(int i = 0 ; i < mylistP.size(); i++) {
			x =  x + mylistP.get(i).getName() + "\n";
		}
		
		return x;
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

}
