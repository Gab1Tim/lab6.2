package server;

import common.network.Request;
import common.network.Response;
import common.network.Serializer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpServer {
    private final int port;
    private final int bufferSize;
    private final RequestHandler requestHandler;

    public UdpServer(int port, int bufferSize, RequestHandler requestHandler) {
        this.port = port;
        this.bufferSize = bufferSize;
        this.requestHandler = requestHandler;
    }

    public void start() {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));

            System.out.println("UDP server is listening on port " + port);

            ByteBuffer receiveBuffer = ByteBuffer.allocate(bufferSize);

            while (true) {
                receiveBuffer.clear();

                InetSocketAddress clientAddress =
                        (InetSocketAddress) channel.receive(receiveBuffer);

                if (clientAddress == null) {
                    Thread.sleep(10);
                    continue;
                }

                Response response;

                try {
                    receiveBuffer.flip();
                    byte[] requestBytes = new byte[receiveBuffer.remaining()];
                    receiveBuffer.get(requestBytes);

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
                    ByteBuffer sendBuffer = ByteBuffer.wrap(responseBytes);
                    channel.send(sendBuffer, clientAddress);
                } catch (Exception e) {
                    System.err.println("Failed to send response: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not start UDP server on port " + port, e);
        }
    }
}