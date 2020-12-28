package ejb;

import java.util.List;

import javax.ejb.Remote;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import jpa.AdminMessages;
import jpa.PublicationMeta3;
import jpa.Users;

@Remote
public interface AdminBeanRemote {
	public void RegisterUser(String nome, String password);
    public void addPublication(String name, String type, String date, String user);
    public void updatePublication(String nome, String type, String date, String user);
    public void removePublication(String nome);
    public String getAllUsers();
    public String getAllPubs();
    public void ApproveMessages(String name);
    public void RejectMessages(String name);
    public void DisableUser(String username);
    public List<AdminMessages> getAllTaks();
    public String GetPub(String name);
}
