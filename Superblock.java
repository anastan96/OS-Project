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

/**
 * A class that holds the information of a Super-block.
 **/
public class Superblock {
    public short magicNumber;
    public int totalInodes;
    public int totalBlocks;
    public int blockSize;
    public int blocksPerGroup;
    public int inodesPerGroup;
    public short inodeSize;
    public byte[] volumeName = new byte[16];
}