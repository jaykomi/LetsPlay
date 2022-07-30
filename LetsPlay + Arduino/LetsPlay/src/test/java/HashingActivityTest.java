
import login.HashingActivity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class HashingActivityTest {
    
       public HashingActivityTest() {
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
    public void testHashingActivity() {
        
        HashingActivity h = new HashingActivity();
    
    // hashing activity bekende gegevens test id 4
    
    System.out.println("Hashing");
    String expResult = "1d1fd0d223700e4cc3ea2f19f836d367";
    String result = h.main("abcABC123!@#");
    String stringActualResults = String.valueOf(result);   
    assertEquals(expResult, stringActualResults);
    
    // hashing activity lege strings test id 5
    
    System.out.println("Hashing");
    String expResult1 = "d41d8cd98f00b204e9800998ecf8427e";
    String result1 = h.main("");
    String stringActualResults1 = String.valueOf(result1);  
    assertEquals(expResult1, stringActualResults1);
    
    // hashing activity alleen digits test id 6
    
    System.out.println("Hashing");
    String expResult2 = "25f9e794323b453885f5181f1b624d0b";
    String result2 = h.main("123456789");
    String stringActualResults2 = String.valueOf(result2);  
    assertEquals(expResult2, stringActualResults2);
    
     // hashing activity 5 spaties test id 7 
     
      System.out.println("Hashing");
    String expResult3 = "1545e945d5c3e7d9fa642d0a57fc8432";
    String result3 = h.main("     ");
    String stringActualResults3 = String.valueOf(result3);  
    assertEquals(expResult3, stringActualResults3);
        
}
}
