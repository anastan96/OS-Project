/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import java.io.*;

/**
 *
 * @author anast
 */

/**
 * Opens the Volume represented by the ext2fs file
 * Reads the content of the volume.
 **/
public class Volume {
    public RandomAccessFile filesystem;
    private Superblock sb;
    private int numGroups;
    

    /**
     * The next two bytes are interpreted as a short.
     * They are ordered in little endian.
     **/
    public short readShort() throws IOException {
	byte[] b = new byte[2];
	if(filesystem.read(b) != 2) {
            System.out.println("Failed to read an int");
        }
        short val = (short) ( (b[0] & 0xff) | ((b[1] & 0xff) << 8) );
        return val;
    }
    /**
     * Reads the bytes in a short from the given offset ordered in little endian.
     * 
     * @param b the buffer into which the data is read.
     * @param offset the start offset
     **/
    private short readShort(byte[] b, int offset) throws IOException {
        short val = (short) ( (b[offset] & 0xff) | ((b[offset+1] & 0xff) << 8) );
        return val;
    }

    /**
     * The next four bytes are interpreted as an int.
     * They are ordered in little endian.
     **/
    public int readInt() throws IOException {
	byte[] b = new byte[4];
	if(filesystem.read(b) != 4) {
		System.out.println("Failed to read an int");
	}
	int val = (b[0] & 0xff) | ((b[1] & 0xff) << 8) | ((b[2] & 0xff) << 16) | ((b[3] & 0xff) << 24);
	return val;
    }
    
    /**
     * Reads the given bytes in an int from the given offset ordered in little endian.
     * 
     * @param b the buffer into which the data is read.
     * @param offset the start offset.
     **/
    private int readInt(byte[] b, int offset) throws IOException {
        int val = (b[offset] & 0xff) | ((b[offset+1] & 0xff) << 8) | ((b[offset+2] & 0xff) << 16) | ((b[offset+3] & 0xff) << 24);
        return val;
    }

