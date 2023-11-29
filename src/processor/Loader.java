package processor;

import java.io.*;

public class Loader {
  private Memory memory;

  public Loader(Memory memory) {
    this.memory = memory;
  }

  public void loadInstructions(String filename) throws IOException {
	  
	  
	  //TODO: Given any file
	  //TODO: Loader class has to be able to load data memory
	  //TODO Plan: make two instructions load instructions and load data where one 

    FileInputStream fis = new FileInputStream(filename);
    DataInputStream dis = new DataInputStream(fis);
    

    String address = Utility.ALLZEROS;    
    
    // Read each line separately and store it into memory
    while (dis.available() > 0) {
        String line = dis.readLine(); // Read one line from the file

        // Set the line content into memory at the specified address
        memory.setMemoryValue(address, line);

        // Increment the address for the next memory location
        address = Utility.StringCrement(address, 1);
    }

    dis.close();
    fis.close();
  }
}
