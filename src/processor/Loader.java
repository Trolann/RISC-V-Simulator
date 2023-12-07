package processor;
import java.io.*;

public class Loader {
  private Memory memory;

  public Loader(Memory memory) {
    this.memory = memory;
  }

  public void load(String filename, String datafilename) throws IOException {

    FileInputStream fis = new FileInputStream(filename);
    DataInputStream dis = new DataInputStream(fis);
    
    if(datafilename.length()!=0) {
    	FileInputStream fis1 = new FileInputStream(datafilename);
        DataInputStream dataDis = new DataInputStream(fis1);
    }
    

    String address = Utility.ALLZEROS;    
    
    // Read each line separately and store it into memory
    while (dis.available() > 0) {
        //System.out.println(address);
        String line = dis.readLine(); // Read one line from the file

        // Set the line content into memory at the specified address
        memory.setMemoryValue(address, line);

        // Increment the address for the next memory location
        address = Utility.StringCrement(address, 1);
    }
       
    // Check if dataAddress is provided
    if (datafilename.length() != 0) {
        FileInputStream dataFis = new FileInputStream(datafilename);
        DataInputStream dataDis = new DataInputStream(dataFis);

        String dataMemAddress = "1000"; // Start address for data memory

        // Read data from dataAddress file and store it in memory at address 1000 onwards
        while (dataDis.available() > 0) {
            String dataLine = dataDis.readLine();
            memory.setMemoryValue(dataMemAddress, dataLine);
            dataMemAddress = Utility.StringCrement(dataMemAddress, 1); // Increment data memory address
        }

        dataDis.close();
        dataFis.close();
    }
        

    dis.close();
    fis.close();
  }
}