package sample.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sample.component.RegisterModel;
import sample.serial.SerialComm;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleClientWebSocketHandler extends TextWebSocketHandler {
    private static final Gson gson = new GsonBuilder().create();
    private static final String NAME_RASP = "Rasp";
    private String namePeer;

    private WebSocketSession session;
    private String inputString;

    SerialComm comm = new SerialComm();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        RegisterModel registerModel = new RegisterModel("register", NAME_RASP);

        Gson gson = new Gson();
        getSession().sendMessage(new TextMessage(gson.toJson(registerModel)));

        comm.serial.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                try {
                    //comm.console.println("[HEX DATA]   " + event.getHexByteString());
                    //comm.console.println("[ASCII DATA] " + event.getAsciiString());
                    inputString = event.getAsciiString();
                    comm.console.println("ReceivedFromSerial -> " + inputString);
                    checkSerialMessage(inputString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refuseSerialMessage(String message) throws IOException {
        comm.console.println("SendToSerial -> $pi.rfs,msg{" + message + "}");
        comm.serial.writeln("$pi.rfs,msg{" + message + "}");
    }

    private void refusePeerMessage() throws IOException {
        comm.console.println("SendToSerial -> $pi.rfs,peer");
        comm.serial.writeln("$pi.rfs,peer");
    }

    private void checkSerialMessage(String message) throws IOException{
        if(inputString.substring(0,2).equals("$ad")){
            switch (inputString.substring(4, 6)) {
                case "cmd":
                    checkSerialCommand(message);
                    break;
                case "sen":
                    checkSensorMessage(message);
                    break;
                case "rfs":
                    checkRefuseMessage(message);
                    break;
                case "rsp":
                    break;
                default:
                    refuseSerialMessage(message);
                    break;
            }
        } else refusePeerMessage();
    }

    private void checkSerialCommand(String message) throws IOException {
        String command = message.substring(8);
        if(command.equals("blk.fwd") || command.equals("blk.bwd") || command.equals("rls.fwd") || command.equals("rls.bwd")) {
            forwardSerialCommand(command);
        } else refuseSerialMessage(message);
    }

    private void forwardSerialCommand(String command) throws IOException {
        comm.console.println("$pi.rsp," + command);
        comm.serial.writeln("$pi.rsp," + command);
        sendSocketCommand("$pi.cmd," + command);
    }

    private void forwardSocketCommand(String command) throws IOException {
        comm.console.println("$pi.cmd," + command);
        comm.serial.writeln("$pi.cmd," + command);
        sendSocketCommand("$pi.rsp," + command);
    }

    private void checkSensorMessage(String sensor) {
        // Escreve no log txt
        //comm.console.println("SensorLogInsert ->" + log);
    }

    private void checkRefuseMessage(String refuse) {
        // Escreve no log txt
        //comm.console.println("RefuseLogInsert ->" + log);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
        executeCommand(jsonMessage);
    }

    private void executeCommand(JsonObject jsonMessage) throws IOException{
        String message = jsonMessage.get("id").getAsString();
        System.out.println(jsonMessage.toString());

        ArrayList<String> commands = new ArrayList<>();
            commands.add("$js.cmd,fwd");
            commands.add("$js.cmd,bwd");
            commands.add("$js.cmd,lft");
            commands.add("$js.cmd,rgt");
            commands.add("$js.cmd.stp");

        ArrayList<String> responses = new ArrayList<>();
            commands.add("$js.rsp,blk.fwd");
            commands.add("$js.rsp,blk.bwd");

        if (message.equals("command")) {
            JsonObject response = new JsonObject();
            String responseMsg;
            String commandMsg = "$pi.cmd,";

            this.namePeer = jsonMessage.get("from").getAsString();

            if(commands.contains(jsonMessage.get("message").getAsString())) {
                responseMsg = "$pi.rsp," +jsonMessage.get("message").getAsString().substring(8);
                commandMsg += jsonMessage.get("message").getAsString().substring(8);
                forwardSocketCommand(commandMsg);
            }
            else {
                responseMsg = "$pi.rfs,msg{" + jsonMessage.get("message").getAsString() + "}";
            }
            response.addProperty("id", "response");
            response.addProperty("from", this.NAME_RASP);
            response.addProperty("to", this.namePeer);
            response.addProperty("message", responseMsg);
            session.sendMessage(new TextMessage(response.toString()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) { }

    public WebSocketSession getSession() {
        return session;
    }

    private void sendSocketCommand(String command) throws IOException {
        JsonObject response = new JsonObject();
        response.addProperty("id", "command");
        response.addProperty("message", command);
        session.sendMessage(new TextMessage(response.toString()));
    }
}