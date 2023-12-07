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

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

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
		String imm = instructionComponents.get("imm"); // immediate offset

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate memory address
		int address = valueIntRs1 + immediate;

		// Load word from memory
		String loadedWord = memory.loadWord(address);

		// Update rd register value
		registers.setRegisterValue(rd, loadedWord);
		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("lw %s, %d(%s)", rd, immediate, rs1);
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
		String imm = instructionComponents.get("imm"); // immediate register

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		// Convert immediate value from binary string to integer
		int immediate = Integer.parseInt(Utility.leftPad(imm), 2);

		// Perform SLLI operation
		int result = valueIntRs1 << immediate;

		// Convert result to 32-bit binary string
		String resultBinary = Utility.leftPad(Integer.toBinaryString(result));

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
		String rs2 = instructionComponents.get("rs2"); // source register 2
		String imm = instructionComponents.get("imm"); // offset

		// Get values from registers
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);
		// Convert immediate value from binary string to integer
		int offset = (int) Long.parseUnsignedLong(Utility.leftPad(imm), 2);

		// Calculate effective address
		int effectiveAddress = valueIntRs2 + offset;

		// Store the word at the effective address
		memory.storeWord(effectiveAddress, valueIntRs2);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sw %s, %d(%s)", rs2, offset, rs1);
	}

	public String ADD(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Convert register values from binary string to integer
		int intValueRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int intValueRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform ADD operation
		int result = intValueRs1 + intValueRs2;

		// Store the result in the destination register
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("add %s, %s, %s", rd, rs1, rs2);
	}

	public String SUB(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Convert register values from binary string to integer
		int intValueRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int intValueRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform SUB operation
		int result = intValueRs1 - intValueRs2;

		// Store the result in the destination register
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sub %s, %s, %s", rd, rs1, rs2);
	}

	public String SLL(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String shamtStr = instructionComponents.get("shamt"); // shift amount

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);

		// Convert register value and shift amount from binary string to integer
		int intValueRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int shamt = Integer.parseInt(shamtStr, 2);

		// Perform SLL operation
		int result = intValueRs1 << shamt;

		// Store the result in the destination register
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sll %s, %s, %d", rd, rs1, shamt);
	}

	public String SLT(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Compare values and set the result in the destination register
		int result = (Integer.parseInt(valueRs1) < Integer.parseInt(valueRs2)) ? 1 : 0;
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("slt %s, %s, %s", rd, rs1, rs2);
	}

	public String SLTU(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Compare values and set the result in the destination register
		long unsignedValueRs1 = Long.parseUnsignedLong(valueRs1);
		long unsignedValueRs2 = Long.parseUnsignedLong(valueRs2);
		int result = (unsignedValueRs1 < unsignedValueRs2) ? 1 : 0;
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sltu %s, %s, %s", rd, rs1, rs2);
	}

	public String XOR(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// XOR values and set the result in the destination register
		int result = Integer.parseInt(valueRs1) ^ Integer.parseInt(valueRs2);
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("xor %s, %s, %s", rd, rs1, rs2);
	}

	public String SRL(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Perform SRL operation and set the result in the destination register
		int result = Integer.parseInt(valueRs1) >>> Integer.parseInt(valueRs2);
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("srl %s, %s, %s", rd, rs1, rs2);
	}

	public String SRA(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Perform SRA operation and set the result in the destination register
		int result = Integer.parseInt(valueRs1) >> Integer.parseInt(valueRs2);
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("sra %s, %s, %s", rd, rs1, rs2);
	}

	public String OR(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// Perform OR operation and set the result in the destination register
		int result = Integer.parseInt(valueRs1) | Integer.parseInt(valueRs2);
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("or %s, %s, %s", rd, rs1, rs2);
	}

	public String AND(HashMap<String, String> instructionComponents) {
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); // source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		// AND values and set the result in the destination register
		int result = Integer.parseInt(valueRs1) & Integer.parseInt(valueRs2);
		registers.setRegisterValue(result, rd);

		registers.incrementProgramCounter();

		// Build and return the instruction result string
		return String.format("and %s, %s, %s", rd, rs1, rs2);
	}

}
