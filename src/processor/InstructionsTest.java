package processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructionsTest {

    private Memory testMemory;
    private Registers testRegisters;
    private Instructions testInstructions;

    @BeforeEach
    void setUp() {
        testMemory = new Memory();
        testRegisters = new Registers();
        testInstructions = new Instructions(testMemory, testRegisters);
    }

    @Test
    void SLT() {
        // Set up initial register values
        testRegisters.setRegisterValue("x1", "00000000000000000000000000000100"); // 4 in binary
        testRegisters.setRegisterValue("x2", "00000000000000000000000000001000"); // 8 in binary
        testRegisters.setRegisterValue("x3", "00000000000000000000000000000100"); // 4 in binary
        testRegisters.setRegisterValue("x5", "11111111111111111111111111111100"); // -4 in two's complement

        // Test where rs1 < rs2
        HashMap<String, String> instructionMap1 = new HashMap<>();
        instructionMap1.put("rs1", "x1");
        instructionMap1.put("rs2", "x2");
        instructionMap1.put("rd", "x10");
        testInstructions.SLT(instructionMap1);
        assertEquals("00000000000000000000000000000001", testRegisters.getRegisterValue("x10"));

        // Test where rs1 > rs2
        HashMap<String, String> instructionMap2 = new HashMap<>();
        instructionMap2.put("rs1", "x2");
        instructionMap2.put("rs2", "x1");
        instructionMap2.put("rd", "x11");
        testInstructions.SLT(instructionMap2);
        assertEquals("00000000000000000000000000000000", testRegisters.getRegisterValue("x11"));

        // Test where rs1 == rs2
        HashMap<String, String> instructionMap3 = new HashMap<>();
        instructionMap3.put("rs1", "x1");
        instructionMap3.put("rs2", "x3");
        instructionMap3.put("rd", "x12");
        testInstructions.SLT(instructionMap3);
        assertEquals("00000000000000000000000000000000", testRegisters.getRegisterValue("x12"));

        // Test with a negative value
        HashMap<String, String> instructionMap4 = new HashMap<>();
        instructionMap4.put("rs1", "x5");
        instructionMap4.put("rs2", "x1");
        instructionMap4.put("rd", "x13");
        testInstructions.SLT(instructionMap4);
        assertEquals("00000000000000000000000000000001", testRegisters.getRegisterValue("x13"));

        // Verify that the program counter is incremented correctly
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(16, programCounterValue); // Assuming each test increment is 4
    }
    @Test
    void SLTU() {
        // Set up initial register values
        testRegisters.setRegisterValue("x1", "00000000000000000000000000000100"); // 4 in binary
        testRegisters.setRegisterValue("x2", "00000000000000000000000000001000"); // 8 in binary
        testRegisters.setRegisterValue("x3", "00000000000000000000000000000100"); // 4 in binary

        // Test where rs1 < rs2
        HashMap<String, String> instructionMap1 = new HashMap<>();
        instructionMap1.put("rs1", "x1");
        instructionMap1.put("rs2", "x2");
        instructionMap1.put("rd", "x10");
        testInstructions.SLTU(instructionMap1);
        assertEquals("00000000000000000000000000000001", testRegisters.getRegisterValue("x10"));

        // Test where rs1 > rs2
        HashMap<String, String> instructionMap2 = new HashMap<>();
        instructionMap2.put("rs1", "x2");
        instructionMap2.put("rs2", "x1");
        instructionMap2.put("rd", "x11");
        testInstructions.SLTU(instructionMap2);
        assertEquals("00000000000000000000000000000000", testRegisters.getRegisterValue("x11"));

        // Test where rs1 == rs2
        HashMap<String, String> instructionMap3 = new HashMap<>();
        instructionMap3.put("rs1", "x1");
        instructionMap3.put("rs2", "x3");
        instructionMap3.put("rd", "x12");
        testInstructions.SLTU(instructionMap3);
        assertEquals("00000000000000000000000000000000", testRegisters.getRegisterValue("x12"));

        // Verify that the program counter is incremented correctly
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(12, programCounterValue); // Assuming each test increment is 4
    }
    @Test
    void ADDI() {
        // Set up initial values
        String binaryValueX1 = "00000000000000000000000000001010"; // 10 in binary
        testRegisters.setRegisterValue("x1", binaryValueX1);

        // Test ADDI with positive immediate value
        HashMap<String, String> instructionMapPos = new HashMap<>();
        instructionMapPos.put("rs1", "x1");
        instructionMapPos.put("rd", "x10");
        instructionMapPos.put("imm", "00000000000000000000000000000101"); // 5 in binary
        testInstructions.ADDI(instructionMapPos);

        String resultPos = testRegisters.getRegisterValue("x10");
        assertEquals("00000000000000000000000000001111", resultPos); // Expected 15 (10 + 5)

        // Test ADDI with negative immediate value
        HashMap<String, String> instructionMapNeg = new HashMap<>();
        instructionMapNeg.put("rs1", "x1");
        instructionMapNeg.put("rd", "x11");
        instructionMapNeg.put("imm", "11111111111111111111111111111011"); // -5 in two's complement
        testInstructions.ADDI(instructionMapNeg);

        String resultNeg = testRegisters.getRegisterValue("x11");
        assertEquals("00000000000000000000000000000101", resultNeg); // Expected 5 (10 - 5)

        // Test ADDI with zero immediate value
        HashMap<String, String> instructionMapZero = new HashMap<>();
        instructionMapZero.put("rs1", "x1");
        instructionMapZero.put("rd", "x12");
        instructionMapZero.put("imm", "00000000000000000000000000000000"); // 0 in binary
        testInstructions.ADDI(instructionMapZero);

        String resultZero = testRegisters.getRegisterValue("x12");
        assertEquals(binaryValueX1, resultZero); // Expected 10 (10 + 0)

        // Verify that the program counter is incremented
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(12, programCounterValue); // Assuming each test increment is 4
    }
    @Test
    void SLLI() {
        // Set up initial values
        String binaryValueX1 = "00000000000000000000000000001010"; // 10 in binary
        testRegisters.setRegisterValue("x1", binaryValueX1);

        // Test SLLI with small immediate value
        HashMap<String, String> instructionMapSmall = new HashMap<>();
        instructionMapSmall.put("rs1", "x1");
        instructionMapSmall.put("rd", "x10");
        instructionMapSmall.put("shamt", "00000000000000000000000000000101"); // 5 in binary
        testInstructions.SLLI(instructionMapSmall);

        String resultSmall = testRegisters.getRegisterValue("x10");
        assertEquals("00000000000000000000000101000000", resultSmall); // Expected 10 << 5

        // Test SLLI with zero immediate value
        HashMap<String, String> instructionMapZero = new HashMap<>();
        instructionMapZero.put("rs1", "x1");
        instructionMapZero.put("rd", "x11");
        instructionMapZero.put("shamt", "00000000000000000000000000000000"); // 0 in binary
        testInstructions.SLLI(instructionMapZero);

        String resultZero = testRegisters.getRegisterValue("x11");
        assertEquals(binaryValueX1, resultZero); // Expected 10 (10 << 0)

        // Test SLLI with maximum immediate value (31)
        HashMap<String, String> instructionMapMax = new HashMap<>();
        String binaryValueX2 = "00000000000000000000000000000001"; // 10 in binary
        testRegisters.setRegisterValue("x2", binaryValueX2);
        instructionMapMax.put("rs1", "x2");
        instructionMapMax.put("rd", "x12");
        instructionMapMax.put("shamt", "00000000000000000000000000011111"); // 31 in binary
        testInstructions.SLLI(instructionMapMax);

        String resultMax = testRegisters.getRegisterValue("x12");
        assertEquals("10000000000000000000000000000000", resultMax); // Expected 10 << 31

        // Verify that the program counter is incremented
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(12, programCounterValue); // Assuming each test increment is 4
    }
    
    @Test
    void LB() {
    	// Set up initial values
        String binaryValueX1 = "00000000000000000000000000001010"; // Example base register value (10 in binary)
        testRegisters.setRegisterValue("x1", binaryValueX1);

        // Test LB with a small positive offset
        HashMap<String, String> instructionMapSmallOffset = new HashMap<>();
        instructionMapSmallOffset.put("rs1", "x1");
        instructionMapSmallOffset.put("rd", "x10");
        instructionMapSmallOffset.put("imm", "00000000000000000000000000000101"); // Example offset (5 in binary)
        testMemory.storeByte(15, "11111111"); // Example byte value at memory address 15
        testInstructions.LB(instructionMapSmallOffset);

        String resultSmallOffset = testRegisters.getRegisterValue("x10");
        System.out.println("Result Small Offset: " + resultSmallOffset);
        assertEquals("11111111111111111111111111111111", resultSmallOffset); // Expected value at memory address 15
        
        // Test LB with zero offset
        HashMap<String, String> instructionMapZeroOffset = new HashMap<>();
        instructionMapZeroOffset.put("rs1", "x1");
        instructionMapZeroOffset.put("rd", "x11");
        instructionMapZeroOffset.put("imm", "00000000000000000000000000000000"); // Example offset (0 in binary)
        testMemory.storeByte(10, "00010111"); // Example byte value at memory address 10
        testInstructions.LB(instructionMapZeroOffset);

        String resultZeroOffset = testRegisters.getRegisterValue("x11");
        System.out.println("Result Zero Offset: " + resultZeroOffset);
        assertEquals("00000000000000000000000000010111", resultZeroOffset); // Expected value at memory address 10

        // Test LB with a negative offset
        HashMap<String, String> instructionMapNegativeOffset = new HashMap<>();
        instructionMapNegativeOffset.put("rs1", "x1");
        instructionMapNegativeOffset.put("rd", "x12");
        instructionMapNegativeOffset.put("imm", "11111111111111111111111111111011"); // Example offset (-5 in binary)
        testMemory.storeByte(5, "00100101"); // Example byte value at memory address 5
        testInstructions.LB(instructionMapNegativeOffset);

        String resultNegativeOffset = testRegisters.getRegisterValue("x12");
        System.out.println("Result Negative Offset: " + resultNegativeOffset);
        assertEquals("00000000000000000000000000100101", resultNegativeOffset); // Expected value at memory address 5

        // Verify that the program counter is incremented
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(12, programCounterValue); // Assuming each test increment is 4
    }
    
    @Test
    void LBU() {
    	// Set up initial values
        String binaryValueX1 = "00000000000000000000000000001010"; // Example base register value (10 in binary)
        testRegisters.setRegisterValue("x1", binaryValueX1);

        // Test LBU with a small positive offset
        HashMap<String, String> instructionMapSmallOffset = new HashMap<>();
        instructionMapSmallOffset.put("rs1", "x1");
        instructionMapSmallOffset.put("rd", "x10");
        instructionMapSmallOffset.put("imm", "00000000000000000000000000000101"); // Example offset (5 in binary)
        testMemory.storeByte(15, "11111111"); // Example byte value at memory address 15
        testInstructions.LBU(instructionMapSmallOffset);

        String resultSmallOffset = testRegisters.getRegisterValue("x10");
        System.out.println("Result Small Offset: " + resultSmallOffset);
        assertEquals("00000000000000000000000011111111", resultSmallOffset); // Expected value at memory address 15
        
        // Test LBU with zero offset
        HashMap<String, String> instructionMapZeroOffset = new HashMap<>();
        instructionMapZeroOffset.put("rs1", "x1");
        instructionMapZeroOffset.put("rd", "x11");
        instructionMapZeroOffset.put("imm", "00000000000000000000000000000000"); // Example offset (0 in binary)
        testMemory.storeByte(10, "00010111"); // Example byte value at memory address 10
        testInstructions.LBU(instructionMapZeroOffset);

        String resultZeroOffset = testRegisters.getRegisterValue("x11");
        System.out.println("Result Zero Offset: " + resultZeroOffset);
        assertEquals("00000000000000000000000000010111", resultZeroOffset); // Expected value at memory address 10

        // Test LBU with a negative offset
        HashMap<String, String> instructionMapNegativeOffset = new HashMap<>();
        instructionMapNegativeOffset.put("rs1", "x1");
        instructionMapNegativeOffset.put("rd", "x12");
        instructionMapNegativeOffset.put("imm", "11111111111111111111111111111011"); // Example offset (-5 in binary)
        testMemory.storeByte(5, "00100101"); // Example byte value at memory address 5
        testInstructions.LBU(instructionMapNegativeOffset);

        String resultNegativeOffset = testRegisters.getRegisterValue("x12");
        System.out.println("Result Negative Offset: " + resultNegativeOffset);
        assertEquals("00000000000000000000000000100101", resultNegativeOffset); // Expected value at memory address 5

        // Verify that the program counter is incremented
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(12, programCounterValue); // Assuming each test increment is 4
    }
    
    @Test
    void LH() {
    	// Set up initial values
        String binaryValueX1 = "00000000000000000000000000001010"; // Example base register value (10 in binary)
        testRegisters.setRegisterValue("x1", binaryValueX1);

        // Test LH with a small positive offset
        HashMap<String, String> instructionMapSmallOffset = new HashMap<>();
        instructionMapSmallOffset.put("rs1", "x1");
        instructionMapSmallOffset.put("rd", "x10");
        instructionMapSmallOffset.put("imm", "00000000000000000000000000000101"); // Example offset (5 in binary)
        testMemory.storeByte(15, "0000010011010010"); // Example halfword value at memory address 15
        testInstructions.LH(instructionMapSmallOffset);

        String resultSmallOffset = testRegisters.getRegisterValue("x10");
        System.out.println("Result Small Offset: " + resultSmallOffset);
        assertEquals("00000000000000000000010011010010", resultSmallOffset); // Expected value at memory address 15
        
        // Test LH with zero offset
        HashMap<String, String> instructionMapZeroOffset = new HashMap<>();
        instructionMapZeroOffset.put("rs1", "x1");
        instructionMapZeroOffset.put("rd", "x11");
        instructionMapZeroOffset.put("imm", "00000000000000000000000000000000"); // Example offset (0 in binary)
        testMemory.storeByte(10, "0001011000001110"); // Example halfword value at memory address 10
        testInstructions.LH(instructionMapZeroOffset);

        String resultZeroOffset = testRegisters.getRegisterValue("x11");
        System.out.println("Result Zero Offset: " + resultZeroOffset);
        assertEquals("00000000000000000001011000001110", resultZeroOffset); // Expected value at memory address 10

        // Test LH with a negative offset
        HashMap<String, String> instructionMapNegativeOffset = new HashMap<>();
        instructionMapNegativeOffset.put("rs1", "x1");
        instructionMapNegativeOffset.put("rd", "x12");
        instructionMapNegativeOffset.put("imm", "11111111111111111111111111111011"); // Example offset (-5 in binary)
        testMemory.storeByte(5, "1111111111111001"); // Example halfword value at memory address 5
        testInstructions.LH(instructionMapNegativeOffset);

        String resultNegativeOffset = testRegisters.getRegisterValue("x12");
        System.out.println("Result Negative Offset: " + resultNegativeOffset);
        assertEquals("11111111111111111111111111111001", resultNegativeOffset); // Expected value at memory address 5

        // Verify that the program counter is incremented
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(12, programCounterValue); // Assuming each test increment is 4
    }
    
    @Test
    void LHU() {
    	// Set up initial values
        String binaryValueX1 = "00000000000000000000000000001010"; // Example base register value (10 in binary)
        testRegisters.setRegisterValue("x1", binaryValueX1);

        // Test LB with a small positive offset
        HashMap<String, String> instructionMapSmallOffset = new HashMap<>();
        instructionMapSmallOffset.put("rs1", "x1");
        instructionMapSmallOffset.put("rd", "x10");
        instructionMapSmallOffset.put("imm", "00000000000000000000000000000101"); // Example offset (5 in binary)
        testMemory.storeByte(15, "0000010011010010"); // Example halfword value at memory address 15
        testInstructions.LHU(instructionMapSmallOffset);

        String resultSmallOffset = testRegisters.getRegisterValue("x10");
        System.out.println("Result Small Offset: " + resultSmallOffset);
        assertEquals("00000000000000000000010011010010", resultSmallOffset); // Expected value at memory address 15
        
        // Test LHU with zero offset
        HashMap<String, String> instructionMapZeroOffset = new HashMap<>();
        instructionMapZeroOffset.put("rs1", "x1");
        instructionMapZeroOffset.put("rd", "x11");
        instructionMapZeroOffset.put("imm", "00000000000000000000000000000000"); // Example offset (0 in binary)
        testMemory.storeByte(10, "0001011000001110"); // Example halfword value at memory address 10
        testInstructions.LHU(instructionMapZeroOffset);

        String resultZeroOffset = testRegisters.getRegisterValue("x11");
        System.out.println("Result Zero Offset: " + resultZeroOffset);
        assertEquals("00000000000000000001011000001110", resultZeroOffset); // Expected value at memory address 10

        // Test LHU with a negative offset
        HashMap<String, String> instructionMapNegativeOffset = new HashMap<>();
        instructionMapNegativeOffset.put("rs1", "x1");
        instructionMapNegativeOffset.put("rd", "x12");
        instructionMapNegativeOffset.put("imm", "11111111111111111111111111111011"); // Example offset (-5 in binary)
        testMemory.storeByte(5, "1111111111111001"); // Example halfword value at memory address 5
        testInstructions.LHU(instructionMapNegativeOffset);

        String resultNegativeOffset = testRegisters.getRegisterValue("x12");
        System.out.println("Result Negative Offset: " + resultNegativeOffset);
        assertEquals("00000000000000001111111111111001", resultNegativeOffset); // Expected value at memory address 5

        // Verify that the program counter is incremented
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(12, programCounterValue); // Assuming each test increment is 4
    }

    @Test
    void BNE() {
        // Set up initial register values
        testRegisters.setRegisterValue("x1", "00000000000000000000000000000100"); // 4 in binary
        testRegisters.setRegisterValue("x2", "00000000000000000000000000001000"); // 8 in binary
        testRegisters.setRegisterValue("x3", "00000000000000000000000000000100"); // 4 in binary
        testRegisters.setRegisterValue("x5", "11111111111111111111111111111100"); // -4 in two's complement

        // Test where rs1 < rs2
        HashMap<String, String> instructionMap1 = new HashMap<>();
        instructionMap1.put("rs1", "x1");
        instructionMap1.put("rs2", "x2");
        instructionMap1.put("imm", "00000000000000000000000000000010"); // imm set to 2
        testInstructions.BNE(instructionMap1);  // should increment PC by 2
        assertEquals("00000000000000000000000000000100", testRegisters.getProgramCounter());

        // Test where rs1 > rs2
        HashMap<String, String> instructionMap2 = new HashMap<>();
        instructionMap2.put("rs1", "x2");
        instructionMap2.put("rs2", "x1");
        instructionMap2.put("imm", "00000000000000000000000000000010"); // imm set to 2
        testInstructions.BNE(instructionMap2);  // should increment PC by 2
        assertEquals("00000000000000000000000000001000", testRegisters.getProgramCounter());

        // Test where rs1 == rs2
        HashMap<String, String> instructionMap3 = new HashMap<>();
        instructionMap3.put("rs1", "x1");
        instructionMap3.put("rs2", "x3");
        instructionMap3.put("imm", "00000000000000000000000000000010"); // imm set to 2
        testInstructions.BNE(instructionMap3);  // should increment PC by 4
        assertEquals("00000000000000000000000000001100", testRegisters.getProgramCounter());

        // Test with a negative value
        HashMap<String, String> instructionMap4 = new HashMap<>();
        instructionMap4.put("rs1", "x5");
        instructionMap4.put("rs2", "x1");
        instructionMap4.put("imm", "00000000000000000000000000000010"); // imm set to 2
        testInstructions.BNE(instructionMap4);  // should increment PC by 2
        assertEquals("00000000000000000000000000010000", testRegisters.getProgramCounter());

        // Verify that the program counter is incremented correctly
        int programCounterValue = Integer.parseInt(testRegisters.getProgramCounter(), 2);
        assertEquals(10, programCounterValue); // Assuming each *successful* test increment is 2
    }
}
