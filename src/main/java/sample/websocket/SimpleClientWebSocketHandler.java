package sample.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import sample.component.ReceivedTextAreaComponent;
import sample.component.RegisterModel;
import sample.serial.SerialComm;

import java.io.IOException;

public class SimpleClientWebSocketHandler extends TextWebSocketHandler {
    private static final Gson gson = new GsonBuilder().create();

    @Autowired
    private ReceivedTextAreaComponent receivedTextAreaComponent;

    private WebSocketSession session;

//    SerialComm comm = new SerialComm();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {


        this.session = session;
        RegisterModel registerModel = new RegisterModel("register", "Rasp");

        Gson gson = new Gson();
        getSession().sendMessage(new TextMessage(gson.toJson(registerModel)));

//        comm.serial.addListener(new SerialDataEventListener() {
//            @Override
//            public void dataReceived(SerialDataEvent event) {
//
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

        executaComando(message.getPayload());

//        receivedTextAreaComponent.appendText(message.getPayload() + System.lineSeparator());
    }

    private void executaComando(String message) throws IOException{
        System.out.println(message);

        switch (message) {
            case "$js.cmd.fwd" :
                System.out.println("$pi.cmd.fwd");
//                comm.serial.writeln("$pi.cmd.fwd");
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

    }

    public WebSocketSession getSession() {
        return session;
    }

}