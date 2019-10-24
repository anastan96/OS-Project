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
 * A class that holds the information of an inode.
 **/
public class Inode {                    // offsets:
    public short filemode;              // 0
    public short userID;                // 2
    public int size;                    // 4
    public int accessTime;              // 8
    public int creationTime;            // 12
    public int lastModifiedTime;        // 16
    public int deletedTime;             // 20
    public short groupID;               // 24
    public short hardLinksNumber;       // 26
    public int num512Blocks;            // 28
    public int flags;                   // 32
    public int osDependent;             // 36
    public int[] direct = new int[12];  // 40
    public int indirect1;               // 88
    public int indirect2;               // 92
    public int indirect3;               // 96
    public int gen;                     // 100
    public int acl;                     // 104
    public int upperSize;               // 108
    public int fragAddr;                // 112
    public byte fragNum;                // 116
    public byte fragSize;               // 117
    public short pad;                   // 118
    public short upperUserID;           // 120
    public short upperGroupID;          // 122
    public int reserved;                // 124
}                                       // size = 128 B