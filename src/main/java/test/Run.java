package test;

import api.RequestHelper;
import java.io.IOException;



public class Run extends RequestHelper {


    public static void main(String[] args) throws IOException {
        initClient();
        String response = get("https://reqres.in/api/users/2");
//        System.out.println(response);
    }
}
