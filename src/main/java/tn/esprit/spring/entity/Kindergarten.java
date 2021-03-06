package tn.esprit.spring.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
public class Kindergarten implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id ;
	private String  Name;
	private String phonenumber;
	private String email;
	private String password;
    private boolean Confirmation=false;
    String fileName;
	@Lob
    @Column(name = "image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;
	@JsonIgnore
	private HashMap<String, Integer> forvote;
	
	private String delegate=null;
	@JsonIgnore
	private   final String role="Kindergarten";
	
	@OneToMany(mappedBy="kindergarten")
	@JsonIgnore
	List<TimesheetDelegate> delegates;
	@ManyToOne
	@JsonIgnore
	private Administrator administrator;
	@OneToMany(mappedBy="kindergarten")
	@JsonIgnore
	Set<Bus_reservation> bus_reservations;
	@ManyToMany(mappedBy="kindergarten",cascade = CascadeType.ALL)
	@JsonIgnore
	private  List<Parent> parents ;
	


	public HashMap<String, Integer> getVote() {
		return forvote;
	}

	public void setVote(HashMap<String, Integer> vote) {
		this.forvote = vote;
	}

	public List<TimesheetDelegate> getDelegates() {
		return delegates;
	}

	public void setDelegates(List<TimesheetDelegate> delegates) {
		this.delegates = delegates;
	}

	public String getRole() {
		return role;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public boolean isConfirmation() {
		return Confirmation;
	}

	public void setConfirmation(boolean confirmation) {
		Confirmation = confirmation;
	}

	public Administrator getAdministrator() {
		return administrator;
	}

	public void setAdministrator(Administrator administrator) {
		this.administrator = administrator;
	}

	public Set<Bus_reservation> getBus_reservations() {
		return bus_reservations;
	}

	public void setBus_reservations(Set<Bus_reservation> bus_reservations) {
		this.bus_reservations = bus_reservations;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	public List<Parent> getParents() {
		return parents;
	}

	public void setParents(List<Parent> parents) {
		this.parents = parents;
	}

	public String getDelegate() {
		return delegate;
	}

	public void setDelegate(String delegate) {
		this.delegate = delegate;
	}
	

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}



	public Kindergarten(long id, String name, String phonenumber, String email, String password, boolean confirmation,
			String fileName, byte[] image, HashMap<String, Integer> forvote, String delegate,
			List<TimesheetDelegate> delegates, Administrator administrator, Set<Bus_reservation> bus_reservations,
			List<Parent> parents) {
		super();
		this.id = id;
		Name = name;
		this.phonenumber = phonenumber;
		this.email = email;
		this.password = password;
		Confirmation = confirmation;
		this.fileName = fileName;
		this.image = image;
		this.forvote = forvote;
		this.delegate = delegate;
		this.delegates = delegates;
		this.administrator = administrator;
		this.bus_reservations = bus_reservations;
		this.parents = parents;
	}

	public Kindergarten() {
		super();
		// TODO Auto-generated constructor stub
	}






	
	
	

}
