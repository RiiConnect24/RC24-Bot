module SerieBot
  module Userjoin
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer
    log_channel = 'server-log'
    mail_exe = "#{Dir.home}/bin/mail"

    member_join do |event|
      time = Time.now.getutc
      message_to_send = "User: #{event.user.mention} | **#{event.user.distinct}**\n"
      message_to_send << "Account creation: `#{event.user.creation_time.getutc.asctime} UTC`\n"

      channel = event.server.channels.select { |x| x.name == log_channel }.first
      channel.send_embed do |e|
        e.title = 'A user just joined the server!'
        e.description = message_to_send.to_s
        e.colour = '#00C853'
        e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")
      end
    end
    member_leave do |event|
      time = Time.now.getutc

      # Check if the account was banned or just left.
      user = event.user
      was_banned = false
      event.server.bans.each do |banned_user|
        if banned_user.id == user.id
          # We don't need to announce they're leaving.
          was_banned = true
        end
      end
      unless was_banned
        channel = event.server.channels.select { |x| x.name == log_channel }.first
        channel.send_embed do |e|
          e.title = 'A user left the server!'
          e.description = "User: #{event.user.mention} | **#{event.user.distinct}**"
          e.colour = '#FFEB3B'
          e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")
        end
      end
    end

    user_ban do |event|
      time = Time.now.getutc

      channel = event.server.channels.select { |x| x.name == log_channel }.first
      channel.send_embed do |e|
        e.title = 'A user was banned from the server!'
        e.description = "User: #{event.user.mention} | **#{event.user.distinct}**"
        e.colour = '#D32F2F'
        e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")
      end
    end

    user_unban do |event|
      # D32F2F
      time = Time.now.getutc

      channel = event.server.channels.select { |x| x.name == log_channel }.first
      channel.send_embed do |e|
        e.title = 'A user was unbanned from the server!'
        e.description = "User: #{event.user.mention} | **#{event.user.distinct}**"
        e.colour = '#4CAF50'
        e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")
      end
    end

    # For patching
    pm do |event|
      # Check that it's enabled.
      if Config.patch_mail
        puts 'was here'
        # Make sure that this is actually a mail file.
        file = event.message.attachments[0]
        unless file.nil?
          if file.filename == 'nwc24msg.cfg'
            status_message = event.respond('Downloading config...')
            # Download file to non-user defined name
            Helper.download_file(file.url, 'tmp', "#{event.user.id}.cfg")
            # Run the patcher
            # mail <bot dir>/tmp/<user_id>.cfg
            status_message.edit('Patching config...')
            command_to_run = mail_exe + ' ' + Dir.pwd + '/tmp/' + event.user.id.to_s + '.cfg'
            puts 'Running ' + command_to_run
            %x( #{command_to_run} )
            # Assume it worked. If it didn't rip xd

          end
        end
      end
    end
  end
end
