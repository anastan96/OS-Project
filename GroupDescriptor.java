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
 * A class that holds the information of a Group Descriptor.
 **/
public class GroupDescriptor{
    public int blockBitmapP;
    public int inodeBitmapP;
    public int inodeTableP;
    public short freeBlockCount;
    public short freeInodeCount;
    public short dirCount;
}