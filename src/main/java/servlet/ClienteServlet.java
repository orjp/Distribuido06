package servlet;


import dto.Cliente;
import java.io.IOException;
import java.io.PrintWriter;

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

@WebServlet(name = "ClienteServlet", urlPatterns = {"/cliente"})
public class ClienteServlet extends HttpServlet {

    @PersistenceUnit(unitName = "com.mycompany_TPD06_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = getEntityManager();
        try {
            List<Cliente> clientes = em.createNamedQuery("Cliente.findAll", Cliente.class).getResultList();
            JSONArray jsonArray = new JSONArray();
            for (Cliente c : clientes) {
                JSONObject obj = new JSONObject();
                obj.put("codiClie", c.getCodiClie());
                obj.put("nombClie", c.getNombClie());
                jsonArray.put(obj);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(jsonArray.toString());
            out.flush();
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = getEntityManager();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            String nombClie = jsonObject.getString("nombClie");

            // Obtener el siguiente código de cliente
            int codiClie = obtenerSiguienteCodigo(em); // Generar automáticamente el código

            // Crear y persistir el nuevo cliente
            Cliente cliente = new Cliente();
            cliente.setCodiClie(codiClie); // Asignar el código generado
            cliente.setNombClie(nombClie);

            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();

            // Enviar un código de estado 201 (Created) para indicar éxito
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject successJson = new JSONObject();
            successJson.put("mensaje", "Cliente agregado con éxito.");
            out.print(successJson.toString());
            out.flush();

        } catch (Exception e) {
            em.getTransaction().rollback();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // Puedes agregar un mensaje de error aquí si lo deseas
        } finally {
            em.close();
        }
    }

    private int obtenerSiguienteCodigo(EntityManager em) {
        Integer maxCodigo = (Integer) em.createQuery("SELECT MAX(c.codiClie) FROM Cliente c").getSingleResult();
        return (maxCodigo == null ? 1 : maxCodigo + 1);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = getEntityManager();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            int codiClie = jsonObject.getInt("codiClie");
            Cliente cliente = em.find(Cliente.class, codiClie);

            if (cliente != null) {
                cliente.setNombClie(jsonObject.getString("nombClie"));
                em.getTransaction().begin();
                em.merge(cliente);
                em.getTransaction().commit();

                // ✅ Agregamos respuesta en formato JSON
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                JSONObject successJson = new JSONObject();
                successJson.put("mensaje", "Cliente actualizado con éxito.");
                out.print(successJson.toString());
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", "Cliente no encontrado.");
                out.print(errorJson.toString());
                out.flush();
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Error al actualizar el cliente.");
            out.print(errorJson.toString());
            out.flush();
        } finally {
            em.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = getEntityManager();
        try {
            int codiClie = Integer.parseInt(request.getParameter("codiClie"));
            Cliente cliente = em.find(Cliente.class, codiClie);

            if (cliente != null) {
                em.getTransaction().begin();
                em.remove(cliente);
                em.getTransaction().commit();
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            em.close();
        }
    }
}
