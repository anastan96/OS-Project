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

public class Ext2File {
    public int length; 
    Inode in;
    Volume vol;
    
    /**
     * Reads and prints the content of a file with the help of readAll() method.
     * Splits the path received through a parameter.
     * Compares the split path with the filename found in the directory.
     * 
     * @param vol represents the volume.
     * @param path the path that wants to be found
     **/
    public Ext2File(Volume vol, String path) throws IOException{   
        this.vol = vol;
        String[] result = path.split("/");
        int currentInode = 2;
        for(int j = 0; j < result.length; j++){
            Inode inodeInfo = vol.readInode(currentInode);
            long dataBlock;
            
            boolean found = false;
            
            for(int i = 0; i < 12; ++i) {
                dataBlock = 0xffffffff & inodeInfo.direct[i];
                int offset = 0, inodeNum, next, nameLen, type;
             
                if(dataBlock == 0) continue;
             
                while(offset < 1024) {
                    vol.seek(dataBlock * 1024 + offset);
                    inodeNum = vol.readInt();
                    next = 0xffff & vol.readShort();
                    nameLen = 0xff & vol.readByte();
                    type = 0xff & vol.readByte();
                
                    if(inodeNum == 0 || nameLen == 0) break;
                
                    byte[] chars = new byte[nameLen];
                    vol.read(chars);
                    String name = new String(chars);
                    
                   // System.out.println(name + " " + result[j]);
                    if( name.equals(result[j])){
                        currentInode = inodeNum;
                        System.out.println("Moving to file: " + name + " Inode: " + inodeNum);
                        found = true;
                        break;
                    }
                    offset += next;
                }
                if(found) {
                    break;
                }
            }
        }
        System.out.println("Inode: " + currentInode);
        in = vol.readInode(currentInode); // update the current inode
        length = in.size;
        vol.seek( in.direct[0] * 1024 );
        readAll();
        
    }
    
   /**
    * Finds the data block referenced by the pointers.
    **/ 
    public void seek(long position) throws IOException{
        int block = (int)(position / 1024);
        int offset = (int)(position % 1024);
        int b;

        //use direct pointers
        if((0 <= block) && (block < 12)){
            vol.seek(in.direct[block] * 1024 + offset); 
        }

        if((12 <= block) && (block < 268)){
            b = block - 12;
            vol.seek(in.indirect1 + 4*b);
            b = vol.readInt();
            vol.seek(b*1024 + offset);
        }
        
        if((268 <= block) && (block < 65804)){
            b = block - 268;
            int b2 = b / 256;    //we have 256 pointers in one block
            vol.seek(in.indirect2*1024 + 4*b2);
            b2 = vol.readInt();
            vol.seek(b2*1024 + (b % 256)*4); //find the data block, we know the relative position
        }   
    }
    
    /**
     * Returns the size of the file.
     **/
    public long size() {
        return 0xffffffff & length; //8 f = 32 biti, valoare a unui intreg ca sa se poata interpreta ca poz si nu neg
    }

    /**
     *Reads and prints the content of a data block.
     **/
    public void readAll() throws IOException{
        // direct pointer
        for(int b = 0; b < 12; ++b) {
            int real_b = in.direct[b];
            //System.out.println("\n--- BLOCK " + real_b + "(" + b + ")");
            if( real_b == 0) { //content
            //System.out.println("\tSkipped");
                continue;
	        	}

            vol.seek(real_b * 1024); //offset
            for(int i = 0; i < 1024; ++i){
                System.out.print((char)vol.readByte()); //read the characters
            }
        }
        //System.out.println("End direct");

	//indirect pointer
        int block = in.indirect1;
        //System.out.println("Indirect: " + block);
        if(block > 0) {
            for(int i = 0; i < 1024; i+=4) { //int
                vol.seek(block * 1024 + i); 
                int block2 = vol.readInt();
                if(block2 == 0) continue;

                //System.out.println("BLOCK " + block2);
                vol.seek(block2*1024);
                for(int j = 0; j < 1024; ++j) {
                    System.out.print((char)vol.readByte());
                }
            }
        }

        // double indirect pointer
        block = in.indirect2;
       // System.out.println("Double indirect: " + block);
        if(block > 0) {
            for(int i = 0; i < 1024; i+=4) {
                vol.seek(block * 1024 + i);
                int block2 = vol.readInt();
                if(block2 == 0) continue;

                for(int j = 0; j < 1024; j+=4) {
                    vol.seek(block2 * 1024 + j);
                    int block3 = vol.readInt();
                    if(block3 == 0) continue;

                    //System.out.println("BLOCK " + block3);
                    vol.seek(block3*1024);
                    for(int c = 0; c < 1024; ++c) {
                        System.out.print((char)vol.readByte());
                    }
                }
            }
        }

        block = in.indirect3;
        //System.out.println("Triple indirect: " + block);
        if(block > 0) {
            for(int i = 0; i < 1024; i+=4) {
                vol.seek(block * 1024 + i);
                int block2 = vol.readInt();
                if(block2 == 0) continue;

                for(int j = 0; j < 1024; j+=4) {
                    vol.seek(block2 * 1024 + j);
                    int block3 = vol.readInt();
                    if(block3 == 0) continue;

                    for(int k = 0; k < 1024; k+=4) {
                        vol.seek(block3 * 1024 + k);
                        int block4 = vol.readInt();
                        if(block4 == 0) continue;

                        //System.out.println("BLOCK " + block4);
                        vol.seek(block4*1024);
                        for(int c = 0; c < 1024; ++c) {
                            System.out.print((char)vol.readByte());
                        }
                    }
                }
            }
        }  
    }
}
