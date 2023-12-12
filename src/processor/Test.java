package processor;
import java.util.HashMap;

public class Test {
	private Memory memory;
	public Registers registers;
	
	 // Assuming you have a method to execute the AND instruction
    public void AND(HashMap<String, String> instructionComponents) {
        // Your AND method implementation goes here
    	// Extract components from the HashMap
		String rd = instructionComponents.get("rd"); // destination register
		String rs1 = instructionComponents.get("rs1"); // source register 1
		String rs2 = instructionComponents.get("rs2"); //source register 2

		// Get values from registers
		String valueRs1 = registers.getRegisterValue(rs1);
		String valueRs2 = registers.getRegisterValue(rs2);

		int valueIntRs1 = (int) Long.parseUnsignedLong(valueRs1, 2);
		int valueIntRs2 = (int) Long.parseUnsignedLong(valueRs2, 2);

		// Perform AND operation
		int result = valueIntRs1 & valueIntRs2;

		// Convert result back to binary string representation
		String resultBinary = Integer.toBinaryString(result);

		// Ensure resultBinary is 32-bit length
		resultBinary = Utility.leftPad(resultBinary);

		// Update rd register value
		registers.setRegisterValue(rd, resultBinary);
		registers.incrementProgramCounter();
    }
    
    public void main(String[] args) {
        // Set up initial register values for testing
        String binaryValueX1 = "00000000000000000000000000001101";  // Value 13 in binary
        String binaryValueX2 = "00000000000000000000000000001010";  // Value 10 in binary

        HashMap<String, String> initialRegisters = new HashMap<>();
        initialRegisters.put("x1", binaryValueX1);
        initialRegisters.put("x2", binaryValueX2);

        // Test AND instruction
        HashMap<String, String> andInstruction = new HashMap<>();
        andInstruction.put("rd", "x3");
        andInstruction.put("rs1", "x1");
        andInstruction.put("rs2", "x2");

        // Print initial register values
        System.out.println("Initial Register Values:");
        initialRegisters.forEach((register, value) -> {
            System.out.println(register + ": " + value);
        });

        // Execute AND instruction
        AND(andInstruction);

        // Verify the result in register x3
        String resultAND = registers.getRegisterValue("x3");
        System.out.println("\nResult of AND: " + resultAND);
    }
}
