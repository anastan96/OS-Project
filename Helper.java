/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import java.util.Scanner;

/**
 *
 * @author anast
 */

/**
 * Outputs an array of bytes in a readable hexadecimal format.
 **/
public class Helper {
    public static void dumpHexBytes(byte[] bytes){
        StringBuilder sb = new StringBuilder();            //Constructs a string builder with no characters in it
        for(byte b : bytes) {
            sb.append(String.format("%02X ", b));          //Appends the string representation to the sequence.
        }
        System.out.println(sb.toString());
    }
}