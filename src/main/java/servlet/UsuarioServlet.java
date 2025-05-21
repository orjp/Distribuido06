package servlet;


import dto.Usuario;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "UsuarioServlet", urlPatterns = "/usuarios" )
public class UsuarioServlet extends HttpServlet {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");
    private EntityManager em;

    @Override
    public void init() {
        em = emf.createEntityManager();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Usuario> usuarios = em.createNamedQuery("Usuario.findAll", Usuario.class).getResultList();

        JSONArray Ajsn = new JSONArray();

        for (Usuario usu : usuarios) {
            JSONObject jsn = new JSONObject();

            jsn.put("codiUsua", usu.getCodiUsua());
            jsn.put("logiUsua", usu.getLogiUsua());
            jsn.put("passUsua", usu.getPassUsua());
            Ajsn.put(jsn);
        }
        resp.setContentType("application/json");
        resp.getWriter().write(Ajsn.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder jsonB = new StringBuilder();
        String linea;

        while ((linea = reader.readLine()) != null) {
            jsonB.append(linea);
        }
        JSONObject jsono = new JSONObject(jsonB.toString());

        Usuario nuevo = new Usuario();

        nuevo.setLogiUsua(jsono.getString("logiUsua"));
        nuevo.setPassUsua(jsono.getString("passUsua"));

        // Autoincrementar manualmente si es necesario
        Integer maxId = (Integer) em.createQuery("SELECT COALESCE(MAX(u.codiUsua), 0) FROM Usuario u").getSingleResult();
        nuevo.setCodiUsua(maxId + 1);

        em.getTransaction().begin();
        em.persist(nuevo);
        em.getTransaction().commit();

        resp.setContentType("application/json");
        resp.getWriter().write("{\"mensaje\": \"Usuario creado correctamente\"}");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder jsonB = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) {
            jsonB.append(linea);
        }
        JSONObject jsono = new JSONObject(jsonB.toString());

        Usuario actualizado = new Usuario();
        
        actualizado.setCodiUsua(jsono.getInt("codiUsua"));
        actualizado.setLogiUsua(jsono.getString("logiUsua"));
        actualizado.setPassUsua(jsono.getString("passUsua"));
        
        
        em.getTransaction().begin();
        Usuario existente = em.find(Usuario.class, actualizado.getCodiUsua());
        if (existente != null) {
            existente.setLogiUsua(actualizado.getLogiUsua());
            existente.setPassUsua(actualizado.getPassUsua());
            em.merge(existente);
            em.getTransaction().commit();
            resp.getWriter().write("{\"mensaje\": \"Usuario actualizado\"}");
        } else {
            em.getTransaction().rollback();
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        em.getTransaction().begin();
        Usuario u = em.find(Usuario.class, id);
        if (u != null) {
            em.remove(u);
            em.getTransaction().commit();
            resp.getWriter().write("{\"mensaje\": \"Usuario eliminado\"}");
        } else {
            em.getTransaction().rollback();
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
        }
    }

    @Override
    public void destroy() {
        em.close();
        emf.close();
    }
}
