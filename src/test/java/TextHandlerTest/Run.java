package TextHandlerTest;

import api.RequestHelper;
import handlers.TextHandler;
import org.testng.annotations.Test;

import java.io.IOException;


public class Run extends RequestHelper {


    @Test(description = "Позитивный кейс")
    public void test1() throws IOException {
        TextHandler textHandler = new TextHandler("simbirsoft.com/");
        textHandler.findUniqueWord();
    }

    @Test(description = "URL не указан")
    public void test2() throws IOException {
        TextHandler textHandler = new TextHandler("");
        textHandler.findUniqueWord();
    }

    @Test(description = "URL возвращает пустую строку")
    public void test3() throws IOException {
        TextHandler textHandler = new TextHandler("https://www.simbirsoft.com/");
        textHandler.findUniqueWord();
    }
}
