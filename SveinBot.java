package Bot;

import Commands.*;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Tom on 24.12.2016.
 */
public class SveinBot {
    public static BotSettings botSettings;
    Map<String, BotCommand> botCommands = new HashMap<String, BotCommand>();
    final static TS3Query query = CreateConfigAndConnect();
    final static TS3Api api = query.getApi();
    public Integer mauerCounter = 11;
    public Integer Modus = 0;

    public void run() {
        InitialSettingsJustForTest(); //Nur zum testen. Wird am Ende auskommentiert.
        botSettings = LoadBotSettings(); //Load Bot Settings
        BotStartup();

        //ShowAllRegisteredUsers();
        Client myMaster = api.getClientByUId(botSettings.getSveinUId());
        //GiveMyMasterAdminQuery(api, myMaster);
        //AddMyMasterToAdmins(api, myMaster);
        //LoadRegisteredUsers(registeredUsers);
        //OutputEveryRegisteredUser(registeredUsers);
        //showAllCommands(botCommands, api);
        //SaveBotSettings(botSettings);

    }

    private void BotStartup() {
        LoginBotToServer(api);
        api.registerAllEvents();
        CommandsAnlegen(botCommands);
        api.addTS3Listeners(new TSChatListener(api, botCommands));
        api.addTS3Listeners(new TSChannelListener(api, this));
    }

    private void InitialSettingsJustForTest() {
        BotSettings botsettings = new BotSettings();
        XMLSerializer ser = new XMLSerializer();
        botsettings.setBotname("Donald J. Trump");
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("settings.xml");
            ObjectOutputStream o = new ObjectOutputStream(fileOut);
            o.writeObject(botsettings);
        }
        catch (IOException e){e.printStackTrace();}finally {
            try {
                fileOut.close();
            }catch (Exception e){e.printStackTrace();}
        };
    }

    private BotSettings LoadBotSettings() {
        InputStream fis = null;
        BotSettings botSettings = null;
        try {
            fis = new FileInputStream("settings.xml");
            ObjectInputStream o = new ObjectInputStream(fis);
            botSettings = (BotSettings) o.readObject();
        }catch (IOException e){e.printStackTrace();} catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return botSettings;
    }

    private void SaveBotSettings(BotSettings botSettings) {
        XMLSerializer ser = new XMLSerializer();
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("settings.xml");
            ObjectOutputStream o = new ObjectOutputStream(fileOut);
            o.writeObject(botSettings);
        }
        catch (IOException e){e.printStackTrace();}finally {
            try {
                fileOut.close();
            }catch (Exception e){e.printStackTrace();}
        };
    }

    private static void showAllCommands(Map<String, BotCommand> botCommands, TS3Api api) {
        Iterator it = botCommands.entrySet().iterator();
        api.sendChannelMessage("Alle Commands auf einen Blick:");
        api.sendChannelMessage("--------------------------------");
        while (it.hasNext()){
            Map.Entry command = (Map.Entry) it.next();
            api.sendChannelMessage(command.getKey().toString());
        }
        api.sendChannelMessage("--------------------------------");
    }

    private void CommandsAnlegen(Map<String, BotCommand> botCommands) {
        botCommands.put("!register", new RegisterCommand(this){});

        botCommands.put("!exit", new ExitCommand(this){});

        botCommands.put("!moveall", new MoveAllClientsCommand(this){});

        botCommands.put("!mauer", new StopMauer(this){});
    }

    private static void AddMyMasterToAdmins(TS3Api api, Client myMaster) {
        api.sendPrivateMessage(myMaster.getId(), "Sie wurden erfolgreich als Admin dieses Bots registriert!");
    }

    private static void GiveMyMasterAdminQuery(TS3Api api, Client myMaster) {
        for (ServerGroup group : api.getServerGroups()) {
            if (group.getName().contains("Query") && group.getName().contains("Admin")){
                api.addClientToServerGroup(group.getId(), myMaster.getDatabaseId());
                api.sendChannelMessage("Group Added!");
            }
        }

    }

    private static void LoginBotToServer(TS3Api api) {
        api.login(botSettings.getUsername(), botSettings.getPassword());
        api.selectVirtualServerById(botSettings.getServerId());
        api.setNickname(botSettings.getBotname());
    }

    private static TS3Query CreateConfigAndConnect() {
        final TS3Config config = new TS3Config();
        config.setHost("localhost");
        config.setDebugLevel(Level.ALL);
        config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
        config.setConnectionHandler(new ConnectionHandler() {
            public void onConnect(TS3Query ts3Query) {
            }

            public void onDisconnect(TS3Query ts3Query) {
                api.sendChannelMessage("Bot has disconnected");
            }
        }
        );

        final TS3Query query = new TS3Query(config);
        query.connect();
        return query;
    }

    private static void ShowAllRegisteredUsers(){
        api.sendChannelMessage("Alle registrierten User");
        for (String client:botSettings.getRegisteredUsers()
             ) {
            api.sendChannelMessage(client);
        }
    }


    public static void addToRegisteredUsers(ClientInfo clientByUId) {
        botSettings.getRegisteredUsers().add(clientByUId.getUniqueIdentifier());
    }

    public void disconnectQuery() {
        //SaveRegisteredUsers(registeredUsers);
        query.exit();
        SaveBotSettings(botSettings);
    }

    public void addToMexico(ClientInfo client) {
        botSettings.getMexico().add(client.getUniqueIdentifier());
    }

    public void setMauerCounter(Integer mauerCounter) {
        this.mauerCounter = mauerCounter;
    }

    public void increaseMauer() {
        mauerCounter = mauerCounter +1;
        botSettings.setMauerCounter(mauerCounter);
    }
    //Getter and Setter

    public Integer getMauerCounter() {
        return mauerCounter;
    }

    public static BotSettings getBotSettings() {
        return botSettings;
    }

    public static void setBotSettings(BotSettings botSettings) {
        SveinBot.botSettings = botSettings;
    }

    public Integer getModus() {
        return Modus;
    }

    public void setModus(Integer modus) {
        Modus = modus;
    }
}
