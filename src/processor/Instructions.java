package processor;

import java.util.HashMap;
import java.util.HashSet;

public class Instructions {
    private Memory memory;
    private Registers registers;

    public Instructions(Memory memory, Registers registers) {
        this.memory = memory;
        this.registers = registers;
    }

    	public HashMap<String, Object> executeInstruction(String opcode, HashMap<String, String> instructionComponents) {
		HashMap<String, Object> result = new HashMap<>();

		switch (opcode) {
		case "LUI":
			result.put("asmInstruction", LUI(instructionComponents));
			break;
		case "AUIPC":
			result.put("asmInstruction", AUIPC(instructionComponents));
			break;
		case "JAL":
			result.put("asmInstruction", JAL(instructionComponents));
			result.put("newPC", calculateNewPC(instructionComponents));
			break;
		case "JALR":
			result.put("asmInstruction", JALR(instructionComponents));
			result.put("newPC", calculateNewPC(instructionComponents));
			break;
		case "BEQ":
			result.put("asmInstruction", BEQ(instructionComponents));
			result.put("newPC", calculateBranchPC(instructionComponents));
			break;
		// Add cases for other instructions

		default:
			throw new UnsupportedOperationException("Unsupported opcode: " + opcode);
		}

		return result;
	}

	/**private int calculateBranchPC(HashMap<String, String> instructionComponents) {
		// Calculate the new program counter for branch instructions
		int currentPC = registers.getProgramCounter();
		// Perform the calculation based on the branch instruction and update the PC
		// Replace with the actual calculation based on branch conditions
		int newPC = currentPC + 1; // Placeholder, replace with actual branch calculation
		registers.setProgramCounter(newPC);
		return newPC;
	}*/

	private int calculateNewPC(HashMap<String, String> instructionComponents) {
		// Calculate the new program counter based on the instruction
		int currentPC = registers.getProgramCounter();
		// Perform the calculation based on the instruction and update the PC
		int newPC = currentPC + 1; // Assuming a simple increment for the next instruction
		registers.setProgramCounter(newPC);
		return newPC;
	}

	private String LUI(HashMap<String, String> instructionComponents) {
	    // Extract components from the HashMap
	    String rd = instructionComponents.get("rd");
	    String imm = instructionComponents.get("imm");

	    // Convert immediate value from binary string to integer
	    int immediate = Integer.parseInt(imm, 2);

	    // Left shift immediate by 12 bits to get the result
	    int result = immediate << 12;

	    // Convert result to binary string representation
	    String resultBinary = Integer.toBinaryString(result);

	    // Ensure resultBinary is 32-bit length
	    while (resultBinary.length() < 32) {
	        resultBinary = "0" + resultBinary;
	    }

	    // Update rd register value
	    registers.setRegisterValue(rd, resultBinary);

	    return "LUI assembly instruction";
	}

	private String AUIPC(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd");
		String imm = instructionComponents.get("imm");

		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(imm, 2);

		// Add immediate to the current PC value
		int result = registers.getProgramCounter() + immediate;

		// Convert result back to binary string representation
		String resultBinary = Integer.toBinaryString(result);

		// Ensure resultBinary is 32-bit length
		while (resultBinary.length() < 32) {
			resultBinary = "0" + resultBinary;
		}

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);

