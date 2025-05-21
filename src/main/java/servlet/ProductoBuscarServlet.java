package servlet;

import dao.ProductoJpaController;
import dto.Producto;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet(name = "ProductoBuscarServlet", urlPatterns = {"/productobuscar"})
public class ProductoBuscarServlet extends HttpServlet {

    ProductoJpaController produc = new ProductoJpaController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject json = new JSONObject();

        try {
            int codiProd = Integer.parseInt(request.getParameter("codiProd"));
            ProductoJpaController produc = new ProductoJpaController();
            Producto producto = produc.findProducto(codiProd);

            if (producto != null) {
                json.put("res", "ok");
                json.put("codiProd", producto.getCodiProd());
                json.put("nombProd", producto.getNombProd());
                json.put("precProd", producto.getPrecProd());
                json.put("stocProd", producto.getStocProd());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                json.put("res", "error");
                json.put("message", "Producto no encontrado");
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

