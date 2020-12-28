package jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Entity
public class PublicationMeta3 implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// we use this generation type to match that of SQLWriteStudents
	@Id
	@Column(name = "name")
	private String name;
	@Column(name = "date")
	private String date;
	@Column(name = "type")
	private String type;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="institution_name", referencedColumnName = "name")
	private Users author;
	
	public PublicationMeta3() {}
	
	public PublicationMeta3(String name, String date,String type) {
		this.name = name;
		this.date = date;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Users getAuthor(){
		return this.author;
	}
	
	public void setAuthor(Users author) {
		this.author = author;
	}
	
	
	@Override
	public String toString()
	{
		return "Publication : " + this.name + "  Date: " + this.date;
	}	
	
}
