package processor;

import java.util.HashMap;

@FunctionalInterface
interface InstructionFunction {
	void execute(HashMap<String, String> asmInstruction);
}

public class Instructions {
    private Memory memory;
    public Registers registers;

    public Instructions(Memory memory, Registers registers) {
        this.memory = memory;
        this.registers = registers;
    }

	public void LUI(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String imm = instructionComponents.get("imm"); // immediate value

		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(imm, 2);

		// Left-shift immediate value by 12 bits to load upper 20 bits
		int result = immediate << 12;

		// Convert result back to binary string representation
		String resultBinary = Integer.toBinaryString(result);

		// Ensure resultBinary is 32-bit length
		while (resultBinary.length() < 32) {
			resultBinary = "0" + resultBinary;
		}

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.setProgramCounter(Utility.StringCrement(registers.getProgramCounter(), 1));
	}

	public void AUIPC(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String imm = instructionComponents.get("imm"); // immediate value

		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(imm, 2);

		// Add immediate value to the current value of the program counter
		int result = immediate + Integer.parseInt(registers.getProgramCounter(), 2);

		// Convert result back to binary string representation
		String resultBinary = Integer.toBinaryString(result);

		// Ensure resultBinary is 32-bit length
		while (resultBinary.length() < 32) {
			resultBinary = "0" + resultBinary;
		}

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.setProgramCounter(Utility.StringCrement(registers.getProgramCounter(), 1));
	}

	public void JAL(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String imm = instructionComponents.get("imm"); // immediate value

		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(imm, 2);

		// Calculate the target address by adding immediate to the current program
		// counter
		int targetAddress = immediate + Integer.parseInt(registers.getProgramCounter(), 2);

		// Save the return address (address of the next instruction) in rd
		registers.setRegisterValue(rd, registers.getProgramCounter());

		// Convert target address back to binary string representation
		String targetAddressBinary = Integer.toBinaryString(targetAddress);

		// Ensure targetAddressBinary is 32-bit length
		while (targetAddressBinary.length() < 32) {
			targetAddressBinary = "0" + targetAddressBinary;
		}
		// Update program counter to the target address
		registers.setProgramCounter(targetAddressBinary);
	}

	public void JALR(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate value

		// Get value from source register
		String valueRs1 = registers.getRegisterValue(rs1);

		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(imm, 2);

		// Calculate the target address by adding immediate to the value in rs1
		int targetAddress = immediate + Integer.parseInt(valueRs1, 2);

		// Save the return address (address of the next instruction) in rd
		registers.setRegisterValue(rd, registers.getProgramCounter());

		// Convert target address back to binary string representation
		String targetAddressBinary = Integer.toBinaryString(targetAddress);

		// Ensure targetAddressBinary is 32-bit length
		while (targetAddressBinary.length() < 32) {
			targetAddressBinary = "0" + targetAddressBinary;
		}
		// Update program counter to the target address
		registers.setProgramCounter(targetAddressBinary);
	}

	public void BEQ(HashMap<String, String> instructionComponents) {
	    // Extract components from the HashMap
	    String rs1 = instructionComponents.get("rs1"); // source register 1
	    String rs2 = instructionComponents.get("rs2"); // source register 2
	    String imm = instructionComponents.get("imm"); // immediate value
	    // Get values from source registers
	    String valueRs1 = registers.getRegisterValue(rs1);
	    String valueRs2 = registers.getRegisterValue(rs2);

	    // Convert immediate value from binary string to integer
	    int immediate = Integer.parseInt(imm, 2);

	    // Check if the values in rs1 and rs2 are equal
	    if (valueRs1.equals(valueRs2)) {
	    	// Calculate the target address by adding immediate to the current program counter
	    	int currentProgramCounter = Integer.parseInt(registers.getProgramCounter(), 2);
	    	int targetAddress = currentProgramCounter + immediate;
	        // Convert target address back to binary string representation
	        String targetAddressBinary = Integer.toBinaryString(targetAddress);
	        // Ensure targetAddressBinary is 32-bit length
	        while (targetAddressBinary.length() < 32) {
	            targetAddressBinary = "0" + targetAddressBinary;
	        }
	        // Update program counter to the target address
	        registers.setProgramCounter(targetAddressBinary);
	    } else {
	        // If the values are not equal, proceed to the next instruction
	        registers.incrementProgramCounter();
	    }
	}
	
