package Bot;

import Commands.BotCommand;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Tom on 24.12.2016.
 */
public class TSChatListener implements TS3Listener {
    private TS3Api api;
    private Map<String, BotCommand> botCommands;

    public TSChatListener(TS3Api api, Map<String, BotCommand> botCommands){
        this.api = api;
        this.botCommands = botCommands;
    }

    public void onTextMessage(TextMessageEvent textMessageEvent) {
        Client absender;
        if (textMessageEvent.getTargetMode() == TextMessageTargetMode.CLIENT) {
            absender = api.getClientByUId(textMessageEvent.getInvokerUniqueId());
            if (CheckForCommand(textMessageEvent, botCommands) == true) {
                botCommands.get(textMessageEvent.getMessage()).execute(BotCommand.sveinBot, api, textMessageEvent);

            }
        }
    }

    private boolean CheckForCommand(TextMessageEvent textMessageEvent, Map<String, BotCommand> botCommands) {
        Boolean returnValue = false;
        Iterator it = botCommands.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry command = (Map.Entry) it.next();
            if (command.getKey().equals(textMessageEvent.getMessage())){
                returnValue = true;
            }
            else
                {};
        }
        return returnValue;
    }

    public void onClientJoin(ClientJoinEvent clientJoinEvent) {

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
