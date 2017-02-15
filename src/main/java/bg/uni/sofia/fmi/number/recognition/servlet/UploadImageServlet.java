package bg.uni.sofia.fmi.number.recognition.servlet;

import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig
public class UploadImageServlet extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    Part filePart = req.getPart("file"); 
	    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	    resp.setContentType("text/plain");
	    resp.getWriter().write("Hello - "+fileName);
	    resp.getWriter().close();
	}
}
