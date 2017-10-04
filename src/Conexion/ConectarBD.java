/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Wilson Xicará
 */
public class ConectarBD extends javax.swing.JDialog {
    private Connection conexion;
    private static String SEPARADOR, CARPETA_PRINCIPAL, rutaArchivo;
    private final String nombreBD, usuarioBD, contraseñaBD;
    private boolean hacerVisible;
    private String direccionIPServidor;
    private final DefaultTableModel modelIPs;
    private ArrayList<String> listaIPsAccesibles;
    /**
     * Creates new form ConectarBD
     */
    public ConectarBD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // Definición de los valores de las variables importantes (no deben ser conocidas fuera de la clase)
        ConectarBD.SEPARADOR = System.getProperty("file.separator");
        ConectarBD.CARPETA_PRINCIPAL = System.getProperty("user.home") + SEPARADOR + "BD";
        ConectarBD.rutaArchivo = CARPETA_PRINCIPAL + SEPARADOR + "datos.sbd";
        nombreBD = "sbd_inebxela";
        usuarioBD = "inebxela";
        contraseñaBD = "inebxela_quetgo";
        
        this.hacerVisible = true;
        modelIPs = (DefaultTableModel) tabla_ips_accesibles.getModel();
        this.setLocationRelativeTo(null);   // Para centrar esta ventana sobre la pantalla.
        intentar_conexion();
    }
    /**
     * En este método se intenta determinar si el equipo local es el servidor. Este equipo puede ser el servidor sí y solo sí
     * tiene la Base de Datos (con los valores de conexión establecidos). Se intenta realizar la conexión; en caso de ser
     * exitosa se carga la dirección IP del host local en los campos y se notifica que este equipo tiene la BD (puede ser el servidor).
     */
    private void probar_si_soy_servidor() {
        try {
            String miIP = InetAddress.getLocalHost().getHostAddress();
            Class.forName("org.gjt.mm.mysql.Driver");
            // Intento hacer la conexión con el equipo local, asumiendo que este es el servidor.
            Connection conexionPrueba = DriverManager.getConnection("jdbc:mysql://" + miIP + "/" + nombreBD, usuarioBD, contraseñaBD);
            // Si no cae a una excepción, entonces este equipo tiene la Base de Datos. Notifico al usuario
            String[] miIPPorPartes = miIP.split("\\.");
            campo_direccion_ip1.setText(miIPPorPartes[0]);  // Cargo la dirección IP del host local
            campo_direccion_ip2.setText(miIPPorPartes[1]);
            campo_direccion_ip3.setText(miIPPorPartes[2]);
            campo_direccion_ip4.setText(miIPPorPartes[3]);
            JOptionPane.showMessageDialog(this,
                    "Este equipo tiene una copia de la Base de Datos."
                            + "\nEsto implica que puede ser el servidor."
                            + "\n\nSe cargará la Dirección IP de este equipo, pero usted\ndecida si es o no el servidor!",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (UnknownHostException | ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void intentar_conexion() {
        File carpetaPrincipal = new File(CARPETA_PRINCIPAL);
        File archivoPrincipal = new File(rutaArchivo);
        try {
            // Si la carpeta principal no existe se crea para guardar los datos de conexión con la Base de Datos
            if (!carpetaPrincipal.exists() || !archivoPrincipal.exists()) {
                etiqueta_titulo.setText("No se encontró la Dirección IP del Servidor.");
                // Se cargarán las Direcciones IPs que son accesibles en la tabla
                // Definición del ancho de las columnas para la Tabla Encontrados (valores definidos en base a pruebas)
                tabla_ips_accesibles.getColumnModel().getColumn(0).setPreferredWidth(50);
                tabla_ips_accesibles.getColumnModel().getColumn(1).setPreferredWidth(300);

                probar_si_soy_servidor();   // Este equipo puede ser servidor sí y solo sí tiene la Base de Datos
            } else {
                RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "r");
                String firma = "", ipServidor = "";
                for(int i=0; i<6; i++) firma+= ""+(char)Byte.toUnsignedInt(archivo.readByte());
                if ("SBDdat".equals(firma)) {   // Es el archivo correcto
                    ipServidor+= Byte.toUnsignedInt(archivo.readByte())+".";
                    ipServidor+= Byte.toUnsignedInt(archivo.readByte())+".";
                    ipServidor+= Byte.toUnsignedInt(archivo.readByte())+".";
                    ipServidor+= Byte.toUnsignedInt(archivo.readByte());
                }
                archivo.close();
                System.out.println("Extrayendo la Dirección IP del Servidor: "+ipServidor);

                conectar(ipServidor);
                etiqueta_titulo.setText("Conexión establecida");
                // No es necesario inhabilitar campos ya que el JDialog no se mostrará
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al intentar obtener la Dirección IP del servidor\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al intentar obtener la Dirección IP del servidor\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se puede conectar con la Base de Datos.\nAl parecer ha cambiado la Dirección IP del servidor.\nConsulte con el Administrador\n\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            probar_si_soy_servidor();   // Verifico si este equipo es el Servidor
//            Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        etiqueta_titulo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        campo_direccion_ip3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        campo_direccion_ip4 = new javax.swing.JTextField();
        campo_direccion_ip1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        campo_direccion_ip2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        conectar_con_base_datos = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_ips_accesibles = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        obtener_ips_accesibles = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Conexión con el Servidor");

        jPanel2.setBackground(new java.awt.Color(0, 204, 153));

        etiqueta_titulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        etiqueta_titulo.setText("Conexión fallida. Intente lo siguiente:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Ingrese la Dirección IP del servidor");

        campo_direccion_ip3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        campo_direccion_ip3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campo_direccion_ip3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                campo_direccion_ip3FocusGained(evt);
            }
        });
        campo_direccion_ip3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                campo_direccion_ip3KeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText(".");

        campo_direccion_ip4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        campo_direccion_ip4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campo_direccion_ip4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                campo_direccion_ip4FocusGained(evt);
            }
        });
        campo_direccion_ip4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                campo_direccion_ip4KeyTyped(evt);
            }
        });

        campo_direccion_ip1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        campo_direccion_ip1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campo_direccion_ip1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                campo_direccion_ip1FocusGained(evt);
            }
        });
        campo_direccion_ip1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                campo_direccion_ip1KeyTyped(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText(".");

        campo_direccion_ip2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        campo_direccion_ip2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campo_direccion_ip2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                campo_direccion_ip2FocusGained(evt);
            }
        });
        campo_direccion_ip2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                campo_direccion_ip2KeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText(".");

        conectar_con_base_datos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        conectar_con_base_datos.setText("Conectar");
        conectar_con_base_datos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conectar_con_base_datosActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Direcciones IP accesibles:");

        tabla_ips_accesibles.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tabla_ips_accesibles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Dirección IP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
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
        tabla_ips_accesibles.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabla_ips_accesibles.setRowHeight(25);
        tabla_ips_accesibles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabla_ips_accesiblesMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tabla_ips_accesibles);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Dirección IP:");

        obtener_ips_accesibles.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        obtener_ips_accesibles.setText("Obtener IPs accesibles");
        obtener_ips_accesibles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                obtener_ips_accesiblesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campo_direccion_ip1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campo_direccion_ip2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campo_direccion_ip3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campo_direccion_ip4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(conectar_con_base_datos))
                            .addComponent(etiqueta_titulo)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(obtener_ips_accesibles)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiqueta_titulo)
                .addGap(34, 34, 34)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campo_direccion_ip1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(campo_direccion_ip2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(campo_direccion_ip3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(campo_direccion_ip4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conectar_con_base_datos)
                    .addComponent(jLabel8))
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(obtener_ips_accesibles))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void conectar_con_base_datosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conectar_con_base_datosActionPerformed
        new File(CARPETA_PRINCIPAL).mkdirs();
        try {
            validar_direccion_ip();
            // Si la Dirección IP tiene un formato correcto, inicio el intento de conexión
            String ipServidor = campo_direccion_ip1.getText() + "." + campo_direccion_ip2.getText() + "."
                    + campo_direccion_ip3.getText() + "." + campo_direccion_ip4.getText();
            conectar(ipServidor);
            // Si logra hacer la conexión:
            JOptionPane.showMessageDialog(this, "Conexión Establecida", "Información", JOptionPane.INFORMATION_MESSAGE);

            // Si todo está bien, ya se ha creado la conexión y puedo guardar la Dirección IP del servidor
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.writeBytes("SBDdat");   // Escribo la firma del archivo
            // Almaceno los 4 bytes de la Dirección IP del servidor
            archivo.writeByte(Integer.parseInt(campo_direccion_ip1.getText()));
            archivo.writeByte(Integer.parseInt(campo_direccion_ip2.getText()));
            archivo.writeByte(Integer.parseInt(campo_direccion_ip3.getText()));
            archivo.writeByte(Integer.parseInt(campo_direccion_ip4.getText()));
            archivo.close();

            this.dispose(); // Cierro el JDialog
        } catch (ExcepcionDatoIncorrecto ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    //                Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se puede conectar con la Base de Datos", "Error", JOptionPane.ERROR_MESSAGE);
//            Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al intentar guardar la Dirección IP del Servidor", "Error", JOptionPane.ERROR_MESSAGE);
    //                Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_conectar_con_base_datosActionPerformed

    private void obtener_ips_accesiblesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_obtener_ips_accesiblesActionPerformed
        int opcion = JOptionPane.showOptionDialog(this,
                "Este proceso puede tardar hasta cinco minutos\n\nDesea empezar?\n\n\nPor favor, espere hasta que se obtengan los resultados.",
                "Advertencia", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (opcion == JOptionPane.YES_OPTION) {
            listaIPsAccesibles = new ArrayList<>();
            int tiempoEspera = 1000, contadorSinAccesibles = 0;
            String miIP = "";
            String[] ipRed = null;
            for (int contadorIP=0; contadorIP<256; contadorIP++) {
                try {
                    if ("".equals(miIP)) {
                        miIP = InetAddress.getLocalHost().getHostAddress();
                        ipRed = miIP.split("\\.");  // Obtengo los 4 conjuntos de números de la Dirección IP
                    }
                    String ipPrueba = ipRed[0] + "." + ipRed[1] + "." + ipRed[2] + "." + contadorIP;
                    System.out.print("\nDirección IP "+ipPrueba+": ");
                    if (InetAddress.getByName(ipPrueba).isReachable(tiempoEspera)){
                        System.out.print("es Accesible");
                        listaIPsAccesibles.add(ipPrueba);
                        contadorSinAccesibles = 0;
                    } else {
                        System.out.print("no es Accesible");
                        contadorSinAccesibles++;
                    }
                    if (contadorSinAccesibles == 255)
                        contadorIP = 255;
                } catch (UnknownHostException ex) {
//                    Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
//                    Logger.getLogger(ConectarBD.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   // Hasta aquí ya se han obtenido las Direcciones IP a los que se puede acceder
            // Cargo las direcciones a la tabla
            int cantidad = listaIPsAccesibles.size();
            modelIPs.setRowCount(0);    // Borro los datos de la tabla
            for(int i=0; i<cantidad; i++)
                modelIPs.addRow(new String[]{""+(i+1),listaIPsAccesibles.get(i)});
        }
    }//GEN-LAST:event_obtener_ips_accesiblesActionPerformed
    /**Eventos al ingresar texto en los campos de Dirección IP.
     * Estos eventos controlan que el texto de entrada sea sólo dígito y que los valores estén entre [0,255]
     * Los casos que se evalúan son los siguientes:
     * (1) -> Que la tecla presionada sea un dígito
     * (2) -> Si la tecla presionada hace que el nuevo valor sea mayor a 255, no se acepta la tecla
     * (3) -> Si el primer dígito es cero y el que se quiere insertar es cero, no se acepta la tecla
     * (4) -> Si el primer dígito es cero y el que se quiere insertar es diferente de cero, se borra el texto y se inserta la tecla != 0
     * (5) -> Si el texto está seleccionado y se presiona un dígito, se borra el contenido anterior y se aceptala tecla
     */
    private void campo_direccion_ip1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_campo_direccion_ip1KeyTyped
        char teclaPresionada = evt.getKeyChar();
        if (Pattern.compile("\\d").matcher(String.valueOf(teclaPresionada)).matches()) {// (1)
            if (campo_direccion_ip1.getSelectionStart()==0 && campo_direccion_ip1.getSelectionEnd()==campo_direccion_ip1.getText().length())
                campo_direccion_ip1.setText("");    // (5)
            else {
                int nuevoValor = Integer.parseInt(campo_direccion_ip1.getText()+String.valueOf(teclaPresionada));
                int valorTecla = Integer.parseInt(String.valueOf(teclaPresionada));
                if ((nuevoValor>255) || (campo_direccion_ip1.getText().length()==1 && Integer.parseInt(campo_direccion_ip1.getText())==0 && valorTecla==0))
                    evt.consume();  // (2) o (3)
                if (campo_direccion_ip1.getText().length()!=0 && Integer.parseInt(campo_direccion_ip1.getText())==0 && valorTecla!=0)
                    campo_direccion_ip1.setText("");    //(4)
            }
        }
        else
            evt.consume();
    }//GEN-LAST:event_campo_direccion_ip1KeyTyped
    private void campo_direccion_ip2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_campo_direccion_ip2KeyTyped
        char teclaPresionada = evt.getKeyChar();
        if (Pattern.compile("\\d").matcher(String.valueOf(teclaPresionada)).matches()) {// (1)
            if (campo_direccion_ip2.getSelectionStart()==0 && campo_direccion_ip2.getSelectionEnd()==campo_direccion_ip2.getText().length())
                campo_direccion_ip2.setText("");    // (5)
            else {
                int nuevoValor = Integer.parseInt(campo_direccion_ip2.getText()+String.valueOf(teclaPresionada));
                int valorTecla = Integer.parseInt(String.valueOf(teclaPresionada));
                if ((nuevoValor>255) || (campo_direccion_ip2.getText().length()==1 && Integer.parseInt(campo_direccion_ip2.getText())==0 && valorTecla==0))
                    evt.consume();  // (2) o (3)
                if (campo_direccion_ip2.getText().length()!=0 && Integer.parseInt(campo_direccion_ip2.getText())==0 && valorTecla!=0)
                    campo_direccion_ip2.setText("");    //(4)
            }
        }
        else
            evt.consume();
    }//GEN-LAST:event_campo_direccion_ip2KeyTyped
    private void campo_direccion_ip3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_campo_direccion_ip3KeyTyped
        char teclaPresionada = evt.getKeyChar();
        if (Pattern.compile("\\d").matcher(String.valueOf(teclaPresionada)).matches()) {// (1)
            if (campo_direccion_ip3.getSelectionStart()==0 && campo_direccion_ip3.getSelectionEnd()==campo_direccion_ip3.getText().length())
                campo_direccion_ip3.setText("");    // (5)
            else {
                int nuevoValor = Integer.parseInt(campo_direccion_ip3.getText()+String.valueOf(teclaPresionada));
                int valorTecla = Integer.parseInt(String.valueOf(teclaPresionada));
                if ((nuevoValor>255) || (campo_direccion_ip3.getText().length()==1 && Integer.parseInt(campo_direccion_ip3.getText())==0 && valorTecla==0))
                    evt.consume();  // (2) o (3)
                if (campo_direccion_ip3.getText().length()!=0 && Integer.parseInt(campo_direccion_ip3.getText())==0 && valorTecla!=0)
                    campo_direccion_ip3.setText("");    //(4)
            }
        }
        else
            evt.consume();
    }//GEN-LAST:event_campo_direccion_ip3KeyTyped
    private void campo_direccion_ip4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_campo_direccion_ip4KeyTyped
        char teclaPresionada = evt.getKeyChar();
        if (Pattern.compile("\\d").matcher(String.valueOf(teclaPresionada)).matches()) {// (1)
            if (campo_direccion_ip4.getSelectionStart()==0 && campo_direccion_ip4.getSelectionEnd()==campo_direccion_ip4.getText().length())
                campo_direccion_ip4.setText("");    // (5)
            else {
                int nuevoValor = Integer.parseInt(campo_direccion_ip4.getText()+String.valueOf(teclaPresionada));
                int valorTecla = Integer.parseInt(String.valueOf(teclaPresionada));
                if ((nuevoValor>255) || (campo_direccion_ip4.getText().length()==1 && Integer.parseInt(campo_direccion_ip4.getText())==0 && valorTecla==0))
                    evt.consume();  // (2) o (3)
                if (campo_direccion_ip4.getText().length()!=0 && Integer.parseInt(campo_direccion_ip4.getText())==0 && valorTecla!=0)
                    campo_direccion_ip4.setText("");    //(4)
            }
        }
        else
            evt.consume();
    }//GEN-LAST:event_campo_direccion_ip4KeyTyped
    /**
     * Eventos para cuando se posiciona el cursor sobre un campo de Dirección IP. Se selecciona todo el texto (si contiene).
     */
    private void campo_direccion_ip1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_campo_direccion_ip1FocusGained
        campo_direccion_ip1.setSelectionStart(0);
        campo_direccion_ip1.setSelectionEnd(campo_direccion_ip1.getText().length());
    }//GEN-LAST:event_campo_direccion_ip1FocusGained
    private void campo_direccion_ip2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_campo_direccion_ip2FocusGained
        campo_direccion_ip2.setSelectionStart(0);
        campo_direccion_ip2.setSelectionEnd(campo_direccion_ip2.getText().length());
    }//GEN-LAST:event_campo_direccion_ip2FocusGained
    private void campo_direccion_ip3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_campo_direccion_ip3FocusGained
        campo_direccion_ip3.setSelectionStart(0);
        campo_direccion_ip3.setSelectionEnd(campo_direccion_ip3.getText().length());
    }//GEN-LAST:event_campo_direccion_ip3FocusGained
    private void campo_direccion_ip4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_campo_direccion_ip4FocusGained
        campo_direccion_ip4.setSelectionStart(0);
        campo_direccion_ip4.setSelectionEnd(campo_direccion_ip4.getText().length());
    }//GEN-LAST:event_campo_direccion_ip4FocusGained

    private void tabla_ips_accesiblesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_ips_accesiblesMousePressed
        int index = tabla_ips_accesibles.getSelectedRow();
        if (index != -1) {
            String[] ipSeleccionada = listaIPsAccesibles.get(index).split("\\.");
            campo_direccion_ip1.setText(ipSeleccionada[0]);
            campo_direccion_ip2.setText(ipSeleccionada[1]);
            campo_direccion_ip3.setText(ipSeleccionada[2]);
            campo_direccion_ip4.setText(ipSeleccionada[3]);
        }
    }//GEN-LAST:event_tabla_ips_accesiblesMousePressed
    /**
     * Método que valida el texto ingresado como dirección IP. Debido a que en los eventos de los campos de dirección aceptan
     * que la dirección sea válida, lo único que se debe evaluar es que los campos no estén vacíos.
     * @throws Conexion.ConectarBD.ExcepcionDatoIncorrecto excepción que lanza el mensaje de error si los campos están vacios.
     */
    private void validar_direccion_ip() throws ExcepcionDatoIncorrecto {
        // Compruebo que los campos no estén vacios
        if ("".equals(campo_direccion_ip1.getText()) || "".equals(campo_direccion_ip2.getText()) || "".equals(campo_direccion_ip3.getText()) || "".equals(campo_direccion_ip4.getText()))
            throw new ExcepcionDatoIncorrecto("Compruebe que la Dirección IP no tenga campos nulos");
        // Si llega hasta acá, la Dirección IP es correcta
    }
    private void conectar(String ipServidor) throws ClassNotFoundException, SQLException  {
        Class.forName("org.gjt.mm.mysql.Driver");
        conexion = DriverManager.getConnection("jdbc:mysql://" + ipServidor + "/" + nombreBD, usuarioBD, contraseñaBD);
        hacerVisible = false;   // Si se logra la conexión, no es necesario mostrar este JDialog
        this.direccionIPServidor = ipServidor;  // Guardo la Dirección IP del Servidor
    }
    public Connection getConexion() {
        // Verifico si la conexión no se pudo realizar, pero se creó la carpeta con el archivo
        File carpetaPrincipal = new File(CARPETA_PRINCIPAL);
        if (carpetaPrincipal.exists())
            carpetaPrincipal.delete();
        return this.conexion;
    }
    public boolean getHacerVisible() { return this.hacerVisible; }
    public String getDireccionIPServidor() { return direccionIPServidor; }
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
            java.util.logging.Logger.getLogger(ConectarBD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConectarBD dialog = new ConectarBD(new javax.swing.JFrame(), true);
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
    
    private class ExcepcionDatoIncorrecto extends Exception {
        public ExcepcionDatoIncorrecto(String message) { super(message); }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField campo_direccion_ip1;
    private javax.swing.JTextField campo_direccion_ip2;
    private javax.swing.JTextField campo_direccion_ip3;
    private javax.swing.JTextField campo_direccion_ip4;
    private javax.swing.JButton conectar_con_base_datos;
    private javax.swing.JLabel etiqueta_titulo;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton obtener_ips_accesibles;
    private javax.swing.JTable tabla_ips_accesibles;
    // End of variables declaration//GEN-END:variables
}
