package com.cosmos.app.controller;

import com.cosmos.protocol.EchoProtocol.EchoPb;
import com.cosmos.protocol.user.UserServicePb;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.junit.Test;

import java.net.Socket;

/**
 * Test Case
 *
 * @author David
 */
public class EchoControllerTest {

    @Test
    public void testLogin() {
        long start = System.currentTimeMillis();
        try {
            Socket socket = new Socket("localhost", 20303);
            EchoPb.Builder builder = EchoPb.newBuilder();

            builder.setInterfaceVersion(EchoPb.InterfaceVersion.V30);
            builder.setSystemType(EchoPb.SystemType.ANDROID);

            UserServicePb.UserService.Builder userService = UserServicePb.UserService.newBuilder();

            UserServicePb.LoginRequest.Builder loginRequest = UserServicePb.LoginRequest.newBuilder();
            loginRequest.setAccount("david");
            loginRequest.setPassword("password");
            userService.setLoginRequest(loginRequest);

            builder.setUserService(userService.build());

            byte[] dd = builder.build().toByteArray();

            System.out.println("-------------- client request message ---------------");
            System.out.println(EchoPb.parseFrom(dd).toString());

            CodedOutputStream codeOut = CodedOutputStream.newInstance(socket.getOutputStream());
            codeOut.writeRawVarint32(dd.length);
            codeOut.writeRawBytes(dd);
            codeOut.flush();

            CodedInputStream codeInput = CodedInputStream.newInstance(socket.getInputStream());

            int length = codeInput.readRawVarint32();
            System.out.println("length = " + length);

            byte[] buffer = codeInput.readRawBytes(length);
            EchoPb baseModel = EchoPb.parseFrom(buffer);

            System.out.println("-------------- server response message ---------------");
            System.out.println(baseModel.toString());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("time........." + (System.currentTimeMillis() - start));
    }
}
