module SerieBot
    module Help
        extend Discordrb::Commands::CommandContainer
        extend Discordrb::EventContainer

        command(:help) do |event|
            help = ''
            help << "**__Using the bot__**\n"
            help << "\n"
            help << "**Adding codes:**\n"
            help << "`#{Config.prefix}code add wii | Wii Name | 1234-5678-9012-3456` (You can add multiple Wiis with different names)\n"
            help << "`#{Config.prefix}code add game | Game Name | 1234-5678-9012`\n"
            help << "\n"
            help << '**Editing codes**\n'
            help << "`#{Config.prefix}code edit wii | Wii Name | 1234-5678-9012-3456`\n"
            help << "`#{Config.prefix}code edit game | Game Name | 1234-5678-9012`\n"
            help << "\n"
            help << "**Removing codes**\n"
            help << "`#{Config.prefix}code remove wii | Wii Name`\n"
            help << "`#{Config.prefix}code remove game | Game Name`\n"
            help << "\n"
            help << "**Looking up codes**\n"
            help << "`#{Config.prefix}code lookup @user`\n"
            help << "\n"
            help << "**Adding a user's Wii**\n"
            help << "`#{Config.prefix}add @user`\n"
            help << "This will send you their codes, and then send them your Wii/game codes.\n"
            help << "\n"
            help << "**Specific commands**\n"
            help << "`#{Config.prefix}wads` may or may not DM you some WADs.\n"
            help << "`#{Config.prefix}help` or `#{Config.prefix}code help` will send you this help message.\n"
            help << "`#{Config.prefix}gametdb <platform> <title id>` will provide a GameTDB wiki page with the specified Title ID. Valid platforms are Wii, WiiU, PS3, 3DS, and DS.\n"
            help << "`#{Config.prefix}error <error code>` will provide you information about the specified error code from Wiimmfi.\n"
            help << "`#{Config.prefix}instructions` will reply with some setup instructions for RiiConnect24.\n"
            help << "`#{Config.prefix}dns` will reply with the DNS settings for RiiConnect24."
            event.user.pm(help)
            if Helper.isadmin?(event.user) || Helper.ismoderator?(event, event.user) || Helper.isdeveloper?(event, event.user)
                modhelp = ''
                modhelp << "\n\n**__Mod commands__**\n"
                modhelp << "As this RiiConnect24 bot is a stripped down version of Yuu-Chan/Serie-Bot, you have a limited option of some moderation commands.\n"
                modhelp << "\n"
                modhelp << "**General commands**\n"
                modhelp << "`#{Config.prefix}ignore @user`/`#{Config.prefix}unignore @user` will respectively ignore and unignore the specified user.\n"
                modhelp << "`#{Config.prefix}about` will tell you information about the bot.\n"
                event.user.pm(modhelp)
            end
            if Helper.isadmin?(event.user) || Helper.isdeveloper?(event, event.user)
                devhelp = ''
                devhelp << "\n\n**Developers:**\n"
                devhelp << "`#{Config.prefix}setavatar <file/URL>` will change the avatar to the provided URL/image.\n"
                devhelp << "`#{Config.prefix}status <status>` changes the status of the bot to one of the options of idle, dnd, invisible or online.\n"
                devhelp << "`#{Config.prefix}dump <id>` will dump all messages from the channel represented by the specified ID.\n"
                devhelp << "\n"
                devhelp << "**Bot-specific commands**\n"
                devhelp << "`#{Config.prefix}wipecodes @user` will wipe all codes the specified user has added.\n"
                devhelp << "`#{Config.prefix}save` will save the current state of codes to data/codes.yml.\n"
                event.user.pm(devhelp)
            end
            if Helper.isadmin?(event.user)
                adminhelp = "\n"
                adminhelp << "\n\n**Admins**\n"
                adminhelp << "`#{Config.prefix}eval <code>` will evaluate the specified Ruby string. !!! USE WITH CARE !!!\n"
                adminhelp << "`#{Config.prefix}bash <command>` will run the specified command in a bash shell. As before, !!! USE WITH CARE !!!\n"
                adminhelp << "`#{Config.prefix}shutdown` will do exactly as the name suggests to the bot.\n"
                event.user.pm(adminhelp)
          end
        end
end
end
