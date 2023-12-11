package processor;
import java.io.*;

public class Loader {
  private Memory memory;


  public Loader(Memory memory) {
    this.memory = memory; // Use given memory
  }

  public void load(String instructionfilename, String datafilename) throws IOException {

    FileInputStream insFis = new FileInputStream(instructionfilename);
    DataInputStream insDis = new DataInputStream(insFis);

    String address = Utility.ALLZEROS; // Start address for instruction memory
    // Read each line separately and store it into memory
    while (insDis.available() > 0) {
        String line = insDis.readLine(); // Read one line from the file
        // Set the line content into memory at the specified address
        memory.setMemoryValue(address, line);
        // Increment the address for the next memory location
        address = Utility.StringCrement(address, 1);
    }
    insFis.close();
    insDis.close();

    // Check if dataAddress is provided
    if (!datafilename.isEmpty()) {
        FileInputStream dataFis = new FileInputStream(datafilename);
        DataInputStream dataDis = new DataInputStream(dataFis);

        String dataMemAddress = Utility.DATA_MEMORY_ADDRESS; // Start address for data memory

        // Read data from dataAddress file and store it in memory at address 1000 onwards
        while (dataDis.available() > 0) {
            System.out.println("LOADER DEBUG: dataMemAddress: " + dataMemAddress);
            String dataLine = dataDis.readLine();
            System.out.println("LOADER DEBUG: dataLine: " + dataLine);
            memory.setMemoryValue(dataMemAddress, dataLine);
            dataMemAddress = Utility.StringCrement(dataMemAddress, 1); // Increment data memory address
        }
        dataFis.close();
        dataDis.close();
    }
  }
}