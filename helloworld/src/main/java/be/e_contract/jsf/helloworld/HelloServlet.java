package be.e_contract.jsf.helloworld;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello-servlet")
public class HelloServlet extends HttpServlet {

    private static String message = "";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String messageParam = request.getParameter("message");
        if (null != messageParam) {
            message = messageParam;
        }
        response.setContentType("text/html");
        PrintWriter printWriter = response.getWriter();
        printWriter.println("<html><body>");
        printWriter.println("<p>Hello: " + message + "</p>");
        printWriter.println("<form action=\"./hello-servlet\" method=\"get\">");
        printWriter.println("<input name=\"message\" type=\"text\"/>");
        printWriter.println("<button>Submit</button>");
        printWriter.println("</form>");
        printWriter.println("</body></html>");
    }
}
