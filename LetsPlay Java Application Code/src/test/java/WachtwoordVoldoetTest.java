import dataKlassen.Begeleider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class WachtwoordVoldoetTest {
    
     public WachtwoordVoldoetTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    
     @Test
    public void testUserLoginActie() {
        
    Begeleider b = new Begeleider();
        
     // wachtwoord validatie voldoet test id 8
    
    System.out.println("Valid Password");
    boolean expResult = true;
     String stringExpResult = String.valueOf(expResult);  
    Boolean result = b.isValidPassword("abcABC123!@#");
    String stringActualResults = String.valueOf(result);   
    assertEquals(stringExpResult, stringActualResults);
   
    // wachtwoord validatie leeg test id 9
    
     System.out.println("Valid Password");
    boolean expResult1 = false;
     String stringExpResult1 = String.valueOf(expResult1);  
    Boolean result1 = b.isValidPassword("");
    String stringActualResults1 = String.valueOf(result1);   
    assertEquals(stringExpResult1, stringActualResults1);
    
    
    // wachtwoord validatie voldoet niet test id 10
    
      System.out.println("Valid Password");
    boolean expResult2 = false;
     String stringExpResult2 = String.valueOf(expResult2);  
    Boolean result2 = b.isValidPassword("asdasd");
    String stringActualResults2 = String.valueOf(result2);   
    assertEquals(stringExpResult2, stringActualResults2);
        
    
    }
    
}
