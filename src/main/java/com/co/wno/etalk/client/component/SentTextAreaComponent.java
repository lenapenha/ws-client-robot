package com.co.wno.etalk.client.component;

import org.springframework.stereotype.Component;

import javafx.scene.control.TextArea;

@Component
public class SentTextAreaComponent extends TextArea {

    public SentTextAreaComponent() {
        this.setEditable(false);
    }
}
