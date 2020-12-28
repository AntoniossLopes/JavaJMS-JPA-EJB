package jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Entity
public class Users implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "password", nullable = false)
	private String password;
	@Column(name = "active")
	private boolean active;
	

	@OneToMany(mappedBy = "author", cascade=CascadeType.ALL)
	private List<PublicationMeta3> publications;
	
	
	public Users() {}
	
	public Users(String name, String password) {
		this.name = name;
		this.password = password;
		this.active = true;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean getActive() {
		return this.active;
	}
	
	public List<PublicationMeta3> getPublications() {
		return this.publications;
	}
	
	public void setPublications(List<PublicationMeta3> publications) {
		this.publications = publications;
	}
	
	public void addPublication(PublicationMeta3 publication) {
		this.publications.add(publication);
	}
	
	public void removePublication(PublicationMeta3 publication) {
		int index = -1;
		for(int i = 0 ; i < this.publications.size(); i++) {
			if(this.publications.get(i).getName().equals(publication.getName())) {
				index = i;
			}
		}
		if(index != -1)
			this.publications.remove(index);
	}
	
	@Override
	public String toString()
	{
		return "User: "+name;
	}
	
}
