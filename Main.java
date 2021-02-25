import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;

/*
 * Alexander Schneider
 * Created 2/24/2021
 * Last Edit 2/24/2021
 * Due 2/25/2021
 * CS 420 Assignment 05
 * Written in Java 11 with VS Code
 */

public class Main {

    static String fileName = "RandomAccessFile.txt";
    public static void main(String[] args) {

        char[] chars = new char[27];
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n";
        //convert string to byte array
        byte[] byteArray = letters.getBytes();
        byte[] dest = new byte[15];
        byte[] contents = new byte[4104];
        String numbers = "0123456789";
        byte[] numberBytes = numbers.getBytes();
        
        //populate array
        for(int i = 0; i < chars.length; i++) {
            chars[i] = letters.charAt(i);
        }

        try {
            //create file, set to READ WRITE
            RandomAccessFile file = new RandomAccessFile(fileName, "rw");
            //2052 bytes is 76 lines *2 = 4104 
            //set the rw channel to READ WRITE, with a buffer of 4104 bytes
            MappedByteBuffer rw = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 4104);

            for(int j = 0; j < 76; j++) {
                for(int i = 0; i < chars.length; i++) {
                    rw.put((byte) chars[i]);
                }
            }

            for(int i = 0; i < 76; i++) {
                rw.put(byteArray);
            }  
            
            //At this point when i look at the file size it is 4104 bytes 
            //write to persistent memory
            rw.force();

            //set file position to 15
            file.seek(15);
            //read 11 bytes to array from postition 15, no offset
            file.read(dest, 0, 11);
            
            System.out.println("\nRandomAccessFile Read: \n");
            for(int i = 0; i < dest.length; i++) {
                System.out.print((char)dest[i]);
            }

            //set file position to 15
            rw.position(15);
            //read 11 bytes to array from postition 15, no offset
            rw.get(dest, 0, 11);

            System.out.println("\nMappedByteBuffer Read: \n");
            for(int i = 0; i < dest.length; i++) {
                System.out.print((char)dest[i]);
            }

            //first 76 rows
            for(int i = 16; i < 2052-27; i++) {
                //this is to do every other row
                if(i % 27 == 0) {
                    i += 43;
                }
                //from i, insert each byte into the data
                for(int j = 0; j < numberBytes.length; j++) {
                    rw.put(i, numberBytes[j]);
                    i++;
                }
            }

            int newPos;
            //for the last 76 rows
            rw.position(2052+16);
            try {
                while(rw.hasRemaining()) {                
                    //at position replace the data 
                    rw.put(numberBytes, 0, numberBytes.length);
                    //skip line
                    newPos = rw.position()+44;             
                    rw.position(newPos);                          
                }
            }
            catch(Exception e) {
                //end loop
                //throws an error when there is no more data to be read
            }
            
            //write to persistent memory
            rw.force();

            //reset the position to read whole file
            rw.position(0);
            //grab all data
            rw.get(contents, 0 ,rw.remaining());
            //print all data
            for(int i = 0; i < contents.length; i++) {
                System.out.print((char)contents[i]);
            }

            //close the resource connection
            file.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    
    }
}