    /**
     * Method that reads the Superblock.
     **/
    private void readSuperblock() {
        if(sb == null)
            sb = new Superblock();
        
        try {
            filesystem.seek(1024);
            sb.totalInodes = readInt();
            sb.totalBlocks = readInt();
            filesystem.seek(1024 + 24);
            sb.blockSize = readInt();
            filesystem.seek(1024 + 32);
            sb.blocksPerGroup = readInt();
            filesystem.seek(1024 + 40);
            sb.inodesPerGroup = readInt();
            filesystem.seek(1024 + 56);
            sb.magicNumber = readShort();
            filesystem.seek(1024 + 88);
            sb.inodeSize = readShort();
            filesystem.seek(1024 + 120);

            if(filesystem.read(sb.volumeName) != 16) {
                System.out.println("Couldn't read volume name properly");
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }

    /**
     * Method that prints the Superblock.
     **/
    private void printSuperblock() {
      /*  System.out.println("SUPERBLOCK");
	System.out.printf("Magic number: 0x%x\n", sb.magicNumber);
        System.out.println("Total number of inodes: " + sb.totalInodes);
	System.out.println("Total number of blocks: " + sb.totalBlocks);
        System.out.println("Block size: " + sb.blockSize);
	System.out.println("Blocks per group: " + sb.blocksPerGroup);
        System.out.println("Inodes per group: " + sb.inodesPerGroup);
	System.out.println("Inode size: " + sb.inodeSize);
        System.out.println("Volume name: " + new String(sb.volumeName));*/
    }
    
    /**
     * Method that reads the Group Descriptor and the Block Bitmap.
     * 
     * @param groupNumber the number of the group.
     **/
    private GroupDescriptor readGroupDescriptor(int groupNumber) {
        String clr = (char)27 + "[0m";
        String gre = (char)27 + "[32;1m";
        String mag = (char)27 + "[35;21m";
        GroupDescriptor gd = new GroupDescriptor();

        int start = 2048; 

        try {
            filesystem.seek(start + 32*groupNumber);
            gd.blockBitmapP = readInt(); 
	    gd.inodeBitmapP = readInt();
            gd.inodeTableP = readInt();
	    gd.freeBlockCount = readShort();
            gd.freeInodeCount = readShort();
	    gd.dirCount = readShort();

            //The Group Descriptor is printed.
         /*   System.out.println("\nGROUP DESCRIPTOR " + groupNumber);
	    System.out.println("Block Bitmap Pointer:" + gd.blockBitmapP);
	    System.out.println("Inode Bitmap Pointer:"  + gd.inodeBitmapP);
	    System.out.println("Inode Table Pointer:" + gd.inodeTableP);
            System.out.println("Free Block Count:" + gd.freeBlockCount);
            System.out.println("Free Inode Count:" + gd.freeInodeCount);
	    System.out.println("Used Directories Count:" + gd.dirCount + "\n"); 

            //The Block Bitmap is printed.
            System.out.println("BLOCK BITMAP");
            filesystem.seek(1024 * gd.blockBitmapP);
            for(int c = 0; c < 1024; ++c) {
                if(c % 20 == 0) {
                  
                    System.out.printf("\nBlock %4d: ", c*8);
                }
                byte b = filesystem.readByte();
	      	if(b != 0) {
                    System.out.print(gre);
                   
                    System.out.printf("%02x", b);
                    System.out.print(clr + " "); 
                } else {
                    System.out.print(mag);
                    System.out.printf("%02x", b);
                    System.out.print(clr + " ");
                }
            }
            System.out.println();*/
        } catch (IOException e) {
            System.out.println("Error reading group descriptor " + groupNumber);
        }
        return gd;
    }

    /**
     * Method that reads a given inode.
     * 
     * @param inodeNumber the given inode number
     **/
    public Inode readInode(int inodeNumber) {
        inodeNumber--;
        Inode id = new Inode();
        if(inodeNumber < 0 || inodeNumber >= sb.totalInodes) {
            System.out.println("Bad inode number");
        }

        try {
            int groupNumber = inodeNumber / sb.inodesPerGroup;
            int inodeOffset = inodeNumber % sb.inodesPerGroup; 
            GroupDescriptor gd = readGroupDescriptor(groupNumber);

            filesystem.seek(1024 * gd.inodeTableP + 128*inodeOffset);
            byte[] inode = new byte[128];
            filesystem.read(inode);
            id.filemode = readShort(inode, 0);
            id.userID = readShort(inode, 2);
            id.groupID = readShort(inode, 24);
            id.size = readInt(inode ,4);
            id.lastModifiedTime = readInt(inode, 16);
            id.accessTime = readInt(inode, 8);
            id.hardLinksNumber = readShort(inode, 26);
            id.indirect1 = readInt(inode, 88);
            id.indirect2 = readInt(inode, 92);
            id.indirect3 = readInt(inode, 96);
            
            for(int i = 0; i < 12; ++i)
            	id.direct[i] = readInt(inode, 40 + i*4);
            
            //Some of the contents of an inode are printed
            System.out.println("\nINODE " + (inodeNumber + 1));
            System.out.printf("File mode: 0x%04x\n", readShort(inode, 0));
            System.out.println("User ID: " + readShort(inode, 2));
            System.out.println("Group ID: " + readShort(inode, 24));
            System.out.println("File size: " + readInt(inode, 4));
	    System.out.println("Number of hard links: " + readShort(inode, 26));
	    int data = readInt(inode, 40);   //first pointer
	    System.out.println("\n - DATA at block " + data);

            //Prints the data found in the data block referenced by the first pointer
	    System.out.println("inode\tlength\tn_chars\ttype\tname");
	    int offset = 0;
	    int next = 0;
	    int nameLen = 0;

	    while (data != 0 && offset < 1024) {
                filesystem.seek(data*1024 + offset);
                System.out.printf("%d\t%d\t%d\t%d\t", readInt(), next = 0xffff & readShort(), nameLen = 0xff & filesystem.readByte(), filesystem.readByte());
	       	byte[] chars = new byte[nameLen];
	       	filesystem.read(chars);
	       	String name = new String(chars);
	       	System.out.println(name);
	       	offset += next;
            }
        } catch (IOException e) {
            System.out.println("Error reading inode " + (inodeNumber+1));
        }
        return id;
    }

    /**
     * Opens the volume.
     * 
     * @param filename the filename that will be read.
     **/
    public Volume(String filename) {
        try {
            filesystem = new RandomAccessFile(filename, "r");
        } catch (FileNotFoundException e) {
            System.out.println("Filesystem not found");
            return;
        }

        readSuperblock();
	numGroups = 1 + (sb.totalBlocks - 1) / sb.blocksPerGroup;
        printSuperblock();
	readInode(2);
        //readInode(1722); //folder
        tree(2, "");
	}

    /**
     * Method that close the filesystem.
     **/
    public void close() {
        try {
            filesystem.close();
        } catch (IOException e) {
            System.out.println("Error closing filesystem");
        }
    }
    
    /**
     * Method that sets the file-pointer offset.
     * 
     * @param pos the offset position
     **/
    public void seek(long pos) throws IOException{
        filesystem.seek(pos);
    }
    
    /**
     * Method that reads a byte from the file, starting from the current file pointer.
     **/
    public byte readByte() throws IOException {
        return filesystem.readByte();
    }
    
    /**
     * Reads the data from this file into an array of bytes.
     * 
     * @param  b the buffer into which the data is read.
     **/
    public void read(byte[] b) throws IOException {
        filesystem.read(b);
    }
    
    /**
     * Traverse the filesystem.
     * 
     * @param inode the given inode.
     * @param tab a string 
     **/
    public void tree(int inode, String tab) {
        String[] col = {(char)27 + "[0m", (char)27 + "[32;1m", (char)27 + "[33m", (char)27 + "[35;21m"};
        try {
            Inode inodeInfo = readInode(inode);
            long dataBlock;
            for(int i = 0; i < 12; ++i) {                                       //12 pointers
                dataBlock = 0xffffffff & inodeInfo.direct[i];                   //Access the data from the direct pointers
		int offset = 0, inodeNum, next, nameLen, type;

                if(dataBlock == 0) continue;                                    //blocks with holes

                while(offset < 1024) {
                    filesystem.seek(dataBlock*1024 + offset); 
                    inodeNum = readInt();
                    next = 0xffff & readShort();                                //next = size; to know where to set the next offset
                    nameLen = 0xff & filesystem.readByte(); 
                    type = 0xff & filesystem.readByte();                        //type 2 is a folder
                    //System.out.printf("%d\t%d\t%d\t%d\t", inodeNum, next, nameLen, type);

                    if(inodeNum == 0 || nameLen == 0) break;

                    //read the name
                    byte[] chars = new byte[nameLen];
                    filesystem.read(chars);
                    String name = new String(chars);

                    if(chars[0] != '.') {                                       //current directory, entry starts with a point 
                        System.out.print(tab + "+ ");
                        System.out.print(col[type]);
                        System.out.println(name);
                        System.out.print(col[0]);
			if(type == 2)                                           //if it is a folder => depth-first search => make a tree
                            tree(inodeNum, tab + "  ");
                    }
                    offset += next;                                             
                }
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}