package com.co.wno.etalk.client.component;

import org.springframework.stereotype.Component;

import javafx.scene.control.TextArea;

@Component
public class ReceivedTextAreaComponent extends TextArea {

    public ReceivedTextAreaComponent() {
        this.setEditable(false);
    }
}
