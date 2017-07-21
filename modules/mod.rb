# frozen_string_literal: true

module SerieBot
  # Moderator command container.
  module Mod
    extend Discordrb::Commands::CommandContainer

    command(:clear, max_args: 1, description: 'Deletes x messages, mod only.', usage: "#{Config.prefix}clear x") do |event, count|
      BotHelper.ignore_bots(event)
      unless RoleHelper.named_role?(event, %i[owner dev mod hlp])
        event.respond("❌ You don't have permission for that!")
        break
      end
      unless count.nil? || /\A\d+\z/.match?(count)
        event.respond("`#{count}` is not a valid number!")
        break
      end
      original_num = count.to_i
      clear_num = count.to_i + 1
      ids = []

      begin
        while clear_num.positive?
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

    command(:kick, description: 'Kick somebody from the server.', usage: "#{Config.prefix}kick @user reason", min_args: 2) do |event, *kick_reason|
      if event.channel.private?
        event.respond("❌ You can't kick over DMs!")
        break
      end

      unless RoleHelper.named_role?(event, %i[owner dev mod hlp])
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
        message = "You have been kicked from the server **#{event.server.name}** by #{event.message.author.mention} | **#{event.message.author.display_name}**\n" \
        "They gave the following reason: ``#{display}``"
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

    command(:warn, description: 'Warn somebody on the server.', usage: "#{Config.prefix}warn @user reason", min_args: 2) do |event, *kick_reason|
      unless RoleHelper.named_role?(event, %i[adm dev mod hlp])
        event.respond("❌ You don't have permission for that!")
        break
      end

      member = event.server.member(event.message.mentions[0])
      if event.user == member
        event.respond("❌ You can't warn yourself. 😉")
        break
      end

      break if event.channel.private?
      if event.message.mentions[0]
        final_message = kick_reason.drop(1)
        display = final_message.join(' ')
        message = "You have been warned on the server **#{event.server.name}** by #{event.message.author.mention} | **#{event.message.author.display_name}**\n" \
        "They gave the following reason: ``#{display}``"
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

    command(:ban, description: 'Ban someone from the server.', usage: "#{Config.prefix}ban @user reason", min_args: 2) do |event, *ban_reason|
      if event.channel.private?
        event.respond("❌ You can't ban over DMs!")
        break
      end

      unless RoleHelper.named_role?(event, %i[owner dev mod])
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
        message = "You have been **permanently banned** from the server #{event.server.name} by #{event.message.author.mention} | **#{event.message.author.display_name}**\n" \
        "They gave the following reason: ``#{ban_display}``\n\n" \
        "If you wish to appeal for your ban's removal, please contact this person, or the server owner."
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
      unless RoleHelper.named_role?(event, %i[owner bot adm])
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
      message = "Hi! we did some automatic cleanup and kicked users who weren't verified and have the default avatar.\n" \
      "If you were kicked, don't worry! Join us again. We have the invite URL on our website."
      reason = "Automated cleaning started by #{event.user.display_name}"

      event.server.members.each do |member|
        # Verified role
        next if BotHelper.is_verified?(event, member)
        # Banned
        next if event.server.bans.include? member.id
        # Is a bot
        next if member.bot_account?
        # Default avatar has no filename
        next unless File.basename(URI.parse(member.avatar_url).path) == '.jpg'
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

    command(:lockdown, min_args: 1, usage: "#{Config.prefix}lockdown <reason>") do |event, reason|
      unless RoleHelper.named_role?(event, %i[owner dev bot adm])
        event.respond("❌ You don't have permission for that!")
        break
      end
      display_reason = reason.join(' ')

      lockdown = Discordrb::Permissions.new
      lockdown.can_send_messages = true
      everyone_role = BotHelper.role_from_name(event.server, '@everyone')
      event.channel.define_overwrite(everyone_role, 0, lockdown)
      event.respond("🔒**This channel is now in lockdown. Only staff can send messages. Reason: #{display_reason}**🔒")
    end

    command(:unlockdown) do |event|
      unless RoleHelper.named_role?(event, %i[owner dev bot adm])
        event.respond("❌ You don't have permission for that!")
        break
      end

      lockdown = Discordrb::Permissions.new
      lockdown.can_send_messages = true
      everyone_role = BotHelper.role_from_name(event.server, '@everyone')
      event.channel.define_overwrite(everyone_role, lockdown, 0)
      event.respond('🔓**Channel has been unlocked.**🔓')
    end

    command(:history) do |event, user_mention|
      if event.channel.private?
        event.respond('You can only run this on a server, not over DMs!')
        break
      end
      unless RoleHelper.named_role?(event, %i[owner dev bot adm])
        event.respond("❌ You don't have permission for that!")
        break
      end
      # Mention, search for, current user
      # Mention on local server
      user = event.server.member(event.bot.parse_mention(user_mention)) unless event.bot.parse_mention(user_mention).nil?
      # Search for across bot/on server
      user = event.bot.find_user(user_mention)[0] if user_mention.nil?
      test = event.server.member(event.bot.find_user(user_mention)[0])
      user = test unless test.nil?
      # Fall back to the user themself
      user = event.user if user.nil?
      embed_sent = Discordrb::Webhooks::Embed.new
      logged_types = {
        warn: 'Warns',
        ban: 'Bans',
        kick: 'Kicks'
      }

      user_name = ''
      begin
        user_name = user.on(event.server).display_name
      rescue NoMethodError
        user_name = user.name
      end

      if Logging.recorded_actions[user.id].nil?
        event.respond("No actions recorded for user #{user_name}!")
        break
      end

      actions = Logging.recorded_actions[user.id]

      logged_types.each do |action_type, action_title|
        next if actions[action_type].nil?
        to_add = ''
        actions[action_type].each do |action_recorded|
          to_add += "By #{event.bot.user(action_recorded[:doer]).name} for `#{action_recorded[:reason]}`. Log notified: #{action_recorded[:notified] ? 'yes' : 'no'}\n"
        end
        embed_sent.add_field(name: action_title, value: to_add)
      end

      # 33762 is the same as hex #0083e2
      embed_sent.colour = BotHelper.color_from_user(user, event.channel, '0083e2')
      embed_sent.author = Discordrb::Webhooks::EmbedAuthor.new(name: "Server log history for #{user_name}",
                                                               url: nil,
                                                               icon_url: BotHelper.avatar_url(user, 32))
      event.channel.send_embed('', embed_sent)
    end
  end
end
