package num;

import java.io.IOException;
import java.util.Scanner;

import javax.jms.*;
import javax.naming.*;

import io.undertow.io.Sender;

public class Client implements MessageListener {
	
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private Destination destinationReceiver;
	private static String nome;
	private static boolean status = false;
	private boolean denied;

	public Client() throws NamingException
	{
		this.connectionFactory = InitialContext.doLookup("jms/RemoteConnectionFactory");
		this.destination = InitialContext.doLookup("jms/queue/playQueue");
		this.destinationReceiver = InitialContext.doLookup("jms/topic/playTopic");
		this.denied = true;
	}
	
	private void send(String text)
	{
		try(JMSContext context = connectionFactory.createContext("john","!1secret");)
		{
			JMSProducer messageprod = context.createProducer();
			TextMessage msg = context.createTextMessage();
			Destination tmp = context.createTemporaryQueue();
			msg.setJMSReplyTo(tmp);
			msg.setText(text);
			messageprod.send(destination, msg);
			
			JMSConsumer cons = context.createConsumer(tmp);
			String receive = cons.receiveBody(String.class);
			System.out.println("Received in Temporary Queue: "+receive);
			String[] parser = receive.split(" ");
			if(parser[0].equals("LOGIN")) {
				status = Boolean.parseBoolean(parser[2]);
			}
		}catch(Exception re) {
			re.printStackTrace();
		}
	}
	                                                                                                                                                                                                                                                              
	@Override
	public void onMessage(Message msg) {
		// TODO Auto-generated method stub
		TextMessage textMsg = (TextMessage) msg;
		try {
			System.out.println("Got message: "+textMsg.getText());
			if(textMsg.getText().equals("USER PERMISSION DENIED BY ADMIN")){
				this.denied = false;
				this.status = false;
			}
		}
		catch(JMSException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args)throws NamingException
	{
		Client receiver = new Client();
		try(JMSContext context = receiver.connectionFactory.createContext("john","!1secret");)
		{
			while(receiver.denied) {
				Client sender = new Client();
				System.out.println("Welcome\n1-Register\n2-Login\n-1 Exit\n\nSelect Option:");
				Scanner sc = new Scanner(System.in);
				int option = Integer.parseInt(sc.nextLine());
				if(option == 1) {
					System.out.println("User:");
					String user = sc.nextLine();
					System.out.println("Password:");
					String password = sc.nextLine();
					String x = "REGISTER "+user+" "+password;
					if(nome == null || !nome.equals(user)) {
						nome = user;
						context.setClientID(nome);
						String property = "user='";
						property = property + nome+"'";
						JMSConsumer consumer = context.createDurableConsumer((Topic)receiver.destinationReceiver,nome, property, true);
						consumer.setMessageListener(receiver);
					}
					sender.send(x);	
				}
				else if(option == 2) {
					System.out.println("User:");
					String user = sc.nextLine();
					System.out.println("Password:");
					String password = sc.nextLine();
					String x = "LOGIN "+user+" "+password;
					if(nome == null || !nome.equals(user)) {
						nome = user;
						context.setClientID(nome);
						String property = "user='";
						property = property + nome + "'";
						JMSConsumer consumer = context.createDurableConsumer((Topic)receiver.destinationReceiver, nome, property, true);
						consumer.setMessageListener(receiver);
					}
					sender.send(x);	
					while(status) {
						
						System.out.println("1 - Get all Publications");
						System.out.println("2 - Get Publication By name");
						System.out.println("3 - Add a new Publication");
						System.out.println("4 - Update Publication");
						System.out.println("5 - Remove Publication");
						System.out.println("6 - Logout");
						int option2 = Integer.parseInt(sc.nextLine());
						switch(option2) {
							case 1:
								sender.send("GetAllPublication");
								break;
							case 2:
								System.out.println("Publication Name(cant have _ in its name):");
								String pub_name = sc.nextLine();
								pub_name = pub_name.replace(" ", "_");
								String x1 = "GetPublication "+pub_name;
								sender.send(x1);
								break;
							case 3:
								System.out.println("Publication Name(cant have _ in its name):");
								String new_pub_name = sc.nextLine();
								System.out.println("Type:");
								String new_pub_type = sc.nextLine();
								System.out.println("Date:");
								String new_pub_date = sc.nextLine();
								new_pub_name = new_pub_name.replace(" ", "_");
								String x_add = "ADD_PUBLICATION "+new_pub_name + " "+ new_pub_type + " " + new_pub_date + " " + nome;
								sender.send(x_add);
								break;
							case 4:
								System.out.println("Publication Name(cant have _ in its name):");
								String update_pub_name = sc.nextLine();
								System.out.println("Type:");
								String update_pub_type = sc.nextLine();
								System.out.println("Date:");
								String update_pub_date = sc.nextLine();
								update_pub_name = update_pub_name.replace(" ", "_");
								String x_update = "UPDATE_PUBLICATION "+update_pub_name + " "+ update_pub_type + " " + update_pub_date + " " + nome;
								sender.send(x_update);
								break;
							case 5:
								System.out.println("Publication Name(cant have _ in its name):");
								String remove_pub_name = sc.nextLine();
								remove_pub_name = remove_pub_name.replace(" ", "_");
								String x_remove = "REMOVE_PUBLICATION "+remove_pub_name;
								sender.send(x_remove);
								break;
							case 6:
								status = false;
								break;
						}
						
					}
				}else if(option ==-1) {
					break;
				}
				//sender.send("Hello Receiver");
			}
		}
		System.out.println("Finish Sender....");
	}
}

