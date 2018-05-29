package comercio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Logica {

    Connection connect;
    Statement s;
    Statement s1;
    Statement s2;
    ResultSet rs;
    ResultSet rs2;
    ResultSet rs3;

    // Medoto para conectar la base
    public void conectar() {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:Base.db");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se pudo conectadar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
// Medoto para desconectar la base

    public void desconectar() {
        try {

            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(Logica.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo que carga los datos de la base y en este caso los muestra por una Tabla del Neatbeans
    public void btnCargar(DefaultTableModel modelo, String nombre) {
        this.conectar();

        // Limpia la tabla
        modelo.setColumnCount(0);
        modelo.setRowCount(0);

        try {

            switch (nombre) {
                case "Producto":
                    s = connect.createStatement();
                    ResultSet rs = s.executeQuery("select * from ReferenciaProducto");

                    modelo.addColumn("Ref Producto");
                    modelo.addColumn("Nombre Producto");
                    modelo.addColumn("Ref Precio");
                    // Recorremos el Array y vamos introduciendo los datos en la tabla
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});
                    }
                    break;
                case "Ventas":
                    s = connect.createStatement();
                    rs = s.executeQuery("select * from Ventas");

                    modelo.addColumn("Ref Producto");
                    modelo.addColumn("Nota");
                    modelo.addColumn("Referencia");
                    // Recorremos el Array y vamos introduciendo los datos en la tabla
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getString(3)});
                    }
                    break;
                case "Precio":
                    s = connect.createStatement();
                    rs = s.executeQuery("select * from Precio");

                    modelo.addColumn("Ref Precio");
                    modelo.addColumn("Precio");

                    // Recorremos el Array y vamos introduciendo los datos en la tabla
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getString(1), rs.getInt(2)});
                    }
                    break;

            }

        } catch (SQLException ex) {
            Logger.getLogger(Logica.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.desconectar();

    }

    public void factura(DefaultTableModel modelo, String pedido) {
        this.conectar();
                // Limpia la tabla
        modelo.setColumnCount(0);
        modelo.setRowCount(0);
        try {

            s = connect.createStatement();
            s1 = connect.createStatement();
            s2 = connect.createStatement();

            rs = s.executeQuery("select * from Ventas where nVenta= " + pedido + "");
            rs2 = s1.executeQuery("select * from ReferenciaProducto where refProducto in(select refProducto from Ventas where nVenta= " + pedido + ")");
            rs3 = s2.executeQuery("select * from Precio where refPrecio in(select refPrecio from ReferenciaProducto where refProducto in(select refProducto from Ventas where nVenta= " + pedido + "))");
            int Cantidad = rs.getInt(3);
            int Pt = rs3.getInt(2);
            int PrecioTotal = Cantidad * Pt;
            modelo.addColumn("NumVenta");
            modelo.addColumn("Nombre Producto");
            modelo.addColumn("Precio");
            while (rs.next()) {
                modelo.addRow(new Object[]{rs.getInt(2), rs2.getString(2), PrecioTotal});
            }
        } catch (SQLException ex) {

            Logger.getLogger(Logica.class.getName()).log(Level.SEVERE, null, ex);

        }

        this.desconectar();
    }

}
