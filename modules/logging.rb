module SerieBot
  module Logging
    require 'rumoji'
    require 'rainbow'
    require 'fileutils'
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer
    class << self
      attr_accessor :messages
      attr_accessor :recorded_actions
    end

    @messages = {}
    # For patching
    mail_exe = "#{Dir.home}/bin/mail"

    def self.get_message(event, state)
      if event.nil? || event.message.nil? || event.message.content.nil?
        # Why is this nil?
      elsif event.channel.private? || event.message.content.start_with?(Config.prefix) || Config.logged_servers.include?(event.server.id)
      # We only want to log commands run, or messages on specified servers, or DMed from the bot.
        if event.channel.private?
          server_name = 'DM'
          channel_name = event.channel.name
        else
          server_name = event.server.name
          channel_name = "##{event.channel.name}"
        end
        content = Helper.parse_mentions(event.bot, event.message.content)
        content = Rumoji.encode(content)
        attachments = event.message.attachments
        id = Base64.strict_encode64([event.message.id].pack('L<'))

        # Format expected:
        # (ID) [D H:M] server name/channel name <author>: message
        log_message = "#{state}(#{id}) #{event.message.timestamp.utc.strftime('[%D %H:%M]')} #{server_name}/#{channel_name} <#{event.author.distinct}>: #{content}"
        attachment_message = "<Attachments: #{attachments[0].filename}: #{attachments[0].url} >" unless attachments.empty?
        if state == '{EDIT}'
          puts Rainbow(log_message.to_s).yellow
          puts Rainbow(attachment_message.to_s).yellow unless attachments.empty?
        else
          puts Rainbow(log_message.to_s).green
          puts Rainbow(attachment_message.to_s).green unless attachments.empty?
        end
        # Store message
        @messages[event.message.id] = {
          message: event.message,
          channel: channel_name,
          server: server_name
        }
       end
    end

    def self.get_deleted_message(event, state)
      if @messages[event.id].nil?
        # Do nothing, as this message wasn't for the bot.
        # This'd better be the case.
        return nil
      end

      message = @messages[event.id][:message]
      channel_name = @messages[event.id][:channel]
      server_name = @messages[event.id][:server]

      content = Rumoji.encode(message.content)
      content = Helper.parse_mentions(event.bot, content)

      attachments = message.attachments
      id = Base64.strict_encode64([message.id].pack('L<'))
      puts Rainbow("/!\\#{state}(#{id}) #{message.timestamp.utc.strftime('[%D %H:%M]')} #{server_name}/#{channel_name} <#{message.author.distinct}> #{content}").red
      puts Rainbow("<Attachments: #{attachments[0].filename}: #{attachments[0].url} >}").red unless attachments.empty?
    end

    def self.get_server_log?(event)
      # Get channel from ID
      id = event.bot.channel(Helper.get_xxx_channel?(event, 'srv', 'server-log'))
      if id.nil?
        event.server.general_channel.send_message("❌ I couldn't find the server log!")
      end
      return id
    end

    def self.get_mod_log?(event)
      # Get channel from ID
      id = event.bot.channel(Helper.get_xxx_channel?(event, 'mod', 'mod-log'))
      if id.nil?
        event.server.general_channel.send_message("❌ I couldn't find the server log!")
      end
      return id
    end

    message do |event|
      get_message(event, nil) unless Config.ignore_ids.include? event.channel.id
    end

    message_edit do |event|
      get_message(event, '{EDIT}') unless Config.ignore_ids.include? event.channel.id
    end

    message_delete do |event|
      get_deleted_message(event, '{DELETE}') unless Config.ignore_ids.include? event.channel.id
    end

    member_join do |event|
      puts Rainbow("#{Time.now.utc.strftime('[%D %H:%M]')} #{event.member.distinct} joined #{event.server.name}").blue
      time = Time.now.getutc
      message_to_send = "User: #{event.user.mention} | **#{event.user.distinct}**\n"
      message_to_send << "Account creation: `#{event.user.creation_time.getutc.asctime} UTC`\n"

      channel = get_server_log?(event)
      unless channel.nil?
        channel.send_embed do |e|
          e.title = 'A user just joined the server!'
          e.description = message_to_send.to_s
          e.colour = '#00C853'
          e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")
        end
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
        channel = get_server_log?(event)
        unless channel.nil?
          channel.send_embed do |e|
            e.title = 'A user left the server!'
            e.description = "User: #{user.mention} | **#{user.distinct}**"
            # puts "code for another commit :eyes:"
            # unless Mod.actions[user.id].nil?
            #   puts 'hi'
            #   e.description =+ " by #{event.bot.user(Mod.actions[user.id]).mention}"
            #   Mod.actions[user.id] = nil
            # end
            e.colour = '#FFEB3B'
            e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")
          end
        end
      end
    end

    user_ban do |event|
      time = Time.now.getutc
      e = Discordrb::Webhooks::Embed.new
      e.title = 'A user was banned from the server!'
      e.description = "User: #{event.user.mention} | **#{event.user.distinct}**"
      e.colour = '#D32F2F'
      e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")

      channel = get_server_log?(event)
      unless channel.nil?
        channel.send_embed('', e)
      end

      channel = get_mod_log?(event)
      unless channel.nil?
        channel.send_embed('', e)
      end
    end

    user_unban do |event|
      time = Time.now.getutc
      embed_sent = Discordrb::Webhooks::Embed.new
      embed_sent.title = 'A user was unbanned from the server!'
      embed_sent.description = "User: #{event.user.mention} | **#{event.user.distinct}**"
      embed_sent.colour = '#4CAF50'
      embed_sent.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")

      channel = get_server_log?(event)
      unless channel.nil?
        channel.send_embed('', embed_sent)
      end

      channel = get_mod_log?(event)
      unless channel.nil?
        channel.send_embed('', embed_sent)
      end
    end

    # For patching
    pm do |event|
      # Check that it's enabled.
      if Config.patch_mail
        file = event.message.attachments[0]
        # Check that this isn't the bot itself, and that this is actually a mail file.
        unless file.nil? || event.message.from_bot?
          if file.filename == 'nwc24msg.cfg'
            status_message = event.respond('Downloading config...')

            # Download file to tmp/userid/nwc24msg.cfg
            Helper.download_file(file.url, "tmp/#{event.user.id}", 'nwc24msg.cfg')

            # Run the patcher
            # mail <bot dir>/tmp/<user_id>/nwc24msg.cfg
            status_message.edit('Patching config...')
            bot_tmp = Dir.pwd + '/tmp/'
            downloaded_cfg_path = bot_tmp + event.user.id.to_s + '/nwc24msg.cfg'
            if Config.debug
              puts 'Path is ' + downloaded_cfg_path
            end
            system mail_exe, downloaded_cfg_path
            # Assume it worked. If it didn't rip xd
            status_message.delete

            # Upload patched copy
            event.channel.send_file(File.new([downloaded_cfg_path].sample), caption: "Here's your patched mail file, deleted from our server:")
            FileUtils.rm_r bot_tmp + '/' + event.user.id.to_s
          end
        end
      end
    end
  end
end
