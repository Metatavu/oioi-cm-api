package fi.metatavu.oioi.cm.files;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet that handles file upload requests
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@RequestScoped
@MultipartConfig
@WebServlet (urlPatterns = { "/files", "/files/*" })
public class FilesServlet extends HttpServlet {
  
  private static final long serialVersionUID = 1558940825686488887L;

  @Inject
  private Logger logger;

  @Inject
  private FileController fileController;
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      Part file = req.getPart("file");
      if (file == null) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      
      String folder = req.getParameter("folder");      
      setCorsHeaders(resp);
      String contentType = file.getContentType();
      String fileName = file.getSubmittedFileName();
      InputStream inputStream = file.getInputStream();
      
      try (InputFile inputFile = new InputFile(folder, new FileMeta(contentType, fileName), inputStream)) {
        OutputFile outputFile = fileController.storeFile(inputFile);
  
        resp.setContentType("application/json");
        ServletOutputStream servletOutputStream = resp.getOutputStream();
        try {
          (new ObjectMapper()).writeValue(servletOutputStream, outputFile);
        } finally {
          servletOutputStream.flush();
        }
      }      
    } catch (Exception e) {
      logger.error("Upload failed on internal server error", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  
  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
  }
  
  /**
   * Sets CORS headers for the response
   * 
   * @param response
   */
  private void setCorsHeaders(HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
  }
  
}