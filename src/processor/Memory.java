package processor;

import java.util.HashMap;

public class Memory {
	private HashMap<String, String> memoryMap;
	private byte[] memoryArray;
	private int size = 268500992 * 2;

	public Memory() {
		memoryMap = new HashMap<>();
		memoryArray = new byte[size];
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

	public String loadByte(int address) {
		byte loadedByte = memoryArray[address];
		return Byte.toString(loadedByte);
	}

	public String loadHalfword(int address) {
		short loadedHalfword = (short) ((memoryArray[address] & 0xFF) | ((memoryArray[address + 1] & 0xFF) << 8));
		return Short.toString(loadedHalfword);
	}

	public String loadWord(int address) {
		int loadedWord = (memoryArray[address] & 0xFF) | ((memoryArray[address + 1] & 0xFF) << 8)
				| ((memoryArray[address + 2] & 0xFF) << 16) | ((memoryArray[address + 3] & 0xFF) << 24);
		return Integer.toString(loadedWord);
	}

	public void storeByte(int address, String substring) {
		// Convert the substring to a byte value
		byte byteValue = (byte) Integer.parseInt(substring, 2);

		// Store the byte in memory at the specified address
		memoryArray[address] = byteValue;
	}

	public void storeWord(int address, String string) {
	    // Convert the string to an integer
	    int intValue = Integer.parseInt(string, 2);

	    // Store the word at the specified address
	    memoryArray[address] = (byte) (intValue & 0xFF);
	    memoryArray[address + 1] = (byte) ((intValue >> 8) & 0xFF);
	    memoryArray[address + 2] = (byte) ((intValue >> 16) & 0xFF);
	    memoryArray[address + 3] = (byte) ((intValue >> 24) & 0xFF);
	}

	public void storeHalfword(int address, int valueInt) {
		// Store the halfword at the specified address
		memoryArray[address] = (byte) (valueInt & 0xFF);
		memoryArray[address + 1] = (byte) ((valueInt >> 8) & 0xFF);
	}

}
