package num;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import ejb.AdminBeanRemote;
import jpa.AdminMessages;


public class Admin {
	
	public static void main(String[] args)throws NamingException
	{
		 Context context;
		 Properties jndiProperties = new Properties();	  
		 jndiProperties.setProperty("java.naming.factory.initial", "org.jboss.naming.remote.client.InitialContextFactory");
		 jndiProperties.setProperty("java.naming.provider.url","http-remoting://localhost:8080");
		 jndiProperties.setProperty("jboss.naming.client.ejb.context","true");
		 try {
			 context = new InitialContext(jndiProperties);
			 //java:jboss/exported/projetoEAR/Projeto-EJB/EJBResearcher!ejb.EJBResearcherRemote
			 //java:jboss/exported/EJBProjeto3/AdminBean!ejb.AdminBeanRemote
			 AdminBeanRemote myejb = (AdminBeanRemote)context.lookup("EJB/AdminBean!ejb.AdminBeanRemote");
			 int flag = 1;
			 while(flag == 1) {
					System.out.println("Welcome\n1-Get All Users\n2-Get all Pending tasks\n3-Approve tasks\n4-Reject tasks\n5-Desactivate User\n6-Get all Pubs\n7-Get Pub by name\n0-Exit\n\nSelect Option:");
					Scanner sc = new Scanner(System.in);
					int option = Integer.parseInt(sc.nextLine()); 
					switch(option) {
						case 0:
							flag = 0;
							break;
						case 1:
							String users = myejb.getAllUsers();
							System.out.println(users);
							break;
						case 2:
							List<AdminMessages> msg = myejb.getAllTaks();
							for(int i = 0 ; i < msg.size(); i++) {
								System.out.println(msg.get(i).getMessage());
							}
							break;
						case 3:
							List<AdminMessages> msg1 = myejb.getAllTaks();
							for(int i = 0 ; i < msg1.size(); i++) {
								System.out.println(i+" -" + msg1.get(i).getMessage());
							}
							int number = Integer.parseInt(sc.nextLine());
							myejb.ApproveMessages(msg1.get(number).getMessage());
							break;
						case 4:
							List<AdminMessages> msg2 = myejb.getAllTaks();
							for(int i = 0 ; i < msg2.size(); i++) {
								System.out.println(i+" -" + msg2.get(i).getMessage());
							}
							int number2 = Integer.parseInt(sc.nextLine());
							myejb.RejectMessages(msg2.get(number2).getMessage());
							break;
						case 5:
							String users2 = myejb.getAllUsers();
							System.out.println(users2);
							System.out.println("Type username:");
							String input = sc.nextLine();
							myejb.DisableUser(input);
							break;
						case 6:
							String pubs = myejb.getAllPubs();
							System.out.println(pubs);
							break;
						case 7:
							System.out.println("Publication Name(cant have _ in its name):");
							String pub_name = sc.nextLine();
							pub_name = pub_name.replace(" ", "_");
							myejb.GetPub(pub_name);
							break;
					}
			 }
			 
			 } catch (NamingException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	}
}
