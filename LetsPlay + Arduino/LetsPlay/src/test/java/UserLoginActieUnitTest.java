import login.LoginActie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class UserLoginActieUnitTest {
    
    public UserLoginActieUnitTest() {
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
        
    LoginActie u = new LoginActie();
    
    // password unknown to database test id 1
    
    System.out.println("login actie");
    boolean expResult = false;
    String stringExpResult = String.valueOf(expResult);  
    Boolean result = u.login("asdasd", "asdasd");
    String stringActualResults = String.valueOf(result);  
    
    assertEquals(stringExpResult, stringActualResults);
    
    // password known to database test id 2
    
    System.out.println("login actie");
    boolean expResult1 = true;
    String stringExpResult1 = String.valueOf(expResult1);  
    Boolean result1 = u.login("asdasd", "1d1fd0d223700e4cc3ea2f19f836d367");
    String stringActualResults1 = String.valueOf(result1);  
    
    assertEquals(stringExpResult1, stringActualResults1);
    
    // rare string combinatie met spaties test id 3
    
    System.out.println("login actie");
    boolean expResult2 = false;
    String stringExpResult2 = String.valueOf(expResult2);  
    Boolean result2 = u.login("-@*#&!@( SND K!@#", "a8f5f1 67f44 f4964e6c998d ee827110c");
    String stringActualResults2 = String.valueOf(result2);  
    
    assertEquals(stringExpResult2, stringActualResults2);
    
    // empty string test test id 4
    
    System.out.println("login actie");
    boolean expResult3 = false;
    String stringExpResult3 = String.valueOf(expResult3);  
    Boolean result3 = u.login("", "");
    String stringActualResults3 = String.valueOf(result3);  
    
    assertEquals(stringExpResult3, stringActualResults3);
    
    }

    
}
