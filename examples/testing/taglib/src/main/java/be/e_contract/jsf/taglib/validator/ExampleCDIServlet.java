package be.e_contract.jsf.taglib.validator;

import java.io.IOException;
import java.io.PrintWriter;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExampleCDIServlet extends HttpServlet {

    @Inject
    private BeanManager beanManager;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter printWriter = response.getWriter();
        printWriter.print("hello world " + (null != this.beanManager));
    }
}
