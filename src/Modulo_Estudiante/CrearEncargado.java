/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modulo_Estudiante;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana desde la cual se puede crear un nuevo registro con información de un Encargado para poder asignárselo un
 * Estudiante. Ésta clase es llamada desde CrearEstudiante. Esta ventana tiene atributos que indican cuál es el encargado
 * seleccionado (que se le asignará al Estudiante).
 * @author Wilson Xicará
 */
public class CrearEncargado extends javax.swing.JDialog {
    private Connection conexion;
    private boolean hacerVisible;
    private ArrayList<Integer> listaIDEncargadosRecientes, listaIDEncargadosEncontrados, listaIDMunicipios;
    private int idEncargadoSeleccionado;
    private String nombreEncargadoSeleccionado;
    /**
     * Creates new form CrearEncargado
     */
    public CrearEncargado(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    /**
     * Inicializa la ventana para poder crear un nuevo registro de Encargado, además de obtener los datos necesarios de los
     * encargados creados recientemente (desde que se abre CrearEstudiante).
     * @param parent ventana padre de la actual.
     * @param conexion objeto que permite la conexión con la Base de Datos.
     * @param listaIDEncargadosRecientes lista de enteros con los ID de los Encargados creados recientemente.
     */
    public CrearEncargado(java.awt.Frame parent, Connection conexion, String posibleDireccion, int posibleMunicipio, ArrayList<Integer> listaIDEncargadosRecientes) {
        super(parent, true);
        initComponents();
        this.conexion = conexion;
        hacerVisible = true;
        
        // Obtengo los Municpios desde la Base de Datos
        try {
            ResultSet cConsulta = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                    .executeQuery("SELECT * FROM Municipio");
            listaIDMunicipios = new ArrayList<>();
            while (cConsulta.next()) {
                listaIDMunicipios.add(cConsulta.getInt("Id"));
                encargado_municipio.addItem(cConsulta.getString("Nombre"));
            }
        } catch (SQLException ex) {
            hacerVisible = false;   // Si ocurre un error en este punto, no se mostrará la ventana
            JOptionPane.showMessageDialog(this, "Error al intentar obtener información de la Base de Datos\n"+ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(CrearEncargado.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        // Obtengo los datos de los Encargados que han sido agregados recientemente
        int cantidad = listaIDEncargadosRecientes.size(), idEncargado;
        ResultSet cEncargado, cTelefonos;
        DefaultTableModel modelRecientes = (DefaultTableModel)tabla_encargados_agregados.getModel();
        for(int cont=0; cont<cantidad; cont++) {
            idEncargado = listaIDEncargadosRecientes.get(cont);
            try {
                cEncargado = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                        .executeQuery("SELECT * FROM Encargado WHERE Id = "+idEncargado);
                cTelefonos = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                        .executeQuery("SELECT Telefono FROM Telefono WHERE Encargado_Id = "+idEncargado);
                while (cEncargado.next()) {
                    String[] fecha = cEncargado.getString("FechaNacimiento").split("-");
                    modelRecientes.addRow(new String[]{
                        ""+(tabla_encargados_agregados.getRowCount()+1),
                        cEncargado.getString("DPI"),
                        cEncargado.getString("Nombres"),
                        cEncargado.getString("Apellidos"),
                        cEncargado.getString("Direccion"),
                        encargado_municipio.getItemAt(listaIDMunicipios.indexOf(cEncargado.getInt("Municipio_Id"))),
                        fecha[2]+"/"+fecha[1]+"/"+fecha[0],     // Para que tenga el formato día/mes/año
                        cTelefonos.next() ? cTelefonos.getString(1) : "",   // Teléfono
                        cTelefonos.next() ? cTelefonos.getString(1) : "",   // Celular
                        cEncargado.getString("Trabajo")
                    });
                }
            } catch (SQLException ex) {
                // Si ocurre un error al intentar obtener el registro, elimino su ID de la lista (para evitar repetir el mismo error)
                listaIDEncargadosRecientes.remove(cont);
                cantidad--;
                Logger.getLogger(CrearEncargado.class.getName()).log(Level.SEVERE, null, ex);
            }   // Un error en este punto no cierra la ventana, sólo no muestra el registro
        }
        // Otras configuraciones importantes
        this.listaIDEncargadosRecientes = listaIDEncargadosRecientes;
        listaIDEncargadosEncontrados = new ArrayList<>();
        idEncargadoSeleccionado = -1;   // Si no se selecciona un encargado se queda con -1
        encargado_fechaNacimiento.getJCalendar().setWeekOfYearVisible(false);  // Para no mostrar el número de semana en el Calendario
        tabla_encontrados.setShowHorizontalLines(true); // Para mostrar los bordes de las celdas de la tabla
        tabla_encontrados.setShowVerticalLines(true);
        tabla_encargados_agregados.setShowHorizontalLines(true);    // Para mostrar los bordes de las celdas de la tabla
        tabla_encargados_agregados.setShowVerticalLines(true);
        encargado_direccion.setText(posibleDireccion);
        encargado_municipio.setSelectedIndex(posibleMunicipio);
        this.setLocationRelativeTo(null);   // Para centrar esta ventana sobre la pantalla.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_datos_encargado = new javax.swing.JPanel();
        panel_botones_encargado = new javax.swing.JPanel();
        crear_registro_encargado = new javax.swing.JButton();
        panel_datos_primarios_encargado = new javax.swing.JPanel();
        encargado_dpi = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        encargado_nombres = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        encargado_apellidos = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        buscar_dpi_encargado = new javax.swing.JButton();
        panel_datos_secundarios_encargado = new javax.swing.JPanel();
        encargado_direccion = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        encargado_municipio = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        encargado_fechaNacimiento = new com.toedter.calendar.JDateChooser();
        encargado_telefono = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        encargado_celular = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        etiqueta_encargado_trabajo = new javax.swing.JLabel();
        encargado_trabajo = new javax.swing.JTextField();
        panel_registros_encontrados = new javax.swing.JPanel();
        scroll_tabla_encontrados = new javax.swing.JScrollPane();
        tabla_encontrados = new javax.swing.JTable();
        agregar_registro_encontrado = new javax.swing.JButton();
        panel_registros_recientes = new javax.swing.JPanel();
        scroll_tabla_recientes = new javax.swing.JScrollPane();
        tabla_encargados_agregados = new javax.swing.JTable();
        seleccionar_registro_encargado = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Selección de Encargado");

        panel_datos_encargado.setBackground(new java.awt.Color(153, 153, 255));
        panel_datos_encargado.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información del Encargado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        panel_botones_encargado.setBackground(new java.awt.Color(51, 51, 255));

        crear_registro_encargado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        crear_registro_encargado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/guardar(32x32).png"))); // NOI18N
        crear_registro_encargado.setText("Crear Registro");
        crear_registro_encargado.setEnabled(false);
        crear_registro_encargado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crear_registro_encargadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_botones_encargadoLayout = new javax.swing.GroupLayout(panel_botones_encargado);
        panel_botones_encargado.setLayout(panel_botones_encargadoLayout);
        panel_botones_encargadoLayout.setHorizontalGroup(
            panel_botones_encargadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_botones_encargadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(crear_registro_encargado)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_botones_encargadoLayout.setVerticalGroup(
            panel_botones_encargadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_botones_encargadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(crear_registro_encargado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        panel_datos_primarios_encargado.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        encargado_dpi.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        encargado_dpi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                encargado_dpiKeyTyped(evt);
            }
        });
        panel_datos_primarios_encargado.add(encargado_dpi, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 3, 150, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel21.setText("DPI:");
        panel_datos_primarios_encargado.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(41, 7, -1, -1));

        encargado_nombres.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        panel_datos_primarios_encargado.add(encargado_nombres, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 28, 175, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel23.setText("Nombres:");
        panel_datos_primarios_encargado.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 31, -1, 15));

        encargado_apellidos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        panel_datos_primarios_encargado.add(encargado_apellidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 53, 175, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel25.setText("Apellidos:");
        panel_datos_primarios_encargado.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 56, -1, 15));

        buscar_dpi_encargado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        buscar_dpi_encargado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/buscar(32x33).png"))); // NOI18N
        buscar_dpi_encargado.setText("Buscar coincidencias");
        buscar_dpi_encargado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscar_dpi_encargadoActionPerformed(evt);
            }
        });
        panel_datos_primarios_encargado.add(buscar_dpi_encargado, new org.netbeans.lib.awtextra.AbsoluteConstraints(229, 80, -1, 45));

