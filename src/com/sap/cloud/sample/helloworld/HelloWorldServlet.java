package com.sap.cloud.sample.helloworld;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Query;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.sap.cloud.sample.helloworld.Person;
import com.sap.security.core.server.csi.IXSSEncoder;
import com.sap.security.core.server.csi.IXSSEncoder;
import com.sap.security.core.server.csi.XSSEncoder;
import com.sap.security.core.server.csi.XSSEncoder;

/**
 * Servlet implementing simplest possible hello world application for SAP Cloud Platform.
 */
public class HelloWorldServlet extends HttpServlet {
//  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceWithJPAServlet.class);

   private DataSource ds;
   private EntityManagerFactory emf;

   /** {@inheritDoc} */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public void init() throws ServletException {
       Connection connection = null;
       try {
           InitialContext ctx = new InitialContext();
           ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
           Map properties = new HashMap();
           properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
           emf = Persistence.createEntityManagerFactory("persistence-with-jpa", properties);
       } catch (NamingException e) {
           throw new ServletException(e);
       }
   }

   /** {@inheritDoc} */
   @Override
   public void destroy() {
       emf.close();
   }

   /** {@inheritDoc} */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       response.getWriter().println("<p>Persistence with JPA Sample!</p>");
       try {
           appendPersonTable(response);
           appendAddForm(response);
       } catch (Exception e) {
           response.getWriter().println("Persistence operation failed with reason: " + e.getMessage());
//           LOGGER.error("Persistence operation failed", e);
       }
   }

   /** {@inheritDoc} */
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
           IOException {
       try {
    	   String cmd=request.getParameter("cmd");
    	   if(cmd.equals("Add department"))
    	   {
    		   doAddDepartment(request);
    	   }
    	 else
    	 if(cmd.equals("Add Person"))
    	  {
    		 doAdd(request);
    	  }
    	 else
    		 if (cmd.contentEquals("Add project"))
    		 {
    			  doAddProject(request);
    		 }else
    			 if(cmd.contentEquals("Add Person to proj"))
    		 {
    			 doAddPersToProj(request);
    		 }
           doGet(request, response);
       } catch (Exception e) {
           response.getWriter().println("Persistence operation failed with reason: " + e.getMessage());
//           LOGGER.error("Persistence operation failed", e);
       }
   }

   private void appendPersonTable(HttpServletResponse response) throws SQLException, IOException {
       // Append table that lists all persons
       EntityManager em = emf.createEntityManager();

       try {
           @SuppressWarnings("unchecked")
           List<Person> resultList = em.createNamedQuery("AllPersons").getResultList();
           List<Project> prtList = em.createNamedQuery("AllProjects").getResultList();
           List<Department> depList = em.createNamedQuery("AllDepartments").getResultList();
           response.getWriter().println(
                   "<p><table border=\"1\"><tr><th colspan=\"4\">"
                           + (resultList.isEmpty() ? "" : resultList.size() + " ")
                           + "Entries in the Database</th></tr>");
           if (resultList.isEmpty()) {
               response.getWriter().println("<tr><td colspan=\"4\">Database is empty</td></tr>");
           } else {
               response.getWriter().println("<tr><th>First name</th><th>Last name</th><th>Id</th><th>Department id</th></tr>");
           }
         
           IXSSEncoder xssEncoder = XSSEncoder.getInstance();
       
           
           for (Person p : resultList) {
               response.getWriter().println(
                       "<tr><td>" + xssEncoder.encodeHTML(p.getFirstName()) + "</td><td>"
                               + xssEncoder.encodeHTML(p.getLastName()) + "</td><td>" + p.getId() + "</td><td>"
                               + xssEncoder.encodeHTML(p.getDepartment().getDepartmentName())//+ "</td><td>"
                            		 //  + xssEncoder.encodeHTML(p.getProjects().toArray().toString())
                           
                               + "</td></tr>"
            		   );
           }
           response.getWriter().println("</table></p>");
           
           response.getWriter().println("<br><b>" + "____ALL PROJECTS____" + "</b><br>" );
           for(Project pr : prtList)
           {
        	   response.getWriter().println("<b>" + xssEncoder.encodeHTML( pr.getProjName()) + "</b>"  + "<br>");
        	   List<Person> prPers = new ArrayList<>(pr.getPersons());
        	   for (Person p: prPers ) {
        		   response.getWriter().println( "<p>" + xssEncoder.encodeHTML( p.getLastName()) + "</p>");
        	   }
        	   response.getWriter().println( "<br>");
           }
           
           response.getWriter().println("<br><b>" + "____ALL DEPARTMENTS____" + "</b><br>" );
           for(Department d : depList)
           {
        	   response.getWriter().println( xssEncoder.encodeHTML( d.getDepartmentName()) +  "<br>");
           }
           
       } finally {
           em.close();
       }
   }

   private void appendAddForm(HttpServletResponse response) throws IOException {
       // Append form through which new persons can be added
	   EntityManager em = emf.createEntityManager();
	   IXSSEncoder xssEncoder = XSSEncoder.getInstance();
	   List<Department> depList = em.createNamedQuery("AllDepartments").getResultList();
	   List<Project> prtList = em.createNamedQuery("AllProjects").getResultList();
	   List<Person> resultList = em.createNamedQuery("AllPersons").getResultList();
       response.getWriter().println(
               "<p><form action=\"\" method=\"post\">" + "First name:<input type=\"text\" name=\"FirstName\">"
                       + "&nbsp;Last name:<input type=\"text\" name=\"LastName\">"
                       + "Department:<select name=\"department\">");
       
       for(Department d : depList)
       {
    	   response.getWriter().println("<option  value = \"" + d.getId() +"\">" + xssEncoder.encodeHTML( d.getDepartmentName()) + "<option>" );
       }
  
       response.getWriter().println("</select>"+"&nbsp;<input type=\"submit\"name=\"cmd\" value=\"Add Person\">" + "<br>");

       
       
       response.getWriter().println(
       "Department:<input type=\"text\" name=\"newdepartment\">" 
                       + "&nbsp;<input type=\"submit\"name=\"cmd\" value=\"Add department\">"
                       + "<br>"
                       + "Project:<input type=\"text\" name=\"newproject\">" 
                       + "&nbsp;<input type=\"submit\"name=\"cmd\" value=\"Add project\"> <br>"
                       + "Project:<select name=\"project\">");
                       
       for(Project pr : prtList)
       {
    	   response.getWriter().println("<option name = \"project_id\" value = \"" + pr.getId() +"\" >" + xssEncoder.encodeHTML( pr.getProjName()) + "</option>" //+
//                   "<input type = hidden name = \"project_id\" value = " + pr.getId() + ">"
    			   );
       }
       response.getWriter().println("</select>"+ "Person:<select name=\"proj_person\">");
       for(Person p : resultList)
       {
    	   response.getWriter().println("<option name = \"person_id\" value = \"" + p.getId() +"\" >"+ xssEncoder.encodeHTML( p.getFirstName()+ " " + p.getLastName()) + "</option>"// +
//    			   "<input type = hidden name = \"person_id\" value = " + p.getId() + "/>"
    			   );
       }
       response.getWriter().println("</select>");
       response.getWriter().println("&nbsp;<input type=\"submit\"name=\"cmd\" value=\"Add Person to proj\"><br>");
       
                       response.getWriter().println("</form></p>");
       
       
   }

   private void doAdd(HttpServletRequest request) throws ServletException, IOException, SQLException {
       // Extract name of person to be added from request
       String firstName = request.getParameter("FirstName");
       String lastName = request.getParameter("LastName");
       String department = request.getParameter("department");
       EntityManager em = emf.createEntityManager();
       Department dep = em.find(Department.class, Long.parseLong(department));
       List<Department> depList = em.createNamedQuery("AllDepartments").getResultList();
//       for(Department d: depList)
//       {
//    	   if (d.getDepartmentName().contentEquals(department))
//    	   {
//    		   dep=d;
//    	   }
//       }
      
       
       try {
           if (firstName != null && lastName != null && !firstName.trim().isEmpty() && !lastName.trim().isEmpty()) {
               Person person = new Person();
               person.setFirstName(firstName);
               person.setLastName(lastName);
               person.setDepartment(dep);
               em.getTransaction().begin();
               em.persist(person);
               em.getTransaction().commit();
           }
       } finally {
           em.close();
       }
   }
   
   private void doAddDepartment(HttpServletRequest request) throws ServletException, IOException, SQLException {
       // Extract name of person to be added from request
       String department = request.getParameter("newdepartment");
       EntityManager em = emf.createEntityManager();
       Department dep = new Department();
       try {
           if (department != null  && !department.trim().isEmpty() ) {
               dep.setDepartmentName(department);
               em.getTransaction().begin();
               em.persist(dep);
               em.getTransaction().commit();
           }
       } finally {
           em.close();
       }
   }
   
   private void doAddProject(HttpServletRequest request) throws ServletException, IOException, SQLException {
       // Extract name of person to be added from request
       String project = request.getParameter("newproject");
       EntityManager em = emf.createEntityManager();
       Project proj = new Project();
       try {
           if (project != null  && !project.trim().isEmpty() ) {
               proj.setProjName(project);
               em.getTransaction().begin();
               em.persist(proj);
               em.getTransaction().commit();
           }
       } finally {
           em.close();
       }
   }
   
   private void  doAddPersToProj(HttpServletRequest request) throws ServletException, IOException, SQLException {
       // Extract name of person to be added from request
       String project = request.getParameter("project");
       String person = request.getParameter("proj_person");
    //   Project prj = new Project();
       
       EntityManager em = emf.createEntityManager();  
       
    //   Project prj = em.find(Project.class,Integer.parseInt(project)); // get project by PK 
       List<Project> prtList = em.createNamedQuery("AllProjects").getResultList();
       try {
           if (project != null  && !project.trim().isEmpty() && person != null  && !person.trim().isEmpty() ) {
               em.getTransaction().begin();
         //    em.persist(proj);
         //    em.persist(prj.getPersons());
               javax.persistence.Query q = em.createNativeQuery("INSERT INTO PROJECT_PERSON VALUES("+ person + " , " + project + ");");
               q.executeUpdate();
               em.getTransaction().commit();
           }
       } finally {
           em.close();
       }
   }
   
   private void dropPerson(HttpServletRequest request) {
	   //detach(Object entity)
   }
   private void dropProject(HttpServletRequest request) {
	   
   }
   private void dropDepartment(HttpServletRequest request) {
	   
   }
}

