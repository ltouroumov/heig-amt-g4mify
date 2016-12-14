package ch.heig.amt.g4mify.Utils;

import ch.heig.amt.g4mify.model.Domain;
import com.google.gson.Gson;

import java.util.HashMap;

import static ch.heig.amt.g4mify.Utils.HttpTestRequest.isError;
import static org.junit.Assert.assertEquals;

/**
 * Created by Le Poulet Suisse on 14.12.2016.
 */
public class UtilsApiTest {
    public static final int BEFORE = 0;
    public static final int BEFORE_CLASS = 1;
    public static final int AFTER = 0;
    public static final int AFTER_CLASS = 1;

    public static Domain baseDomainInit(int type){
        if(type == BEFORE){
            System.out.println("- METHOD INITIALISATION");
        }else if(type == BEFORE_CLASS){
            System.out.println("- CLASS INITIALISATION");
        }else{
            System.err.println("Error in the type specified in init");
        }
        HttpTestRequest request = new HttpTestRequest();
        Gson gson = new Gson();
        TestResponse response = request.test("/register", "{\"name\": \"TestDomain\"}", null, "POST");
        if(isError(response)) return null;
        Domain testDomain = gson.fromJson(response.getBody(), Domain.class);
        System.out.println("Name: " + testDomain.getName() + " // Id: " + testDomain.getId() + " // Key: " + testDomain.getKey());
        assertEquals(201, response.getStatusCode());
        System.out.println("");
        return testDomain;
    }

    public static void baseDomainPostExec(Domain testDomain, int type){
        if(type == AFTER){
            System.out.println("- METHOD POST_EXECUTION");
        }else if(type == AFTER_CLASS){
            System.out.println("- CLASS POST_EXECUTION");
        }else{
            System.err.println("Error in the type specified in Post Exec!");
        }
        HttpTestRequest request = new HttpTestRequest();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("identity", testDomain.getId() + ":" + testDomain.getKey());
        TestResponse response = request.test("/api/domain", null, headers, "DELETE");
        if(isError(response)) return;
        if(response.getStatusCode() == 200){
            System.out.println("Correctly deleted domain " + testDomain.getId());
        }
        System.out.println("\n");
        assertEquals(200, response.getStatusCode());
    }
}
