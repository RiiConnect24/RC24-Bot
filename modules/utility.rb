module SerieBot
  module Utility
    extend Discordrb::Commands::CommandContainer

    command(:avatar, description: 'Displays the avatar of a user.') do |event, *mention|
      event.channel.start_typing # Let people know the bot is working on something.
        if mention.nil?
          user = event.message.author
        elsif event.message.mentions[0]
          user = event.server.member(event.message.mentions[0])
        else
          event << "❌ Mention a valid user!"
          next
        end
        avatar_path = Helper.download_avatar(user, "tmp")
        event.channel.send_file File.new([avatar_path].sample)
    end

    command(:info, description: "Displays info about a user.") do |event, *mention|
      event.channel.start_typing
      if event.channel.private? # ignore PMs
        event << "❌ This command can only be used in a server."
        next
      end

      if event.message.mentions[0]
        user = event.message.mentions[0]
        if user.game.nil?
          playing = "[N/A]"
        else
          playing = user.game
        end
        member = user.on(event.server)
        if member.nickname.nil?
          nick = "[N/A]" #
        else
          nick = member.nickname
        end
        event << "👥  Infomation about **#{member.display_name}**"
        event << "-ID: **#{user.id}**"
        event << "-Username: `#{user.distinct}`"
        event << "-Nickname: **#{nick}**"
        event << "-Status: **#{user.status}**"
        event << "-Playing: **#{playing}**"
        event << "-Account created: **#{user.creation_time.getutc.asctime}** UTC"
        event << "-Joined server at: **#{member.joined_at.getutc.asctime}** UTC"
      end
    end

    # Requires manage_roles permission since that's what we're doing.
    command(:config, description: 'Change settings per-server for the bot.', required_permissions: [:manage_roles]) do |event, option, *args|
      # TODO: more configuration options?
      if option == 'help'
        help = "__Help for #{Config.prefix}config:__\n\n"
        help += "_setrole_: Set the role to be used in place of Developers and Moderators. See #{Config.prefix}help for information about what these roles provide.\n"
        help += "Usage: #{Config.prefix}config setrole <mod, dev> <new role name>"
        event.respond(help)
      elsif option == 'setrole'
        # Make sure that there are 2+ options here, so we don't waste our time.
        if args.length < 2
          event.respond('❌ Make sure to follow the syntax correctly!')
          break
        end

        role_type = args[0]

        # Make sure that the short ID is valid
        is_valid = case role_type
                     when 'dev', 'bot', 'mod'
                       true
                     else
                       false
                   end
        unless is_valid
          event.respond('❌ Make sure to type in a valid role type!')
          break
        end

        # All appears to be good to go. Remove the type and go.
        args.delete_at(0)
        role_name = args.join(' ')
        puts "About to change #{role_type} to #{role_name}"

        # Find given role by name. Or, at least attempt to.
        begin
          new_role_id = Helper.role_from_name(event.server, role_name).id
          Helper.save_role_id?(role_type, event.server.id, new_role_id)
          event.respond('✅ Successfully set!')
        rescue NoMethodError
          event.respond("❌ I wasn't able to find that role on this server! No changes have been made to your server's config.")
        end
      else
        event << '❌ You need to have an option! Valid options are:'
        event << '`help`, `setrole`'
      end
    end
  end
end
