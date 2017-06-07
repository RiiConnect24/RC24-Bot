module SerieBot
  module Help
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer

    command(:help) do |event|
      help = ''
      help << Helper.get_help
      help << "\n\n"
      help << "**Specific commands**\n"
      help << "`#{Config.prefix}wads` may or may not DM you some WADs.\n"
      help << "`#{Config.prefix}help` or `#{Config.prefix}code help` will send you this help message.\n"
      help << "`#{Config.prefix}gametdb <platform> <title id>` will provide a GameTDB wiki page with the specified Title ID. Valid platforms are Wii, WiiU, PS3, 3DS, and DS.\n"
      help << "`#{Config.prefix}error <error code>` will provide you information about the specified error code from Wiimmfi.\n"
      help << "`#{Config.prefix}instructions` will reply with some setup instructions for RiiConnect24.\n"
      help << "`#{Config.prefix}dns` will reply with the DNS settings for RiiConnect24. \n"
      help << "`#{Config.prefix}about` will tell you information about the bot.\n"
      help << "Need to patch your `nwc24msg.cfg` file? Just DM me the file, and I'll patch it for you."
      extra_help = ''
      if Helper.is_bot_owner?(event.user) || Helper.is_moderator?(event) || Helper.is_developer?(event)
        extra_help << "\n\n**__Mod commands__**\n"
        extra_help << "As this RiiConnect24 bot is a stripped down version of Yuu-Chan/Serie-Bot, you have a limited option of some moderation commands.\n"
        extra_help << "\n"
        extra_help << "**General commands**\n"
        extra_help << "`#{Config.prefix}ignore @user`/`#{Config.prefix}unignore @user` will respectively ignore and unignore the specified user.\n"
      end
      if Helper.is_bot_owner?(event.user) || Helper.is_bot_helper?(event) || Helper.is_developer?(event)
        extra_help << "\n\n**Developers:**\n"
        extra_help << "`#{Config.prefix}setavatar <file/URL>` will change the avatar to the provided URL/image.\n"
        extra_help << "`#{Config.prefix}status <status>` changes the status of the bot to one of the options of idle, dnd, invisible or online.\n"
        extra_help << "`#{Config.prefix}dump <id>` will dump all messages from the channel represented by the specified ID.\n"
        extra_help << "`#{Config.prefix}clear <num>` will clear <num> messages from chat.\n"
        extra_help << "`#{Config.prefix}kick @user` will kick @user from the server.\n"
        extra_help << "`#{Config.prefix}ban @user` will ban @user from the server.\n"
        extra_help << "`#{Config.prefix}lockdown <minutes>` will lockdown the channel. If specified, the channel will unlock after <minutes> or not at all.\n"
        extra_help << "`#{Config.prefix}unlockdown` will remove the lockdown from the channel.\n"
      end
      if Helper.is_bot_owner?(event.user) || Helper.is_bot_helper?(event)
        extra_help << "\n\n**Admins**\n"
        extra_help << "`#{Config.prefix}eval <code>` will evaluate the specified Ruby string. !!! USE WITH CARE !!!\n"
        extra_help << "`#{Config.prefix}bash <command>` will run the specified command in a bash shell. As before, !!! USE WITH CARE !!!\n"
        extra_help << "`#{Config.prefix}shutdown` will do exactly as the name suggests to the bot.\n"
      end
      begin
        event.user.pm(help)
        if Helper.is_bot_owner?(event.user) || Helper.is_moderator?(event) || Helper.is_developer?(event)
          event.user.pm(extra_help)
        end
        event.respond('Check your DMs!')
      rescue Discordrb::Errors::NoPermission
        event.respond("❌ Sorry, but it looks like you're blocking DMs.")
      end
    end
  end
end
