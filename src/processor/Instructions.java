package processor;

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

	private int calculateBranchPC(HashMap<String, String> instructionComponents) {
		// Calculate the new program counter for branch instructions
		int currentPC = registers.getProgramCounter();
		// Perform the calculation based on the branch instruction and update the PC
		// Replace with the actual calculation based on branch conditions
		int newPC = currentPC + 4; // Placeholder, replace with actual branch calculation
		registers.setProgramCounter(newPC);
		return newPC;
	}

	private int calculateNewPC(HashMap<String, String> instructionComponents) {
		// Calculate the new program counter based on the instruction
		int currentPC = registers.getProgramCounter();
		// Perform the calculation based on the instruction and update the PC
		int newPC = currentPC + 4; // Assuming a simple increment for the next instruction
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

		// Convert result back to binary string representation
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
    
    
    public void addi(HashMap<String, String> instructionComponents) {
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
}

