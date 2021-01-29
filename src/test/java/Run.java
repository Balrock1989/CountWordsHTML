import api.RequestHelper;
import org.testng.annotations.Test;

import java.io.IOException;


public class Run extends RequestHelper {


    @Test
    public void test() throws IOException {
        initProperties();
        String response = get("https://reqres.in/api/users/2");
        System.out.println(response);
    }
}
