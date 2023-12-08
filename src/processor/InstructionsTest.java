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
        // Set up initial values
        String binaryValueX1 = "00000000000000000000000000001101";  // 13 in binary
        String binaryValueX2 = "00000000000000000000000000001010";  // 10 in binary

        testRegisters.setRegisterValue("x1", binaryValueX1);
        testRegisters.setRegisterValue("x2", binaryValueX2);

        // Execute SLT instruction
        HashMap<String, String> instructionMap = new HashMap<>();
        instructionMap.put("rs1", "x1");
        instructionMap.put("rs2", "x2");
        instructionMap.put("rd", "x3");
        testInstructions.SLT(instructionMap);

        // Check results
        String result = testRegisters.getRegisterValue("x3");
        assertEquals("00000000000000000000000000000000", result); // Expected 0 since x1 > x2

        testRegisters.setRegisterValue("x1", binaryValueX2);
        testRegisters.setRegisterValue("x2", binaryValueX1);
        testInstructions.SLT(instructionMap);
        result = testRegisters.getRegisterValue("x3");
        assertEquals("00000000000000000000000000000001", result); // Expected 1 since x1 < x2
    }

    @Test
    void SLTU() {
        // Set up initial values
        String binaryValueX1 = "00000000000000000000000000001101";  // 13 in binary
        String binaryValueX2 = "00000000000000000000000000001010";  // 10 in binary

        testRegisters.setRegisterValue("x1", binaryValueX1);
        testRegisters.setRegisterValue("x2", binaryValueX2);

        // Execute SLTU instruction
        HashMap<String, String> instructionMap = new HashMap<>();
        instructionMap.put("rs1", "x1");
        instructionMap.put("rs2", "x2");
        instructionMap.put("rd", "x3");
        testInstructions.SLTU(instructionMap);

        // Check results
        String result = testRegisters.getRegisterValue("x3");
        assertEquals("00000000000000000000000000000000", result); // Expected 0 since x1 > x2

        testRegisters.setRegisterValue("x1", binaryValueX2);
        testRegisters.setRegisterValue("x2", binaryValueX1);
        testInstructions.SLTU(instructionMap);
        result = testRegisters.getRegisterValue("x3");
        assertEquals("00000000000000000000000000000001", result); // Expected 1 since x1 < x2
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

}
