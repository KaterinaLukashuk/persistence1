/**
 * 
 */
package com.sap.cloud.sample.helloworld;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author katsiaryna.lukashuk
 *
 */

@Entity
@Table(name = "T_DEPARTMENT", uniqueConstraints = @UniqueConstraint (columnNames={"DEPARTMENTNAME"}))
@NamedQuery(name = "AllDepartments", query = "select d from Department d")
public class Department {
	  @Id
	    @GeneratedValue
	    private long id;
	    @Basic
	    private String departmentName;
	    
	    @OneToMany(	mappedBy = "department")
	    private Set<Person> persons;
	    
	    
		public Department() {
		}
		public Department(long id, String departmentName) {
			this.id = id;
			this.departmentName = departmentName;
		}
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getDepartmentName() {
			return departmentName;
		}
		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}
		public Set<Person> getPersons() {
			return persons;
		}
		public void setPersons(Set<Person> persons) {
			this.persons = persons;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((departmentName == null) ? 0 : departmentName.hashCode());
			result = prime * result + (int) (id ^ (id >>> 32));
			result = prime * result + ((persons == null) ? 0 : persons.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Department other = (Department) obj;
			if (departmentName == null) {
				if (other.departmentName != null)
					return false;
			} else if (!departmentName.equals(other.departmentName))
				return false;
			if (id != other.id)
				return false;
			if (persons == null) {
				if (other.persons != null)
					return false;
			} else if (!persons.equals(other.persons))
				return false;
			return true;
		}
	    
	    
}
