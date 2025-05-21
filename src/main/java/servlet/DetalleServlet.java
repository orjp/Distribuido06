package servlet;


import dao.DetalleJpaController;
import dao.ProductoJpaController;
import dao.VentaJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Detalle;
import dto.Producto;
import dto.Venta;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "DetalleServlet", urlPatterns = {"/detalle"})
public class DetalleServlet extends HttpServlet {

    private DetalleJpaController controladorDetalle = new DetalleJpaController();
    private ProductoJpaController controladorProducto = new ProductoJpaController();
    private VentaJpaController controladorVenta = new VentaJpaController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retorna todos los detalles en JSON
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<Detalle> listaDetalles = controladorDetalle.findDetalleEntities();

            JsonArrayBuilder arregloJson = Json.createArrayBuilder();
            for (Detalle d : listaDetalles) {
                JsonObjectBuilder detalleJson = Json.createObjectBuilder()
                        .add("codiDeta", d.getCodiDeta())
                        .add("cantidad", d.getCantDeta())
                        .add("precio", d.getPrecProd());

                // Venta (solo id)
                if (d.getCodiVent() != null) {
                    detalleJson.add("codiVent", Json.createObjectBuilder()
                            .add("codiVent", d.getCodiVent().getCodiVent()));
                } else {
                    detalleJson.addNull("codiVent");
                }

                // Producto (solo id y nombre)
                if (d.getCodiProd() != null) {
                    detalleJson.add("codiProd", Json.createObjectBuilder()
                            .add("codiProd", d.getCodiProd().getCodiProd())
                            .add("nomProd", d.getCodiProd().getNombProd()));
                } else {
                    detalleJson.addNull("codiProd");
                }

                arregloJson.add(detalleJson);
            }

            out.print(arregloJson.build().toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Crear o editar detalle con JSON recibido
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (InputStream is = request.getInputStream();
             JsonReader reader = Json.createReader(is)) {

            JsonObject jsonEntrada = reader.readObject();

            Integer codiDeta = jsonEntrada.containsKey("codiDeta") ? jsonEntrada.getInt("codiDeta") : null;
            int cantidad = jsonEntrada.getInt("cantidad");
            double precio = jsonEntrada.getJsonNumber("precio").doubleValue();

            JsonObject jsonVenta = jsonEntrada.getJsonObject("codiVent");
            JsonObject jsonProducto = jsonEntrada.getJsonObject("codiProd");

            if (jsonVenta == null || jsonProducto == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Venta y Producto son requeridos\"}");
                return;
            }

            int idVenta = jsonVenta.getInt("codiVent");
            int idProducto = jsonProducto.getInt("codiProd");

            Venta venta = controladorVenta.findVenta(idVenta);
            Producto producto = controladorProducto.findProducto(idProducto);

            if (venta == null || producto == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Venta o Producto no encontrados\"}");
                return;
            }

            Detalle detalle = new Detalle();
            if (codiDeta != null) {
                detalle.setCodiDeta(codiDeta);
            }
            detalle.setCantDeta(cantidad);
            detalle.setPrecProd(precio);
            detalle.setCodiVent(venta);
            detalle.setCodiProd(producto);

            if (codiDeta == null) {
                controladorDetalle.create(detalle);
                out.print("{\"mensaje\":\"Detalle insertado correctamente\"}");
            } else {
                controladorDetalle.edit(detalle);
                out.print("{\"mensaje\":\"Detalle actualizado correctamente\"}");
            }

        } catch (NonexistentEntityException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (JsonException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"JSON inválido\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Eliminar detalle por id (parámetro)
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Falta parámetro id\"}");
            out.close();
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            controladorDetalle.destroy(id);
            out.print("{\"mensaje\":\"Detalle eliminado correctamente\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"ID inválido\"}");
        } catch (NonexistentEntityException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }
}
