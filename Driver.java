/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

/**
 *
 * @author anast
 */
import java.io.IOException;

public class Driver {
    
    public static void main(String[] args) throws IOException{
        Volume vol = new Volume("C:\\Users\\anast\\Documents\\NetBeansProjects\\Volume\\src\\volume\\ext2fs");
        Ext2File file = new Ext2File(vol, "files/trpl-ind-s");
       // Directory directory = new Directory(vol);
        vol.close(); 
    }
}