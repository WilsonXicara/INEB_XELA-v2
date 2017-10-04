/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tipos_tablas;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USUARIO
 */
public class No_editable_crear_ciclo  extends DefaultTableModel{
    public boolean isCellEditable (int row, int column)
     { boolean editable = true;
       // Aquí devolvemos true o false según queramos que una celda
       // identificada por fila,columna (row,column), sea o no editable
         if(column == 1){
             editable = false;
         }
       return editable;
     }
    
}
