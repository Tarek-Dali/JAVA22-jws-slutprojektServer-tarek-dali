import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("Server är nu Redo");

        //Init stuff
        Socket socket;
        InputStreamReader inputStreamReader;
        OutputStreamWriter outputStreamWriter;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        ServerSocket serverSocket;

        try{
            //Kontrollera att Socket nummer är ledig. Avbryt om socket är upptagen
            serverSocket = new ServerSocket(4321);
        }
        catch (IOException e){
            System.out.println(e);
            return;
        }

        try {
            //Väntar på specifik socket efter trafik
            socket = serverSocket.accept();

            //Initiera
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            while(true){
                //Hämta klientens meddelande och skicka den till openUpData()
                String message = bufferedReader.readLine();
                String returnData = openUpData(message);


                //System.out.println("Client: " + message);
                System.out.println("Message Recieved and sent back");

                //Skicka acknowledgement svar tillbaka
                bufferedWriter.write(returnData);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                //Avsluta om QUIT
                if(message.equalsIgnoreCase("quit")){
                    break;
                }
            }
            //Stäng kopplingar
            socket.close();
            inputStreamReader.close();
            outputStreamWriter.close();
            bufferedReader.close();
            bufferedWriter.close();
        }
        catch (IOException e){
            System.out.println(e);
        }

        catch (ParseException e) {
            System.out.println(e);
        }
        finally {
            System.out.println("Server Avslutas");
        }

    }

    static String openUpData(String message) throws ParseException, IOException {
        //Steg 1. Bygg upp JSON Object basserat på inkommande string
        JSONParser parser = new JSONParser();
        JSONObject jsonOb = (JSONObject) parser.parse(message);

        //Steg 2. Läs av URL och HTTP-metod för att veta vad klienten vill
        String url = jsonOb.get("httpURL").toString();
        String method = jsonOb.get("httpMethod").toString();;

        //Steg 2.5. Dela upp URL med .split() metod
        String[] urls = url.split("/");

        for(int i = 0; i < urls.length; i++){
            System.out.println(urls[i]);
        }

        //Steg 3. Använd en SwitchCase för att kolla vilken data som skall användas
        switch (urls[0]) {
            case "persons": {
                if (method.equals("get")){
                    //Vill hämta data om personer
                    //TODO lägg till logik om det är specifik person som skall hämtas

                    //Skapa JSONReturn objektet
                    JSONObject jsonReturn = new JSONObject();

                    //Hämta data från JSON fil
                    jsonReturn.put("data", parser.parse(new FileReader("data/data.json")).toString());

                    //Inkluderat HTTP status code
                    jsonReturn.put("httpStatusCode", 200);

                    //Return
                    return jsonReturn.toJSONString();
                }
                break;
            }
        }


        return "message Recieved";
    }
}