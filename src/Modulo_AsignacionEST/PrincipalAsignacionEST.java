/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modulo_AsignacionEST;

import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Clase que permite crear VARIAS ASIGNACIONES O REASIGNACIONES PARA VARIOS ESTUDIANTES.
 * Las ASIGNACIONES consisten en buscar a todos los estudiantes que no tienen ninguna asignación (cuando un estudiante ha sido
 * agregado recientemente a la Base de Datos) y los muestra en la Tabla para poder crearles una Asignación por primera vez.
 * Las REASIGNACIONES consisten en seleccionar un Ciclo Escolar anterior y cargar los datos de todos los estudiantes que tienen
 * una Asignación en dicho ciclo. Esto es útil cuando un estudiante aumenta de Grado.
 * Esta ventana llama a AsignarEstudiante después de que se realiza una búsqueda de Estudiantes y se seleccione alguno (no
 * es de interés evaluar aquí si el Ciclo Escolar está Listo o no está Cerrado, ya que sólo muestra información).
 * @author Wilson Xicará
 */
public class PrincipalAsignacionEST extends javax.swing.JFrame {
    private Connection conexion;
    private JFrame ventanaPadre;
    private boolean hacerVisible, paraReasignacion, ciclosCargados;
    private ArrayList<Integer> listaIDEstudiantes, listaIDCiclos, listaIDGrados;
    private ArrayList<Boolean> listaCicloListo, listaCicloCerrado;
    /**
     * Creates new form PrincipalAsignacion
     */
    public PrincipalAsignacionEST() {
        initComponents();
    }
    /**
     * Inicializa la ventana para mostrar estudiantes para Asignación o Reasignación. Si son Asignaciones, muestra el listado
     * de Estudiantes que no han tenido alguna asignación; si son Reasignaciones, obtiene todos los ciclos escolares existentes
     * desde los cuales se pueden mostrar a los estudiantes que tiene una asignación en dicho ciclo escolar. En ambos casos,
     * obtiene los datos desde la Base de Datos.
     * @param conexion objeto que permite la conexión con la Base de Datos.
     * @param ventanaPadre ventana desde la cual se llama a ésta ventana (al llamar a esta ventana 'ventanaPadre' se inhabilita
     * por lo que al cerrar ésta ventana se debe de habilitar 'ventanaPadre').
     * @param paraReasignacion booleano que indica si se llama a esta ventana para Asignaciones o Reasignaciones.
     */
    public PrincipalAsignacionEST(Connection conexion, JFrame ventanaPadre, boolean paraReasignacion) {
        initComponents();
        this.conexion = conexion;
        this.ventanaPadre = ventanaPadre;
        this.paraReasignacion = paraReasignacion;
        hacerVisible = !(ciclosCargados = false);    // Inicialmente se intentará mostrar la ventana, y no se han cargado datos
        
        // Obtengo los datos necesarios desde la Base de Datos
        try {
            Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet cConsulta;
            // Verifico que exista por lo menos un Ciclo Escolar
            cConsulta = sentencia.executeQuery("SELECT COUNT(Id) FROM CicloEscolar");
            cConsulta.next();
            if (cConsulta.getInt(1) == 0) {
                hacerVisible = false;
                JOptionPane.showMessageDialog(this, "No se ha creado algún Ciclo Escolar.\nConsulte con el Administrador para más información", "Datos faltantes", JOptionPane.ERROR_MESSAGE);
                ventanaPadre.setEnabled(true); // Si no se mostrará esta ventana, habilito ventanaPadre para evitar que quede inhabilitada
                return;
            }
            listaIDEstudiantes = new ArrayList<>();
            if (paraReasignacion) { // Obtengo el listado de todos los Ciclos Escolares existentes en la Base de Datos
                buscar_para_reasignacion.setEnabled(true);
                // Obtengo el listado de Ciclos Escolares de los cuales se puede buscar y crear Asignaciones
                cConsulta = sentencia.executeQuery("SELECT Id, Anio, Listo, Cerrado FROM CicloEscolar");
                listaIDCiclos = new ArrayList<>();
                listaCicloListo = new ArrayList<>();
                listaCicloCerrado = new ArrayList<>();
                listaIDGrados = new ArrayList<>();
                while (cConsulta.next()) {
                    listaIDCiclos.add(cConsulta.getInt("Id"));
                    listaCicloListo.add(cConsulta.getBoolean("Listo"));
                    listaCicloCerrado.add(cConsulta.getBoolean("Cerrado"));
                    ciclo_escolar.addItem(cConsulta.getString("Anio"));
                } ciclosCargados = true;
                ciclo_escolar.setSelectedIndex(-1);
                ciclo_escolar.setSelectedIndex(ciclo_escolar.getItemCount()-1);
            } else {    // Obtengo todos los Estudiantes que no tienen ninguna Asignación
                cConsulta = sentencia.executeQuery("SELECT Estudiante.Id idEstudiante, Estudiante.CodigoPersonal, CONCAT(Estudiante.Nombres, ' ', Estudiante.Apellidos) nombreEstudiante FROM Estudiante "
                        + "LEFT JOIN AsignacionEST ON Estudiante.Id = AsignacionEST.Estudiante_Id "
                        + "WHERE AsignacionEst.Id IS NULL");
                DefaultTableModel modelEstudiantes = (DefaultTableModel)tabla_estudiantes.getModel();
                // Agrego los Estudiantes sin ninguna Asignación a la tabla_estudiantes
                int contador = 0;
                while (cConsulta.next()) {
                    listaIDEstudiantes.add(cConsulta.getInt("idEstudiante"));
                    modelEstudiantes.addRow(new String[]{
                        ""+(++contador),
                        cConsulta.getString("CodigoPersonal"),
                        cConsulta.getString("nombreEstudiante"),
                        "No"    // Indicador de que no se le ha creado una Asignación nueva
                    });
                }   // Hasta aquí se garantiza la obtención de todos los estudiantes sin ninguna Asignación
                panel_reasignacion.setVisible(false);   // Oculto el panel donde se puede filtrar búsquedas
                panel_estudiantes_encontrados.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hay "+contador+" estudiante"+((contador==1)?"":"s")+" sin Asignación", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14)));
            }
            // Otras configuraciones importantes
            this.setTitle(((paraReasignacion)?"Rea":"A")+"signación de Estudiantes");
            this.setLocationRelativeTo(null);   // Para centrar esta ventana sobre la pantalla.
        } catch (SQLException ex) {
            hacerVisible = false;
            JOptionPane.showMessageDialog(this, "No se puede obtener algunos datos.\n\nDescripción:\n"+ex.getMessage(), "Error en conexión", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(PrincipalAsignacionEST.class.getName()).log(Level.SEVERE, null, ex);
        }
        ventanaPadre.setEnabled(!hacerVisible); // Si no se mostrará esta ventana, habilito ventanaPadre para evitar que quede inhabilitada
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_reasignacion = new javax.swing.JPanel();
        etiqueta_ciclo_escolar = new javax.swing.JLabel();
        ciclo_escolar = new javax.swing.JComboBox<>();
        etiqueta_grado = new javax.swing.JLabel();
        grado = new javax.swing.JComboBox<>();
        buscar_para_reasignacion = new javax.swing.JButton();
        panel_estudiantes_encontrados = new javax.swing.JPanel();
        etiqueta_aviso = new javax.swing.JLabel();
        panel_historial_asignaciones = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_historial_asignaciones = new javax.swing.JTable();
        crear_asignacion = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_estudiantes = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Asignación/Reasignación de Estudiantes");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panel_reasignacion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seleccione el Ciclo Escolar del que desea Reasignar:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        etiqueta_ciclo_escolar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        etiqueta_ciclo_escolar.setText("CIclo Escolar:");
        etiqueta_ciclo_escolar.setEnabled(false);

        ciclo_escolar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ciclo_escolar.setEnabled(false);
        ciclo_escolar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ciclo_escolarItemStateChanged(evt);
            }
        });

        etiqueta_grado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        etiqueta_grado.setText("Grado:");
        etiqueta_grado.setEnabled(false);

        grado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        grado.setEnabled(false);

        buscar_para_reasignacion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buscar_para_reasignacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/buscar(16x16).png"))); // NOI18N
        buscar_para_reasignacion.setText("Nueva Búsqueda");
        buscar_para_reasignacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscar_para_reasignacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_reasignacionLayout = new javax.swing.GroupLayout(panel_reasignacion);
        panel_reasignacion.setLayout(panel_reasignacionLayout);
        panel_reasignacionLayout.setHorizontalGroup(
            panel_reasignacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_reasignacionLayout.createSequentialGroup()
                .addComponent(etiqueta_ciclo_escolar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ciclo_escolar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiqueta_grado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(grado, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buscar_para_reasignacion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_reasignacionLayout.setVerticalGroup(
            panel_reasignacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_reasignacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(etiqueta_grado)
                .addComponent(grado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(buscar_para_reasignacion))
            .addGroup(panel_reasignacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(etiqueta_ciclo_escolar)
                .addComponent(ciclo_escolar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panel_estudiantes_encontrados.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estudiantes no asignados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        etiqueta_aviso.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N

        panel_historial_asignaciones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Historial de asignaciones:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        tabla_historial_asignaciones.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tabla_historial_asignaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Ciclo Escolar", "Grado", "Seccion"
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
        tabla_historial_asignaciones.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabla_historial_asignaciones.setRowHeight(25);
        tabla_historial_asignaciones.getTableHeader().setResizingAllowed(false);
        tabla_historial_asignaciones.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabla_historial_asignaciones);
        if (tabla_historial_asignaciones.getColumnModel().getColumnCount() > 0) {
            tabla_historial_asignaciones.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabla_historial_asignaciones.getColumnModel().getColumn(1).setPreferredWidth(100);
            tabla_historial_asignaciones.getColumnModel().getColumn(2).setPreferredWidth(100);
            tabla_historial_asignaciones.getColumnModel().getColumn(3).setPreferredWidth(75);
        }

        javax.swing.GroupLayout panel_historial_asignacionesLayout = new javax.swing.GroupLayout(panel_historial_asignaciones);
        panel_historial_asignaciones.setLayout(panel_historial_asignacionesLayout);
        panel_historial_asignacionesLayout.setHorizontalGroup(
            panel_historial_asignacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
        );
        panel_historial_asignacionesLayout.setVerticalGroup(
            panel_historial_asignacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
        );

        crear_asignacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        crear_asignacion.setText("Crear Asignación");
        crear_asignacion.setEnabled(false);
        crear_asignacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crear_asignacionActionPerformed(evt);
            }
        });

        tabla_estudiantes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tabla_estudiantes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Código Personal", "Nombre completo", "Asignación nueva"
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
        tabla_estudiantes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabla_estudiantes.setRowHeight(25);
        tabla_estudiantes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_estudiantes.getTableHeader().setReorderingAllowed(false);
        tabla_estudiantes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabla_estudiantesMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tabla_estudiantes);
        if (tabla_estudiantes.getColumnModel().getColumnCount() > 0) {
            tabla_estudiantes.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabla_estudiantes.getColumnModel().getColumn(1).setPreferredWidth(115);
            tabla_estudiantes.getColumnModel().getColumn(2).setPreferredWidth(300);
            tabla_estudiantes.getColumnModel().getColumn(3).setPreferredWidth(115);
        }

        javax.swing.GroupLayout panel_estudiantes_encontradosLayout = new javax.swing.GroupLayout(panel_estudiantes_encontrados);
        panel_estudiantes_encontrados.setLayout(panel_estudiantes_encontradosLayout);
        panel_estudiantes_encontradosLayout.setHorizontalGroup(
            panel_estudiantes_encontradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_estudiantes_encontradosLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(etiqueta_aviso))
            .addGroup(panel_estudiantes_encontradosLayout.createSequentialGroup()
                .addComponent(panel_historial_asignaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crear_asignacion)
                .addGap(0, 147, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        panel_estudiantes_encontradosLayout.setVerticalGroup(
            panel_estudiantes_encontradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_estudiantes_encontradosLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiqueta_aviso)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_estudiantes_encontradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_historial_asignaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_estudiantes_encontradosLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(crear_asignacion))))
        );

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/atras(25x26).PNG"))); // NOI18N
        jButton1.setText("Regresar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_estudiantes_encontrados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panel_reasignacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_reasignacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_estudiantes_encontrados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Acción que se ejecuta cuando se selecciona un nuevo Ciclo Escolar. Para ello, se obtiene todos los Grados asignados
     * al ciclo escolar seleccionado y se carga en (JComboBox)grado. Además, notifica el estado del Ciclo (Listo y/o Cerrado)
     * pero no toma acción alguna.
     * @param evt 
     */
    private void ciclo_escolarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ciclo_escolarItemStateChanged
        // Al seleccionar un nuevo ciclo escolar se deben actualizar 'grado' con los grados del ciclo seleccionado
        int indexCiclo = ciclo_escolar.getSelectedIndex();  // Obtengo el item seleccionado
        if (evt.getStateChange()==ItemEvent.SELECTED && ciclosCargados && indexCiclo != -1) {
            grado.removeAllItems();
            listaIDGrados.clear();
            try {   // Obtengo todos los grados del ciclo escolar seleccionado
                Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet cGrados = sentencia.executeQuery("SELECT AsignacionCAT.Grado_Id idGrado, Grado.Nombre Grado, Grado.Seccion FROM AsignacionCAT "
                        + "INNER JOIN Grado ON AsignacionCAT.Grado_Id = Grado.Id "
                        + "WHERE AsignacionCAT.CicloEscolar_Id = "+listaIDCiclos.get(indexCiclo)+" "
                        + "GROUP BY AsignacionCAT.Grado_Id");
                while (cGrados.next()) {
                    listaIDGrados.add(cGrados.getInt("idGrado"));
                    grado.addItem(cGrados.getString("Grado")+" "+cGrados.getString("Seccion"));
                } grado.addItem("Todos los grados");  // Opción que permitirá buscar a los estudiantes de todos los grados
                grado.setSelectedIndex(0);
                // Notifico si el Ciclo Escolar está o no Listo y/o Cerrado
                etiqueta_aviso.setText((!listaCicloListo.get(indexCiclo) || listaCicloCerrado.get(indexCiclo)) ?
                        "El Ciclo Escolar "+ciclo_escolar.getSelectedItem().toString()+" "+(!listaCicloListo.get(indexCiclo)?"no está Listo":"ya fue Cerrado") : "");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al intentar obtener los grados:\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(PrincipalAsignacionEST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_ciclo_escolarItemStateChanged
    /**
     * Para Reasignaciones, acción que permite buscar y mostrar a todos los Estudiantes que tienen una asignación en el Ciclo
     * y/o Grado seleccionados (en ciclo_escolar y grado, respectivamente). Esto es útil si a todos los estudiantes de un año
     * se les aumentará un grado más.
     * @param evt 
     */
    private void buscar_para_reasignacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscar_para_reasignacionActionPerformed
        if ("Nueva Búsqueda".equals(buscar_para_reasignacion.getText())) {  // Se habilita el filtro para realizar la búsqueda
            buscar_para_reasignacion.setText("Mostrar Estudiantes");
            buscar_para_reasignacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/aceptar(16x16).png")));
            setEnabled_campos_resultado(false);
            crear_asignacion.setEnabled(false); // Se habilitará hasta seleccionar un registro
        } else {    // Se deshabilita los campos de filtro para realizar la búsqueda. Se realiza la búsqueda
            buscar_para_reasignacion.setText("Nueva Búsqueda");
            buscar_para_reasignacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/buscar(16x16).png")));
            setEnabled_campos_resultado(true);  // Se realizará una búsqueda. Habilito la tabla para poder seleccionar registros
            // Se obtiene todos los Estudiantes que tienen una Asignación en las especificaciones de búsqueda
            int cicloSelec = ciclo_escolar.getSelectedIndex(), gradoSelec = grado.getSelectedIndex();
            String instruccion = "SELECT Estudiante.Id idEstudiante, Estudiante.CodigoPersonal, CONCAT(Estudiante.Nombres, ' ', Estudiante.Apellidos) nombreEstudiante FROM Estudiante "
                    + "INNER JOIN AsignacionEST ON Estudiante.Id = AsignacionEST.Estudiante_Id "
                    + "WHERE AsignacionEst.CicloEscolar_Id = "+listaIDCiclos.get(cicloSelec)+"";
            if (gradoSelec != (grado.getItemCount()-1))  // Si se quiere mostrar los alumnos de un Ciclo y Grado específico
                instruccion+= " AND AsignacionEST.Grado_Id = "+listaIDGrados.get(gradoSelec)+"";
            try {
                Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet cConsulta = sentencia.executeQuery(instruccion);
                listaIDEstudiantes.clear();
                DefaultTableModel modelEstudiantes = (DefaultTableModel)tabla_estudiantes.getModel();
                modelEstudiantes.setRowCount(0);
                int contador = 0;
                while (cConsulta.next()) {
                    listaIDEstudiantes.add(cConsulta.getInt("idEstudiante"));
                    modelEstudiantes.addRow(new String[]{
                        ""+(++contador),
                        cConsulta.getString("CodigoPersonal"),
                        cConsulta.getString("nombreEstudiante"),
                        "No"    // Indicador de que no se le ha creado una Asignación nueva
                    });
                }   // Hasta aquí se garantiza la extracción de todos los registros de estudiantes que aún no tienen una asignación
                panel_estudiantes_encontrados.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hay "+contador+" estudiante"+((contador==1)?"":"s")+" para Reasignar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14)));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al intentar extraer datos.\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(PrincipalAsignacionEST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_buscar_para_reasignacionActionPerformed
    /**
     * Acción que muestra las Asignaciones que el Estudiante seleccionado ha tenido, y habilita el botón en caso de que no
     * se le ha creado una nueva asignación (desde que se llama a esta ventana).
     * @param evt 
     */
    private void tabla_estudiantesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_estudiantesMousePressed
        // La tabla tiene la propiedad de que sólo se puede seleccionar una fila a la vez
        int index = tabla_estudiantes.getSelectedRow();
        // Obtengo el Historial de Asignaciones del Estudiante seleccionado
        try {
            Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet cConsulta = sentencia.executeQuery("SELECT CicloEscolar.Anio, Grado.Nombre Grado, Grado.Seccion FROM AsignacionEST "
                + "INNER JOIN CicloEscolar ON AsignacionEST.CicloEscolar_Id = CicloEscolar.Id "
                + "INNER JOIN Grado ON AsignacionEST.Grado_Id = Grado.Id "
                + "WHERE AsignacionEST.Estudiante_Id = "+listaIDEstudiantes.get(index));
            DefaultTableModel modelHistorial = (DefaultTableModel)tabla_historial_asignaciones.getModel();
            modelHistorial.setRowCount(0);
            while (cConsulta.next()) {
                modelHistorial.addRow(new String[]{
                    ""+(modelHistorial.getRowCount()+1),
                    cConsulta.getString("Anio"),
                    cConsulta.getString("Grado"),
                    cConsulta.getString("Seccion")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se puede obtener el Historial de Asignaciones.\n\nDescripción:\n"+ex.getMessage(), "Error en conexión", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(PrincipalAsignacionEST.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Del estudiante seleccionado, se le podrá crear una nueva Asignación si tiene su 'Asignación nueva' como 'No' (en la tabla)
        crear_asignacion.setEnabled(index!=-1 && "No".equals((String)tabla_estudiantes.getValueAt(index, 3)));
    }//GEN-LAST:event_tabla_estudiantesMousePressed
    /**
     * Acción que permite crear una nueva Asignación. Para ello, llama a AsignarEstudiante (la cual tiene un atributo que
     * indica si se crea o no la asignación) y marca que dicho estudiante ya tiene una asignación (si es el caso).
     * @param evt 
     */
    private void crear_asignacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crear_asignacionActionPerformed
        int indexEstudiante = tabla_estudiantes.getSelectedRow();
        int idEstudiante = listaIDEstudiantes.get(indexEstudiante);
        String codigoPersonal = (String)tabla_estudiantes.getValueAt(indexEstudiante, 1), nombreCompleto = (String)tabla_estudiantes.getValueAt(indexEstudiante, 2);
        // Instancio el JDialog encargado de realizar la Asignación
        AsignarEstudiante nuevaAsignacion = new AsignarEstudiante(this, conexion, paraReasignacion, idEstudiante, codigoPersonal, nombreCompleto);
        nuevaAsignacion.setVisible(nuevaAsignacion.getHacerVisible());
        if (nuevaAsignacion.getAsignacionCreada()) {    // Si se crea la Asignación
            tabla_estudiantes.setValueAt("Si", indexEstudiante, 3); // Indico de que el Estudiante ya tiene una Asignación  nueva
            crear_asignacion.setEnabled(false); // Habilito este botón hasta seleccionar otro registro
        }
    }//GEN-LAST:event_crear_asignacionActionPerformed
    /**
     * Evento que permite habilitar 'ventanaPadre' antes de cerrar la ventana actual.
     * @param evt 
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        ventanaPadre.setEnabled(true);
    }//GEN-LAST:event_formWindowClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ventanaPadre.setEnabled(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * Procedimiento que habilita o inhabilita la opción de seleccionar un ciclo escolar y un grado. Esto es útil ya que
     * dichos eventos de cambios de ítem generan obtención de datos por lo que se puede perder accidentalmente los datos
     * mostrados. Evita que se pueda seleccionar un Estudiante cuando se está filtrando una búsqueda (ya que dicha selección
     * genera una petición a la Base de Datos).
     * @param valorEnabled 
     */
    private void setEnabled_campos_resultado(boolean valorEnabled) {
        etiqueta_ciclo_escolar.setEnabled(!valorEnabled);
        etiqueta_grado.setEnabled(!valorEnabled);
        ciclo_escolar.setEnabled(!valorEnabled);
        grado.setEnabled(!valorEnabled);
        tabla_estudiantes.setEnabled(valorEnabled);
    }
    /**
     * Función que devuelve el valor de 'hacerVisible'. Al obtener datos desde la Base de Datos pueden surgir errores de
     * conexión o con la instrucción SQL por lo que puede arrojar valores erróneos o generar problemas mayores, por lo que
     * se controla y notifica en 'hacerVisible'.
     * @return 'true' si no ocurrió algún problema al intentar obtener datos desde la Base de Datos; 'false' en caso contrario.
     */
    public boolean getHacerVisible() { return hacerVisible; }
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrincipalAsignacionEST.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalAsignacionEST().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buscar_para_reasignacion;
    private javax.swing.JComboBox<String> ciclo_escolar;
    private javax.swing.JButton crear_asignacion;
    private javax.swing.JLabel etiqueta_aviso;
    private javax.swing.JLabel etiqueta_ciclo_escolar;
    private javax.swing.JLabel etiqueta_grado;
    private javax.swing.JComboBox<String> grado;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panel_estudiantes_encontrados;
    private javax.swing.JPanel panel_historial_asignaciones;
    private javax.swing.JPanel panel_reasignacion;
    private javax.swing.JTable tabla_estudiantes;
    private javax.swing.JTable tabla_historial_asignaciones;
    // End of variables declaration//GEN-END:variables
}
