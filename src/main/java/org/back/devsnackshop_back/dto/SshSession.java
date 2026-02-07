package org.back.devsnackshop_back.dto;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.io.OutputStream;
@AllArgsConstructor
public class SshSession {

    Session session;
    Channel channel;
    public OutputStream outputStream;
    InputStream inputStream;


    public void close(){
        try{
            if(channel!=null) channel.disconnect();
            if(session!=null) session.disconnect();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
