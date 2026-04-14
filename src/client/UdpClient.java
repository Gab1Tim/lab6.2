package client;

import common.network.Request;
import common.network.Response;
import common.network.Serializer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpClient {

    private final String host;
    private final int port;
    private static final int BUFFER_SIZE = 65535;
    private static final int TIMEOUT_MILLIS = 3000;

    public UdpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response sendAndReceive(Request request) {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);

            InetSocketAddress serverAddress = new InetSocketAddress(host, port);

            byte[] requestBytes = Serializer.serialize(request);
            channel.send(ByteBuffer.wrap(requestBytes), serverAddress);

            ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start < TIMEOUT_MILLIS) {
                receiveBuffer.clear();
                InetSocketAddress sender = (InetSocketAddress) channel.receive(receiveBuffer);

                if (sender != null) {
                    receiveBuffer.flip();
                    byte[] responseBytes = new byte[receiveBuffer.remaining()];
                    receiveBuffer.get(responseBytes);
                    return (Response) Serializer.deserialize(responseBytes);
                }

                Thread.sleep(10);
            }

            return new Response(false, "Server did not respond in time.");
        } catch (Exception e) {
            return new Response(false, "Client error: " + e.getMessage());
        }
    }
}