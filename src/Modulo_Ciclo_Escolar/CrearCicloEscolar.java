/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modulo_Ciclo_Escolar;

import java.awt.Frame;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana que permite crear un nuevo registro de Ciclo Escolar en la Base de Datos. Sólo crear el ciclo, la asignación de
 * cursos y grados se realiza después.
 * @author Wilson Xicará y Hugo Tzul
 */
public class CrearCicloEscolar extends javax.swing.JDialog {
    private Connection conexion;
    /**
     * Creates new form Crear_Ciclo_Escolar
     */
    public CrearCicloEscolar(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public CrearCicloEscolar(java.awt.Frame parent, Connection conexion) {
        super(parent, true);
        initComponents();
        this.conexion = conexion;
        
        // Inicialmente se asume que el nuevo Ciclo Escolar es el año en curso, indicado por la Base de Datos
        try {
            Statement sentencia = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet cCicloEscolar = sentencia.executeQuery("SELECT YEAR(NOW())");
            cCicloEscolar.next();
            nombre_ciclo.setText(cCicloEscolar.getString(1));
            // Ahora obtengo todos los Ciclos Escolares almacenados en la Base de Datos
            cCicloEscolar = sentencia.executeQuery("SELECT Anio FROM CicloEscolar");
            DefaultTableModel modelTabla = (DefaultTableModel)tabla_ciclos_existentes.getModel();
            while (cCicloEscolar.next())
                modelTabla.addRow(new String[]{""+(modelTabla.getRowCount()+1), cCicloEscolar.getString(1)});
            cCicloEscolar.close();
        } catch (SQLException ex) {
//            Logger.getLogger(CrearCicloEscolar.class.getName()).log(Level.SEVERE, null, ex);
            // En caso de ocurrir un error, se toma el año del equipo actual
            Calendar fecha = new GregorianCalendar();
            nombre_ciclo.setText(Integer.toString(fecha.get(Calendar.YEAR)));
        }
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_ciclos_existentes = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nombre_ciclo = new javax.swing.JTextField();
        crear_ciclo_escolar = new javax.swing.JButton();
        cancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Crear nuevo Ciclo Escolar");
        setLocationByPlatform(true);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ciclos Escolares existentes:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        tabla_ciclos_existentes.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tabla_ciclos_existentes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Ciclo Escolar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_ciclos_existentes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabla_ciclos_existentes.setRowHeight(25);
        jScrollPane1.setViewportView(tabla_ciclos_existentes);
        if (tabla_ciclos_existentes.getColumnModel().getColumnCount() > 0) {
            tabla_ciclos_existentes.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabla_ciclos_existentes.getColumnModel().getColumn(1).setPreferredWidth(150);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos del nuevo ciclo:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Nombre del Ciclo: ");

        nombre_ciclo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        nombre_ciclo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nombre_cicloKeyTyped(evt);
            }
        });

        crear_ciclo_escolar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        crear_ciclo_escolar.setText("Crear Ciclo");
        crear_ciclo_escolar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crear_ciclo_escolarActionPerformed(evt);
            }
        });

        cancelar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cancelar.setText("Cancelar");
        cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(crear_ciclo_escolar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelar))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nombre_ciclo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nombre_ciclo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(crear_ciclo_escolar)
                    .addComponent(cancelar))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelarActionPerformed
    /**
     * Acción que crea un nuevo registro (en la Base de Datos) para el nuevo Ciclo Escolar, siempre que aún no exista.
     * No es necesario hacer una consulta a la Base de Datos ya que todos los ciclos se muestran en la tabla.
     * @param evt 
     */
    private void crear_ciclo_escolarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crear_ciclo_escolarActionPerformed
        String nuevoCiclo = nombre_ciclo.getText();
        if (nuevoCiclo.length() < 4) {
            JOptionPane.showMessageDialog(this, "Asigne un nombre de 4 dígitos al nuevo Ciclo Escolar, de preferencia el año en curso", "Error en datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Inicio la búsqueda de coincidencias
        int cantidad = tabla_ciclos_existentes.getRowCount(), i;
        for (i=0; i<cantidad; i++) {
            if (nuevoCiclo.equals((String)tabla_ciclos_existentes.getValueAt(i, 1)))
                break;
        }   // Hasta aquí se garantiza la comparación del nuevo ciclo con los existentes
        if (i != cantidad) {    // Si llegó al final del ciclo sin econtrar coincidencias
            tabla_ciclos_existentes.setRowSelectionInterval(i, i);
            JOptionPane.showMessageDialog(this, "El Ciclo Escolar "+nuevoCiclo+" ya existe", "Datos repetidos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Creación del nuevo Ciclo Escolar
        try {
            conexion.prepareStatement("INSERT INTO CicloEscolar(Anio) VALUES('"+nuevoCiclo+"')").executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Ciclo Escolar "+nuevoCiclo+" creado exitosamente."+(cantidad==0?
                            "\n\nDesde su sesión, vaya a Ver -> Ciclo Escolar para crear los\nGrados, Cursos, Catedráticos y las Asignaciones correspondientes del nuevo ciclo.":""),
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            // Evalúo si hay algún ciclo escolar del que pueda extraer sus datos
            if(cantidad != 0) {
                //Pregunto si desea copiar los cursos y grados de un ciclo anterior
                int eleccion = JOptionPane.showOptionDialog(null, "Desea importar datos de un ciclo anteriror?\nDichos datos incluyen Grados y Cursos.", "Importar datos", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (eleccion == JOptionPane.YES_OPTION){ //Si la respuesta es si
                    this.dispose();
                    new Importar_datos(new Frame(), true, conexion, nuevoCiclo).setVisible(true);
                } else
                    JOptionPane.showMessageDialog(this, "Desde su sesión, vaya a Ver -> Ciclo Escolar para crear los\nGrados, Cursos, Catedráticos y las Asignaciones correspondientes del nuevo ciclo.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
            this.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se puede crear el Ciclo Escolar.\n\nDescripción:\n"+ex.getMessage(), "Error en conexión", JOptionPane.ERROR_MESSAGE);
//            Logger.getLogger(CrearCicloEscolar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_crear_ciclo_escolarActionPerformed

    private void nombre_cicloKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nombre_cicloKeyTyped
        if (!Pattern.compile("\\d").matcher(String.valueOf(evt.getKeyChar())).matches() || nombre_ciclo.getText().length() == 4)
            evt.consume();
    }//GEN-LAST:event_nombre_cicloKeyTyped

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
            java.util.logging.Logger.getLogger(CrearCicloEscolar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CrearCicloEscolar dialog = new CrearCicloEscolar(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton cancelar;
    private javax.swing.JButton crear_ciclo_escolar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nombre_ciclo;
    private javax.swing.JTable tabla_ciclos_existentes;
    // End of variables declaration//GEN-END:variables
}