	public void BNE(HashMap<String, String> instructionComponents) {
	    String rs1 = instructionComponents.get("rs1");
	    String rs2 = instructionComponents.get("rs2");
	    String imm = instructionComponents.get("imm");
	
	    // Ensure that rs1 and rs2 are valid register names
	    if (!registers.isValidRegister(rs1) || !registers.isValidRegister(rs2)) {
	        throw new IllegalArgumentException("Invalid register name");
	    }
	
	    // Get the values of rs1 and rs2 from registers
	    String valueRs1 = registers.getRegisterValue(rs1);
	    String valueRs2 = registers.getRegisterValue(rs2);
	
	    // Parse the immediate value from binary to integer
	    int immediate = Integer.parseInt(imm, 2);
	
	    // Compare the values of rs1 and rs2
	    if (!valueRs1.equals(valueRs2)) {
	        // Branch if not equal
	        int currentProgramCounter = Integer.parseInt(registers.getProgramCounter(), 2);
	        int targetAddress = currentProgramCounter + immediate;
	
	        // Ensure that the target address is within the valid address range
	        if (targetAddress < 0 || targetAddress >= (1 << 32)) {
	            throw new IllegalArgumentException("Invalid target address");
	        }
	
	        // Set the program counter to the target address
	        registers.setProgramCounter(String.format("%32s", Integer.toBinaryString(targetAddress)).replace(' ', '0'));
	    } else {
	        // Increment the program counter if values are equal
	        registers.incrementProgramCounter();
	    }
	}
	
	public void BGE(HashMap<String, String> instructionComponents) {
	    String rs1 = instructionComponents.get("rs1");
	    String rs2 = instructionComponents.get("rs2");
	    String imm = instructionComponents.get("imm");

	    String valueRs1 = registers.getRegisterValue(rs1);
	    String valueRs2 = registers.getRegisterValue(rs2);

	    int immediate = Integer.parseInt(imm, 2);

	    if (!valueRs1.equals(valueRs2)) {
	        int currentProgramCounter = Integer.parseInt(registers.getProgramCounter(), 2);
	        int targetAddress = currentProgramCounter + immediate;

	        String targetAddressBinary = Integer.toBinaryString(targetAddress);

	        while (targetAddressBinary.length() < 32) {
	            targetAddressBinary = "0" + targetAddressBinary;
	        }

	        registers.setProgramCounter(targetAddressBinary);
	    } else {
	        registers.incrementProgramCounter();
	    }
	}
	
	public void BLTU(HashMap<String, String> instructionComponents) {
	    // Extracting instruction components
	    String rs1 = instructionComponents.get("rs1");
	    String rs2 = instructionComponents.get("rs2");
	    String imm = instructionComponents.get("imm");

	    // Getting register values
	    String valueRs1 = registers.getRegisterValue(rs1);
	    String valueRs2 = registers.getRegisterValue(rs2);

	    // Parsing immediate value
	    int immediate = Integer.parseInt(imm, 2);

	    // Comparing rs1 and rs2
	    if (Integer.parseInt(valueRs1, 2) < Integer.parseInt(valueRs2, 2)) {
	        // Branch taken
	        int currentProgramCounter = Integer.parseInt(registers.getProgramCounter(), 2);
	        int targetAddress = currentProgramCounter + immediate;

	        // Ensure the target address is a 32-bit binary string
	        String targetAddressBinary = String.format("%32s", Integer.toBinaryString(targetAddress)).replace(' ', '0');

	        // Set the program counter to the target address
	        registers.setProgramCounter(targetAddressBinary);
	    } else {
	        // Branch not taken, increment program counter
	        registers.incrementProgramCounter();
	    }
	}

	
	public void BGEU(HashMap<String, String> instructionComponents) {
	    String rs1 = instructionComponents.get("rs1");
	    String rs2 = instructionComponents.get("rs2");
	    String imm = instructionComponents.get("imm");

	    String valueRs1 = registers.getRegisterValue(rs1);
	    String valueRs2 = registers.getRegisterValue(rs2);

	    int immediate = Integer.parseInt(imm, 2);

	    if (!valueRs1.equals(valueRs2)) {
	        int currentProgramCounter = Integer.parseInt(registers.getProgramCounter(), 2);
	        long targetAddress = (long) currentProgramCounter + immediate;

	        if (targetAddress > Integer.MAX_VALUE) {
	            throw new IllegalStateException("Target address overflow");
	        }

	        String targetAddressBinary = Integer.toBinaryString((int) targetAddress);
	        targetAddressBinary = String.format("%32s", targetAddressBinary).replace(' ', '0');

	        registers.setProgramCounter(targetAddressBinary);
	    } else {
	        registers.incrementProgramCounter();
	    }
	}

