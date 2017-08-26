package sample.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sample.component.RegisterModel;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleClientWebSocketHandler extends TextWebSocketHandler {
    private static final Gson gson = new GsonBuilder().create();
    private static final String NAME_RASP = "Rasp";
    private String namePeer;

    private WebSocketSession session;
    private boolean fwdBlocked;
    private boolean bwdBlocked;

//    SerialComm comm = new SerialComm();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {


        this.session = session;
        RegisterModel registerModel = new RegisterModel("register", NAME_RASP);

        Gson gson = new Gson();
        getSession().sendMessage(new TextMessage(gson.toJson(registerModel)));

//        comm.serial.addListener(new SerialDataEventListener() {
//            @Override
//            public void dataReceived(SerialDataEvent event) {

//                // NOTE! - It is extremely important to read the data received from the
//                // serial port.  If it does not get read from the receive buffer, the
//                // buffer will continue to grow and consume memory.
//
//                // print out the data received to the console
//                try {
//                    //comm.console.println("[HEX DATA]   " + event.getHexByteString());
//                    comm.console.println("[ASCII DATA] " + event.getAsciiString());


//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
        executeComand(jsonMessage);
    }

    private void executeComand(JsonObject jsonMessage) throws IOException{
        String message = jsonMessage.get("id").getAsString();
        System.out.println(jsonMessage.toString());

        ArrayList<String> comands = new ArrayList<>();
        comands.add("$js.cmd.fwd");
        comands.add("$js.cmd.bwd");
        comands.add("$js.cmd.lft");
        comands.add("$js.cmd.rgt");
        comands.add("$js.cmd.stp");

        if (!message.equals("connectionResponseWS") && !message.equals("registerResponseWS")) {
            String responseMsg = "received";
            this.namePeer = jsonMessage.get("from").getAsString();
            if(comands.contains(jsonMessage.get("message").getAsString())){
                System.out.println("$pi.cmd.fwd");
//                comm.serial.writeln("$pi.cmd.fwd");
            }

            JsonObject response = new JsonObject();
            response.addProperty("id", "response");
            response.addProperty("from", this.NAME_RASP);
            response.addProperty("to", this.namePeer);
            response.addProperty("message", responseMsg);
            session.sendMessage(new TextMessage(response.toString()));
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

    }

    public WebSocketSession getSession() {
        return session;
    }

    private void sendComand(String comand) throws IOException {
        JsonObject response = new JsonObject();
        response.addProperty("id", "command");
        response.addProperty("cmd", comand);
        session.sendMessage(new TextMessage(response.toString()));
    }
}