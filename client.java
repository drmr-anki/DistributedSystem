/**
 * Created by Kathe on 2/5/2017.
 */
import java.io.*;
import java.net.*;


public class client {
    Socket socket;
    String username;
    String serverAddr;
    BufferedReader ins;
    PrintStream outs;
    int port;
    boolean connected;

    // initialize client object, connecting to server with port, send client username to the server
    public client(int port, String server, String name){
        this.port=port;
        this.username=name;
        serverAddr=server;
        
        //}
        //catch(IOException e){
        //    System.out.println(username+" error writing username");
        //}
    }

    public void start(){
        try{
            socket=new Socket(serverAddr,port);
            //System.out.println(name+"has connected to server");
            System.out.println("type msg here. Type 'quit' to leave the room");
            this.connected=true;

        }
        catch (IOException e){
            System.out.println("error connecting server");
        }

        try{
            ins=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outs= new PrintStream(socket.getOutputStream());
        }
        catch (IOException e){
            System.out.println(this.username+" error creating stream");
        }

        new clientThread(connected).start();
        //try{
            //System.out.println("writing username!");
            outs.println(username+'\n');
        
    }


    class clientThread extends Thread {
        boolean connect;
        public clientThread(boolean connected){
            this.connect=connected;
        }
        
        public void run(){
            while(connect){
                try{
                    String serverMsg=ins.readLine();
                    if(serverMsg.equals("bye!")) break;
                    System.out.println(serverMsg);
                }
                catch (IOException e){
                    //System.out.println(username+" error reading from server");
                    break;
                }

            }
        }
    }


    public void sendMsg(String msg){
        //try{
            outs.println(msg);
        //}
        // catch (IOException e){
        //     System.out.println(username+" error sending msg to server");
        // }
    }

    public void disconnect(){
        try{
            if(socket!=null)  socket.close();
        }
        catch (IOException e){
            System.out.println(username +" error closing socket");
        }
        try{
            if(ins!=null) ins.close();
        }
        catch (IOException e){
            System.out.println(username +" error closing buffer reader");
        }
        //try{
            if(outs!=null) outs.close();
        //}
        // catch (IOException e){
        //     System.out.println(username +" error closing outputStream writer");
        // }

    }

    /* argument   client port username*/
    public static void main(String[] args){
        if(args.length!=3){
            System.out.println("Usage: client hostAddr port username ");
            System.exit(-1);
        }
        int port=Integer.parseInt(args[1]);
        String server=args[0];
        String username=args[2];
        client user=new client(port,server, username);
        user.start();
        //new clientThread().run();

        System.out.println("enter here:");
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        while(true){
            try {
                String line = br.readLine();
                if (!line.equals("quit!")) {
                    user.sendMsg(line);
                } else {
                    user.sendMsg("quit!");
                    user.connected=false;
                    user.disconnect();
                    break;
                }
            }
            catch (IOException e){
                System.out.println("error reading std in");
                break;
            }
        }
        user.disconnect();

    }

}
