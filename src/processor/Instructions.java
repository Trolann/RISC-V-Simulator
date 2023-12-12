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
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("lui %s, %d", rd, immediate);
	}

	public String AUIPC(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String imm = instructionComponents.get("imm"); // immediate value

		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Get the current value of the Program Counter (PC)
		String pcBinary = registers.getProgramCounter(); // Assuming pc is stored as a binary string

		// Convert pcBinary to integer
		int pcValue = Integer.parseInt(pcBinary, 2);

		// Calculate the result by adding immediate to the PC
		int result = pcValue + immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("auipc %s, %d", rd, immediate);
	}

	public String JAL(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String imm = instructionComponents.get("imm"); // immediate value

		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Get the current value of the Program Counter (PC)
		String pcBinary = registers.getProgramCounter(); // Assuming pc is stored as a binary string

		// Convert pcBinary to integer
		int pcValue = Integer.parseInt(pcBinary, 2);

		// Calculate the result by adding immediate to the PC
		int result = pcValue + immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);

		// Build and return the instruction result string
		return String.format("jal %s, %d", rd, immediate);
	}

	public String JALR(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register for base address
		String imm = instructionComponents.get("imm"); // immediate value

		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Get the value of the base address register (rs1)
		String rs1ValueBinary = registers.getRegisterValue(rs1);
		int rs1Value = Integer.parseInt(rs1ValueBinary, 2);

		// Calculate the result by adding immediate to the base address
		int result = rs1Value + immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);

		// Build and return the instruction result string
		return String.format("jalr %s, %s, %d", rd, rs1, immediate);
	}

	public String BEQ(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		if (rs1Value.equals(rs2Value)) {
			int pcValue = Integer.parseInt(registers.getProgramCounter(), 2);
			int targetAddress = pcValue + immediate;

			registers.setProgramCounter(Utility.leftPad(Integer.toBinaryString(targetAddress)));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("beq %s, %s, %d", rs1, rs2, immediate);
	}

	public String BNE(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		if (!rs1Value.equals(rs2Value)) {
			int pcValue = Integer.parseInt(registers.getProgramCounter(), 2);
			int targetAddress = pcValue + immediate;

			registers.setProgramCounter(Utility.leftPad(Integer.toBinaryString(targetAddress)));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("bne %s, %s, %d", rs1, rs2, immediate);
	}

	public String BLT(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		int intRs1Value = Integer.parseInt(rs1Value, 2);
		int intRs2Value = Integer.parseInt(rs2Value, 2);

		if (intRs1Value < intRs2Value) {
			int pcValue = Integer.parseInt(registers.getProgramCounter(), 2);
			int targetAddress = pcValue + immediate;

			registers.setProgramCounter(Utility.leftPad(Integer.toBinaryString(targetAddress)));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("blt %s, %s, %d", rs1, rs2, immediate);
	}

	public String BGE(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		if (rs1Value.compareTo(rs2Value) >= 0) {
			int pcValue = Integer.parseInt(registers.getProgramCounter(), 2);
			int targetAddress = pcValue + immediate;

			registers.setProgramCounter(Utility.leftPad(Integer.toBinaryString(targetAddress)));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("bge %s, %s, %d", rs1, rs2, immediate);
	}

	public String BLTU(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		if (Long.compareUnsigned(Long.parseUnsignedLong(rs1Value, 2), Long.parseUnsignedLong(rs2Value, 2)) < 0) {
			int pcValue = Integer.parseInt(registers.getProgramCounter(), 2);
			int targetAddress = pcValue + immediate;

			registers.setProgramCounter(Utility.leftPad(Integer.toBinaryString(targetAddress)));
		} else {
			registers.incrementProgramCounter();
		}

		return String.format("bltu %s, %s, %d", rs1, rs2, immediate);
	}

	public String BGEU(HashMap<String, String> instructionComponents) {
		String rs1 = instructionComponents.get("rs1");
		String rs2 = instructionComponents.get("rs2");
		String imm = instructionComponents.get("imm");

		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		String rs1Value = registers.getRegisterValue(rs1);
		String rs2Value = registers.getRegisterValue(rs2);

		if (Long.compareUnsigned(Long.parseUnsignedLong(rs1Value, 2), Long.parseUnsignedLong(rs2Value, 2)) >= 0) {
			int pcValue = Integer.parseInt(registers.getProgramCounter(), 2);
			int targetAddress = pcValue + immediate;

			registers.setProgramCounter(Utility.leftPad(Integer.toBinaryString(targetAddress)));
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
		System.out.println("ADDI DEBUG: Adding " + valueIntRs1 + " and " + immediate + " to get " + result);
		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));
		if (result < 0 ) {
			resultBinary = Utility.leftPad("1" + Integer.toBinaryString(result));
		}
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
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("slti %s, %s, %d", rd, rs1, immediate);
	}

	public String SRLI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Perform logical right shift operation
		int result = valueIntRs1 >>> immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("srli %s, %s, %d", rd, rs1, immediate);
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
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

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
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

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
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("andi %s, %s, %d", rd, rs1, immediate);
	}

	public String LB(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // base register
		String imm = instructionComponents.get("imm"); // immediate offset

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate memory address
		int address = valueIntRs1 + immediate;

		// Load byte from memory
		String loadedByte = memory.loadByte(address);

		// Update rd register value
		registers.setRegisterValue(rd, loadedByte);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("lb %s, %d(%s)", rd, immediate, rs1);
	}

	public String LH(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // base register
		String imm = instructionComponents.get("imm"); // immediate offset

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate memory address
		int address = valueIntRs1 + immediate;

		// Load halfword from memory
		String loadedHalfword = memory.loadHalfword(address);

		// Update rd register value
		registers.setRegisterValue(rd, loadedHalfword);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("lh %s, %d(%s)", rd, immediate, rs1);
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
	    String loadedWord = memory.loadWord(memoryAddress);

	    // Update rd register value
	    registers.setRegisterValue(rd, loadedWord);
	    registers.incrementProgramCounter();

	    // Build and return the instruction result string
	    return String.format("lw %s, %s, %d", rd, rs1, offsetValue);
	}


	public String LBU(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // base register
		String imm = instructionComponents.get("imm"); // immediate offset

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate memory address
		int address = valueIntRs1 + immediate;

		// Load byte from memory
		String loadedByte = memory.loadByte(address);

		// Update rd register value
		registers.setRegisterValue(rd, loadedByte);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("lbu %s, %d(%s)", rd, immediate, rs1);
	}

	public String LHU(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // base register
		String imm = instructionComponents.get("imm"); // immediate offset

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate memory address
		int address = valueIntRs1 + immediate;

		// Load halfword from memory
		String loadedHalfword = memory.loadHalfword(address);

		// Update rd register value
		registers.setRegisterValue(rd, loadedHalfword);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("lhu %s, %d(%s)", rd, immediate, rs1);
	}

	public String SB(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rs1 = instructionComponents.get("rs1"); // base register
		String rs2 = instructionComponents.get("rs2"); // source register
		String imm = instructionComponents.get("imm"); // immediate offset

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate memory address
		int address = valueIntRs1 + immediate;

		// Store byte to memory
		memory.storeByte(address, valueRs2.substring(24)); // Assuming 32-bit registers, store the least significant
															// byte

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sb %s, %d(%s)", rs2, immediate, rs1);
	}
	
	public String SLLI(HashMap<String, String> instructionComponents) {
	    // Extract components from the HashMap
	    String rd = instructionComponents.get("rd"); // destination register
	    String rs1 = instructionComponents.get("rs1"); // source register 1
	    String imm = instructionComponents.get("imm"); // immediate value


	    // Get values from registers
	    String valueRs1 = registers.getRegisterValue(rs1);

	    int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
	    // Convert shift amount from binary string to integer
	    int shiftAmount = Integer.parseInt(imm, 2);


	    // Perform shift left logical operation
	    int result = valueIntRs1 << shiftAmount;
	    System.out.println("SLLI DEBUG: Shifting " + valueIntRs1 + " left by " + shiftAmount + " to get " + result);
	    // Convert result to 32-bit binary string
	    String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

	    // Update rd register value
	    registers.setRegisterValue(rd, resultBinary);
	    registers.incrementProgramCounter();

	    // Build and return the instruction result string
	    return String.format("slli %s, %s, %d", rd, rs1, shiftAmount);
	}

	public String SRAI(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String imm = instructionComponents.get("imm"); // immediate register

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

	public String SH(HashMap<String, String> instructionComponents) {
		// Extract components from the HashMap
		String rs1 = instructionComponents.get("rs1"); // base register
		String rs2 = instructionComponents.get("rs2"); // source register 2
		String imm = instructionComponents.get("imm"); // offset

		// Get values from registers
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);
		// Convert immediate value from binary string to integer
		int offset = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate effective address
		int effectiveAddress = valueIntRs2 + offset;

		// Store the halfword at the effective address
		memory.storeHalfword(effectiveAddress, valueIntRs2);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sh %s, %d(%s)", rs2, offset, rs1);
	}

	public String SW(HashMap<String, String> instructionComponents) {
	    // Extract components from the HashMap
	    String rs1 = instructionComponents.get("rs1"); // base register
	    String rs2 = instructionComponents.get("rs2"); // source register
	    String imm = instructionComponents.get("imm"); // offset

	    // Get values from registers
	    String valueRs1 = registers.getRegisterValue(rs1);
	    String valueRs2 = registers.getRegisterValue(rs2);

	    int baseAddress = Integer.parseUnsignedInt(valueRs1, 2);
	    // Convert offset value from binary string to integer
	    int offsetValue = Integer.parseUnsignedInt(Utility.leftPad(imm), 2);

	    // Calculate memory address to store to
	    int memoryAddress = baseAddress + offsetValue;

	    // Store the word to memory
	    memory.storeWord(memoryAddress, valueRs2);

	    registers.incrementProgramCounter();

	    // Build and return the instruction result string
	    return String.format("sw %s, %s, %d", rs2, rs1, offsetValue);
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




	public String SLT(HashMap<String, String> instructionComponents) {
	    // Extract components from the HashMap
	    String rd = instructionComponents.get("rd"); // destination register
	    String rs1 = instructionComponents.get("rs1"); // source register 1
	    String rs2 = instructionComponents.get("rs2"); // source register 2

	    // Get values from registers
	    String valueRs1 = registers.getRegisterValue(rs1);
	    String valueRs2 = registers.getRegisterValue(rs2);

	    // Convert values to signed integers
	    int valueIntRs1 = Integer.parseInt(valueRs1, 2);
	    int valueIntRs2 = Integer.parseInt(valueRs2, 2);

	    // Perform the set less than operation
	    int result = (valueIntRs1 < valueIntRs2) ? 1 : 0;
	    System.out.println("SLT DEBUG: Comparing " + valueIntRs1 + " < " + valueIntRs2 + " to get " + result);

	    // Convert result to 32-bit binary string
	    String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

	    // Update rd register value
	    registers.setRegisterValue(rd, resultBinary);
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
	    System.out.println("SLTU DEBUG: Comparing " + valueUnsignedRs1 + " < " + valueUnsignedRs2 + " to get " + result);

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
	    System.out.println("XOR DEBUG: Performing bitwise XOR on " + valueIntRs1 + " and " + valueIntRs2 + " to get " + result);

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
	    System.out.println("AND DEBUG: Performing bitwise AND on " + valueIntRs1 + " and " + valueIntRs2 + " to get " + result);

	    // Convert result to 32-bit binary string
	    String resultBinary = Utility.leftPad("0" + Integer.toBinaryString(result));

	    // Update rd register value
	    registers.setRegisterValue(rd, resultBinary);
	    registers.incrementProgramCounter();

	    // Build and return the instruction result string
	    return String.format("and %s, %s, %s", rd, rs1, rs2);
	}




}
