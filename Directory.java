/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import java.io.IOException;


/**
 *
 * @author anast
 */

public class Directory extends Ext2File{

    public Directory(Volume vol, String path) throws IOException {
        super(vol, path);
    }
}