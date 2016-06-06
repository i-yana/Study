package protocol.requests;

import protocol.messagetype.Message;
import protocol.messagetype.Request;
import protocol.info.RegisterINFO;

/**
 * Created by Yana on 03.12.15.
 */
public class RegisterMSG implements Request, Message {

    private final RegisterINFO registerINFO;

    public RegisterMSG(RegisterINFO registerINFO){
        this.registerINFO = registerINFO;
    }

    public String getLogin(){
        return registerINFO.getLogin();
    }

    public String getPassword(){
        return registerINFO.getPassword();
    }
}
