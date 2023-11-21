package processor;

import java.io.*;

public class Loader {
  private Memory memory;

  public Loader(Memory memory) {
    this.memory = memory;
  }

  public void loadInstructions(String filename) throws IOException {

    FileInputStream fis = new FileInputStream(filename);
    DataInputStream dis = new DataInputStream(fis);

    String address = Utility.ALLZEROS;

    while(dis.available() > 0) {
     
      String instruction = "";

      for(int i=0; i<4; i++) {
        String line = dis.readLine();
        instruction = line + instruction; 
      }

      memory.setMemoryValue(address, instruction);
      address = Utility.StringCrement(address, 1);
    }

    dis.close();
    fis.close();
  }
}
