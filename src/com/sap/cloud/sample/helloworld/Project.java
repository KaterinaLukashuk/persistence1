package com.sap.cloud.sample.helloworld;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "T_PROJECT", uniqueConstraints = @UniqueConstraint (columnNames={"PROJNAME"}))
@NamedQuery(name = "AllProjects", query = "select pr from Project pr")
public class Project {
	 @Id
	    @GeneratedValue
	    private long id;
	 @Basic
	 private String projName;
	 
	 @ManyToMany
	 @JoinTable(
			 name = "PROJECT_PERSON",
			 joinColumns = @JoinColumn(name = "project_id") ,
			 inverseJoinColumns = @JoinColumn(name = "person_id") 
			 )
	 private Set<Person> persons;

	 
	 
	public Project() {
		super();
	}



	public Project(long id, String projName, Set<Person> persons) {
		
		this.id = id;
		this.projName = projName;
		this.persons = persons;
	}



	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public String getProjName() {
		return projName;
	}



	public void setProjName(String projName) {
		this.projName = projName;
	}



	public Set<Person> getPersons() {
		return persons;
	}



	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}
	 
	public void addPerson(Person person)
	{
		persons.add(person);
	}
	 
}
