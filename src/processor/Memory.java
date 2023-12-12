package processor;

import java.util.HashMap;

public class Memory {
	private HashMap<String, String> memoryMap;
	private byte[] memoryArray;
	private int size = 268500992 * 2;

	public Memory() {
		memoryMap = new HashMap<>();
	}

	public void setMemoryValue(String address, String value) {
		// TODO: error handling for memory? ie. check if value is valid?
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
        for (int i = 0; i < 2; i++) {
            wordValue = getMemoryValue(memoryValue) + wordValue;
            memoryValue = Utility.StringCrement(memoryValue, 1);
        }
        return wordValue;
    }
	
    public String loadWord(int memoryAddress) {
        // Assuming each word is 32 bits
        StringBuilder loadedWord = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            // Get the value of each byte in the word
            String byteValue = getMemoryValue(Integer.toString(memoryAddress + i));

            // Ensure that the byte value is 8 bits long
            byteValue = Utility.leftPad(byteValue);

            // Append the byte value to the loadedWord
            loadedWord.append(byteValue);
        }

        return loadedWord.toString();
    }
    
    public String loadHalfword2(int address) {
    	String halfwordValue = getMemoryValue(Utility.leftPadSigned(address));
        System.out.println("MEMORY DEBUG: halfwordValue: " + halfwordValue);
        return halfwordValue;
    }
    
    public String loadHalfword(int address) {
    	// Assuming that the address is an integer for simplicity
        // Load the two bytes starting from the given address
        String byte1 = getMemoryValue(Integer.toString(address));
        String byte2 = getMemoryValue(Integer.toString(address + 1));

        // Combine the two bytes to form the halfword
        String halfwordValue = byte1 + byte2;
        return halfwordValue;
    }
    
    public String loadByte(int address) {
        // Assuming that the address is an integer for simplicity
        String byteValue = getMemoryValue(Utility.leftPadSigned(address));
        System.out.println("MEMORY DEBUG: byteValue: " + byteValue);
        return byteValue;
    }

    public void storeByte(int memoryAddress, String byteValue) {
        // Store the byte at the specified memory address
        String address = Utility.leftPadSigned(memoryAddress);
        setMemoryValue(address, byteValue);
    }
    
    public void storeHalfword(int address, int halfwordValue) {
        // Convert the halfword value to binary string representation
        String halfwordBinary = Integer.toBinaryString(halfwordValue);

        // Pad the binary string to ensure it is 16 bits long
        halfwordBinary = Utility.leftPad(halfwordBinary);

        // Split the 16-bit binary string into two 8-bit strings
        String lowByte = halfwordBinary.substring(0, 8);
        String highByte = halfwordBinary.substring(8);

        // Store the low byte at the specified address
        setMemoryValue(String.valueOf(address), lowByte);

        // Increment the address and store the high byte
        setMemoryValue(Utility.StringCrement(String.valueOf(address), 1), highByte);
    }

    public void storeWord(int memoryAddress, String value) {
        // Split the 32-bit value into 4 bytes
        String[] bytes = new String[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = value.substring(i * 8, (i + 1) * 8);
        }

        // Store each byte at the corresponding memory address
        for (int i = 0; i < 4; i++) {
            String address = Utility.leftPad(Integer.toBinaryString(memoryAddress + i));
            setMemoryValue(address, bytes[i]);
        }
    }


}
