package processor;

import java.util.HashMap;

public class Memory{

  private HashMap<String, String> memoryMap;
  
  public Memory() {
    memoryMap = new HashMap<>(); 
  }

  public void setMemoryValue(String address, String value) {
	  //TODO: error handling for memory? ie. check if value is valid?
    memoryMap.put(address, value); 
  }

  public String getMemoryValue(String address) {
    if(memoryMap.containsKey(address)) {
      return memoryMap.get(address);
    } else {
      return Utility.ALLZEROS; // default to 0
    }
  }

}
