package server;

import common.network.Request;
import common.network.Response;
import common.network.Serializer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpServer {

    private final int port;
    private final RequestHandler requestHandler;
    private static final int BUFFER_SIZE = 65535;

    public UdpServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP server is listening on port " + port);

            while (true) {
                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket requestPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(requestPacket);

                Response response;

                try {
                    byte[] requestBytes = new byte[requestPacket.getLength()];
                    System.arraycopy(
                            requestPacket.getData(),
                            requestPacket.getOffset(),
                            requestBytes,
                            0,
                            requestPacket.getLength()
                    );

                    Request request = (Request) Serializer.deserialize(requestBytes);
                    response = requestHandler.handle(request);

                    if (response == null) {
                        response = new Response(false, "Handler returned null response.");
                    }
                } catch (Exception e) {
                    response = new Response(false, "Server error: " + e.getMessage());
                }

                try {
                    byte[] responseBytes = Serializer.serialize(response);
                    DatagramPacket responsePacket = new DatagramPacket(
                            responseBytes,
                            responseBytes.length,
                            requestPacket.getAddress(),
                            requestPacket.getPort()
                    );
                    socket.send(responsePacket);
                } catch (Exception e) {
                    System.err.println("Failed to send response: " + e.getMessage());
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Could not start UDP server on port " + port, e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected server error: " + e.getMessage(), e);
        }
    }
}