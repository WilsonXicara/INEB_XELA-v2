/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bitacora;

import Conexion.ConectarBD;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que implementa el manejo de un archivo como bitácora para las Transacciones. El manejo de la información se inicia
 * y finaliza respectivamente con la Transacción.
 * @author Wilson Xicará
 */
public class Transaccion {
    private Connection conexion;
    private final String SEPARADOR, CARPETA_PRINCIPAL, rutaBitacora;
    private RandomAccessFile bitacora;
    /** Una constante para indicar que la transacción se finaliza con éxito, CONFIRMANDO los cambios realizados. */
    public static final int COMPROMETIDA = 4;
    /** Una constante para indicar que la transacción se finaliza con algún ERROR. */
    public static final int FALLIDA = 2;
    /** Una constante para indicar que la transacción se finaliza con éxito, DESCARTANDO los cambios realizados. */
    public static final int ABORTADA = 3;
    private static final int ACTIVA = 0;
    private static final int PARCIALMENTE_COMPROMETIDA = 1;
    
    private int longitudBloque, cantidadInstrucciones;
    private long punteroLongitudBloque, punteroEstado, punteroCantidadInstrucciones;
    private boolean iniciada;
    
    /**
     * Inicializa el objeto que implementará la bitácora. Además, intenta hacer un ROLLBACK a la Base de Datos en caso de
     * que hay registro de alguna Transacción que haya finalizado como FALLIDA.
     * @param conexion Conexión activa con la Base de Datos.
     */
    public Transaccion(Connection conexion) {
        this.conexion = conexion;
        SEPARADOR = ConectarBD.SEPARADOR;
        CARPETA_PRINCIPAL = ConectarBD.CARPETA_PRINCIPAL;
        rutaBitacora = CARPETA_PRINCIPAL + SEPARADOR + "bitacora.log";
        
        File carpetaBitacora = new File(CARPETA_PRINCIPAL);
        File archivoBitacora = new File(rutaBitacora);
        RandomAccessFile archivo;
        if (!carpetaBitacora.exists() || !archivoBitacora.exists()) { // Si el archivo no existe, se crea uno vacío
            carpetaBitacora.mkdirs();
            try {
                archivo = new RandomAccessFile(archivoBitacora, "rw");
                String cadenaInicial = "SBlog"; // Firma y tipo del archivo
                archivo.writeBytes(cadenaInicial);
                archivo.writeByte(0);
                archivo.writeByte(0);
                archivo.writeByte(0xFF);
            } catch (FileNotFoundException ex) {
//                Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
//                Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // En caso de que exista el archivo, verifico si tiene almacenado el historial de transacciones anteriores.
            // Si las transacciones almacenadas no fueron completadas con éxito estarán como ACTIVA, PARCIALMENTE_COMPROMETIDA
            // o FALLIDA. En cualquier caso, se hará un ROLLBACK sin importar el estado.
            try {
                conexion.setAutoCommit(false);  // Inhabilito el AUTO-COMIT para poder hacer el ROLLBACK
                conexion.rollback();    // Hago un ROLLBACK para finalizar cualquier Transacción no terminada.
                conexion.setAutoCommit(true);
                
                archivo = new RandomAccessFile(archivoBitacora, "rw");
                archivo.skipBytes(6);   // Salto hasta el byte con la cantidad de transacciones guardadas
                int cantidad = Byte.toUnsignedInt(archivo.readByte()), auxCantidad, cont, longBloque, contSentecias;
                long punteroBloqueT;
                String[] estados = {"Activa", "Parcialmente Comprometida", "Fallida", "Abortada", "Comprometida"};
                // Inicio de la eliminación de los bloques almacenados de las Transacciones anteriores.
                punteroBloqueT = archivo.getFilePointer();  // Almaceno el puntero al inicio del bloque de información
                for(cont=0, auxCantidad=cantidad; cont<cantidad; cont++) {
                    // La eliminación se hace del primer al último bloque, por lo que punteroBloqueT siempre tendrá el mismo valor
                    longBloque = Short.toUnsignedInt(archivo.readShort());  // Almaceno la longitud del bloque de información
                    
                    // Impresión del bloque de información de la Transacción actual
                    System.out.println("**********  Iniciando la eliminación de la Transacción con fecha '"+archivo.readLine()+"':");
                    System.out.println("Estado: "+estados[Byte.toUnsignedInt(archivo.readByte())]);
                    System.out.println("Sentecias SQL ejecutadas:");
                    contSentecias = Byte.toUnsignedInt(archivo.readByte());   // Obtengo la cantidad de sentencias SQL de la Transacción actual
                    for(int i=0; i<contSentecias; i++)
                        System.out.println("  "+(i+1)+" -> "+archivo.readLine());
                    System.out.println("**  Fin de la Transacción.");
                    
                    // Eliminación del bloque de información de la Transacción actual
//                    archivo.seek(punteroBloqueT + 2 + longBloque);  # Descomentar en caso de no imprimir la información del bloque
                    byte[] bloqueFinal = new byte[(int)(archivo.length()-1 - archivo.getFilePointer())];
                    archivo.read(bloqueFinal);  // Leo todo el bloque que va después de la Transacción a eliminar
                    archivo.seek(punteroBloqueT);   // Regreso al inicio del bloque de información a eliminar
                    archivo.write(bloqueFinal); // Escribo todo el bloque leido
                    archivo.setLength(archivo.length() - 2 - longBloque);   // Acortación del tamaño de archivo
                    auxCantidad--;
                    archivo.seek(punteroBloqueT);
                }
                archivo.seek(6);    // Regreso a donde está la cantidad anterior de Transacciones guardadas
                archivo.writeShort(auxCantidad);
                archivo.close();
                // HASTA AQUÍ SE GARANTIZA LA ATENCIÓN Y ELIMINACIÓN DE LAS TRANSACCIONES FALLIDAS
            } catch (FileNotFoundException ex) {
                System.out.println("Error: Es posible que el archivo no exista. Al evaluar el estado del archivo");
//                Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                System.out.println("Error: Es posible que el archivo tenga mal los punteros haciendo referencia a posiciones no válidas.");
//                Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                System.out.println("Error: Es posible que la conexión haya sido cerrada.");
//                Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Hasta aquí se garantiza la existencia del archivo
    }
    /**
     * Prepara en el archivo el bloque de información que se almacenará para la Transacción actual.
     */
    public void iniciar() {
        try {
            bitacora = new RandomAccessFile(rutaBitacora, "rw");
            // Llevo el puntero hasta el penúltimo byte del archivo (donde se escribirá el registro de la transacción iniciada)
            bitacora.seek(bitacora.length() - 1);
            punteroLongitudBloque = bitacora.getFilePointer();
            bitacora.writeShort(0); // Inicialmente la longitud del bloque es 0
            // Obtengo la fecha y hora que identificará a la transacción actual
            ResultSet cConsulta = conexion.createStatement().executeQuery("SELECT NOW()");
            cConsulta.next();
            String aux = cConsulta.getString(1)+'\n';   // Concatenación de la fecha y hora, con un salto de linea al final
            bitacora.writeBytes(aux);
            punteroEstado = bitacora.getFilePointer();
            bitacora.writeByte(ACTIVA); // Concatenación del indicador de que la transacción está activactual
            punteroCantidadInstrucciones = bitacora.getFilePointer();
            bitacora.writeByte(0); // Inicialmente hay 0 instrucciones SQL ejecutadas
            cantidadInstrucciones = 0;
            longitudBloque = aux.length() + 2;  // Longitudes de fecha y hora, estado y cantidad de instrucciones
            bitacora.writeBytes(""+(char)0xFF); // Escritura del indicador de Fin de archivo
            bitacora.seek(punteroLongitudBloque);   // Escritura de la longitud actual del bloque de información de la transacción actual
            bitacora.writeShort(longitudBloque);
            // Actualización de la cantidad de Transacciones almacenadas en el archivo
            bitacora.seek(6);
            int cantidad = Byte.toUnsignedInt(bitacora.readByte());
            bitacora.seek(6);
            bitacora.writeByte(++cantidad);
            // Dejo el puntero al final del bloque de información de la transacción actual
            bitacora.seek(bitacora.length() - 1);
            // Indicador de que la transacción ha sido iniciada
            iniciada = true;
        } catch (FileNotFoundException ex) {
            System.out.println("Error: Es posible que el archivo no exista. Al iniciar la transacción");
//            Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error: Es posible que el archivo tenga mal los punteros haciendo referencia a posiciones no válidas. Al iniciar la transacción");
//            Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            System.out.println("Error: Al intentar obtener la Fecha-Hora que identificará a la transacción actual");
//            Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Escribe en la bitácora la sentencia SQL ejecutada. No interesa si la sentencia es correcta o no, ya que en caso de
     * ocurrir un error se finaliza la conexión y se llama al procedimiento correspondiente.
     * @param sentenciaSQL String con la sentencia SQL enviada a la Base de Datos.
     */
    public void almacenarSentenciaSQL(String sentenciaSQL) {
        // Aquí sólo se guarda la instrucción SQL y se actualiza el contador de cantidad de instrucciones en el archivo
        try {
            bitacora.writeBytes(sentenciaSQL + '\n' + (char)0xFF);  // También se escribe el indicador de Fin de archivo
            longitudBloque+= sentenciaSQL.length() + 1;
            // Actualización de la longitud del bloque
            bitacora.seek(punteroLongitudBloque);
            bitacora.writeShort(longitudBloque);
            // Actualización de la cantidad de instrucciones
            bitacora.seek(punteroCantidadInstrucciones);
            bitacora.writeByte(++cantidadInstrucciones);
            // Dejo el puntero en el penúltimo byte del archivo
            bitacora.seek(bitacora.length() - 1);
        } catch (FileNotFoundException ex) {
            System.out.println("Error: Es posible que el archivo no exista. Al almacenar una sentencia SQL");
//            Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error: Es posible que el archivo tenga mal los punteros haciendo referencia a posiciones no válidas. Al almacenar una sentencia SQL");
//            Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Finaliza el almacenamiento de la información de la Transacción finalizada. Una vez finalizada la Transacción es
     * necesario llamar al método iniciar() para poder utilizar nuevamente el objeto.
     * @param estadoFinal Una de las siguientes constantes de Transaccion: Transaccion.COMPROMETIDA, Transaccion.ABORTADA
     * o Transaccion.FALLIDA que indica el estado con el que se finalizó la Transacción.
     */
    public void finalizar(int estadoFinal) {
        // Aquí se actualiza el estado de la transacción y se elimina el bloque (si no es FALLIDA)
        try {
            bitacora.seek(punteroEstado);   // Primero indico que está parcialmente comprometida
            bitacora.writeByte(PARCIALMENTE_COMPROMETIDA);
            
            switch (estadoFinal) {  // Verificación del estado final que tuvo la Transacción
                case FALLIDA:
                    bitacora.seek(punteroEstado);
                    bitacora.writeByte(FALLIDA);
                    break;
                case COMPROMETIDA:
                case ABORTADA:
                    // Se finalizó con éxito. No es necesario guardar el estado pues al final se eliminará el bloque
                    
                    // Inicio de la eliminación del bloque de información de la Transacción finalizada
                    // Se asume que el bloque actual siempre será el último ya que no se puede editar una transacción anterior
                    bitacora.setLength(bitacora.length() - longitudBloque - 2 - 1); // Se toma en cuenta el indicador de Fin de archivo
                    bitacora.seek(bitacora.length());
                    bitacora.writeBytes(""+(char)0xFF); // Escritura del indicador de Fin de archivo
                    bitacora.seek(6);
                    int cantidadT = Byte.toUnsignedInt(bitacora.readByte());
                    bitacora.seek(6);
                    bitacora.writeByte(--cantidadT);
                    break;
                default:
                    // Si se pasa un parámetro desconocido, se asume que se confirma la transacción
                    bitacora.seek(punteroEstado);
                    bitacora.writeByte(COMPROMETIDA);
                    break;
            }
            bitacora.close();   // Cierre del archivo
            iniciada = false;   // Indicador de que la transacción ha sido finalizada
        } catch (FileNotFoundException ex) {
            System.out.println("Error: Es posible que el archivo no exista. Al finalizar la Transacción");
//            Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error: Es posible que el archivo tenga mal los punteros haciendo referencia a posiciones no válidas. Al finalizar la Transacción");
//            Logger.getLogger(Transaccion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Retorna el estado actual de la bitácora, que registra los eventos de la conexión con la Base de Datos.
     * @return Retorna true si la Transacción sigue activa, o false en caso contrario.
     */
    public boolean estaActiva() { return iniciada; }
}
