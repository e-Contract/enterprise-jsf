package be.e_contract.jsf.security;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/servlet")
@ServletSecurity(
        @HttpConstraint(
                rolesAllowed = {
                        "my-role"
                }
        )
)
public class SecuredServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        printWriter.println("hello: " + request.getUserPrincipal());
        boolean myRole = request.isUserInRole("my-role");
        boolean foobarRole = request.isUserInRole("foobar");
        printWriter.println("user in my-role: " + myRole);
        printWriter.println("user in foobar role: " + foobarRole);
        printWriter.flush();
    }
}