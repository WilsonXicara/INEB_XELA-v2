/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modulo_Estudiante;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTable;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author Wilson Xicará
 */
public class ExportarAExcel {
    /**
     * Método que permite exportar varios JTable en un solo Libro de Excel. Cada JTable se exporta a una Hoja de Excel.
     * @param archivoExcel archivo de tipo xls donde se almacenarán los datos de los JTable.
     * @param listaTablas ArrayList de JTable con todas las tablas que se exportarán.
     * @param listaNombresHojasDeExcel ArrayList de  nombres de cada JTable que se asigarán a las Hojas en el archivo xls.
     * En caso de de listaNombresHojasDeExcel.isEmpty() == true o listaTablas.size()!=listaNombresHojasDeExcel.size(),
     * todos los libros tendrán un nombre correlativo como "Hoja "+(contador).
     * @param tituloColumnas ArrayList de booleanos que indican cuáles JTable se exportarán el título de las columnas.
     * @throws IOException
     * @throws WriteException 
     */
    public static void exportar_tablas(File archivoExcel, ArrayList<JTable> listaTablas, ArrayList<String> listaNombresHojasDeExcel, ArrayList<Boolean> tituloColumnas) throws IOException, WriteException {
        // En caso de que no se especifica el nombre de las Hojas de Excel, se agregarán nombres por defecto
        if (listaNombresHojasDeExcel.isEmpty() || listaTablas.size()!=listaNombresHojasDeExcel.size()) {
            listaNombresHojasDeExcel.clear();
            int cantidad = listaTablas.size();
            for(int i=0; i<cantidad; i++)
                listaNombresHojasDeExcel.add("Hoja "+(i+1));   // Le asigno los nombres por defecto
            
            // Ahora inicia la exportación de todas las tablas
            WritableWorkbook libroExcel = Workbook.createWorkbook(archivoExcel);
            for (int cont=0; cont<cantidad; cont++) {
                JTable miTabla = listaTablas.get(cont);
                WritableSheet hojaExcel = libroExcel.createSheet(listaNombresHojasDeExcel.get(cont), 0);
                int contFil = miTabla.getRowCount(), contCol = miTabla.getColumnCount();
                if (tituloColumnas.get(cont)) {
                    // Se exporta el título de las columnas
                    for(int col=0; col<contCol; col++)
                        hojaExcel.addCell(new Label(col, 0, miTabla.getColumnName(col)));
                    // Se exportan los datos de la tabla
                    for(int fil=0; fil<contFil; fil++)
                        for(int col=0; col<contCol; col++)
                            hojaExcel.addCell(new Label(col, fil+1, (String)miTabla.getValueAt(fil, col)));
                } else {
                    // Se exportan los datos de la tabla
                    for(int fil=0; fil<contFil; fil++)
                        for(int col=0; col<contCol; col++)
                            hojaExcel.addCell(new Label(col, fil, (String)miTabla.getValueAt(fil, col)));
                }
            }
            libroExcel.write();
            libroExcel.close();
        }
    }
    /**
     * Método que permite exportar un JTable en un solo archivo de Excel, en una Hoja de Excel.
     * @param archivoExcel archivo de tipo xls donde se almacenarán los datos de los JTable.
     * @param tabla JTable con los datos que se exportarán a una Hoja de Excel.
     * @param nombreLibroDeExcel nombre que se le dará a la Hoja del archivo Excel.
     * @param tituloColumna booleano que indica si se exportarán los títulos de las columnas.
     * @throws IOException
     * @throws WriteException 
     */
    public static void exportar_tabla(File archivoExcel, JTable tabla, String nombreLibroDeExcel, boolean tituloColumna) throws IOException, WriteException {
        WritableWorkbook libroExcel = Workbook.createWorkbook(archivoExcel);
        WritableSheet hojaExcel = libroExcel.createSheet(nombreLibroDeExcel, 0);
        int contFil = tabla.getRowCount(), contCol = tabla.getColumnCount();
        if (tituloColumna) {
            // Se exporta el título de las columnas
            for(int col=0; col<contCol; col++)
                hojaExcel.addCell(new Label(col, 0, tabla.getColumnName(col)));
            // Se exportan los datos de la tabla
            for(int fil=0; fil<contFil; fil++)
                for(int col=0; col<contCol; col++)
                    hojaExcel.addCell(new Label(col, fil+1, (String)tabla.getValueAt(fil, col)));
        } else {
            // Se exportan los datos de la tabla
            for(int fil=0; fil<contFil; fil++)
                for(int col=0; col<contCol; col++)
                    hojaExcel.addCell(new Label(col, fil, (String)tabla.getValueAt(fil, col)));
        }
        libroExcel.write();
        libroExcel.close();
    }
}
