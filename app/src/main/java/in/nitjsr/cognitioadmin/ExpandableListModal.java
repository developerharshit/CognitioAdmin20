package in.nitjsr.cognitioadmin;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ExpandableListModal {
    private String Header, Description,Name,Email;;

    public ExpandableListModal(String question, String answer, String name, String email){
        this.Description =answer;
        this.Header =question;
        this.Name=name;
        this.Email=email;
    }
    ExpandableListModal(){

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Header", Header);
        result.put("Description", Description);
        result.put("Name",Name);
        result.put("Email",Email);
        return result;
    }

    public String getHeader() {
        return Header;
    }

    public String getDescription() {
        return Description;
    }


}
