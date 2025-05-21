package com.example.chatbotapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.*;

public class Chat extends AppCompatActivity {

    TextView chat;
    EditText user;
    Button sendBtn;
    TextView chatDis;
    String backendURL = "http://10.0.2.2:5000/chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat = findViewById(R.id.chatTitle);
        user = findViewById(R.id.userInput);
        sendBtn = findViewById(R.id.sendButton);
        chatDis = findViewById(R.id.chatDisplay);

        String username = getIntent().getStringExtra("username");
        chat.setText("Chatting as: " + username);

        sendBtn.setOnClickListener(view -> {
            String message = user.getText().toString().trim();
            if (!message.isEmpty()) {
                chatDis.append("You: " + message + "\n");
                user.setText("");
                sendMessageToBot(message);
            }
        });
    }

    private void sendMessageToBot(String message) {
        new Thread(() -> {
            try {
                URL url = new URL(backendURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "userMessage=" + URLEncoder.encode(message, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                br.close();

                runOnUiThread(() -> chatDis.append("Bot: " + response.toString().trim() + "\n"));

            } catch (Exception e) {
                runOnUiThread(() -> chatDis.append("Error: " + e.getMessage() + "\n"));
            }
        }).start();
}
}
