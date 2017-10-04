/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modulo_Prestamos;

import Modulo_Estudiante.ExcepcionDatosIncorrectos;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Wilson Xicará
 */
public class CrearPrestamoDePaquete extends javax.swing.JFrame {
    private Connection conexion;
    private JFrame ventanaPadre;
    private boolean hacerVisible;
    private ArrayList<Integer> listaIDPaquetes, listaIDEstudiantes;
    private int idCicloActual, indexPaquete, indexEstudiante;
    private String anioActual;
    private TableRowSorter filtroTablaPaquetes, filtroTablaEstudiantes;
    private Date fechaActual;
    /**
     * Creates new form CrearPrestamoDePaquete
     */
    public CrearPrestamoDePaquete() {
        initComponents();
    }
    public CrearPrestamoDePaquete(Connection conexion, JFrame ventanaPadre) {
        initComponents();
        this.conexion = conexion;
        this.ventanaPadre = ventanaPadre;
        hacerVisible = false;    // Esto para habilitar ventanaPadre antes de los return
        
        // Obtención de información necesaria para la creación de préstamos, desde la Base de Datos
        try {
            Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet cConsulta;
            // Obtención de la fecha actual. El ciclo escolar en curso se basará en el año de dicha fecha
            cConsulta = sentencia.executeQuery("SELECT NOW()");
            cConsulta.next();
            fechaActual = cConsulta.getDate(1);
            // Obtención del estado del Ciclo en curso (LISTO y/o CERRADO)
            fecha_pago.setDate(fechaActual);    // Esto para obtener el año en curso
            String anioActual = ""+fecha_pago.getCalendar().get(Calendar.YEAR);
            cConsulta = sentencia.executeQuery("SELECT Id, Listo, Cerrado FROM CicloEscolar WHERE Anio = "+anioActual);
            cConsulta.next();
            if (!cConsulta.getBoolean("Listo") || cConsulta.getBoolean("Cerrado")) {
                JOptionPane.showMessageDialog(this, "No se pueden crear Préstamos de Libros:"
                        + "\n\n  - El Ciclo Escolar "+anioActual+" "+(!cConsulta.getBoolean("Listo") ? "NO ESTÁ LISTO." : "YA FUE CERRADO.")
                        + "\n\nConsulte con el Administrador Principal para más información", "Aviso", JOptionPane.ERROR_MESSAGE);
                ventanaPadre.setEnabled(true);
                return;
            }
            // Obtención de todos los Paquetes que aún no han sido prestados
            cConsulta = sentencia.executeQuery("SELECT PaqueteLibro.Id, PaqueteLibro.Codigo, PaqueteLibro.Descripcion, COUNT(Libro.PaqueteLibro_Id) cantidadLibros FROM PaqueteLibro "
                    + "INNER JOIN Libro ON PaqueteLibro.Id = Libro.PaqueteLibro_Id "
                    + "WHERE PaqueteLibro.Prestado = 0 AND Libro.Estado != '6' "
                    + "GROUP BY PaqueteLibro_Id");
            listaIDPaquetes = new ArrayList<>();
            int contador = 0;
            DefaultTableModel modelPaquetes = (DefaultTableModel)tabla_paquetes.getModel();
            while(cConsulta.next()) {
                listaIDPaquetes.add(cConsulta.getInt("Id"));
                modelPaquetes.addRow(new String[]{
                    ""+(++contador),
                    cConsulta.getString("Codigo"),
                    cConsulta.getString("Descripcion")==null ? "<Sin especificar>" : cConsulta.getString("Descripcion"),
                    ""+cConsulta.getInt("cantidadLibros")
                });
            }
            if (contador == 0) {    // Si no hay Paquetes de Libros no prestados
                JOptionPane.showMessageDialog(this, "No se pueden crear Préstamos de Libros:"
                        + "\n\n  - No hay Paquetes de Libros para prestar."
                        + "\n\nConsulte con el Administrador Principal para más información", "Aviso", JOptionPane.ERROR_MESSAGE);
                ventanaPadre.setEnabled(true);
                return;
            }
            // Obtención de todos los Estudiantes asignados a quienes no se les ha prestado un Paquete en el Ciclo en curso
            cConsulta = sentencia.executeQuery("SELECT Estudiante.Id idEstudiante, Estudiante.Nombres, Estudiante.Apellidos, CONCAT(Grado.Nombre, ' ', Grado.Seccion) gradoSeccion FROM Prestamo "
                    + "LEFT JOIN AsignacionEST ON Prestamo.AsignacionEST_Id = AsignacionEST.Id "
                    + "INNER JOIN Estudiante ON AsignacionEST.Estudiante_Id = Estudiante.Id "
                    + "INNER JOIN Grado ON AsignacionEST.Grado_Id = Grado.Id "
                    + "INNER JOIN CicloEscolar ON AsignacionEST.CicloEscolar_Id = CicloEscolar.Id "
                    + "WHERE CicloEscolar.Anio = "+anioActual+""
                    + "ORDER BY Estudiante.Nombres");
            listaIDEstudiantes = new ArrayList<>();
            contador = 0;
            DefaultTableModel modelEstudiantes = (DefaultTableModel)tabla_estudiantes.getModel();
            while(cConsulta.next()) {
                listaIDEstudiantes.add(cConsulta.getInt("IdEstudiante"));
                modelEstudiantes.addRow(new String[]{
                    ""+(++contador),
                    cConsulta.getString("Nombres"),
                    cConsulta.getString("Apellidos"),
                    cConsulta.getString("gradoSeccion")
                });
            }
            if (contador == 0) {    // Si no hay Estudiantes asignados sin préstamos
                JOptionPane.showMessageDialog(this, "No se pueden crear Préstamos de Libros:"
                        + "\n\n  - No hay Estudiantes asignados al Ciclo Escolar "+anioActual+" que no tengan paquetes prestados."
                        + "\n\nConsulte con el Administrador Principal para más información", "Aviso", JOptionPane.ERROR_MESSAGE);
                ventanaPadre.setEnabled(true);
                return;
            }
            // Otras configuraciones importantes
            filtroTablaPaquetes = new TableRowSorter(tabla_paquetes.getModel()); // Objetos que permite filtrar filas de las Tablas
            filtroTablaEstudiantes = new TableRowSorter(tabla_estudiantes.getModel());
            tabla_paquetes.setShowHorizontalLines(true);  // Para mostrar los bordes de las celdas de la tabla
            tabla_paquetes.setShowVerticalLines(true);
            tabla_estudiantes.setShowHorizontalLines(true);
            tabla_estudiantes.setShowVerticalLines(true);
            fecha_pago.getJCalendar().setWeekOfYearVisible(false);  // Para no mostrar el número de semana en el Calendario
            eliminar_paquete_seleccionado.setVisible(false);    // Estos botones se harán visibles hasta cuando ya esté cargado un registro
            eliminar_estudiante_seleccionado.setVisible(false);
            this.setTitle("Préstamo de Libros en el Ciclo Escolar "+anioActual);
            this.setLocationRelativeTo(null);   // Para centrar esta ventana sobre la pantalla
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se puede extraer alguno de los registros.\n\nDescripción:\n"+ex.getMessage(), "Error en conexión", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(CrearPrestamoDePaquete.class.getName()).log(Level.SEVERE, null, ex);
        }
        ventanaPadre.setEnabled(!(hacerVisible = true));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        etiqueta_titulo = new javax.swing.JLabel();
        regresar = new javax.swing.JButton();
        panel_libros_disponibles = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        filtrar_paquetes_por = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        valor_paquetes_a_filtrar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_paquetes = new javax.swing.JTable();
        seleccionar_paquete = new javax.swing.JButton();
        panel_estudiantes_disponibles = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        filtrar_estudiantes_por = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        valor_estudiantes_a_filtrar = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_estudiantes = new javax.swing.JTable();
        seleccionar_estudiante = new javax.swing.JButton();
        panel_nuevo_prestamo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        codigo_paquete = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        nombre_estudiante = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        codigo_boleta = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        fecha_pago = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        monto_prestamo = new javax.swing.JTextField();
        realizar_prestamo = new javax.swing.JButton();
        cancelar_prestamo = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        eliminar_paquete_seleccionado = new javax.swing.JButton();
        eliminar_estudiante_seleccionado = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Préstamo de Libros");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        etiqueta_titulo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        etiqueta_titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiqueta_titulo.setText("Préstamo de Libros en el Ciclo Escolar _cicloEscolar");

        regresar.setText("Regresar");
        regresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regresarActionPerformed(evt);
            }
        });

        panel_libros_disponibles.setBorder(javax.swing.BorderFactory.createTitledBorder("Paquetes de Libros no prestados"));

        jLabel6.setText("Filtrar por:");

        filtrar_paquetes_por.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Código", "Descripción", "Cantidad de Libros" }));

        jLabel7.setText("Valor:");

        valor_paquetes_a_filtrar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                valor_paquetes_a_filtrarKeyReleased(evt);
            }
        });

        tabla_paquetes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Código", "Descripción", "Cantidad de Libros"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_paquetes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_paquetes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_paquetesMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabla_paquetesMousePressed(evt);
            }
        });
        tabla_paquetes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tabla_paquetesKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tabla_paquetes);

        seleccionar_paquete.setText("Seleccionar Paquete");
        seleccionar_paquete.setEnabled(false);
        seleccionar_paquete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionar_paqueteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_libros_disponiblesLayout = new javax.swing.GroupLayout(panel_libros_disponibles);
        panel_libros_disponibles.setLayout(panel_libros_disponiblesLayout);
        panel_libros_disponiblesLayout.setHorizontalGroup(
            panel_libros_disponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(panel_libros_disponiblesLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filtrar_paquetes_por, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valor_paquetes_a_filtrar, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 56, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_libros_disponiblesLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(seleccionar_paquete))
        );
        panel_libros_disponiblesLayout.setVerticalGroup(
            panel_libros_disponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_libros_disponiblesLayout.createSequentialGroup()
                .addGroup(panel_libros_disponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(filtrar_paquetes_por, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(valor_paquetes_a_filtrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seleccionar_paquete))
        );

        panel_estudiantes_disponibles.setBorder(javax.swing.BorderFactory.createTitledBorder("Estudiantes asignados sin préstamo de paquetes"));

        jLabel8.setText("Filtrar por:");

        filtrar_estudiantes_por.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nombres", "Apellidos", "Grado y Sección" }));

        jLabel9.setText("Valor:");

        valor_estudiantes_a_filtrar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                valor_estudiantes_a_filtrarKeyReleased(evt);
            }
        });

        tabla_estudiantes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Nombres", "Apellidos", "Grado y Sección"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_estudiantes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_estudiantes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_estudiantesMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabla_estudiantesMousePressed(evt);
            }
        });
        tabla_estudiantes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tabla_estudiantesKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tabla_estudiantes);

        seleccionar_estudiante.setText("Seleccionar Estudiante");
        seleccionar_estudiante.setEnabled(false);
        seleccionar_estudiante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionar_estudianteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_estudiantes_disponiblesLayout = new javax.swing.GroupLayout(panel_estudiantes_disponibles);
        panel_estudiantes_disponibles.setLayout(panel_estudiantes_disponiblesLayout);
        panel_estudiantes_disponiblesLayout.setHorizontalGroup(
            panel_estudiantes_disponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(panel_estudiantes_disponiblesLayout.createSequentialGroup()
                .addGroup(panel_estudiantes_disponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_estudiantes_disponiblesLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filtrar_estudiantes_por, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valor_estudiantes_a_filtrar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(seleccionar_estudiante))
                .addGap(0, 21, Short.MAX_VALUE))
        );
        panel_estudiantes_disponiblesLayout.setVerticalGroup(
            panel_estudiantes_disponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_estudiantes_disponiblesLayout.createSequentialGroup()
                .addGroup(panel_estudiantes_disponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(filtrar_estudiantes_por, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(valor_estudiantes_a_filtrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seleccionar_estudiante))
        );

        panel_nuevo_prestamo.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del nuevo Préstamo"));

        jLabel1.setText("Código del Paquete:");

        codigo_paquete.setEditable(false);

        jLabel2.setText("Estudiante:");

        nombre_estudiante.setEditable(false);

        jLabel3.setText("Código de Boleta");

        jLabel4.setText("Fecha de pago:");

        jLabel5.setText("Monto: Q.");

        monto_prestamo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                monto_prestamoFocusLost(evt);
            }
        });
        monto_prestamo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                monto_prestamoKeyTyped(evt);
            }
        });

        realizar_prestamo.setText("Realizar Préstamo");
        realizar_prestamo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realizar_prestamoActionPerformed(evt);
            }
        });

        cancelar_prestamo.setText("Cancelar");
        cancelar_prestamo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelar_prestamoActionPerformed(evt);
            }
        });

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Detalle del Préstamo:");

        eliminar_paquete_seleccionado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar(16x16).png"))); // NOI18N
        eliminar_paquete_seleccionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminar_paquete_seleccionadoActionPerformed(evt);
            }
        });

        eliminar_estudiante_seleccionado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar(16x16).png"))); // NOI18N
        eliminar_estudiante_seleccionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminar_estudiante_seleccionadoActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Los campos en negrita son obligatorios");

        javax.swing.GroupLayout panel_nuevo_prestamoLayout = new javax.swing.GroupLayout(panel_nuevo_prestamo);
        panel_nuevo_prestamo.setLayout(panel_nuevo_prestamoLayout);
        panel_nuevo_prestamoLayout.setHorizontalGroup(
            panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panel_nuevo_prestamoLayout.createSequentialGroup()
                .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panel_nuevo_prestamoLayout.createSequentialGroup()
                            .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panel_nuevo_prestamoLayout.createSequentialGroup()
                                    .addComponent(codigo_paquete, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(eliminar_paquete_seleccionado))
                                .addGroup(panel_nuevo_prestamoLayout.createSequentialGroup()
                                    .addComponent(nombre_estudiante, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(eliminar_estudiante_seleccionado))))
                        .addGroup(panel_nuevo_prestamoLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(codigo_boleta, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fecha_pago, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panel_nuevo_prestamoLayout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(monto_prestamo)
                            .addGap(262, 262, 262)))
                    .addComponent(jLabel11))
                .addGap(0, 222, Short.MAX_VALUE))
            .addGroup(panel_nuevo_prestamoLayout.createSequentialGroup()
                .addGap(203, 203, 203)
                .addComponent(realizar_prestamo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelar_prestamo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_nuevo_prestamoLayout.setVerticalGroup(
            panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_nuevo_prestamoLayout.createSequentialGroup()
                .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(codigo_paquete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eliminar_paquete_seleccionado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nombre_estudiante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eliminar_estudiante_seleccionado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(codigo_boleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(fecha_pago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(monto_prestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel_nuevo_prestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(realizar_prestamo)
                    .addComponent(cancelar_prestamo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_libros_disponibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_estudiantes_disponibles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiqueta_titulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regresar))
            .addGroup(layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(panel_nuevo_prestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(regresar)
                    .addComponent(etiqueta_titulo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_libros_disponibles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_estudiantes_disponibles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_nuevo_prestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void valor_paquetes_a_filtrarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valor_paquetes_a_filtrarKeyReleased
        String valor = valor_paquetes_a_filtrar.getText();
        if (valor.length() != 0) {
            int filtro = filtrar_paquetes_por.getSelectedIndex();    // Obtengo el tipo de filtro en el que se basará la búsqueda
            filtroTablaPaquetes.setRowFilter(RowFilter.regexFilter(valor, filtro+1));   // No se filtra por 'No.'
            tabla_paquetes.setRowSorter(filtroTablaPaquetes);
            if (seleccionar_paquete.isEnabled()) {  // Al filtrar las filas, se pierde la última fila seleccionada
                seleccionar_paquete.setEnabled(false);
                indexPaquete = -1;
            }
        }
    }//GEN-LAST:event_valor_paquetes_a_filtrarKeyReleased

    private void valor_estudiantes_a_filtrarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valor_estudiantes_a_filtrarKeyReleased
        String valor = valor_estudiantes_a_filtrar.getText();
        if (valor.length() != 0) {
            int filtro = filtrar_estudiantes_por.getSelectedIndex();    // Obtengo el tipo de filtro en el que se basará la búsqueda
            filtroTablaEstudiantes.setRowFilter(RowFilter.regexFilter(valor, filtro+1));   // No se filtra por 'No.'
            tabla_estudiantes.setRowSorter(filtroTablaEstudiantes);
            if (seleccionar_estudiante.isEnabled()) { // Al filtrar las filas, se pierde la última fila seleccionada
                seleccionar_estudiante.setEnabled(false);
                indexEstudiante = -1;
            }
        }
    }//GEN-LAST:event_valor_estudiantes_a_filtrarKeyReleased

    private void tabla_paquetesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_paquetesMousePressed
        if (!seleccionar_paquete.isEnabled()) {
            seleccionar_paquete.setEnabled(true);
        }
    }//GEN-LAST:event_tabla_paquetesMousePressed

    private void tabla_estudiantesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_estudiantesMousePressed
        if (!seleccionar_estudiante.isEnabled()) {
            seleccionar_estudiante.setEnabled(true);
        }
    }//GEN-LAST:event_tabla_estudiantesMousePressed

    private void seleccionar_paqueteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionar_paqueteActionPerformed
        cargar_paquete_seleccionado();
    }//GEN-LAST:event_seleccionar_paqueteActionPerformed

    private void tabla_paquetesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_paquetesMouseClicked
        cargar_paquete_seleccionado();
    }//GEN-LAST:event_tabla_paquetesMouseClicked

    private void tabla_paquetesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabla_paquetesKeyReleased
        if ((int)evt.getKeyChar()==10 && tabla_paquetes.isEnabled() && tabla_paquetes.getSelectedRow()!=-1)
            cargar_paquete_seleccionado();
    }//GEN-LAST:event_tabla_paquetesKeyReleased

    private void seleccionar_estudianteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionar_estudianteActionPerformed
        cargar_estudiante_seleccionado();
    }//GEN-LAST:event_seleccionar_estudianteActionPerformed

    private void tabla_estudiantesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_estudiantesMouseClicked
        cargar_estudiante_seleccionado();
    }//GEN-LAST:event_tabla_estudiantesMouseClicked

    private void tabla_estudiantesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabla_estudiantesKeyReleased
        if ((int)evt.getKeyChar()==10 && tabla_estudiantes.isEnabled() && tabla_estudiantes.getSelectedRow()!=-1)
            cargar_estudiante_seleccionado();
    }//GEN-LAST:event_tabla_estudiantesKeyReleased

    private void eliminar_paquete_seleccionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminar_paquete_seleccionadoActionPerformed
        indexPaquete = -1;  // Indicador de que no se ha seleccionado un paquete
        filtrar_paquetes_por.setEnabled(true);     // Haabilito los componentes de Paquetes para poder seleccionar otro registro
        valor_paquetes_a_filtrar.setEnabled(true);
        tabla_paquetes.setEnabled(true);
        seleccionar_paquete.setEnabled(true);
        codigo_paquete.setText(""); // Borro el Código del Paquete del detalle del préstamo
        eliminar_paquete_seleccionado.setVisible(false);
    }//GEN-LAST:event_eliminar_paquete_seleccionadoActionPerformed

    private void eliminar_estudiante_seleccionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminar_estudiante_seleccionadoActionPerformed
        indexEstudiante = -1;  // Indicador de que no se ha seleccionado un estudiante
        filtrar_estudiantes_por.setEnabled(true);     // Habilito los componentes de Estudiantes para poder seleccionar otro registro
        valor_estudiantes_a_filtrar.setEnabled(true);
        tabla_estudiantes.setEnabled(true);
        seleccionar_estudiante.setEnabled(true);
        nombre_estudiante.setText("");  // Borro la información del Estudiante del detalle del préstamo
        eliminar_estudiante_seleccionado.setVisible(false);
    }//GEN-LAST:event_eliminar_estudiante_seleccionadoActionPerformed

    private void monto_prestamoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_monto_prestamoKeyTyped
        char tecla = evt.getKeyChar();
        if (tecla == '.') {
            if (monto_prestamo.getText().contains("."))
                evt.consume();  // No se permite ingresar un valor como NN.(...).N (con dos puntos)
            else if (monto_prestamo.getText().length() == 0)
                monto_prestamo.setText("0");   // Si la primera tecla es '.' se convierte en '0.'
        } else if (!Pattern.compile("\\d").matcher(String.valueOf(evt.getKeyChar())).matches())
            evt.consume();
    }//GEN-LAST:event_monto_prestamoKeyTyped

    private void monto_prestamoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_monto_prestamoFocusLost
        if (monto_prestamo.getText().length() == 0)
            monto_prestamo.setText("0.00");
        else if (monto_prestamo.getText().indexOf(".") == (monto_prestamo.getText().length()-1))
            monto_prestamo.setText(monto_prestamo.getText()+"00");
        else
            monto_prestamo.setText(String.format("%.2f", Float.parseFloat(monto_prestamo.getText())));
        // Al perder el focus, por lo menos la Cantidad Total es de 0.00
    }//GEN-LAST:event_monto_prestamoFocusLost

    private void realizar_prestamoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realizar_prestamoActionPerformed
        try {
            validar_datos_prestamo();
            int opcion = JOptionPane.showOptionDialog(this,
                    "Detalle del Préstamo:"
                            + "\nCódigo de Paquete: "+codigo_paquete.getText()
                            + "\nEstudiante:           "+nombre_estudiante.getText()
                            + "\n\nDesea continuar?",
                    "Realizar préstamo", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (opcion == JOptionPane.YES_OPTION) {
                Calendar fecha = fecha_pago.getCalendar();
                // Creación del Préstamo en la Base de Datos
                conexion.prepareStatement("INSERT INTO Prestamo(PaqueteLibro_Id, AsignacionEST_Id, CodigoBoleta, FechaPago, Monto) VALUES("
                        + ""+listaIDPaquetes.get(indexPaquete)+", "+listaIDEstudiantes.get(indexEstudiante)+", "
                        + "'"+codigo_boleta.getText()+"', "
                        + "'"+fecha.get(Calendar.YEAR)+"-"+(fecha.get(Calendar.MONTH)+1)+"-"+fecha.get(Calendar.DAY_OF_MONTH)+"', "
                        + ""+monto_prestamo.getText()+")").executeUpdate();
                // Actualización del Paquete de Libros como prestado
                conexion.prepareStatement("UPDATE PaqueteLibro SET Prestado = 1 WHERE Id = "+listaIDPaquetes.get(indexPaquete)).executeUpdate();
                JOptionPane.showMessageDialog(this, "Préstamo realizado con éxito", "Realizar préstamo", JOptionPane.INFORMATION_MESSAGE);
                // Eliminación del Paquete prestado de la tabla Paquetes
                listaIDPaquetes.remove(indexPaquete);
                String valorFiltrado = valor_paquetes_a_filtrar.getText();
                filtroTablaPaquetes.setRowFilter(RowFilter.regexFilter(""));
                tabla_paquetes.setRowSorter(filtroTablaPaquetes);   // Muestro todos los registros de la tabla Paquetes
                int cantidad = tabla_paquetes.getRowCount(), fil;
                for(fil=indexPaquete+1; fil<cantidad; fil++)
                    tabla_paquetes.setValueAt(""+fil, fil, 0);    // Actualizo el No. de los registros que preceden al que se eliminará
                ((DefaultTableModel)tabla_paquetes.getModel()).removeRow(indexPaquete);
                filtroTablaPaquetes.setRowFilter(RowFilter.regexFilter(valorFiltrado));
                tabla_paquetes.setRowSorter(filtroTablaPaquetes);   // Muestro los registros de la última filtración
                // Eliminación del Estudiante al que se le realizó el préstamo de la tabla Estudiantes
                listaIDEstudiantes.remove(indexEstudiante);
                valorFiltrado = valor_estudiantes_a_filtrar.getText();
                filtroTablaEstudiantes.setRowFilter(RowFilter.regexFilter(""));
                tabla_estudiantes.setRowSorter(filtroTablaEstudiantes);   // Muestro todos los registros de la tabla Estudiantes
                cantidad = tabla_estudiantes.getRowCount();
                for(fil=indexEstudiante+1; fil<cantidad; fil++)
                    tabla_estudiantes.setValueAt(""+fil, fil, 0);    // Actualizo el No. de los registros que preceden al que se eliminará
                ((DefaultTableModel)tabla_estudiantes.getModel()).removeRow(indexEstudiante);
                filtroTablaEstudiantes.setRowFilter(RowFilter.regexFilter(valorFiltrado));
                tabla_estudiantes.setRowSorter(filtroTablaEstudiantes);   // Muestro los registros de la última filtración
                // Limpieza de los campos de especificación del Préstamo
                codigo_paquete.setText("");
                eliminar_paquete_seleccionado.setVisible(false);
                indexPaquete = -1;
                nombre_estudiante.setText("");
                eliminar_estudiante_seleccionado.setVisible(false);
                indexEstudiante = -1;
                codigo_boleta.setText("");
                fecha_pago.setDate(fechaActual);
                monto_prestamo.setText("");
                // HASTA AQUÍ SE GARANTIZA LA CREACIÓN DEL PRÉSTAMO DE UN PAQUETE DE LIBROS
            }
        } catch (ExcepcionDatosIncorrectos ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en datos", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(CrearPrestamoDePaquete.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se puede crear el registro del Préstamo.\n\nDescripción:\n"+ex.getMessage(), "Error en conexión", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(CrearPrestamoDePaquete.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_realizar_prestamoActionPerformed

    private void cancelar_prestamoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelar_prestamoActionPerformed
        codigo_paquete.setText(""); // Limpieza de los campos de especificación del Préstamo
        eliminar_paquete_seleccionado.setVisible(false);
        indexPaquete = -1;
        nombre_estudiante.setText("");
        eliminar_estudiante_seleccionado.setVisible(false);
        indexEstudiante = -1;
        codigo_boleta.setText("");
        fecha_pago.setDate(fechaActual);
        monto_prestamo.setText("");
    }//GEN-LAST:event_cancelar_prestamoActionPerformed

    private void regresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regresarActionPerformed
        ventanaPadre.setEnabled(true);
        this.dispose();
    }//GEN-LAST:event_regresarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        ventanaPadre.setEnabled(true);
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    public boolean getHacerVisible() { return hacerVisible; }
    private void cargar_paquete_seleccionado() {
        indexPaquete = filtroTablaPaquetes.convertRowIndexToModel(tabla_paquetes.getSelectedRow());
        filtrar_paquetes_por.setEnabled(false);     // Inhabilito los componentes de Paquetes para evitar seleccionar otro registro
        valor_paquetes_a_filtrar.setEnabled(false);
        tabla_paquetes.setEnabled(false);
        seleccionar_paquete.setEnabled(false);
        // Cargo el Código del Paquete en el detalle del préstamo
        codigo_paquete.setText(((DefaultTableModel)tabla_paquetes.getModel()).getValueAt(indexPaquete, 1).toString());
        eliminar_paquete_seleccionado.setVisible(true);
    }
    private void cargar_estudiante_seleccionado() {
        indexEstudiante = filtroTablaEstudiantes.convertRowIndexToModel(tabla_estudiantes.getSelectedRow());
        filtrar_estudiantes_por.setEnabled(false);     // Inhabilito los componentes de Estudiantes para evitar seleccionar otro registro
        valor_estudiantes_a_filtrar.setEnabled(false);
        tabla_estudiantes.setEnabled(false);
        seleccionar_estudiante.setEnabled(false);
        // Cargo los Nombres y Apellidos del Estudiante en el detalle del préstamo
        DefaultTableModel modelEstudiantes = (DefaultTableModel)tabla_estudiantes.getModel();
        nombre_estudiante.setText(modelEstudiantes.getValueAt(indexEstudiante, 1).toString()+" "+modelEstudiantes.getValueAt(indexEstudiante, 2));
        eliminar_estudiante_seleccionado.setVisible(true);
    }
    private void validar_datos_prestamo() throws ExcepcionDatosIncorrectos {
        if (indexPaquete == -1)
            throw new ExcepcionDatosIncorrectos("Seleccione el Paquete de Libros a prestar");
        if (indexEstudiante == -1)
            throw new ExcepcionDatosIncorrectos("Seleccione el Estudiante a quien se el prestarán los libros");
        if (codigo_boleta.getText().length() == 0)
            throw new ExcepcionDatosIncorrectos("Especifique el Código de la Boleta");
        if (fecha_pago.getDate() == null)
            throw new ExcepcionDatosIncorrectos("Especifique la Fecha de la Boleta");
        if (monto_prestamo.getText().length() == 0) // Los eventos sobre el monto hacen que tenga el formato correcto
            throw new ExcepcionDatosIncorrectos("Especifique el Monto del Préstamo");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CrearPrestamoDePaquete.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CrearPrestamoDePaquete.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CrearPrestamoDePaquete.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CrearPrestamoDePaquete.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CrearPrestamoDePaquete().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelar_prestamo;
    private javax.swing.JTextField codigo_boleta;
    private javax.swing.JTextField codigo_paquete;
    private javax.swing.JButton eliminar_estudiante_seleccionado;
    private javax.swing.JButton eliminar_paquete_seleccionado;
    private javax.swing.JLabel etiqueta_titulo;
    private com.toedter.calendar.JDateChooser fecha_pago;
    private javax.swing.JComboBox<String> filtrar_estudiantes_por;
    private javax.swing.JComboBox<String> filtrar_paquetes_por;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField monto_prestamo;
    private javax.swing.JTextField nombre_estudiante;
    private javax.swing.JPanel panel_estudiantes_disponibles;
    private javax.swing.JPanel panel_libros_disponibles;
    private javax.swing.JPanel panel_nuevo_prestamo;
    private javax.swing.JButton realizar_prestamo;
    private javax.swing.JButton regresar;
    private javax.swing.JButton seleccionar_estudiante;
    private javax.swing.JButton seleccionar_paquete;
    private javax.swing.JTable tabla_estudiantes;
    private javax.swing.JTable tabla_paquetes;
    private javax.swing.JTextField valor_estudiantes_a_filtrar;
    private javax.swing.JTextField valor_paquetes_a_filtrar;
    // End of variables declaration//GEN-END:variables
}