		return "AUIPC assembly instruction";
	}

	private String JAL(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd");
		String imm = instructionComponents.get("imm");

		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(imm, 2);

		// Calculate the new program counter
		int newPC = registers.getProgramCounter() + immediate;

		// Update rd register value with the old PC + 4
		registers.setRegisterValue(rd, Integer.toBinaryString(registers.getProgramCounter() + 4));

		return "JAL assembly instruction";
	}

	private String JALR(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd");
		String rs1 = instructionComponents.get("rs1");
		String imm = instructionComponents.get("imm");

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		int valueIntRs1 = Integer.parseInt(valueRs1, 2);

		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(imm, 2);

		// Calculate the new program counter
		int newPC = (valueIntRs1 + immediate) & ~1;

		// Update rd register value with the old PC + 4
		registers.setRegisterValue(rd, Integer.toBinaryString(registers.getProgramCounter() + 4));

		return "JALR assembly instruction";
	}

	private String BEQ(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(imm, 2);

		// Check if the values in rs1 and rs2 are equal
		if (valueRs1.equals(valueRs2)) {
			// Calculate the new program counter
			int newPC = registers.getProgramCounter() + immediate;

			return "BEQ assembly instruction";
		} else {
			return "Branch not taken";
		}
	}
    
    
    public void ADDI(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rd = instructionComponents.get("rd"); //destination register
        String rs1 = instructionComponents.get("rs1"); //source register 1
        String imm = instructionComponents.get("imm"); //immediate register

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
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
    }
    
    private String BNE(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String rs2 = instructionComponents.get("rs2"); // Source register 2
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        String valueRs2 = registers.getRegisterValue(rs2);

        // Convert immediate value from binary string to signed integer
        int immediate = Integer.parseInt(imm, 2);

        // Check if the values in rs1 and rs2 are not equal
        if (!valueRs1.equals(valueRs2)) {
            // Calculate the new program counter
            int newPC = registers.getProgramCounter() + immediate;

            // Update the program counter register with the new address
            registers.setProgramCounter(newPC);

            return "BNE taken: Jumping to address " + newPC;
        } else {
            return "BNE not taken: rs1 and rs2 are equal";
        }
    }
    
    private String BLT(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String rs2 = instructionComponents.get("rs2"); // Source register 2
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        String valueRs2 = registers.getRegisterValue(rs2);

        // Convert immediate value from binary string to signed integer
        int immediate = Integer.parseInt(imm, 2);

        // Convert register values from binary strings to signed integers
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);
        int valueIntRs2 = Integer.parseInt(valueRs2, 2);

        // Check if the value in rs1 is less than the value in rs2
        if (valueIntRs1 < valueIntRs2) {
            // Calculate the new program counter
            int newPC = registers.getProgramCounter() + immediate;

            // Update the program counter register with the new address
            registers.setProgramCounter(newPC);

            return "BLT taken: Jumping to address " + newPC;
        } else {
            return "BLT not taken: rs1 is not less than rs2";
        }
    }
    
    private String BGE(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String rs2 = instructionComponents.get("rs2"); // Source register 2
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        String valueRs2 = registers.getRegisterValue(rs2);

        // Convert immediate value from binary string to signed integer
        int immediate = Integer.parseInt(imm, 2);

        // Convert register values from binary strings to signed integers
        int valueIntRs1 = Integer.parseInt(valueRs1, 2);
        int valueIntRs2 = Integer.parseInt(valueRs2, 2);

        // Check if the value in rs1 is greater than or equal to the value in rs2
        if (valueIntRs1 >= valueIntRs2) {
            // Calculate the new program counter
            int newPC = registers.getProgramCounter() + immediate;

            // Update the program counter register with the new address
            registers.setProgramCounter(newPC);

            return "BGE taken: Jumping to address " + newPC;
        } else {
            return "BGE not taken: rs1 is less than rs2";
        }
    }
    
    private String BLTU(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String rs2 = instructionComponents.get("rs2"); // Source register 2
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        String valueRs2 = registers.getRegisterValue(rs2);

        // Convert immediate value from binary string to signed integer
        int immediate = Integer.parseInt(imm, 2);

        // Convert register values from binary strings to unsigned integers
        long valueUnsignedRs1 = Long.parseLong(valueRs1, 2) & 0xFFFFFFFFL;
        long valueUnsignedRs2 = Long.parseLong(valueRs2, 2) & 0xFFFFFFFFL;

        // Check if the value in rs1 is less than the value in rs2 (unsigned comparison)
        if (valueUnsignedRs1 < valueUnsignedRs2) {
            // Calculate the new program counter
            int newPC = registers.getProgramCounter() + immediate;

            // Update the program counter register with the new address
            registers.setProgramCounter(newPC);

            return "BLTU taken: Jumping to address " + newPC;
        } else {
            return "BLTU not taken: rs1 is not less than rs2";
        }
    }
    
    private String BGEU(HashMap<String, String> instructionComponents) {
        // Extract components from the HashMap
        String rs1 = instructionComponents.get("rs1"); // Source register 1
        String rs2 = instructionComponents.get("rs2"); // Source register 2
        String imm = instructionComponents.get("imm"); // Immediate value

        // Get values from registers
        String valueRs1 = registers.getRegisterValue(rs1);
        String valueRs2 = registers.getRegisterValue(rs2);

        // Convert immediate value from binary string to signed integer
        int immediate = Integer.parseInt(imm, 2);

        // Convert register values from binary strings to unsigned integers
        long valueUnsignedRs1 = Long.parseLong(valueRs1, 2) & 0xFFFFFFFFL;
        long valueUnsignedRs2 = Long.parseLong(valueRs2, 2) & 0xFFFFFFFFL;

        // Check if the value in rs1 is greater than or equal to the value in rs2 (unsigned comparison)
        if (valueUnsignedRs1 >= valueUnsignedRs2) {
            // Calculate the new program counter
            int newPC = registers.getProgramCounter() + immediate;

            // Update the program counter register with the new address
            registers.setProgramCounter(newPC);

            return "BGEU taken: Jumping to address " + newPC;
        } else {
            return "BGEU not taken: rs1 is less than rs2";
        }
    }
    
    private String LB(HashMap<String, String> instructionComponents) {
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

    private String LH(HashMap<String, String> instructionComponents) {
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
    
    private String LW(HashMap<String, String> instructionComponents) {
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

    private String LBU(HashMap<String, String> instructionComponents) {
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
    
    
    private String LHU(HashMap<String, String> instructionComponents) {
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

    private String SB(HashMap<String, String> instructionComponents) {
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


    private String SLTI(HashMap<String, String> instructionComponents) {
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
    
    private String SLTIU(HashMap<String, String> instructionComponents) {
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

    private String XORI(HashMap<String, String> instructionComponents) {
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


    private String ORI(HashMap<String, String> instructionComponents) {
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


    private String ADDSUB(HashMap<String, String> instructionComponents) {
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
    
    
}

