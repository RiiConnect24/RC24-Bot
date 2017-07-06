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
          event << '❌ Mention a valid user!'
          next
        end
        avatar_path = Helper.download_avatar(user, 'tmp')
        event.channel.send_file File.new([avatar_path].sample)
    end

    command(:ping) do |event|
      return_message = event.respond('Ping!')
      ping = (return_message.id - event.message.id) >> 22
      return_message.edit("Pong! - #{ping}ms")
    end

    command(:patch) do |event|
      begin
        event.user.pm("Just drag and drop your `nwc24msg.cfg` here, and I'll patch it and upload it.")
        event.respond('Check your DMs!')
      rescue Discordrb::Errors::NoPermission
        event.respond("❌ Sorry, but it looks like you're blocking DMs.")
      end
    end

    command(:info, description: 'Displays info about a user.') do |event|
      event.channel.start_typing
      if event.channel.private? # ignore PMs
        event << '❌ This command can only be used in a server.'
        next
      end

      unless event.message.mentions[0].nil?
        user = event.message.mentions[0]
      else
        user = event.user
      end
      if user.game.nil?
        playing = '[N/A]'
      else
        playing = user.game
      end
      member = user.on(event.server)
      if member.nickname.nil?
        nick = '[N/A]' #
      else
        nick = member.nickname
      end
      event << "👥  Information about **#{member.display_name}**"
      event << "-ID: **#{user.id}**"
      event << "-Username: `#{user.distinct}`"
      event << "-Nickname: **#{nick}**"
      event << "-Status: **#{user.status}**"
      event << "-Playing: **#{playing}**"
      event << "-Account created: **#{user.creation_time.getutc.asctime}** UTC"
      event << "-Joined server at: **#{member.joined_at.getutc.asctime}** UTC"
    end

    # Requires manage_roles permission since that's what we're doing.
    command(:config, description: 'Change settings per-server for the bot.', required_permissions: [:manage_roles]) do |event, option, *args|
      # TODO: more configuration options?
      if option == 'help'
        help = "__Help for #{Config.prefix}config:__\n\n"
        help += "_setrole_: Set the role to be used in place of Developers and Moderators. See #{Config.prefix}help for information about what these roles provide.\n"
        help += "_setchannel_: Sets the channel to be used for specific logging items, such as server logs.\n"
        event.respond(help)
      elsif option == 'setrole'
        # Make sure that there are 2+ options here, so we don't waste our time.
        if args.length < 2
          event.respond('❌ Make sure to follow the syntax correctly!')
          break
        end

        role_type = args[0]

        # Make sure that the short ID is valid
        unless Helper.types.include? role_type
          response = '❌ Make sure to type in a valid role type!' + "\n"
          response += 'Valid types are:' + "\n"
          Helper.types.each do |short_code, info|
            next if short_code.to_s == 'owner'
            response += "`#{short_code}` (#{info[0]}), "
          end
          event.respond(response[0...response.length-2])
          break
        end

        # All appears to be good to go. Remove the type and go.
        args.delete_at(0)
        role_name = args.join(' ')
        puts "About to change #{role_type} to #{role_name}"

        # Find given role by name. Or, at least attempt to.
        begin
          new_role_id = Helper.role_from_name(event.server, role_name).id
          Helper.save_xxx_id?(event.server.id, 'role', role_type, new_role_id)
          event.respond('✅ Successfully set!')
        rescue NoMethodError
          event.respond("❌ I wasn't able to find that role on this server! No changes have been made to your server's config.")
        end
      elsif option == 'setchannel'
        # Make sure that there are 2+ options here, so we don't waste our time.
        if args.length < 2
          event.respond('❌ Invalid syntax! `!config setchannel <type> <channel>`')
          break
        end

        channel_type = args[0]
        valid_channel_types = %w(srv mod)

        # Make sure that the short ID is valid
        unless valid_channel_types.include? channel_type
          response = '❌ Make sure to type in a valid channel type!' + "\n"
          response += 'Valid types are:' + "\n"
          valid_channel_types.each do |type|
            response += "`#{type}` "
          end
          event.respond(response[0...response.length-2])
          break
        end

        # All appears to be good to go. Remove the type and go.
        args.delete_at(0)
        channel_name = args.join(' ')
        if Config.debug
          puts "About to change #{channel_type} to #{channel_name}"
        end

        if channel_name.start_with?('<#')
          # Must be a channel
          begin
            check_match = /<#\d+>/.match(channel_name)
            break if check_match.nil?
            before_id = check_match[0]
            new_channel_id = /\d+/.match(before_id)[0]

            # The below is a test.
            event.bot.channel(new_channel_id)

            Helper.save_xxx_id?(event.server.id, 'channel', channel_type, new_channel_id)
            event.respond('✅ Successfully set!')
          rescue NoMethodError
            event.respond("❌ I wasn't able to find that channel on this server! No changes have been made to your server's config.")
          end
        else
        # Find given role by name. Or, at least attempt to.
          begin
            new_channel_id = Helper.channel_from_name(event.server, channel_name).id
            Helper.save_xxx_id?(event.server.id, 'channel', channel_type, new_channel_id)
            event.respond('✅ Successfully set!')
          rescue NoMethodError
            event.respond("❌ I wasn't able to find that channel on this server! No changes have been made to your server's config.")
          end
        end
      else
        event << '❌ You need to have an option! Valid options are:'
        event << '`help`, `setrole`, `setchannel`'
      end
    end
  end
end
