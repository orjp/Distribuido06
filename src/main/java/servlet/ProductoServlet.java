package servlet;

import dto.Producto;
import dao.ProductoJpaController;
import dao.exceptions.NonexistentEntityException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * 
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/producto"})
public class ProductoServlet extends HttpServlet {

    //Colocar la persistencia del proyecto
    @PersistenceUnit(unitName = "com.mycompany_TPD06_war_1.0-SNAPSHOTPU")
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");
    private final ProductoJpaController productoController = new ProductoJpaController(emf);
    
    
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //Optener datos - Listar
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        
        EntityManager em = getEntityManager();
        try {
            String codiProdParam = request.getParameter("codiProd");
            if (codiProdParam != null && !codiProdParam.trim().isEmpty()) {
                try {
                    int codiProd = Integer.parseInt(codiProdParam);
                    Producto producto = em.find(Producto.class, codiProd);
                    if (producto != null) {
                        JSONObject obj = new JSONObject();
                        obj.put("codiProd", producto.getCodiProd());
                        obj.put("nombProd", producto.getNombProd());
                        obj.put("precProd", producto.getPrecProd());
                        obj.put("stocProd", producto.getStocProd());

                        response.getWriter().print(obj.toString());
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
                    }
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Código inválido");
                }
            } else {
                List<Producto> productos = em.createNamedQuery("Producto.findAll", Producto.class).getResultList();
                JSONArray jsonArray = new JSONArray();
                for (Producto p : productos) {
                    JSONObject obj = new JSONObject();
                    obj.put("codiProd", p.getCodiProd());
                    obj.put("nombProd", p.getNombProd());
                    obj.put("precProd", p.getPrecProd());
                    obj.put("stocProd", p.getStocProd());
                    jsonArray.put(obj);
                }
                response.getWriter().print(jsonArray.toString());
            }
        } finally {
            em.close();
        }
    }

    //Agregar
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

       StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        JSONObject json = new JSONObject(sb.toString());


        try {
            Producto nuevo = new Producto();
            nuevo.setNombProd(json.getString("nombProd"));
            nuevo.setPrecProd(json.getDouble("precProd"));
            nuevo.setStocProd(json.getDouble("stocProd"));

            productoController.create(nuevo);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"message\":\"Producto creado correctamente\"}");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    //Modificar
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

       StringBuilder sb = new StringBuilder();
        try (BufferedReader r = request.getReader()) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        JSONObject json = new JSONObject(sb.toString());

        if (!json.has("codiProd")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta codiProd");
            return;
        }

        try {
            int id = json.getInt("codiProd");
            Producto p = productoController.findProducto(id);
            if (p == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no existe");
                return;
            }
            p.setNombProd(json.getString("nombProd"));
            p.setPrecProd(json.getDouble("precProd"));
            p.setStocProd(json.getDouble("stocProd"));

            productoController.edit(p);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Producto actualizado correctamente\"}");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
       

    //Eliminar
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String param = request.getParameter("codiProd");
        if (param == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro codiProd");
            return;
        }

        try {
            int codiProd = Integer.parseInt(param);
            productoController.destroy(codiProd);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Producto eliminado correctamente\"}");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "codiProd inválido");
        } catch (NonexistentEntityException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
