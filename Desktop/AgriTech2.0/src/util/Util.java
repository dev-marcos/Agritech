/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;


/**
 *
 * @author Marcos Vinicius
 */
public class Util {
    
    public static boolean validaInteiro(String valor, int Min, int Max){
        try{
            return Integer.parseInt(valor) >= Min && Integer.parseInt(valor) <= Max;            
        }catch(NumberFormatException e){
            return false;
        } 
    }
    public static boolean validaInteiro(String valor){
        try{
            Integer.parseInt(valor);
            return true;        
        }catch(NumberFormatException e){
            return false;
        } 
    }
    


}
