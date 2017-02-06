import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * Created by Kathe on 2/2/2017.
 */
public class ChatRoom {
    private ServerSocket serverSocket;
    private int port;
    private ArrayList<userThread> users;
    SimpleDateFormat date;
    private int serverId;

    public ChatRoom(int port){
        this.port=port;
        users=new ArrayList<userThread>();
    }

    public void run(){
        try{
            serverSocket=new ServerSocket(port);
            System.out.println("server socket created on port: "+port);
            while(true){
                Socket client=serverSocket.accept();
                //System.out.println("after accept");
                userThread curUser=new userThread(client);
                curUser.start();
                if(curUser!=null)
                    users.add(curUser);
                else{
                    System.out.println("cur user thread is null!");
                    System.exit(-1);
                }
               
                //System.out.println("user "+curUser.userName+" came to the room");
                
                

            }
        }
        catch(IOException e){
            System.out.println("Error in Creating Server Socket!");
        }

    }

    public synchronized void broadcast(String msg){
        for(userThread user:users){
            user.outs.println(msg);
        }
    }

    public synchronized void removeUser(int userId){
        Iterator<userThread> iter=users.iterator();
        while(iter.hasNext()){
            userThread nxtUser=iter.next();
            if(nxtUser.id==userId){
                iter.remove();
            }
        }
    }



    class userThread extends Thread {
        int id;
        String userName;
        Socket socket;
        BufferedReader ins;
        PrintWriter outs;
        PrintStream ps;
        public userThread(Socket sock){
            //System.out.println("creating thread");
            socket=sock;
            id=++serverId;
            try{
                ins=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outs= new PrintWriter(socket.getOutputStream(),true);
                ps=new PrintStream(socket.getOutputStream());
                
            }
            catch (IOException e){
                System.out.println(userName+" error creating stream");
            }
        }

        public void run(){
            try{
                //System.out.println("running thread");
                String username=ins.readLine();
                
                this.userName=username;
                System.out.println("creating user thread and read the name: "+username);
            }
            catch(IOException e){
                System.out.println("error reading user name");
            }
            
            
            broadcast("****"+this.userName+"came to the room!  ****");
            while(true){
                try{
                    String msg=ins.readLine();
                    //System.out.println("printmsg:"+ msg);
                    if(msg.equals("quit!")){
                        outs.println("bye!");
                        broadcast("******"+this.userName+" has left the room ********");
                        break;
                    }
                    else broadcast(this.userName+": "+msg);
                    //System.out.println("after broadcast");
                }
                catch (IOException e){
                    System.out.println(userName+" thread error reading from server");
                    break;
                }
            }
            removeUser(id);
            closeUserThread();
        }
        public void closeUserThread(){
            try{
                socket.close();
                ins.close();
                outs.close();
                ps.close();
            }
            catch (IOException e){
                System.out.println(userName +"thread error closing");
            }
        }



    }


    public static void main(String[] args){
        if(args.length!=1){
            System.out.println("usage: java ChatRoom [port]");
            System.exit(-1);
        }
        int port=Integer.parseInt(args[0]);
        ChatRoom server=new ChatRoom(port);
        server.run();
    }
}
