package tn.esprit.spring.entity;


import javax.persistence.*;

@Entity
@Table(name = "image_model")
public class ImageModel {
@ManyToOne
Parent parent;
	public ImageModel() {
		super();
	}

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "picByte", length = 1000)
	private byte[] picByte;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getPicByte() {
		return picByte;
	}

	public void setPicByte(byte[] picByte) {
		this.picByte = picByte;
	}

	public Parent getParent() {
		return parent;
	}

	public void setParent(Parent parent) {
		this.parent = parent;
	}

	public ImageModel( String name, String type, byte[] picByte) {
		super();
		this.name = name;
		this.type = type;
		this.picByte = picByte;
	}

	
	
}