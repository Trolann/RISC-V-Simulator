package processor;

import java.util.HashMap;

public class Memory{

  private HashMap<String, String> memoryMap;
  
  public Memory() {
    memoryMap = new HashMap<>(); 
  }

  public void setMemoryValue(String address, String value) {
	  //check to make sure value is not too large
    memoryMap.put(address, value); 
  }

  public String getMemoryValue(String address) {
    if(memoryMap.containsKey(address)) {
      return memoryMap.get(address);
    } else {
      return "00000000000000000000000000000000"; // default to 0
    }
  }

}
