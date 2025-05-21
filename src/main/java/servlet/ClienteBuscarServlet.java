package servlet;


import dao.ClienteJpaController;
import dto.Cliente;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet(name = "ClienteBuscarServlet", urlPatterns = {"/clientebuscar"})
public class ClienteBuscarServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject json = new JSONObject();

        try {
            int codiClie = Integer.parseInt(request.getParameter("codiClie"));
            ClienteJpaController clie = new ClienteJpaController();
            Cliente cliente = clie.findCliente(codiClie);

            if (cliente != null) {
                json.put("res", "ok");
                json.put("codiClie", cliente.getCodiClie());
                json.put("nombClie", cliente.getNombClie());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                json.put("res", "error");
                json.put("message", "Cliente no encontrado");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            json.put("res", "error");
            json.put("message", "Parámetro inválido o error en el servidor");
        }

        out.print(json.toString());
        out.flush();
    }

}