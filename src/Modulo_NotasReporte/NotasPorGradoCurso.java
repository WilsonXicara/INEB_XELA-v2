/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modulo_NotasReporte;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Ventana donde se podrá editar las Notas de todos los Estudiantes asignados a un Ciclo Escolar, Grado y Curso específico.
 * Un Catedrático tiene asignado un curso a un grado en un ciclo escolar específico, por lo que aquí se muestran las notas
 * de todos los estudiantes a quienes les da clases.
 * @author Wilson Xicará
 */
public class NotasPorGradoCurso extends javax.swing.JFrame {
    private Connection conexion;
    private JFrame ventanaPadre;
    private boolean hacerVisible;
    private int idCurso, idCicloEscolar, indexNotaEditada;
    private ArrayList<Integer> listaIDNotas;
    private ArrayList<Boolean> listaNotaEditada;
    /**
     * Creates new form Pantalla
     */
    public NotasPorGradoCurso() {
        initComponents();
    }
    public NotasPorGradoCurso(Connection conexion, JFrame ventanaPadre, int idCicloEscolar, int idGrado, int idCurso) {
        initComponents();
        this.conexion = conexion;
        this.ventanaPadre = ventanaPadre;
        this.idCurso = idCurso;
        this.idCicloEscolar = idCicloEscolar;
        hacerVisible = true;
        
        // Obtengo el listado de Estudiantes y sus Notas asignados al Ciclo, Grado y Curso pasados como parámetro
        try {
            Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet cConsulta = sentencia.executeQuery("SELECT Notas.Id idNota, Estudiante.CodigoPersonal, Estudiante.Apellidos, Estudiante.Nombres, Notas.Nota1, Notas.Nota2, Notas.Nota3, Notas.Nota4, Notas.NotaRecuperacion, Notas.NotaFinal FROM AsignacionEST "
                    + "INNER JOIN Estudiante ON AsignacionEST.Estudiante_Id = Estudiante.Id "
                    + "INNER JOIN Notas ON AsignacionEST.Id = Notas.AsignacionEST_Id "
                    + "WHERE AsignacionEST.CicloEscolar_Id = "+idCicloEscolar+" AND AsignacionEST.Grado_Id = "+idGrado+" AND Notas.Curso_Id = "+idCurso);
            DefaultTableModel modelNotas = (DefaultTableModel)tabla_notas_por_curso.getModel();
            listaIDNotas = new ArrayList<>();
            listaNotaEditada = new ArrayList<>();
            int contador = 0;
            while (cConsulta.next()) {
                listaIDNotas.add(cConsulta.getInt("idNota"));
                listaNotaEditada.add(false);    // Inicialmente la i-ésima Nota no ha sido editada
                modelNotas.addRow(new String[]{
                    ""+(++contador),
                    cConsulta.getString("CodigoPersonal"),
                    cConsulta.getString("Apellidos"),
                    cConsulta.getString("Nombres"),
                    (cConsulta.getFloat("Nota1")==-1f) ? "" : String.format("%.2f", cConsulta.getFloat("Nota1")),
                    (cConsulta.getFloat("Nota2")==-1f) ? "" : String.format("%.2f", cConsulta.getFloat("Nota2")),
                    (cConsulta.getFloat("Nota3")==-1f) ? "" : String.format("%.2f", cConsulta.getFloat("Nota3")),
                    (cConsulta.getFloat("Nota4")==-1f) ? "" : String.format("%.2f", cConsulta.getFloat("Nota4")),
                    (cConsulta.getFloat("NotaRecuperacion")==-1f) ? "" : String.format("%.2f", cConsulta.getFloat("NotaRecuperacion")),
                    (cConsulta.getFloat("NotaFinal")==-1f) ? "" : String.format("%.2f", cConsulta.getFloat("NotaFinal")),
                });
            }   // Hasta aquí se garantiza la carga de todas las Notas de los Estudiantes asignados al Curso, Grado y Ciclo Escolar especificados
            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estudiantes: "+contador, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14)));
            // Otras configuraciones importantes
            guardar_cambios_en_notas.setVisible(false);  // Este botón será visible hasta que se edite por lo menos una Nota
            cargar_notas_en_campos(-1); // Preparo los campos como vacíos e inhabilitados
            this.setLocationRelativeTo(null);
        } catch (SQLException ex) {
            hacerVisible = false;
            JOptionPane.showMessageDialog(this, "No se puede obtener el listado de Estudiantes asignados.\n\nDescripción:\n"+ex.getMessage(), "Error en conexión", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(NotasPorGradoCurso.class.getName()).log(Level.SEVERE, null, ex);
        }
        ventanaPadre.setEnabled(!hacerVisible);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_notas_por_curso = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        guardar_cambios_en_notas = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        check_nota_recuperacion = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        nota1 = new javax.swing.JTextField();
        nota2 = new javax.swing.JTextField();
        nota3 = new javax.swing.JTextField();
        nota4 = new javax.swing.JTextField();
        nota_recuperacion = new javax.swing.JTextField();
        nota_final = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        calcular_nota_final = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        actualizar_nota = new javax.swing.JButton();
        cancelar_cambio = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        codigo_personal = new javax.swing.JTextField();
        nombres = new javax.swing.JTextField();
        apellidos = new javax.swing.JTextField();
        regresar = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Notas por Curso");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estudiantes:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        tabla_notas_por_curso.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        tabla_notas_por_curso.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Código Personal", "Apellidos", "Nombres", "Nota 1", "Nota 2", "Nota 3", "Nota 4", "Nota de Recuperación", "Nota Final"
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
        tabla_notas_por_curso.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabla_notas_por_curso.setRowHeight(25);
        tabla_notas_por_curso.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_notas_por_curso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_notas_por_cursoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabla_notas_por_curso);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Doble clic sobre una Nota para editarla");

        guardar_cambios_en_notas.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        guardar_cambios_en_notas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/guardar(32x32).png"))); // NOI18N
        guardar_cambios_en_notas.setText("Guardar cambios");
        guardar_cambios_en_notas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardar_cambios_en_notasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(guardar_cambios_en_notas, javax.swing.GroupLayout.Alignment.TRAILING)))
            .addComponent(jScrollPane2)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guardar_cambios_en_notas))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notas:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Nota 1:");
        jLabel5.setToolTipText("");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Nota 2:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Nota 3:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Nota 4:");

        check_nota_recuperacion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        check_nota_recuperacion.setText("Nota Recuperación:");
        check_nota_recuperacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                check_nota_recuperacionItemStateChanged(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("Nota Final:");

        nota1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        nota1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nota1FocusLost(evt);
            }
        });
        nota1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nota1KeyTyped(evt);
            }
        });

        nota2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        nota2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nota2FocusLost(evt);
            }
        });
        nota2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nota2KeyTyped(evt);
            }
        });

        nota3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        nota3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nota3FocusLost(evt);
            }
        });
        nota3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nota3KeyTyped(evt);
            }
        });

        nota4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        nota4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nota4FocusLost(evt);
            }
        });
        nota4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nota4KeyTyped(evt);
            }
        });

        nota_recuperacion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        nota_recuperacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nota_recuperacionFocusLost(evt);
            }
        });
        nota_recuperacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nota_recuperacionKeyTyped(evt);
            }
        });

        nota_final.setEditable(false);
        nota_final.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        calcular_nota_final.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        calcular_nota_final.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/adelante(16x16).png"))); // NOI18N
        calcular_nota_final.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcular_nota_finalActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        actualizar_nota.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        actualizar_nota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/editar(16x16).png"))); // NOI18N
        actualizar_nota.setText("Actualizar");
        actualizar_nota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualizar_notaActionPerformed(evt);
            }
        });

        cancelar_cambio.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cancelar_cambio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar(16x16).png"))); // NOI18N
        cancelar_cambio.setText("Cancelar");
        cancelar_cambio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelar_cambioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel13)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(nota4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(check_nota_recuperacion)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(nota_recuperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel8)
                                .addComponent(jLabel7)
                                .addComponent(jLabel5))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(nota2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(nota3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(nota1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                        .addComponent(jSeparator2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(calcular_nota_final)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nota_final, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(actualizar_nota)
                    .addComponent(cancelar_cambio))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(nota1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(nota2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8)
                                .addComponent(nota3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(nota4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(check_nota_recuperacion)
                                .addComponent(nota_recuperacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(calcular_nota_final)
                                .addComponent(jLabel14)
                                .addComponent(nota_final, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(actualizar_nota)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelar_cambio)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información del Estudiante:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Código Personal:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Nombres:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Apellidos:");

        codigo_personal.setEditable(false);
        codigo_personal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        nombres.setEditable(false);
        nombres.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        apellidos.setEditable(false);
        apellidos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(codigo_personal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nombres)
                            .addComponent(apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(codigo_personal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(nombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        regresar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        regresar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/atras(25x26).PNG"))); // NOI18N
        regresar.setText("Regresar");
        regresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regresarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(regresar))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regresar)
                .addGap(1, 1, 1)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(139, 139, 139))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void tabla_notas_por_cursoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_notas_por_cursoMouseClicked
        // La tabla tiene la propiedad de que sólo se puede selccionar una fila a la vez
        if (evt.getClickCount() > 1) {
            indexNotaEditada = tabla_notas_por_curso.getSelectedRow();
            cargar_notas_en_campos(indexNotaEditada);   // Cargo las notas en los campos correspondientes
        }
    }//GEN-LAST:event_tabla_notas_por_cursoMouseClicked

    private void actualizar_notaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizar_notaActionPerformed
        // Se regresan los valores de las notas editadas a la tabla
        tabla_notas_por_curso.setValueAt(nota1.getText(), indexNotaEditada, 4);
        tabla_notas_por_curso.setValueAt(nota2.getText(), indexNotaEditada, 5);
        tabla_notas_por_curso.setValueAt(nota3.getText(), indexNotaEditada, 6);
        tabla_notas_por_curso.setValueAt(nota4.getText(), indexNotaEditada, 7);
        tabla_notas_por_curso.setValueAt(check_nota_recuperacion.isSelected() ? nota_recuperacion.getText() : "", indexNotaEditada, 8);
        // Cálculo de la nota final como la nota de recuperación (si se indica) o como el promedio de las 4 notas
        if (check_nota_recuperacion.isSelected())
            nota_final.setText(nota_recuperacion.getText());
        else {
            if (nota1.getText().length()==0 || nota2.getText().length()==0 || nota3.getText().length()==0 || nota4.getText().length()==0)
                nota_final.setText("");
            else
                nota_final.setText(String.format("%.2f",
                        (Float.parseFloat(nota1.getText()) + Float.parseFloat(nota2.getText()) + Float.parseFloat(nota3.getText()) + Float.parseFloat(nota4.getText()))/4));
        }
        tabla_notas_por_curso.setValueAt(nota_final.getText(), indexNotaEditada, 9);
        // Limpio los campos y marco la nota como editada
        cargar_notas_en_campos(-1);
        listaNotaEditada.set(indexNotaEditada, true);   // Indicador de que la nota fue editada
        if (!guardar_cambios_en_notas.isVisible())
            guardar_cambios_en_notas.setVisible(true);  // Muestro el botón que permitirá guardar todos los cambios
    }//GEN-LAST:event_actualizar_notaActionPerformed

    private void cancelar_cambioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelar_cambioActionPerformed
        cargar_notas_en_campos(-1); // Sólo se limpian los campos (no se realiza alguna acción)
    }//GEN-LAST:event_cancelar_cambioActionPerformed
    /**
     * Acción que calcula la Nota Final como promedio de las Notas 1-4, o como la nota de recuperación.
     * @param evt 
     */
    private void calcular_nota_finalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcular_nota_finalActionPerformed
        if (check_nota_recuperacion.isSelected())
            nota_final.setText(nota_recuperacion.getText());
        else {
            if (nota1.getText().length()==0 || nota2.getText().length()==0 || nota3.getText().length()==0 || nota4.getText().length()==0)
                JOptionPane.showMessageDialog(this, "No se puede calcular la Nota Final sin las 4 notas anteriores.", "Nota Final", JOptionPane.INFORMATION_MESSAGE);
            else
                nota_final.setText(String.format("%.2f",
                        (Float.parseFloat(nota1.getText()) + Float.parseFloat(nota2.getText()) + Float.parseFloat(nota3.getText()) + Float.parseFloat(nota4.getText()))/4));
        }
    }//GEN-LAST:event_calcular_nota_finalActionPerformed
    /**
     * Eventos que controlan que las Notas que se ingresen sean dígitos solamente, y que estén en el rango de [0.0,100.0].
     * Dichos eventos se lanzan antes de insertar la tecla.
     */
    private void nota1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nota1KeyTyped
        char teclaPulsada = evt.getKeyChar();
        if (teclaPulsada == '.') {  // Cuando se presiona el punto (para una cantidad decimal)
            if (nota1.getText().contains("."))  // Se rechaza la tecla si la cantidad ya contiene un punto decimal
                evt.consume();
            else if (nota1.getText().length() == 0) // Se concatena un 0 al inicio si la caja está vacía
                nota1.setText("0");
        } else if (!Pattern.compile("\\d").matcher(String.valueOf(teclaPulsada)).matches())
            evt.consume();  // No se acepta la tecla si no es un dígito
        else {  // Si es un dígito
            if (nota1.getText().length()!=0 && !nota1.getText().contains(".") && Integer.parseInt(nota1.getText())==0 && teclaPulsada=='0')
                evt.consume();  // Se rechaza la tecla en caso de querer escribir varios ceros seguidos
            else if (Float.parseFloat(nota1.getText()+String.valueOf(teclaPulsada)) > 100.0)
                evt.consume();  // Se rechaza la tecla si hace que la nota sea mayor a 100.0
        }
    }//GEN-LAST:event_nota1KeyTyped
    private void nota2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nota2KeyTyped
        char teclaPulsada = evt.getKeyChar();
        if (teclaPulsada == '.') {  // Cuando se presiona el punto (para una cantidad decimal)
            if (nota2.getText().contains("."))  // Se rechaza la tecla si la cantidad ya contiene un punto decimal
                evt.consume();
            else if (nota2.getText().length() == 0) // Se concatena un 0 al inicio si la caja está vacía
                nota2.setText("0");
        } else if (!Pattern.compile("\\d").matcher(String.valueOf(teclaPulsada)).matches())
            evt.consume();  // No se acepta la tecla si no es un dígito
        else {  // Si es un dígito
            if (nota2.getText().length()!=0 && !nota2.getText().contains(".") && Integer.parseInt(nota2.getText())==0 && teclaPulsada=='0')
                evt.consume();  // Se rechaza la tecla en caso de querer escribir varios ceros seguidos
            else if (Float.parseFloat(nota2.getText()+String.valueOf(teclaPulsada)) > 100.0)
                evt.consume();  // Se rechaza la tecla si hace que la nota sea mayor a 100.0
        }
    }//GEN-LAST:event_nota2KeyTyped
    private void nota3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nota3KeyTyped
        char teclaPulsada = evt.getKeyChar();
        if (teclaPulsada == '.') {  // Cuando se presiona el punto (para una cantidad decimal)
            if (nota3.getText().contains("."))  // Se rechaza la tecla si la cantidad ya contiene un punto decimal
                evt.consume();
            else if (nota3.getText().length() == 0) // Se concatena un 0 al inicio si la caja está vacía
                nota3.setText("0");
        } else if (!Pattern.compile("\\d").matcher(String.valueOf(teclaPulsada)).matches())
            evt.consume();  // No se acepta la tecla si no es un dígito
        else {  // Si es un dígito
            if (nota3.getText().length()!=0 && !nota3.getText().contains(".") && Integer.parseInt(nota3.getText())==0 && teclaPulsada=='0')
                evt.consume();  // Se rechaza la tecla en caso de querer escribir varios ceros seguidos
            else if (Float.parseFloat(nota3.getText()+String.valueOf(teclaPulsada)) > 100.0)
                evt.consume();  // Se rechaza la tecla si hace que la nota sea mayor a 100.0
        }
    }//GEN-LAST:event_nota3KeyTyped
    private void nota4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nota4KeyTyped
        char teclaPulsada = evt.getKeyChar();
        if (teclaPulsada == '.') {  // Cuando se presiona el punto (para una cantidad decimal)
            if (nota4.getText().contains("."))  // Se rechaza la tecla si la cantidad ya contiene un punto decimal
                evt.consume();
            else if (nota4.getText().length() == 0) // Se concatena un 0 al inicio si la caja está vacía
                nota4.setText("0");
        } else if (!Pattern.compile("\\d").matcher(String.valueOf(teclaPulsada)).matches())
            evt.consume();  // No se acepta la tecla si no es un dígito
        else {  // Si es un dígito
            if (nota4.getText().length()!=0 && !nota4.getText().contains(".") && Integer.parseInt(nota4.getText())==0 && teclaPulsada=='0')
                evt.consume();  // Se rechaza la tecla en caso de querer escribir varios ceros seguidos
            else if (Float.parseFloat(nota4.getText()+String.valueOf(teclaPulsada)) > 100.0)
                evt.consume();  // Se rechaza la tecla si hace que la nota sea mayor a 100.0
        }
    }//GEN-LAST:event_nota4KeyTyped
    private void nota_recuperacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nota_recuperacionKeyTyped
        char teclaPulsada = evt.getKeyChar();
        if (teclaPulsada == '.') {  // Cuando se presiona el punto (para una cantidad decimal)
            if (nota_recuperacion.getText().contains("."))  // Se rechaza la tecla si la cantidad ya contiene un punto decimal
                evt.consume();
            else if (nota_recuperacion.getText().length() == 0) // Se concatena un 0 al inicio si la caja está vacía
                nota_recuperacion.setText("0");
        } else if (!Pattern.compile("\\d").matcher(String.valueOf(teclaPulsada)).matches())
            evt.consume();  // No se acepta la tecla si no es un dígito
        else {  // Si es un dígito
            if (nota_recuperacion.getText().length()!=0 && !nota_recuperacion.getText().contains(".") && Integer.parseInt(nota_recuperacion.getText())==0 && teclaPulsada=='0')
                evt.consume();  // Se rechaza la tecla en caso de querer escribir varios ceros seguidos
            else if (Float.parseFloat(nota_recuperacion.getText()+String.valueOf(teclaPulsada)) > 100.0)
                evt.consume();  // Se rechaza la tecla si hace que la nota sea mayor a 100.0
        }
    }//GEN-LAST:event_nota_recuperacionKeyTyped
    /**
     * Eventos que controlan cuando alguna de las cajas de notas pierde el focus. Los casos a considerar son:
     * - Si el texto es nulo, no ocurrirá nada.
     * - Todo texto numérico se redondea a dos decimales (si fuese entero, se concatena '.00' al final).
     * - Si el texto está incompleto, se lleva a la forma 'n.dd' (entero con dos decimales entre [0.00, 100.00]).
     */
    private void nota1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nota1FocusLost
        if (nota1.getText().length() > 0 && nota1.getText().indexOf(".")==(nota1.getText().length()-1))
            nota1.setText(nota1.getText()+"00");    // Corrección de la nota
        if (nota1.getText().length() > 0)
            nota1.setText(String.format("%.2f", Float.parseFloat(nota1.getText())));    // Redondeo de la nota
    }//GEN-LAST:event_nota1FocusLost
    private void nota2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nota2FocusLost
        if (nota2.getText().length() > 0 && nota2.getText().indexOf(".")==(nota2.getText().length()-1))
            nota2.setText(nota2.getText()+"00");    // Corrección de la nota
        if (nota2.getText().length() > 0)
            nota2.setText(String.format("%.2f", Float.parseFloat(nota2.getText())));    // Redondeo de la nota
    }//GEN-LAST:event_nota2FocusLost
    private void nota3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nota3FocusLost
        if (nota3.getText().length() > 0 && nota3.getText().indexOf(".")==(nota3.getText().length()-1))
            nota3.setText(nota3.getText()+"00");    // Corrección de la nota
        if (nota3.getText().length() > 0)
            nota3.setText(String.format("%.2f", Float.parseFloat(nota3.getText())));    // Redondeo de la nota
    }//GEN-LAST:event_nota3FocusLost
    private void nota4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nota4FocusLost
        if (nota4.getText().length() > 0 && nota4.getText().indexOf(".")==(nota4.getText().length()-1))
            nota4.setText(nota4.getText()+"00");    // Corrección de la nota
        if (nota4.getText().length() > 0)
            nota4.setText(String.format("%.2f", Float.parseFloat(nota4.getText())));    // Redondeo de la nota
    }//GEN-LAST:event_nota4FocusLost
    private void nota_recuperacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nota_recuperacionFocusLost
        if (nota_recuperacion.getText().length() > 0 && nota_recuperacion.getText().indexOf(".")==(nota_recuperacion.getText().length()-1))
            nota_recuperacion.setText(nota_recuperacion.getText()+"00");    // Corrección de la nota
        if (nota_recuperacion.getText().length() > 0)
            nota_recuperacion.setText(String.format("%.2f", Float.parseFloat(nota_recuperacion.getText())));    // Redondeo de la nota
    }//GEN-LAST:event_nota_recuperacionFocusLost

    private void check_nota_recuperacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_check_nota_recuperacionItemStateChanged
        nota_recuperacion.setEnabled(check_nota_recuperacion.isSelected());
    }//GEN-LAST:event_check_nota_recuperacionItemStateChanged

    private void guardar_cambios_en_notasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardar_cambios_en_notasActionPerformed
        // Llamo a la función que guarda las Notas en la Base de Datos. Si no ocurre algún problema, oculto el botón (se muestra hasta modificar otra nota)
        boolean notasGuardadas = guardar_cambios();
        guardar_cambios_en_notas.setVisible(!notasGuardadas);
        if (!notasGuardadas)
            JOptionPane.showMessageDialog(this, "Ocurrió un error al intentar guardar una de las Notas", "Guardar cambios", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_guardar_cambios_en_notasActionPerformed
    /**
     * Acciones previo a cerrar la ventana. Previo a cerrar, se verifica que todos los cambios hayan sido guardados. Una forma
     * de verificar si los cambios ya fueron guardados es que el botón 'guardar_cambios_en_notas' no esté visible (ya que al
     * guardar todos los cambios dicho botón se oculta y al realizar por lo menos un cambio se muestra); si dicho botón está
     * visible, se comprueba (en 'listaNotaEditada') y notifica la cantidad de registros que aún no han sido guardados.
     * @param evt 
     */
    private void regresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regresarActionPerformed
        int opcion = JOptionPane.YES_OPTION;    // Inicialmente se asume que no hay cambios por guardar
        if (guardar_cambios_en_notas.isVisible()) {    // Compruebo y pido acción de guardar los registros
            int cantidad = listaNotaEditada.size(), cont = 0;
            for(int i=0; i<cantidad; i++)
                if (listaNotaEditada.get(i))
                    cont++;
            String[] opciones = new String[]{"Guardar todo", "No guardar", "Cancelar"};
            opcion = JOptionPane.showOptionDialog(this, "Hay "+(cont)+" cambio"+(cont==1?" no guardado":"s no guardados")+".\n\n¿Desea guardar los cambios?", "Guardar registros", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (opcion == JOptionPane.YES_OPTION) { // Eligió guardar los cambios
                boolean pasar = false;
                while (!pasar) {
                    pasar = guardar_cambios();
                    if (!pasar) {   // Si ocurre un error al intentar guardar los cambios se repetirá el ciclo
                        pasar = !(JOptionPane.showOptionDialog(this, "Ocurrió un error al intentar guardar una de la Notas.\n\n¿Desea intentar nuevamente guardar los cambios?", "Guardar cambios", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)
                                == JOptionPane.YES_OPTION);
                    }
                }
            }   // Hasta aquí ya se intentó guardar o rechazar los cambios
        }   // Hasta aquí ya se notificó al usuario de los cambios no guardados y el usuario ya eligió la opción correspondiente
        if (opcion==JOptionPane.YES_OPTION || opcion==JOptionPane.NO_OPTION) {  // Si eligió 'Guardar' o 'No guardar' se cerrará la ventana
            ventanaPadre.setEnabled(true);
            this.dispose();
        }
    }//GEN-LAST:event_regresarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int opcion = JOptionPane.YES_OPTION;    // Inicialmente se asume que no hay cambios por guardar
        if (guardar_cambios_en_notas.isVisible()) {    // Compruebo y pido acción de guardar los registros
            int cantidad = listaNotaEditada.size(), cont = 0;
            for(int i=0; i<cantidad; i++)
                if (listaNotaEditada.get(i))
                    cont++;
            String[] opciones = new String[]{"Guardar todo", "No guardar", "Cancelar"};
            opcion = JOptionPane.showOptionDialog(this, "Hay "+(cont)+" cambio"+(cont==1?" no guardado":"s no guardados")+".\n\n¿Desea guardar los cambios?", "Guardar registros", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (opcion == JOptionPane.YES_OPTION) { // Eligió guardar los cambios
                boolean pasar = false;
                while (!pasar) {
                    pasar = guardar_cambios();
                    if (!pasar) {   // Si ocurre un error al intentar guardar los cambios se repetirá el ciclo
                        pasar = !(JOptionPane.showOptionDialog(this, "Ocurrió un error al intentar guardar una de la Notas.\n\n¿Desea intentar nuevamente guardar los cambios?", "Guardar cambios", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)
                                == JOptionPane.YES_OPTION);
                    }
                }
            }   // Hasta aquí ya se intentó guardar o rechazar los cambios
        }   // Hasta aquí ya se notificó al usuario de los cambios no guardados y el usuario ya eligió la opción correspondiente
        if (opcion==JOptionPane.YES_OPTION || opcion==JOptionPane.NO_OPTION) {  // Si eligió 'Guardar' o 'No guardar' se cerrará la ventana
            ventanaPadre.setEnabled(true);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        } else
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }//GEN-LAST:event_formWindowClosing

    /**
     * Método que permite cargar las notas del registro en la fila index de la tabla, o limpiar los campos si index == -1.
     * @param index entero que indicará si se cargarán notas a los campos, o se limpiarán los campos.
     */
    private void cargar_notas_en_campos(int index) {
        if (index == -1) {  // Opción para limpiar los campos
            codigo_personal.setText("");
            apellidos.setText("");
            nombres.setText("");
            nota1.setEnabled(false);    // Inhabilito los campos de notas
            nota2.setEnabled(false);
            nota3.setEnabled(false);
            nota4.setEnabled(false);
            nota_recuperacion.setEnabled(false);
            nota_final.setEnabled(false);
            nota1.setText("");  // Limpio los campos de notas
            nota2.setText("");
            nota3.setText("");
            nota4.setText("");
            nota_recuperacion.setText("");
            check_nota_recuperacion.setSelected(false);
            check_nota_recuperacion.setEnabled(false);
            nota_final.setText("");
            calcular_nota_final.setEnabled(false);
            actualizar_nota.setEnabled(false);
            cancelar_cambio.setEnabled(false);
        } else {    // Se cargarán las notas de la fila index de la tabla en los campos correspondientes
            codigo_personal.setText((String)tabla_notas_por_curso.getValueAt(index, 1));
            apellidos.setText((String)tabla_notas_por_curso.getValueAt(index, 2));
            nombres.setText((String)tabla_notas_por_curso.getValueAt(index, 3));
            nota1.setEnabled(true);     // Habilito los campos de notas
            nota2.setEnabled(true);
            nota3.setEnabled(true);
            nota4.setEnabled(true);
            nota_final.setEnabled(true);
            nota1.setText((String)tabla_notas_por_curso.getValueAt(index, 4));  // Cargo las notas a los campos correspondientes
            nota2.setText((String)tabla_notas_por_curso.getValueAt(index, 5));
            nota3.setText((String)tabla_notas_por_curso.getValueAt(index, 6));
            nota4.setText((String)tabla_notas_por_curso.getValueAt(index, 7));
            nota_recuperacion.setText((String)tabla_notas_por_curso.getValueAt(index, 8));
            check_nota_recuperacion.setSelected(!(nota_recuperacion.getText().length() == 0));
            check_nota_recuperacion.setEnabled(true);
            nota_final.setText((String)tabla_notas_por_curso.getValueAt(index, 9));
            calcular_nota_final.setEnabled(true);   // Habilito el botón que permite calcular la nota final
            actualizar_nota.setEnabled(true);   // Habilito los botones que permiten guardar o cancelar la modificación de la nota
            cancelar_cambio.setEnabled(true);
        }
    }
    /**
     * Función que guarda en la Base de Datos los cambios de todas las Notas editadas.
     * @return 'true' si todos los cambios son guardados en la BD; 'false' si ocurre un error al intentar guardar alguna nota.
     */
    private boolean guardar_cambios() {
        // Inicio la actualización en la BD de todas las notas que fueron editadas
        int cantidad = listaIDNotas.size();
        int cont = 0;   // Contador de cuántas notas fueron modificadas
        boolean sinProblemas = true;
        String[] notas = new String[6];
        for(int fil=0; fil<cantidad; fil++) {
            if (listaNotaEditada.get(fil)) {  // Esta nota fue editada
                for(int i=0; i<6; i++)  // Obtengo el String de las Notas
                    notas[i] = ((String)tabla_notas_por_curso.getValueAt(fil, i+4)).length()==0 ? "-1.00" : (String)tabla_notas_por_curso.getValueAt(fil, i+4);
                try {
                    // Esta nota fue editada, lo actualizo en la BD
                    String actualizar = "UPDATE Notas SET ";
                    actualizar+= "Nota1 = "+notas[0]+", ";
                    actualizar+= "Nota2 = "+notas[1]+", ";
                    actualizar+= "Nota3 = "+notas[2]+", ";
                    actualizar+= "Nota4 = "+notas[3]+", ";
                    actualizar+= "NotaRecuperacion = "+notas[4]+", ";
                    actualizar+= "NotaFinal = "+notas[5]+" ";
                    actualizar+= "WHERE Id = "+listaIDNotas.get(fil);
                    conexion.prepareStatement(actualizar).executeUpdate();  // Actualización del registro Estudiante en la Base de Datos
                    
                    // Habilito la opción de volver a editar la nota (en caso de no salir al guardar todo)
                    listaNotaEditada.set(fil, false);
                    cont++; // Aumento el contador de notas guardadas con éxito
                } catch (SQLException ex) {
                    sinProblemas = false;
                    Logger.getLogger(NotasPorGradoCurso.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (cont != 0 && sinProblemas)
            JOptionPane.showMessageDialog(this, "Se han guardado los cambios de "+cont+" Nota"+((cont==1)?"":"s"), "Información", JOptionPane.INFORMATION_MESSAGE);
        return sinProblemas;
    }
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
            java.util.logging.Logger.getLogger(NotasPorGradoCurso.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NotasPorGradoCurso().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton actualizar_nota;
    private javax.swing.JTextField apellidos;
    private javax.swing.JButton calcular_nota_final;
    private javax.swing.JButton cancelar_cambio;
    private javax.swing.JCheckBox check_nota_recuperacion;
    private javax.swing.JTextField codigo_personal;
    private javax.swing.JButton guardar_cambios_en_notas;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField nombres;
    private javax.swing.JTextField nota1;
    private javax.swing.JTextField nota2;
    private javax.swing.JTextField nota3;
    private javax.swing.JTextField nota4;
    private javax.swing.JTextField nota_final;
    private javax.swing.JTextField nota_recuperacion;
    private javax.swing.JButton regresar;
    private javax.swing.JTable tabla_notas_por_curso;
    // End of variables declaration//GEN-END:variables
}
