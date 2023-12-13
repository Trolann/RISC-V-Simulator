package processor;

import java.util.HashMap;

public class Memory {
	private HashMap<String, String> memoryMap;
    public int dataLines;

	public Memory() {
		memoryMap = new HashMap<>();
        this.dataLines = 0;
	}

	public void setMemoryValue(String address, String value) {
		if(Long.parseLong(address, 2) >= Long.parseLong(Utility.DATA_MEMORY_ADDRESS, 2)) {
            System.out.println("  MEMORY DEBUG: Putting data " + value + " at address " + address);
            this.dataLines++;
        }
		memoryMap.put(address, value);
	}

	public String getMemoryValue(String address) {
		if (memoryMap.containsKey(address)) {
			return memoryMap.get(address);
		} else {
			//return Utility.ALLZEROS; // default to 0
			// Return 8 bit 0's for unset memory
			return Utility.ALLZEROS.substring(0, 8); // default to 0
		}
	}

	public String getInstruction(String startingAddress) {
		String instruction = "";
		for (int i = 0; i < 4; i++) {
			instruction = getMemoryValue(startingAddress) + instruction;
			startingAddress = Utility.StringCrement(startingAddress, 1);
		}
		return instruction;
	}

    public String loadWord2(int memoryAddress) {
        String memoryValue = Utility.leftPadSigned(memoryAddress);
        String wordValue = "";
        for (int i = 0; i < 4; i++) {
            wordValue = getMemoryValue(memoryValue) + wordValue;
            memoryValue = Utility.StringCrement(memoryValue, 1);
        }
        return wordValue;
    }
    
    public String loadHalfword2(int address) {
    	String halfwordValue = getMemoryValue(Utility.leftPadSigned(address));
        System.out.println("MEMORY DEBUG: halfwordValue: " + halfwordValue);
        return halfwordValue;
    }
    
    public String loadByte(int address) {
        // Assuming that the address is an integer for simplicity
        String byteValue = getMemoryValue(Utility.leftPadSigned(address));
        return byteValue;
    }

    public void storeByte(int memoryAddress, String byteValue) {
        // Store the byte at the specified memory address
        String address = Utility.leftPadSigned(memoryAddress);
        setMemoryValue(address, byteValue);
    }
    
    public void storeHalfword(int memoryAddress, String halfWordvalue) {
        // Split the 16-bit value into 2 bytes
        String[] bytes = new String[2];
        for (int i = 0; i < 2; i++) {
            bytes[i] = halfWordvalue.substring(i * 8, (i + 1) * 8);
        }
        // Store each byte at the corresponding memory address
        for (int i = 0; i < 2; i++) {
            String address = Utility.leftPadSigned(memoryAddress + i);
            setMemoryValue(address, bytes[i]);
        }
    }

    public void storeWord(int memoryAddress, String value) {
        // Split the 32-bit value into 4 bytes
        String[] bytes = new String[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = value.substring((3 - i) * 8, (4 - i) * 8);
        }
        // Store each byte at the consecutive memory addresses
        for (int i = 0; i < 4; i++) {
            String address = Utility.leftPadSigned(memoryAddress + i);
            setMemoryValue(address, bytes[i]);
        }
    }
}
