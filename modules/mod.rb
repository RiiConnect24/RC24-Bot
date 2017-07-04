module SerieBot
  module Mod
    extend Discordrb::Commands::CommandContainer

    command(:clear, max_args: 1, required_permissions: [:manage_messages], description: 'Deletes x messages, mod only.', usage: "#{Config.prefix}clear x") do |event, count|
      Helper.ignore_bots(event)
      if count.nil?
        event.respond('❌ No argument specicied. Enter a valid number!')
        break
      end

      unless /\A\d+\z/ =~ count
        event.respond("`#{count}` is not a valid number!")
        break
      end
      original_num = count.to_i
      clear_num = count.to_i + 1

      begin
        while clear_num > 0
          if clear_num >= 99
            # Welcome back to Workaround city.
            ids = []
            event.channel.history(99).each { |x| ids.push(x.id) }
            Discordrb::API::Channel.bulk_delete_messages(event.bot.token, event.channel.id, ids)
            clear_num -= 99
          else
            ids = []
            event.channel.history(clear_num).each { |x| ids.push(x.id) }
            Discordrb::API::Channel.bulk_delete_messages(event.bot.token, event.channel.id, ids)
            clear_num = 0
          end
        end
        message = event.respond("🚮  Cleared #{original_num} messages!")
        sleep(3)
        message.delete
      rescue Discordrb::Errors::NoPermission
        event.respond("❌ I don't have permission to delete messages!")
        break
      end
      nil
    end

    command(:kick, description: 'Temporarily kick somebody from the server. Mod only.', usage: "#{Config.prefix}kick @user reason", min_args: 2) do |event, *kick_reason|
      if event.channel.private?
        event.respond("❌ You can't kick over DMs!")
        break
      end

      unless Helper.has_role?(event, [:owner, :dev, :mod, :hlp])
        event.respond("❌ You don't have permission for that!")
        break
      end

      member = event.server.member(event.message.mentions[0])
      if event.user == member
        event.respond("❌ You can't kick yourself. 😉")
        break
      end

      break if event.channel.private?
      if event.message.mentions[0]
        final_message = kick_reason.drop(1)
        display = final_message.join(' ')
        message = "You have been kicked from the server **#{event.server.name}** by #{event.message.author.mention} | **#{event.message.author.display_name}**\n"
        message << "They gave the following reason: ``#{display}``"
        begin
          member.pm(message)
        rescue Discordrb::Errors::NoPermission
          event.respond('Could not DM user about kick reason!')
          break
        end
        begin
          # Register for logging
          event.server.kick(member)
          Logging.record_action('kick', event.user, member, display)
        rescue Discordrb::Errors::NoPermission
          event.respond("❗❗❗ The bot doesn't have permission to kick!")
          break
        end
        event.respond('👌 You have kicked the user, hard.')
        break
      else
        event.respond('❌ Invalid argument. Please mention a valid user.')
      end
		end
	  
		command(:warn, description: 'Warn somebody on the server. Mod only.', usage: "#{Config.prefix}warn @user reason", min_args: 2) do |event, *kick_reason|
			unless Helper.has_role?(event, [:adm, :dev, :mod, :hlp])
				event.respond("❌ You don't have permission for that!")
				break
			end

			member = event.server.member(event.message.mentions[0])

			break if event.channel.private?
			if event.message.mentions[0]
				final_message = kick_reason.drop(1)
				display = final_message.join(' ')
				message = "You have been warned on the server **#{event.server.name}** by #{event.message.author.mention} | **#{event.message.author.display_name}**\n"
				message << "They gave the following reason: ``#{display}``"
				begin
						member.pm(message)
				rescue Discordrb::Errors::NoPermission
						event.respond('Could not DM user about warn reason!')
						break
				end
        # Register for logging
        Logging.record_action('warn', event.user, member, display)
				event.respond('👌 Warned!')
				break
			else
				event.respond('❌ Invalid argument. Please mention a valid user.')
			end
		end

    command(:ban, description: 'Permanently ban someone from the server. Mod only.', usage: "#{Config.prefix}ban @user reason", min_args: 2) do |event, *ban_reason|
      if event.channel.private?
        event.respond("❌ You can't ban over DMs!")
        break
      end

      unless Helper.has_role?(event, [:owner, :dev, :mod])
        event.respond("❌ You don't have permission for that!")
        break
      end

      member = event.server.member(event.message.mentions[0])
      if event.user == member
        event.respond("❌ You can't ban yourself. 😉")
        break
      end
      if event.message.mentions[0]
        final_ban_message = ban_reason.drop(1)
        ban_display = final_ban_message.join(' ')
        message = "You have been **permanently banned** from the server #{event.server.name} by #{event.message.author.mention} | **#{event.message.author.display_name}**\n"
        message << "They gave the following reason: ``#{ban_display}``\n\n"
        message << "If you wish to appeal for your ban's removal, please contact this person, or the server owner."
        begin
            member.pm(message)
        rescue Discordrb::Errors::NoPermission
            event.respond('Could not DM user about ban reason!')
            break
        end
        begin
          event.server.ban(member)
          # Register for logging
          Logging.record_action('ban', event.user, member, ban_display)
          event.respond('👌 The ban hammer has hit, hard.')
        rescue
          event.respond("The bot doesn't have permission to ban that user!")
          break
        end
        break
      else
        event.respond('❌ Invalid argument. Please mention a valid user.')
      end
    end

    command(:userprune) do |event, code|
      unless Helper.has_role?(event, [:owner, :bot, :adm])
        event.respond("❌ You don't have permission for that!")
        break
      end
      if code == 'info'
        event.respond("This command kicks all users who are all of the following: a) no verified role, b) aren't banned, c) aren't a bot, d) and has the default avatar.")
        break
      end

      unless code == 'yes'
        event.respond("❌ Since this is command can be abusive, please run as `#{Config.prefix}userprune yes`. For more information about what this command does, `#{Config.prefix}userprune info`")
        break
      end

      event.channel.start_typing
      message = "Hi! we did some automatic cleanup and kicked users who weren't verified and have the default avatar.\n"
      message += "If you were kicked, don't worry! Join us again. We have the invite URL on our website."
      reason = "Automated cleaning started by #{event.user.display_name}"

      event.server.members.each do |member|
        # Verified role
        unless Helper.is_verified?(event, member)
          # Banned
          unless event.server.bans.include? member.id
            # Is a bot
            unless member.bot_account?
              # Default avatar has no filename
              if File.basename(URI.parse(member.avatar_url).path) == '.jpg'
                begin
                  member.pm(message)
                rescue Discordrb::Errors::NoPermission
                  event.respond("Could not DM user #{mention} about kick reason!")
                  break
                end

                begin
                  event.server.kick(member)
                  # Register for logging
                  Logging.record_action('kick', event.user, member, reason)
                  event.respond('👌 The ban hammer has hit, hard.')
                rescue
                  event.respond("The bot doesn't have permission to ban that user!")
                  break
                end
              end
            end
          end
        end
      end
    end

    command(:lockdown) do |event, time|
      unless Helper.has_role?(event, [:owner, :dev, :bot, :adm])
        event.respond("❌ You don't have permission for that!")
        break
      end

      lockdown = Discordrb::Permissions.new
      lockdown.can_send_messages = true
      everyone_role = Helper.role_from_name(event.server, '@everyone')
      event.channel.define_overwrite(everyone_role, 0, lockdown)
      if time.nil?
        event.respond('🔒**This channel is now in lockdown. Only staff can send messages. **🔒')
      elsif /\A\d+\z/.match(time)
        event.respond("🔒**This channel is now in lockdown. Only staff can send messages. **🔒\n**Time:** #{time} minute(s)")
        time_sec = time * 60
        sleep(time_sec)
        lockdown = Discordrb::Permissions.new
        lockdown.can_send_messages = true
        everyone_role = Helper.role_from_name(event.server, '@everyone')
        event.channel.define_overwrite(everyone_role, lockdown, 0)
        event.respond('🔓**Channel has been unlocked.**🔓')
      end
    end

    command(:unlockdown) do |event|
      unless Helper.has_role?(event, [:owner, :dev, :bot, :adm])
        event.respond("❌ You don't have permission for that!")
        break
      end

      lockdown = Discordrb::Permissions.new
      lockdown.can_send_messages = true
      everyone_role = Helper.role_from_name(event.server, '@everyone')
      event.channel.define_overwrite(everyone_role, lockdown, 0)
      event.respond('🔓**Channel has been unlocked.**🔓')
    end
  end
end
