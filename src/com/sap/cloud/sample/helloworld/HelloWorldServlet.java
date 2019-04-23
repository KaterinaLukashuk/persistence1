package com.sap.cloud.sample.helloworld;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
//import javax.
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Servlet implementing simplest possible hello world application for SAP Cloud Platform.
 */
public class HelloWorldServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private DataSource ds;
    private EntityManagerFactory emf;

    /** {@inheritDoc} */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("<p>Hello World!</p>");
        
    }
 

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
           //No Persistence provider for EntityManage
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
    
}
