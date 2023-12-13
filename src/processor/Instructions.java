package processor;

import java.util.HashMap;

@FunctionalInterface
interface InstructionFunction {
	String execute(HashMap<String, String> asmInstruction);
}

public class Instructions {
	private Memory memory;
	public Registers registers;

	public Instructions(Memory memory, Registers registers) {
		this.memory = memory;
		this.registers = registers;
	}

	public String LUI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String imm = instructionComponents.get("imm"); // immediate value

		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform LUI operation (shift immediate value left by 12 bits)
		int result = immediate << 12;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPadSigned(result);
		System.out.println("LUI DEBUG: Shifting " + immediate + " left by " + 12 + " to get " + result);

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("lui %s, %d", rd, immediate);
	}

	public String AUIPC(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd");
		String imm = instructionComponents.get("imm");

		int immediate = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Convert program counter to integer before adding the offset
		int programCounter = Integer.parseUnsignedInt(registers.getProgramCounter(), 2);
		int result = programCounter + immediate;

		// Set the result to the destination register (rd)
		registers.setRegisterValue(rd, Integer.toUnsignedString(result, 2));

		// Increment the program counter
		registers.incrementProgramCounter();

		return String.format("auipc %s, %d", rd, immediate);
	}

	public String JAL(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd");
		String imm = instructionComponents.get("imm");

		int immediate = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Convert program counter to integer before adding the offset
		int programCounter = Integer.parseUnsignedInt(registers.getProgramCounter(), 2);
		int jumpTargetAddress = programCounter + immediate;
		System.out.println("JAL DEBUG: immediate: " + immediate + " programCounter: " + programCounter
				+ " jumpTargetAddress: " + jumpTargetAddress);
		System.out.println("Integer.toUnsignedString(programCounter + 4, 2): " +
				Integer.toUnsignedString(programCounter + 4, 2));

		// Save the return address (program counter + 4) in the destination register
		registers.setRegisterValue(rd, Integer.toUnsignedString(programCounter + 4, 2));

		// Set the program counter to the jump target address
		registers.setProgramCounter(Integer.toUnsignedString(jumpTargetAddress, 2));

		return String.format("jal %s, %d", rd, immediate);
	}

	public String JALR(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd");
		String rs1 = instructionComponents.get("rs1");
		String imm = instructionComponents.get("imm");

		int immediate = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Read the value from the base register (rs1)
		int baseRegisterValue = Integer.parseUnsignedInt(registers.getRegisterValue(rs1), 2);


		// Calculate the jump target address by adding the immediate to the base
		// register value
		int jumpTargetAddress = baseRegisterValue + immediate;
		String jumpTargetString = Utility.leftPad("0" + Integer.toUnsignedString(jumpTargetAddress, 2));
		//replace last char with 0
		jumpTargetString = jumpTargetString.substring(0, jumpTargetString.length() - 1) + "0";

		// Save the return address (program counter + 4) in the destination register
		// (rd)
		registers.setRegisterValue(rd,
				Integer.toUnsignedString(Integer.parseInt(registers.getProgramCounter(), 2) + 4, 2));

		System.out.println("JALR DEBUG: Jump to " + jumpTargetString);

		// Set the program counter to the jump target address
		registers.setProgramCounter(jumpTargetString);

		return String.format("jalr %s, %s, %d", rd, rs1, immediate);
	}

	public String BEQ(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);
		int offset = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Convert program counter to integer before adding the offset
		int programCounter = Integer.parseUnsignedInt(registers.getProgramCounter(), 2);
		int branchTargetAddress = programCounter + offset;

		// Branch if rs1Value is equal to rs2Value
		boolean isEqual = rs1Value.equals(rs2Value);

		System.out.println("BEQ DEBUG: Branch if " + rs1 + " equal " + rs2 + " MOVE TO " + branchTargetAddress);
		if (isEqual) {
			registers.setProgramCounter(Integer.toUnsignedString(branchTargetAddress, 2));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("beq %s, %s, %d", rs1, rs2, immediate);
	}

	public String BNE(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);
		int offset = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Convert program counter to integer before adding the offset
		int programCounter = Integer.parseUnsignedInt(registers.getProgramCounter(), 2);
		int branchTargetAddress = programCounter + offset;

		// Branch if rs1Value is not equal to rs2Value
		boolean isNotEqual = !rs1Value.equals(rs2Value);

		System.out.println("BNE DEBUG: Branch if " + rs1 + " not equal " + rs2 + " MOVE TO " + branchTargetAddress);
		if (isNotEqual) {
			registers.setProgramCounter(Integer.toUnsignedString(branchTargetAddress, 2));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("bne %s, %s, %d", rs1, rs2, immediate);
	}

	public String BLT(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int offset = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		// Branch if rs1Value is less than rs2Value
		boolean isLessThan = rs1Value.compareTo(rs2Value) < 0;

		System.out.println("BLT DEBUG: Branch if " + rs1 + " less than " + rs2 + " MOVE TO " + offset);
		if (isLessThan) {
			int programCounter = Integer.parseUnsignedInt(registers.getProgramCounter(), 2);
			int branchTargetAddress = programCounter + offset;
			registers.setProgramCounter(Integer.toUnsignedString(branchTargetAddress, 2));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("blt %s, %s, %d", rs1, rs2, offset);
	}

	public String BGE(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);
		int offset = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Convert program counter to integer before adding the offset
		int programCounter = Integer.parseUnsignedInt(registers.getProgramCounter(), 2);
		int branchTargetAddress = programCounter + offset;

		// Branch if rs1Value is greater than or equal to rs2Value
		boolean isGreaterThanOrEqual = rs1Value.compareTo(rs2Value) >= 0;

		System.out.println(
				"BGE DEBUG: Branch if " + rs1 + " greater or equal " + rs2 + " MOVE TO " + branchTargetAddress);
		if (isGreaterThanOrEqual) {
			registers.setProgramCounter(Integer.toUnsignedString(branchTargetAddress, 2));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("bge %s, %s, %d", rs1, rs2, immediate);
	}

	public String BLTU(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int offset = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		// Convert program counter to integer before adding the offset
		int programCounter = Integer.parseUnsignedInt(registers.getProgramCounter(), 2);
		int branchTargetAddress = programCounter + offset;

		// Branch if rs1Value is less than rs2Value (unsigned comparison)
		boolean isLessThanUnsigned = Integer.compareUnsigned(Integer.parseUnsignedInt(rs1Value, 2),
				Integer.parseUnsignedInt(rs2Value, 2)) < 0;

		System.out.println("BLTU DEBUG: Branch if " + rs1 + " less than " + rs2 + " (unsigned) MOVE TO " + offset);
		if (isLessThanUnsigned) {
			registers.setProgramCounter(Integer.toUnsignedString(branchTargetAddress, 2));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("bltu %s, %s, %d", rs1, rs2, offset);
	}

	public String BGEU(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		// Parse unsigned immediate value
		int immediate = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Get register values
		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		// Convert program counter to integer before adding the offset
		int programCounter = Integer.parseUnsignedInt(registers.getProgramCounter(), 2);
		int branchTargetAddress = programCounter + immediate;

		// Branch if rs1Value is greater than or equal to rs2Value (unsigned comparison)
		boolean isGreaterThanOrEqual = Integer.compareUnsigned(Integer.parseUnsignedInt(rs1Value, 2),
				Integer.parseUnsignedInt(rs2Value, 2)) >= 0;

		System.out.println(
				"BGEU DEBUG: Branch if " + rs1 + " greater or equal " + rs2 + " MOVE TO " + branchTargetAddress);
		if (isGreaterThanOrEqual) {
			registers.setProgramCounter(Integer.toUnsignedString(branchTargetAddress, 2));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("bgeu %s, %s, %d", rs1, rs2, immediate);
	}

	public String ADDI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform addition operation
		int result = valueIntRs1 + immediate;
		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPadSigned(result);
		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("addi %s, %s, %d", rd, rs1, immediate);
	}

	public String SLTI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform less than immediate operation
		int result = (valueIntRs1 < immediate) ? 1 : 0;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPadSigned(result);

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("slti %s, %s, %d", rd, rs1, immediate);
	}

	public String SLTIU(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		long valueLongRs1 = Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to long
		long immediate = Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform less than immediate (unsigned) operation
		int result = (valueLongRs1 < immediate) ? 1 : 0;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sltiu %s, %s, %d", rd, rs1, immediate);
	}

	public String XORI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform XOR operation
		int result = valueIntRs1 ^ immediate;

		// Convert result to 32-bit binary string

		String resultBinary = Utility.leftPadSigned(result);

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("xori %s, %s, %d", rd, rs1, immediate);
	}

	public String ORI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform OR operation
		int result = valueIntRs1 | immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPadSigned(result);

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("ori %s, %s, %d", rd, rs1, immediate);
	}

	public String ANDI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform AND operation
		int result = valueIntRs1 & immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPadSigned(result);

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("andi %s, %s, %d", rd, rs1, immediate);
	}

	/**
	 * Loads a 8-bit value from memory, then sign-extends to 32-bits before storing in rd
	 * @param instructionComponents
	 * @return
	 */
	public String LB(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
	    String rd = instructionComponents.get("rd"); // destination register
	    String rs1 = instructionComponents.get("rs1"); // base register
	    String imm = instructionComponents.get("imm"); // immediate value
	    // Get values from registers
	    String valueRs1 = registers.getRegisterValue(rs1);

		// Calculate the effective memory address by adding the immediate value to the
		// base register value
		int address = (int) Long.parseUnsignedLong(valueRs1, 2) + (int) Long.parseUnsignedLong(imm, 2);
		System.out.println("LB DEBUG: address: " + address);

		// Load the 8-bit value from memory at the calculated address
		String result = memory.loadByte(address);
		System.out.println("LB DEBUG: result: " + result);
		// System.out.println("Address should be ____: " +
		// Integer.toBinaryString(address));
		// System.out.println("Loaded value should be ________: " + result);

		// Sign-extend the 8-bit value to 32 bits
		String resultBinary = Utility.leftPad(result);
		System.out.println("LB DEBUG: resultBinary: " + resultBinary);
		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);

		// Increment the program counter
		registers.incrementProgramCounter();

		// Return the executed instruction in string format
		return String.format("lb %s, %s(%s)", rd, imm, rs1);
	}
	
	/**
	 * Loads a 16-bit value from memory, then sign-extends to 32-bits before storing in rd
	 * @param instructionComponents
	 * @return
	 */
	public String LH(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // base register
		String imm = instructionComponents.get("imm"); // immediate value

	    // Get values from registers
	    String valueRs1 = registers.getRegisterValue(rs1);
	    
	    // Calculate the effective memory address by adding the immediate value to the base register value
	    int address = (int) Long.parseUnsignedLong(valueRs1, 2) + (int) Long.parseUnsignedLong(imm, 2);
		System.out.println("LH DEBUG: address: " + address);
	    
		// Load the 16-bit value from memory at the calculated address
	    String result = memory.loadHalfword2(address);
		System.out.println("LH DEBUG: result: " + result);
	    
	    // Sign-extend the 16-bit value to 32 bits
	    String resultBinary = Utility.leftPad(result);
		System.out.println("LH DEBUG: resultBinary: " + resultBinary);
	    
		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);

		// Increment the program counter
		registers.incrementProgramCounter();

		// Return the executed instruction in string format
		return String.format("lh %s, %s(%s)", rd, imm, rs1);
	}

	public String LW(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // base register
		String imm = instructionComponents.get("imm"); // offset
		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int baseAddress = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert offset value from binary string to integer
		int offsetValue = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate memory address to load from
		int memoryAddress = baseAddress + offsetValue;

		// Load the word from memory
		String loadedWord = memory.loadWord2(memoryAddress);
		System.out.println("LW DEBUG: loadedWord: " + loadedWord);
		// Update rd register value
		loadedWord = Utility.leftPad(loadedWord);
		System.out.println("LW DEBUG: rd: " + rd);
		registers.setRegisterValue(rd, loadedWord);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("lw %s, %s, %d", rd, rs1, offsetValue);

	}

	public String LBU(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
	    String rd = instructionComponents.get("rd"); // destination register
	    String rs1 = instructionComponents.get("rs1"); // base register
	    String imm = instructionComponents.get("imm"); // immediate value

	    // Get values from registers
	    String valueRs1 = registers.getRegisterValue(rs1);

	    // Calculate the effective memory address by adding the immediate value to the base register value
	    int address = (int) Long.parseUnsignedLong(valueRs1, 2) + (int) Long.parseUnsignedLong(imm, 2);
		System.out.println("LBU DEBUG: address: " + address);
	    
		// Load the 8-bit value from memory at the calculated address
	    String result = memory.loadByte(address);
		System.out.println("LBU DEBUG: result: " + result);
	    //System.out.println("Address should be ____: " + Integer.toBinaryString(address));
	    //System.out.println("Loaded value should be ________: " + result);
	    
	    // Sign-extend the 8-bit value to 32 bits
	    String resultBinary = Utility.leftPad(0 + result);
		System.out.println("LBU DEBUG: resultBinary: " + resultBinary);
	    // Update rd register value
	    registers.setRegisterValue(rd, resultBinary);

	    // Increment the program counter
	    registers.incrementProgramCounter();

	    // Return the executed instruction in string format
	    return String.format("lbu %s, %s(%s)", rd, imm, rs1);
	}

	public String LHU(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
	    String rd = instructionComponents.get("rd"); // destination register
	    String rs1 = instructionComponents.get("rs1"); // base register
	    String imm = instructionComponents.get("imm"); // immediate value

	    // Get values from registers
	    String valueRs1 = registers.getRegisterValue(rs1);
	    
	    // Calculate the effective memory address by adding the immediate value to the base register value
	    int address = (int) Long.parseUnsignedLong(valueRs1, 2) + (int) Long.parseUnsignedLong(imm, 2);
		System.out.println("LH DEBUG: address: " + address);
	    
		// Load the 16-bit value from memory at the calculated address
	    String result = memory.loadHalfword2(address);
		System.out.println("LH DEBUG: result: " + result);
	    
	    // Sign-extend the 16-bit value to 32 bits
	    String resultBinary = Utility.leftPad(0 + result);
		System.out.println("LH DEBUG: resultBinary: " + resultBinary);
	    
		// Update rd register value
	    registers.setRegisterValue(rd, resultBinary);

	    // Increment the program counter
	    registers.incrementProgramCounter();

	    // Return the executed instruction in string format
	    return String.format("lh %s, %s(%s)", rd, imm, rs1);
	}

	public String SRLI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("shamt"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform logical right shift operation
		int result = valueIntRs1 >>> immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPadSigned(result);

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("srli %s, %s, %d", rd, rs1, immediate);
	}

	public String SLLI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("shamt"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform SLLI operation
		int result = valueIntRs1 << immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Integer.toBinaryString(result);
		// Pad result to 32 bits
		resultBinary = String.format("%32s", resultBinary).replace(' ', '0');

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("slli %s, %s, %d", rd, rs1, immediate);
	}

	public String SRAI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("shamt"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform SRAI operation (Arithmetic Right Shift)
		int result = valueIntRs1 >> immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("srai %s, %s, %d", rd, rs1, immediate);
	}

	public String SB(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rs1 = instructionComponents.get("rs1"); // base register
		String rs2 = instructionComponents.get("rs2"); // source register 2
		String imm = instructionComponents.get("imm"); // offset

		// Get values from registers
		String valueRs2 = registers.getRegisterValue(rs2);
		int valueIntRs2 = Integer.parseUnsignedInt(valueRs2, 2);
		// Convert immediate value from binary string to integer
		int offset = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Calculate effective address
		int effectiveAddress = Integer.parseInt(Utility.DATA_MEMORY_ADDRESS, 2) + offset;
		System.out.println("SB DEBUG: StoreByte " + effectiveAddress + " offset " + offset + " to get " + valueIntRs2);

		// Store the byte to memory
		memory.storeByte(effectiveAddress, valueRs2.substring(24));
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sb %s, %s, %d", rs2, rs1, offset);
	}

	public String SH(HashMap<String, String> instructionComponents) {
	    // Extract components from the HashMap
	    String rs1 = instructionComponents.get("rs1"); // base register
	    String rs2 = instructionComponents.get("rs2"); // source register 2
	    String imm = instructionComponents.get("imm"); // offset

	    // Get values from registers
	    String valueRs2 = registers.getRegisterValue(rs2);

		// Convert immediate value from binary string to integer
		int offset = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

		// Calculate effective address
		int effectiveAddress = Integer.parseInt(Utility.DATA_MEMORY_ADDRESS, 2) + offset;
		System.out.println("SH DEBUG: StoreHalfword " + effectiveAddress + " offset " + offset + " to get "
				+ valueRs2.substring(16));

		// Store the halfword to memory
		memory.storeHalfword(effectiveAddress, valueRs2.substring(16));
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sh %s, %s, %d", rs2, rs1, offset);
	}

	public String SW(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rs1 = instructionComponents.get("rs1"); // base register
		String rs2 = instructionComponents.get("rs2"); // source register 2
		String imm = instructionComponents.get("imm"); // offset

	    // Get values from registers
	    String valueRs2 = registers.getRegisterValue(rs2);
	    int valueIntRs2 = Integer.parseUnsignedInt(valueRs2, 2);
	    // Convert immediate value from binary string to integer
	    int offset = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

	    // Calculate effective address
	    int effectiveAddress = Integer.parseInt(Utility.DATA_MEMORY_ADDRESS, 2) + offset;
	    System.out.println("SW DEBUG: StoreWord " + valueIntRs2 + " offset " + offset + " to get " + effectiveAddress);
	    
	    // Store the word to memory
	    memory.storeWord(effectiveAddress, valueRs2);
	    registers.incrementProgramCounter();


	    // Build and return the instruction result string
	    return String.format("sw %s, %s, %d", rs2, rs1, offset);
	}


	public String SLL(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform shift left logical operation
		int result = valueIntRs1 << valueIntRs2;
		System.out.println("SLL DEBUG: Shifting " + valueIntRs1 + " left by " + valueIntRs2 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sll %s, %s, %s", rd, rs1, rs2);
	}


	public String ADD(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform addition operation
		int result = valueIntRs1 + valueIntRs2;
		System.out.println("ADD DEBUG: Adding " + valueIntRs1 + " and " + valueIntRs2 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("add %s, %s, %s", rd, rs1, rs2);
	}

	public String SUB(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform subtraction operation
		int result = valueIntRs1 - valueIntRs2;
		System.out.println("SUB DEBUG: Subtracting " + valueIntRs2 + " from " + valueIntRs1 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sub %s, %s, %s", rd, rs1, rs2);
	}

	public String SLT(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Check if any of the required values are null
		if (rd == null || rs1 == null || rs2 == null) {
			// Handle the error, maybe throw an exception or return an error message
			return "Error: Missing values in instruction";
		}

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);



		// Convert register values from binary string to integer
		int intValueRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int intValueRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform SLT operation
		int result = (intValueRs1 < intValueRs2) ? 1 : 0;
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));
		// Store the result in the destination register
		registers.setRegisterValue(rd, Utility.leftPad("0" + result));

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("slt %s, %s, %s", rd, rs1, rs2);
	}

	public String SLTU(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Convert values to unsigned integers
		long valueUnsignedRs1 = Long.parseUnsignedLong(valueRs1, 2);
		long valueUnsignedRs2 = Long.parseUnsignedLong(valueRs2, 2);

		// Perform the set less than unsigned operation
		int result = (Long.compareUnsigned(valueUnsignedRs1, valueUnsignedRs2) < 0) ? 1 : 0;
		System.out
				.println("SLTU DEBUG: Comparing " + valueUnsignedRs1 + " < " + valueUnsignedRs2 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sltu %s, %s, %s", rd, rs1, rs2);
	}

	public String XOR(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform bitwise XOR operation
		int result = valueIntRs1 ^ valueIntRs2;
		System.out.println(
				"XOR DEBUG: Performing bitwise XOR on " + valueIntRs1 + " and " + valueIntRs2 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("xor %s, %s, %s", rd, rs1, rs2);
	}

	public String SRL(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform shift right logical operation
		int result = valueIntRs1 >>> valueIntRs2;
		System.out.println("SRL DEBUG: Shifting " + valueIntRs1 + " right by " + valueIntRs2 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("srl %s, %s, %s", rd, rs1, rs2);
	}

	public String SRA(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Convert register values to integers
		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform shift right arithmetic operation
		int result = valueIntRs1 >> valueIntRs2;
		System.out.println("SRA DEBUG: Shifting " + valueIntRs1 + " right by " + valueIntRs2 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sra %s, %s, %s", rd, rs1, rs2);
	}

	public String OR(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

	    // Get values from registers
	    String valueRs1 = registers.getRegisterValue(rs1);
	    String valueRs2 = registers.getRegisterValue(rs2);

		// Perform bitwise OR operation
		int result = (int) Long.parseUnsignedLong(valueRs1, 2) | (int) Long.parseUnsignedLong(valueRs2, 2);
		System.out.println("OR DEBUG: Performing bitwise OR on " + valueRs1 + " and " + valueRs2 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("or %s, %s, %s", rd, rs1, rs2);
	}

	public String AND(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform bitwise AND operation
		int result = valueIntRs1 & valueIntRs2;
		System.out.println(
				"AND DEBUG: Performing bitwise AND on " + valueIntRs1 + " and " + valueIntRs2 + " to get " + result);

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

	    // Build and return the instruction result string
	    return String.format("and %s, %s, %s", rd, rs1, rs2);
	}
}