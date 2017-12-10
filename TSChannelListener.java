package Bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

/**
 * Created by Tom on 24.12.2016.
 */
public class TSChannelListener implements TS3Listener {
    private TS3Api ts3Api;
    private SveinBot sveinBot;
    public TSChannelListener(TS3Api ts3Api, SveinBot sveinBot){
        this.ts3Api = ts3Api;
        this.sveinBot = sveinBot;
    }

    public void onTextMessage(TextMessageEvent textMessageEvent) {

    }

    public void onClientJoin(ClientJoinEvent clientJoinEvent) {
        if (clientJoinEvent.getClientCountry().equalsIgnoreCase("Iraq")){
            ts3Api.banClient(clientJoinEvent.getClientId(), 999999999);
        }
    }

    public void onClientLeave(ClientLeaveEvent clientLeaveEvent) {
    }

    public void onServerEdit(ServerEditedEvent serverEditedEvent) {

    }

    public void onChannelEdit(ChannelEditedEvent channelEditedEvent) {

    }

    public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent channelDescriptionEditedEvent) {

    }

    public void onClientMoved(ClientMovedEvent clientMovedEvent) {
        Integer clientId = clientMovedEvent.getClientId();
        ClientInfo client = ts3Api.getClientInfo(clientId);
        if (client.getChannelId() == ts3Api.getChannelByNameExact("USA",true).getId()){
            if (sveinBot.botSettings.getMexico().contains(client.getUniqueIdentifier())){
                ts3Api.sendChannelMessage(ts3Api.getChannelByNameExact("USA", true).getId(),"Mexikaner erkannt. Abschiebung einleiten. Mauer wird um einen Meter erhöht. Neue Mauerhöhe: "+ sveinBot.botSettings.getMauerCounter()+" Meter");
                ts3Api.moveClient(client, ts3Api.getChannelByNameExact("Mexico", true));
                ts3Api.sendPrivateMessage(clientId, "Du bist ein Tacofresser. Der 45. Präsident der USA hat entschieden, dass du nicht auf direktem Weg einreisen darfst. Bitte wende dich an die zuständige Behörde!");
                ts3Api.moveClient(ts3Api.getClientByUId(sveinBot.getBotSettings().getBotUniqueId()).getId(), 102 /*Bothome*/);
                sveinBot.increaseMauer();
            };
        }
        else if (client.getChannelId() == ts3Api.getChannelByNameExact("Mexico", true).getId()){
            if (sveinBot.getBotSettings().getMexico().contains(client.getUniqueIdentifier())){}
            else {
                sveinBot.addToMexico(client);
            }
        }
        else if (client.getChannelId() == 154 /*Einwanderungsbehörde*/){
            ts3Api.sendPrivateMessage(clientId, "Der Antrag auf Einreise wird geprüft. Bitte kurz warten!");
            try {
                Thread.sleep(10000);
            }catch (Exception e){e.printStackTrace();}
            if (client.getChannelId() == 154 /*Einwanderungsbehörde*/) {
                ts3Api.sendPrivateMessage(clientId, "Dem Antrag auf Einreise wurde stattgegeben!");
                sveinBot.getBotSettings().getMexico().remove(client.getUniqueIdentifier());
            }
        }

    }

    public void onChannelCreate(ChannelCreateEvent channelCreateEvent) {

    }

    public void onChannelDeleted(ChannelDeletedEvent channelDeletedEvent) {

    }

    public void onChannelMoved(ChannelMovedEvent channelMovedEvent) {

    }

    public void onChannelPasswordChanged(ChannelPasswordChangedEvent channelPasswordChangedEvent) {

    }

    public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent privilegeKeyUsedEvent) {

    }
}