    public void ADDI(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); //destination register
        String rs1 = instructionComponents.get("rs1"); //source register 1
        String imm = instructionComponents.get("imm"); //immediate register
        // Print the whole hashmap
        System.out.println(instructionComponents);
        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        System.out.println("rs1: " + valueRs1);
        System.out.println("valueRs1: " + valueRs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);

        // Convert immediate value from binary string to integer
        int immediate = Integer.parseInt(imm, 2);

        // Perform addition operation
        int result = valueIntRs1 + immediate;

        // Convert result back to binary string representation
        String resultBinary = Integer.toBinaryString(result);

        // Ensure resultBinary is 32-bit length
        while (resultBinary.length() < 32) {
            resultBinary = "0" + resultBinary;
        }

        // Update rd register value
        registers.setRegisterValue(rd, resultBinary);
        registers.setProgramCounter(Utility.StringCrement(registers.getProgramCounter(), 1));
    }
/*
    public String LB(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);

        // Calculate the memory address to load the byte from
        int address = valueIntRs1 + Integer.parseInt(imm, 2);

        // Fetch the byte from memory based on the address
        String memoryValue = memory.getMemoryValue(Integer.toBinaryString(address));

        // Sign-extend the byte to 32 bits (assuming two's complement)
        String signExtendedValue = memoryValue.substring(7, 8).repeat(24) + memoryValue;

        // Update rd register value with the sign-extended byte
        registers.setRegisterValue(rd, signExtendedValue);

        return "LB assembly instruction executed";
    }

 */
/*
    public String LH(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);

        // Calculate the memory address to load the halfword from
        int address = valueIntRs1 + Integer.parseInt(imm, 2);

        // Fetch the halfword from memory based on the address
        String memoryValue = memory.getMemoryValue(Integer.toBinaryString(address));

        // Sign-extend the halfword to 32 bits (assuming two's complement)
        String signExtendedValue = memoryValue.substring(15, 16).repeat(16) + memoryValue;

        // Update rd register value with the sign-extended halfword
        registers.setRegisterValue(rd, signExtendedValue);

        return "LH assembly instruction executed";
    }

 */
/*
    public String LW(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);

        // Calculate the memory address to load the word from
        int address = valueIntRs1 + Integer.parseInt(imm, 2);

        // Fetch the word (32-bit) from memory based on the address
        String memoryValue = memory.getMemoryValue(Integer.toBinaryString(address));

        // Update rd register value with the loaded word
        registers.setRegisterValue(rd, memoryValue);

        return "LW assembly instruction executed";
    }

 */
/*
    public String LBU(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);

        // Calculate the memory address to load the unsigned byte from
        int address = valueIntRs1 + Integer.parseInt(imm, 2);

        // Fetch the byte (8-bit) from memory based on the address
        String memoryValue = memory.getMemoryValue(Integer.toBinaryString(address));

        // Update rd register value with the loaded byte
        registers.setRegisterValue(rd, memoryValue.substring(24)); // Consider only the 8 LSBs

        return "LBU assembly instruction executed";
    }

 */