        panel_datos_secundarios_encargado.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        encargado_direccion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        encargado_direccion.setEnabled(false);
        panel_datos_secundarios_encargado.add(encargado_direccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 3, 300, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel13.setText("Dirección:");
        panel_datos_secundarios_encargado.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 7, -1, -1));

        encargado_municipio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        encargado_municipio.setEnabled(false);
        panel_datos_secundarios_encargado.add(encargado_municipio, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 30, 225, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel17.setText("Municipio:");
        panel_datos_secundarios_encargado.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 33, -1, -1));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel24.setText("Fecha de Nacimiento:");
        panel_datos_secundarios_encargado.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 60, -1, -1));

        encargado_fechaNacimiento.setDateFormatString("yyyy-MM-dd");
        encargado_fechaNacimiento.setEnabled(false);
        encargado_fechaNacimiento.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        panel_datos_secundarios_encargado.add(encargado_fechaNacimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 57, 150, -1));

        encargado_telefono.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        encargado_telefono.setEnabled(false);
        encargado_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                encargado_telefonoKeyTyped(evt);
            }
        });
        panel_datos_secundarios_encargado.add(encargado_telefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 82, 100, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel22.setText("Teléfono:");
        panel_datos_secundarios_encargado.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(79, 85, -1, -1));

        encargado_celular.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        encargado_celular.setEnabled(false);
        encargado_celular.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                encargado_celularKeyTyped(evt);
            }
        });
        panel_datos_secundarios_encargado.add(encargado_celular, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 108, 100, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel15.setText("Celular:");
        panel_datos_secundarios_encargado.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(89, 111, -1, -1));

        etiqueta_encargado_trabajo.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        etiqueta_encargado_trabajo.setText("Trabajo u oficio:");
        panel_datos_secundarios_encargado.add(etiqueta_encargado_trabajo, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 137, -1, -1));

        encargado_trabajo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        encargado_trabajo.setEnabled(false);
        panel_datos_secundarios_encargado.add(encargado_trabajo, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 134, 200, -1));

        javax.swing.GroupLayout panel_datos_encargadoLayout = new javax.swing.GroupLayout(panel_datos_encargado);
        panel_datos_encargado.setLayout(panel_datos_encargadoLayout);
        panel_datos_encargadoLayout.setHorizontalGroup(
            panel_datos_encargadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_datos_primarios_encargado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_datos_secundarios_encargado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_datos_encargadoLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panel_botones_encargado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panel_datos_encargadoLayout.setVerticalGroup(
            panel_datos_encargadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_datos_encargadoLayout.createSequentialGroup()
                .addComponent(panel_datos_primarios_encargado, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_datos_secundarios_encargado, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_botones_encargado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(189, Short.MAX_VALUE))
        );

        panel_registros_encontrados.setBackground(new java.awt.Color(153, 153, 255));
        panel_registros_encontrados.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registros que coinciden con la búsqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        tabla_encontrados.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tabla_encontrados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "DPI", "Nombres", "Apellidos", "Dirección", "Municipio", "Fecha Nacimiento", "Teléfono", "Celular", "Trabajo u oficio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_encontrados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabla_encontrados.setRowHeight(25);
        tabla_encontrados.getTableHeader().setResizingAllowed(false);
        tabla_encontrados.getTableHeader().setReorderingAllowed(false);
        scroll_tabla_encontrados.setViewportView(tabla_encontrados);
        if (tabla_encontrados.getColumnModel().getColumnCount() > 0) {
            tabla_encontrados.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabla_encontrados.getColumnModel().getColumn(1).setPreferredWidth(115);
            tabla_encontrados.getColumnModel().getColumn(2).setPreferredWidth(130);
            tabla_encontrados.getColumnModel().getColumn(3).setPreferredWidth(130);
            tabla_encontrados.getColumnModel().getColumn(4).setPreferredWidth(215);
            tabla_encontrados.getColumnModel().getColumn(5).setPreferredWidth(125);
            tabla_encontrados.getColumnModel().getColumn(6).setPreferredWidth(115);
            tabla_encontrados.getColumnModel().getColumn(7).setPreferredWidth(90);
            tabla_encontrados.getColumnModel().getColumn(8).setPreferredWidth(90);
            tabla_encontrados.getColumnModel().getColumn(9).setPreferredWidth(130);
        }

        agregar_registro_encontrado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        agregar_registro_encontrado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/bajar(16x16).png"))); // NOI18N
        agregar_registro_encontrado.setText("Agregar a la lista");
        agregar_registro_encontrado.setEnabled(false);
        agregar_registro_encontrado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregar_registro_encontradoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_registros_encontradosLayout = new javax.swing.GroupLayout(panel_registros_encontrados);
        panel_registros_encontrados.setLayout(panel_registros_encontradosLayout);
        panel_registros_encontradosLayout.setHorizontalGroup(
            panel_registros_encontradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll_tabla_encontrados, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
            .addGroup(panel_registros_encontradosLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(agregar_registro_encontrado))
        );
        panel_registros_encontradosLayout.setVerticalGroup(
            panel_registros_encontradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_registros_encontradosLayout.createSequentialGroup()
                .addComponent(scroll_tabla_encontrados, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(agregar_registro_encontrado))
        );

        panel_registros_recientes.setBackground(new java.awt.Color(153, 153, 255));
        panel_registros_recientes.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registros agregados recientemente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        tabla_encargados_agregados.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tabla_encargados_agregados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "DPI", "Nombres", "Apellidos", "Dirección", "Municipio", "Fecha Nacimiento", "Teléfono", "Celular", "Trabajo u oficio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_encargados_agregados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabla_encargados_agregados.setRowHeight(25);
        tabla_encargados_agregados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_encargados_agregados.getTableHeader().setResizingAllowed(false);
        tabla_encargados_agregados.getTableHeader().setReorderingAllowed(false);
        tabla_encargados_agregados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabla_encargados_agregadosMousePressed(evt);
            }
        });
        scroll_tabla_recientes.setViewportView(tabla_encargados_agregados);
        if (tabla_encargados_agregados.getColumnModel().getColumnCount() > 0) {
            tabla_encargados_agregados.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabla_encargados_agregados.getColumnModel().getColumn(1).setPreferredWidth(115);
            tabla_encargados_agregados.getColumnModel().getColumn(2).setPreferredWidth(130);
            tabla_encargados_agregados.getColumnModel().getColumn(3).setPreferredWidth(130);
            tabla_encargados_agregados.getColumnModel().getColumn(4).setPreferredWidth(215);
            tabla_encargados_agregados.getColumnModel().getColumn(5).setPreferredWidth(125);
            tabla_encargados_agregados.getColumnModel().getColumn(6).setPreferredWidth(115);
            tabla_encargados_agregados.getColumnModel().getColumn(7).setPreferredWidth(90);
            tabla_encargados_agregados.getColumnModel().getColumn(8).setPreferredWidth(90);
            tabla_encargados_agregados.getColumnModel().getColumn(9).setPreferredWidth(130);
        }

        seleccionar_registro_encargado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        seleccionar_registro_encargado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/aceptar(16x16).png"))); // NOI18N
        seleccionar_registro_encargado.setText("Seleccionar registro");
        seleccionar_registro_encargado.setEnabled(false);
        seleccionar_registro_encargado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionar_registro_encargadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_registros_recientesLayout = new javax.swing.GroupLayout(panel_registros_recientes);
        panel_registros_recientes.setLayout(panel_registros_recientesLayout);
        panel_registros_recientesLayout.setHorizontalGroup(
            panel_registros_recientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll_tabla_recientes)
            .addGroup(panel_registros_recientesLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(seleccionar_registro_encargado))
        );
        panel_registros_recientesLayout.setVerticalGroup(
            panel_registros_recientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_registros_recientesLayout.createSequentialGroup()
                .addComponent(scroll_tabla_recientes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seleccionar_registro_encargado))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_datos_encargado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_registros_encontrados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_registros_recientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_registros_encontrados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_registros_recientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(panel_datos_encargado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Acción que busca coincidencias de los campos DPI, Nombres o Apellidos del nuevo encargado en la Base de Datos.
     * Muestra los registros que coincidan con al menos un campo en la Tabla de Encontrados (desde donde se pueden agregar
     * a la lista de datos recientes). Previo a ello, evalúa que los datos que se buscarán sean correctos.
     * @param evt 
     */
    private void buscar_dpi_encargadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscar_dpi_encargadoActionPerformed
        try {
            validar_datos_encargado(true);
            String dpi = encargado_dpi.getText(), nombres = encargado_nombres.getText(), apellidos = encargado_apellidos.getText();
            int cantidad = listaIDEncargadosRecientes.size(), index;
            // Busco en la tabla de agregados recientemente un registro similar (mismo DPI, Nombres y Apellidos)
            for(index=0; index<cantidad; index++) {
                if (dpi.equals((String)tabla_encargados_agregados.getValueAt(index, 1)) &&
                        nombres.equals((String)tabla_encargados_agregados.getValueAt(index, 2)) &&
                        apellidos.equals((String)tabla_encargados_agregados.getValueAt(index, 3)))
                    break;  // Finalizo el ciclo
            }
            if (index != cantidad) {    // Si el ciclo finaliza sin encontrar coincidencia, index == cantidad
                JOptionPane.showMessageDialog(this, "El registro se encuentra en la fila "+(index+1)+" de\nla Tabla 'Registros agregados recientemente'", "Resultado", JOptionPane.INFORMATION_MESSAGE);
                tabla_encontrados.setRowSelectionInterval(index, index);
            } else {    // Los campos del registro se buscarán en la Base de Datos
                listaIDEncargadosEncontrados.clear();   // Limpio los registros de la búsqueda anterior
                DefaultTableModel modelEncontrados = (DefaultTableModel)tabla_encontrados.getModel();
                modelEncontrados.setRowCount(0);
                // Realizo una consulta hacia la Base de Datos en busca de registros que coincidan
                Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                String instruccion = "SELECT * FROM Encargado WHERE DPI = '"+dpi+"'";
                instruccion+= (nombres.length()!=0) ? " OR Nombres = '"+nombres+"'" : "";
                instruccion+= (apellidos.length()!=0) ? " OR Apellidos = '"+apellidos+"'" : "";
                ResultSet cEncontrados = sentencia.executeQuery(instruccion), cTelefonos;
                while (cEncontrados.next()) {   // Todos los que encuentra, los agrego a la listaEncontrados
                    listaIDEncargadosEncontrados.add(cEncontrados.getInt("Id"));
                    String[] fecha = cEncontrados.getString("FechaNacimiento").split("-");
                    cTelefonos = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery("SELECT Telefono FROM Telefono WHERE Encargado_Id = "+cEncontrados.getInt("Id"));
                    modelEncontrados.addRow(new String[]{
                        ""+(modelEncontrados.getRowCount()+1),
                        cEncontrados.getString("DPI"),
                        cEncontrados.getString("Nombres"),
                        cEncontrados.getString("Apellidos"),
                        cEncontrados.getString("Direccion"),
                        encargado_municipio.getItemAt(listaIDMunicipios.indexOf(cEncontrados.getInt("Municipio_Id"))),
                        fecha[2]+"/"+fecha[1]+"/"+fecha[0],
                        cTelefonos.next() ? cTelefonos.getString(1) : "",   // Teléfono
                        cTelefonos.next() ? cTelefonos.getString(1) : "",   // Celular
                        cEncontrados.getString("Trabajo")
                    });
                }   // Hasta aquí se garantiza la extracción de todos los registros que coinciden con al menos un campo de la búsqueda
                
                // Se habilitan los campos para completar la información del encargado. En caso de existir, dichos campos
                // se deshabilitaran al agregar el registro a la tabla de agregados recientemente.
                setEnabled_campos_secundarios_encargado(true);  // Habilito los campos para completar la información
                // Mensaje en caso de encontrar o no registros que coincidan
                int encontrados = modelEncontrados.getRowCount();
                JOptionPane.showMessageDialog(this,
                        (encontrados>0)?"Se encontr"+((encontrados==1)?"ó un registro":"aron "+encontrados+" registros")+" que posiblemente coincidan con su búsqueda.":"No se encontró algún registro similiar.\n\nPuede crear uno nuevo.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                agregar_registro_encontrado.setEnabled(encontrados > 0);   // Habilito el botón de agregar el registro encontrado si hay por lo menos un registro
            }
        } catch (ExcepcionDatosIncorrectos ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en datos", JOptionPane.ERROR_MESSAGE);
//            Logger.getLogger(CrearEncargado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al intentar obtener los registros que coinciden\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(CrearEncargado.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buscar_dpi_encargadoActionPerformed
    /**
     * Acción que permite guardar los datos del nuevo Encargado en la Base de Datos, para poder ser asignado a un Estudiante.
     * Para ello, comprueba que los datos sean correctos y de que aún no exista uno repetido (dos registros son repetidos si
     * su DPI son los mismos, sin importar los demás campos).
     * @param evt 
     */
    private void crear_registro_encargadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crear_registro_encargadoActionPerformed
        // Antes de crear el registro, evalúo que realmente no exista en la Base de Datos. La manera de comprobarlo es haciendo
        // una consulta en busca del DPI: si existe una coincidencia se concluye que el registro ya existe (omitiendo los campos
        // de nombres y apellidos).
        try {
            validar_datos_encargado(false);
            String dpi = encargado_dpi.getText();
            Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet cEncargado = sentencia.executeQuery("SELECT DPI FROM Encargado WHERE DPI = '"+dpi+"'"), cIdEncargado;
            if (!cEncargado.next()) {   // Si no existe un registro con el mismo DPI
                int opcion = JOptionPane.showOptionDialog(this,
                    "Está seguro que desea crear el nuevo registro?\n\nNo podrá revertir esta acción.",
                    "Seleccione una opción", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (opcion == JOptionPane.YES_OPTION) {
                    // Guardo los datos del encargado en la BD y los cargo a la tabla de Agregados Recientemente (en caso de que se creen varios registros)
                    String nuevoEncargado = "INSERT INTO Encargado(Nombres, Apellidos, Direccion, DPI, FechaNacimiento, Trabajo, Municipio_Id) VALUES (";
                    nuevoEncargado+= "'"+encargado_nombres.getText()+"',";
                    nuevoEncargado+= "'"+encargado_apellidos.getText()+"',";
                    nuevoEncargado+= "'"+encargado_direccion.getText()+"',";
                    nuevoEncargado+= "'"+dpi+"',";
                    Calendar fechaNac = encargado_fechaNacimiento.getCalendar();
                    nuevoEncargado+= "'"+fechaNac.get(Calendar.YEAR)+"-"+(fechaNac.get(Calendar.MONTH)+1)+"-"+fechaNac.get(Calendar.DAY_OF_MONTH)+"', ";
                    nuevoEncargado+= "'"+encargado_trabajo.getText()+"',";
                    nuevoEncargado+= ""+listaIDMunicipios.get(encargado_municipio.getSelectedIndex())+")";
                    conexion.prepareStatement(nuevoEncargado).executeUpdate();  // Inserción del nuevo registro Encargado
                    cIdEncargado = sentencia.executeQuery("SELECT LAST_INSERT_ID()");
                    cIdEncargado.next();
                    int idEncargado = cIdEncargado.getInt(1);
                    listaIDEncargadosRecientes.add(idEncargado);
                    // De momento se crea nuevos teléfonos para cada nuevo Encargado
                    if (encargado_telefono.getText().length() != 0)
                        conexion.prepareStatement("INSERT INTO Telefono(Telefono, Encargado_Id) VALUES('"+encargado_telefono.getText()+"',"+idEncargado+")").executeUpdate();
                    if (encargado_celular.getText().length() != 0)
                        conexion.prepareStatement("INSERT INTO Telefono(Telefono, Encargado_Id) VALUES('"+encargado_celular.getText()+"',"+idEncargado+")").executeUpdate();
                    // HASTA AQUÍ SE GARANTIZA LA CREACIÓN DEL NUEVO REGISTRO dentro de la Base de Datos
                    
                    // Agregación del nuevo registro en la Tabla de Agregados Recientemente
                    DefaultTableModel modelRecientes = (DefaultTableModel)tabla_encargados_agregados.getModel();
                    modelRecientes.addRow(new String[]{
                        ""+(modelRecientes.getRowCount()+1),
                        encargado_dpi.getText(),
                        encargado_nombres.getText(),
                        encargado_apellidos.getText(),
                        encargado_direccion.getText(),
                        (String)encargado_municipio.getSelectedItem(),
                        ""+fechaNac.get(Calendar.DAY_OF_MONTH)+"/"+(fechaNac.get(Calendar.MONTH)+1)+"/"+fechaNac.get(Calendar.YEAR),
                        encargado_telefono.getText(),
                        encargado_celular.getText(),
                        encargado_trabajo.getText()
                    });
                    limpiar_campos_encargado();     // Limpio los campos de entrada
                    setEnabled_campos_secundarios_encargado(false); // Deshabilito los campos de datos secundarios (para obligar a realizar búsquedas)
                }
            } else
                JOptionPane.showMessageDialog(this, "El registro que intenta crear ya\nexiste en la Base de Datos.\n\nNo se podrá crear el nuevo registro.", "Datos duplicados", JOptionPane.ERROR_MESSAGE);
        } catch (ExcepcionDatosIncorrectos ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            Logger.getLogger(CrearEncargado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Conexión fallida con la Base de Datos.\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(CrearEncargado.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_crear_registro_encargadoActionPerformed
    /**
     * Acción que permitirá agregar el ID de un Encargado que coincide con al menos uno de los campos de la búsqueda a la
     * listaIDEncargadosRecientes. Previo a ello, pide confirmación de si el registro buscado está entre un de los que se
     * agregarán a la lista (ya que da la opción de cargar varios registros).
     * @param evt 
     */
    private void agregar_registro_encontradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregar_registro_encontradoActionPerformed
        int[] rango = tabla_encontrados.getSelectedRows();
        if (rango.length > 0) {
            int opcion = JOptionPane.showOptionDialog(this,
                    "El registro que busca "+((rango.length>1)?"está entre los "+(rango.length)+" registros":"es el registro")+" que desea agregar?"
                            + "\n\nTome en cuenta de que si está pero selecciona NO,\nno se podrá crear el nuevo registro más adelnate.",
                    "Seleccione una opción", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (opcion != JOptionPane.CLOSED_OPTION) {  // Al cerrar el OptionDialog, no se realiza la acción
                // Agrego el(los) registro(s) seleccionado(s) a la tabla de Recientes, siempre que los registros no estén agregados
                int contAnterior = listaIDEncargadosRecientes.size();
                int contFil = tabla_encargados_agregados.getRowCount(), contCol = tabla_encargados_agregados.getColumnCount();
                DefaultTableModel modelRecientes = (DefaultTableModel)tabla_encargados_agregados.getModel();
                for(int selected=0; selected<rango.length; selected++) {
                    boolean yaExiste = false;
                    for(int itRecientes=0; itRecientes<contAnterior; itRecientes++) {
                        if (listaIDEncargadosRecientes.get(itRecientes) == listaIDEncargadosEncontrados.get(rango[selected])) {
                            yaExiste = true;
                            itRecientes = contAnterior;
                        }
                    }
                    if (!yaExiste) {    // Copio la fila de Encontrados hacia Agregados Recientemente (eliminando la fila en la primera)
                        String[] datosEncontrados = new String[contCol];
                        for(int i=0; i<contCol; i++)
                            datosEncontrados[i] = (String)tabla_encontrados.getValueAt(selected, i);
                        modelRecientes.addRow(datosEncontrados);
                        
                        // Eliminación del registro de Encontrados y agregación en Recientes
                        listaIDEncargadosRecientes.add(listaIDEncargadosEncontrados.remove(selected));
                        // Actualización de los rangos selecionados (si hay más de uno, desplazar los index una unidad menos
                        for(int j=selected+1; j<rango.length; j++) {
                            tabla_encontrados.setValueAt(rango[j], rango[j], 0);
                            rango[j]--;
                        }
                        ((DefaultTableModel)tabla_encontrados.getModel()).removeRow(selected);
                    }
                }   // Hasta aquí se garantiza la extracción de los registros seleccioandos
                // Ahora evaluo si el usuario confirma que el registro está entre uno de los que seleccionó
                if (opcion == JOptionPane.YES_OPTION) { // Aquí limpio los campos y deshabilitos los secundarios
                    limpiar_campos_encargado();
                    setEnabled_campos_secundarios_encargado(false);
                }   // Si selecciona NO, se espera completar los datos para crear el nuevo registro
            }
        } else
            JOptionPane.showMessageDialog(this, "Seleccione al menos un registro", "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_agregar_registro_encontradoActionPerformed
    /**
     * Acción que permite seleccionar un RegistroEncargado para asignárselo al Estudiante que se creará.
     * @param evt 
     */
    private void seleccionar_registro_encargadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionar_registro_encargadoActionPerformed
        // La tabla tiene la propiedad de sólo seleccionar una fila a la vez
        int index = tabla_encargados_agregados.getSelectedRow();
        idEncargadoSeleccionado = listaIDEncargadosRecientes.get(index);
        nombreEncargadoSeleccionado = (String)tabla_encargados_agregados.getValueAt(index, 2)+" "+(String)tabla_encargados_agregados.getValueAt(index, 3);
        this.dispose(); // Cierro la ventana (almacenando el ID del Encargado que se le asignará al Estudiante)
    }//GEN-LAST:event_seleccionar_registro_encargadoActionPerformed
    /**
     * Eventos para controlar que en los campos DPI, Teléfono y Celular se ingresen sólo dígitos, hasta cierta cantidad.
     */
    private void encargado_dpiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_encargado_dpiKeyTyped
        // Se acepta la tecla si es un dígito y si hay menos de 13 dígitos en el DPI
        if (!Pattern.compile("\\d").matcher(String.valueOf(evt.getKeyChar())).matches() || encargado_dpi.getText().length() == 13)
            evt.consume();
    }//GEN-LAST:event_encargado_dpiKeyTyped
    private void encargado_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_encargado_telefonoKeyTyped
        // Se acepta la tecla si es un dígito y si hay menos de 8 dígitos en el Número de Teléfono
        if (!Pattern.compile("\\d").matcher(String.valueOf(evt.getKeyChar())).matches() || encargado_telefono.getText().length() == 8)
            evt.consume();
    }//GEN-LAST:event_encargado_telefonoKeyTyped
    private void encargado_celularKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_encargado_celularKeyTyped
        // Se acepta la tecla si es un dígito y si hay menos de 8 dígitos en el Número de Celular
        if (!Pattern.compile("\\d").matcher(String.valueOf(evt.getKeyChar())).matches() || encargado_celular.getText().length() == 8)
            evt.consume();
    }//GEN-LAST:event_encargado_celularKeyTyped
    /**
     * Evento que habilita el botón de elegir un encargado para asignárselo a un Estudiante.
     * @param evt 
     */
    private void tabla_encargados_agregadosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_encargados_agregadosMousePressed
        int contador = evt.getClickCount();
        if (contador == 1)
            seleccionar_registro_encargado.setEnabled(true);
        else {
            // La tabla tiene la propiedad de sólo seleccionar una fila a la vez
            int index = tabla_encargados_agregados.getSelectedRow();
            idEncargadoSeleccionado = listaIDEncargadosRecientes.get(index);
            nombreEncargadoSeleccionado = (String)tabla_encargados_agregados.getValueAt(index, 2)+" "+(String)tabla_encargados_agregados.getValueAt(index, 3);
            this.dispose(); // Cierro la ventana (almacenando el ID del Encargado que se le asignará al Estudiante)
        }
    }//GEN-LAST:event_tabla_encargados_agregadosMousePressed
    
    /**
     * Función que valida que los datos del nuevo Encargado sean correctos. Respecto al DPI, Teléfono y Celular, hay eventos
     * para cada caso en el que se permite sólo ingresar dígitos; lo único a evaluar es que no estén vacíos.
     * @param paraBuscar booleano que indica si la validación se hace a la hora de buscar coincidencias en la Base de Datos.
     * Si los datos son para buscar coincidencias, no se necesitan valores en los campos secundarios (pues estarían bloqueados).
     * @throws ExcepcionDatosIncorrectos Excepción que se lanza si un dato es incorrecto.
     */
    private void validar_datos_encargado(boolean paraBuscar) throws ExcepcionDatosIncorrectos {
        if (encargado_dpi.getText().length() != 13)
            throw new ExcepcionDatosIncorrectos("El DPI debe tener 13 dígitos.");
        if (!paraBuscar) {
            if (encargado_nombres.getText().length() == 0 || encargado_apellidos.getText().length() == 0)
                throw new ExcepcionDatosIncorrectos("Los Nombres y Apellidos no pueden ser nulos.");
            if (encargado_direccion.getText().length() == 0)
                throw new ExcepcionDatosIncorrectos("No se ha especificado la Dirección.");
            if (encargado_municipio.getSelectedIndex() == -1)
                throw new ExcepcionDatosIncorrectos("No se ha especificado el Municipio.");
            if (encargado_fechaNacimiento.getDate() == null)
                throw new ExcepcionDatosIncorrectos("No se ha especificado la Fecha de nacimiento.");
            if (encargado_telefono.getText().length()!=0 && encargado_telefono.getText().length()!=8)
                throw new ExcepcionDatosIncorrectos("El Número de Teléfono debe tener 8 dígitos.");
            if (encargado_celular.getText().length()!=0 && encargado_celular.getText().length()!=8)
                throw new ExcepcionDatosIncorrectos("El Número de Celular debe tener 8 dígitos.");
            // El Trabajo no es campo obligatorio
        }
    }
    /**
     * Método que habilita o inhabilita (según el valor de valorEnabled) los campos de datos secundarios de un Encargado.
     * Dichos campos son todos los que se encuentran en panel_datos_secundarios.
     * @param valorEnabled booleano que indica si los campos secundarios se habilitan o inhabilitan.
     */
    private void setEnabled_campos_secundarios_encargado(boolean valorEnabled) {
        encargado_direccion.setEnabled(valorEnabled);
        encargado_municipio.setEnabled(valorEnabled);
        encargado_fechaNacimiento.setEnabled(valorEnabled);
        encargado_telefono.setEnabled(valorEnabled);
        encargado_celular.setEnabled(valorEnabled);
        encargado_trabajo.setEnabled(valorEnabled);
        crear_registro_encargado.setEnabled(valorEnabled);
    }
    /**
     * Método que limpia los campos donde se ingresa la información de un Encargado.
     */
    private void limpiar_campos_encargado() {
        encargado_dpi.setText("");
        encargado_nombres.setText("");
        encargado_apellidos.setText("");
        encargado_direccion.setText("");
        encargado_municipio.setSelectedIndex(-1);
        encargado_fechaNacimiento.setDate(null);
        encargado_telefono.setText("");
        encargado_celular.setText("");
        encargado_trabajo.setText("");
    }
    /**
     * Función que devuelve el valor de 'hacerVisible'. Al obtener datos desde la Base de Datos pueden surgir errores de
     * conexión o con la instrucción SQL por lo que puede arrojar valores erróneos o generar problemas mayores, por lo que
     * se controla y notifica en 'hacerVisible'.
     * @return 'true' si no ocurrió algún problema al intentar obtener datos desde la Base de Datos; 'false' en caso contrario.
     */
    public boolean getHacerVisible() { return hacerVisible; }
    /**
     * Función que retorna el ID del Encargado seleccionado (ya que el Estudiante necesita una relación con un Encargado y
     * esto se logra a través del ID del Encargado).
     * @return entero que es el ID del Encargado seleccionado de todos los que se muestran en esta ventana.
     */
    public int getIdEncargadoSeleccionado() { return idEncargadoSeleccionado; }
    /**
     * Función que devuelve el Nombre completo del Encargado seleccionado.
     * @return el String con el nombre completo del encargado seleccionado.
     */
    public String getNombreEncargadoSeleccionado() { return nombreEncargadoSeleccionado; }
    
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
            java.util.logging.Logger.getLogger(CrearEncargado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CrearEncargado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CrearEncargado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CrearEncargado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CrearEncargado dialog = new CrearEncargado(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregar_registro_encontrado;
    private javax.swing.JButton buscar_dpi_encargado;
    private javax.swing.JButton crear_registro_encargado;
    private javax.swing.JTextField encargado_apellidos;
    private javax.swing.JTextField encargado_celular;
    private javax.swing.JTextField encargado_direccion;
    private javax.swing.JTextField encargado_dpi;
    private com.toedter.calendar.JDateChooser encargado_fechaNacimiento;
    private javax.swing.JComboBox<String> encargado_municipio;
    private javax.swing.JTextField encargado_nombres;
    private javax.swing.JTextField encargado_telefono;
    private javax.swing.JTextField encargado_trabajo;
    private javax.swing.JLabel etiqueta_encargado_trabajo;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JPanel panel_botones_encargado;
    private javax.swing.JPanel panel_datos_encargado;
    private javax.swing.JPanel panel_datos_primarios_encargado;
    private javax.swing.JPanel panel_datos_secundarios_encargado;
    private javax.swing.JPanel panel_registros_encontrados;
    private javax.swing.JPanel panel_registros_recientes;
    private javax.swing.JScrollPane scroll_tabla_encontrados;
    private javax.swing.JScrollPane scroll_tabla_recientes;
    private javax.swing.JButton seleccionar_registro_encargado;
    private javax.swing.JTable tabla_encargados_agregados;
    private javax.swing.JTable tabla_encontrados;
    // End of variables declaration//GEN-END:variables
}
