package org.example;
import org.java_websocket.server.WebSocketServer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class Main extends WebSocketServer {

    public Main(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Mở kết nối WebSocket với: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Kết nối bị đóng với: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String logName = "Application";
        String source = "HistoryWeb";
        String eventMessage = message;
        String eventType = "Information";
        writeEventLog(logName, source, eventMessage, eventType);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server đã bắt đầu.");
    }

    public void writeEventLog(String logName, String source, String eventMessage, String eventType) {
        String command = String.format("powershell.exe Write-EventLog -LogName %s -Source %s -EntryType %s -EventId 1000 -Message \"%s\"",
                logName, source, eventType, eventMessage);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.inheritIO();

            Process process = processBuilder.start();
            process.waitFor();

            System.out.println("Đã ghi sự kiện vào Event Log.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main server = new Main(5000);
        server.start();
        System.out.println("WebSocket server đang chạy trên cổng 5000...");
    }
}

