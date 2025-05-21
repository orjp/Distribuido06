package servlet;


import dao.VentaJpaController;
import dao.ClienteJpaController;
import dao.ProductoJpaController;
import dao.DetalleJpaController;
import dto.Cliente;
import dto.Detalle;
import dto.Producto;
import dto.Venta;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "VentaRegistrarServlet", urlPatterns = {"/ventaregistrar"})
public class VentaRegistrarServlet extends HttpServlet {

    VentaJpaController ventaController = new VentaJpaController();
    ClienteJpaController clienteController = new ClienteJpaController();
    ProductoJpaController productoController = new ProductoJpaController();
    DetalleJpaController detallecontroller = new DetalleJpaController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<Venta> ventas = ventaController.findVentaEntities();
            JSONArray jsonArray = new JSONArray();

            for (Venta v : ventas) {
                JSONObject obj = new JSONObject();
                obj.put("codiVent", v.getCodiVent());
                obj.put("fechVent", v.getFechVent());
                obj.put("codiClie", v.getCodiClie().getCodiClie());
                obj.put("nombClie", v.getCodiClie().getNombClie());
                // Puedes añadir más info si quieres
                jsonArray.put(obj);
                out.print(obj.toString());
                out.flush();
            }
        } finally {

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        JSONObject jsonResponse = new JSONObject();

        try (PrintWriter out = response.getWriter()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonRequest = new JSONObject(sb.toString());

            Integer codiClie = jsonRequest.getInt("clienteId");
            Date fecha = new Date();
            SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fechVent = formatoBD.format(fecha);
            JSONArray detallesArray = jsonRequest.getJSONArray("detalles");

            Cliente cliente = clienteController.findCliente(codiClie);
            if (cliente == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("error", "Cliente no encontrado");
                out.print(jsonResponse.toString());
                return;
            }

            Venta venta = new Venta();
            venta.setFechVent(fechVent);
            venta.setCodiClie(cliente);

            List<Detalle> detalles = new ArrayList<>();

            for (int i = 0; i < detallesArray.length(); i++) {
                JSONObject det = detallesArray.getJSONObject(i);
                Integer codiProd = det.getInt("productoId");
                Integer cantidad = det.getInt("cantidad");
                Double precio = det.getDouble("precProd");

                Producto producto = productoController.findProducto(codiProd);
                if (producto == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse.put("error", "Producto no encontrado: código " + codiProd);
                    out.print(jsonResponse.toString());
                    return;
                }

                if (producto.getStocProd() < cantidad) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse.put("error", "Stock insuficiente para producto: código " + codiProd);
                    out.print(jsonResponse.toString());
                    return;
                }

                producto.setStocProd(producto.getStocProd() - cantidad);
                productoController.edit(producto);

                Detalle detalle = new Detalle();
                detalle.setCodiProd(producto);
                detalle.setCantDeta(cantidad);
                detalle.setPrecProd(precio);
                detalle.setSbttDeta(cantidad * precio);

                detalles.add(detalle);
            }

            // Primero creamos la venta para que tenga ID y pueda relacionarse con detalles
            ventaController.create(venta);

            // Guardamos los detalles con la venta ya persistida
            for (Detalle detalle : detalles) {
                detalle.setCodiVent(venta); // Asignamos la venta ya guardada
                detallecontroller.create(detalle);
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonResponse.put("mensaje", "Venta registrada con éxito");
            jsonResponse.put("codiVent", venta.getCodiVent());
            out.print(jsonResponse.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("error", "Error al registrar venta: " + e.getMessage());
            response.getWriter().print(error.toString());
        }
    }
}