/*
    public String LHU(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);

        // Calculate the memory address to load the unsigned halfword from
        int address = valueIntRs1 + Integer.parseInt(imm, 2);

        // Fetch the halfword (16-bit) from memory based on the address
        String memoryValue = memory.getMemoryValue(Integer.toBinaryString(address));

        // Update rd register value with the loaded unsigned halfword
        registers.setRegisterValue(rd, memoryValue);

        return "LHU assembly instruction executed";
    }

 */
/*
    public String SB(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String rs2 = instructionComponents.get("rs2"); // Source register 2
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        String valueRs2 = registers.getRegisterValue(rs2);
        int valueIntRs2 = Integer.parseInt(valueRs2, 2);

        // Calculate the memory address to store the byte to
        int address = Integer.parseInt(valueRs1, 2) + Integer.parseInt(imm, 2);

        // Convert the least significant byte of rs2 to a binary string
        String byteToStore = Integer.toBinaryString(valueIntRs2 & 0xFF);

        // Store the byte to memory at the calculated address
        memory.setMemoryValue(Integer.toBinaryString(address), byteToStore);

        return "SB assembly instruction executed";
    }

 */
/*
    public String SLTI(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);
        int immediate = Integer.parseInt(imm, 2);

        // Perform SLTI operation: set rd to 1 if rs1 is less than immediate; otherwise set to 0
        String result = (valueIntRs1 < immediate) ? "00000000000000000000000000000001" : "00000000000000000000000000000000";

        // Update rd register value
        registers.setRegisterValue(rd, result);

        return "SLTI assembly instruction executed";
    }

 */
/*
    public String SLTIU(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseUnsignedInt(valueRs1, 2);
        int immediate = Integer.parseUnsignedInt(imm, 2);

        // Perform SLTIU operation: set rd to 1 if rs1 is less than unsigned immediate; otherwise set to 0
        String result = (valueIntRs1 < immediate) ? "00000000000000000000000000000001" : "00000000000000000000000000000000";

        // Update rd register value
        registers.setRegisterValue(rd, result);

        return "SLTIU assembly instruction executed";
    }

 */
/*
    public String XORI(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);
        int immediate = Integer.parseInt(imm, 2);

        // Perform XOR operation: rd = rs1 ^ immediate
        int result = valueIntRs1 ^ immediate;

        // Convert result back to binary string representation
        String resultBinary = Integer.toBinaryString(result);

        // Ensure resultBinary is 32-bit length
        while (resultBinary.length() < 32) {
            resultBinary = "0" + resultBinary;
        }

        // Update rd register value
        registers.setRegisterValue(rd, resultBinary);

        return "XORI assembly instruction executed";
    }

 */
/*
    public String ORI(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);
        int immediate = Integer.parseInt(imm, 2);

        // Perform OR operation: rd = rs1 | immediate
        int result = valueIntRs1 | immediate;

        // Convert result back to binary string representation
        String resultBinary = Integer.toBinaryString(result);

        // Ensure resultBinary is 32-bit length
        while (resultBinary.length() < 32) {
            resultBinary = "0" + resultBinary;
        }

        // Update rd register value
        registers.setRegisterValue(rd, resultBinary);

        return "ORI assembly instruction executed";
    }

 */
/*
    public String ADDSUB(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); // Destination register
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String rs2 = instructionComponents.get("rs2"); // Source register 2

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        String valueRs2 = registers.getRegisterValue(rs2);
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);
        int valueIntRs2 = Integer.parseInt(valueRs2, 2);

        // Perform ADDSUB operation: rd = rs1 + rs2
        int result = valueIntRs1 + valueIntRs2;

        // Convert result back to binary string representation
        String resultBinary = Integer.toBinaryString(result);

        // Ensure resultBinary is 32-bit length
        while (resultBinary.length() < 32) {
            resultBinary = "0" + resultBinary;
        }

        // Update rd register value
        registers.setRegisterValue(rd, resultBinary);

        return "ADDSUB assembly instruction executed";
    }

 */
    
    
}

