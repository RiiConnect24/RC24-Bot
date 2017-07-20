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
      id = nil
      begin
      # Get channel from ID
        id = event.bot.channel(Helper.get_xxx_channel?(event, 'srv', 'server-log'))
      rescue NoMethodError
        event.server.general_channel.send_message("❌ I couldn't find the server log!")
       end
      return id
    end

    def self.get_mod_log?(event)
      id = nil
      begin
       # Get channel from ID
       id = event.bot.channel(Helper.get_xxx_channel?(event, 'mod', 'mod-log'))
      rescue NoMethodError
        event.server.general_channel.send_message("❌ I couldn't find the mod log!")
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
      was_banned = event.server.bans.include? user
      unless was_banned
        # Check if kicked by a bot command
        user_info = nil
        unless self.recorded_actions[user.id][:kick].nil?
          # It could be that the user was kicked previously and this was already known.
          unless self.recorded_actions[user.id][:kick].last[:notified]
            # Looks like they were kicked.
            user_info = self.recorded_actions[user.id][:kick].last
          end
        end
        e = Discordrb::Webhooks::Embed.new
        if user_info.nil?
          verb = 'left'
        else
          verb = 'was kicked from!'
        end
        description = "User: #{event.user.mention} | **#{event.user.distinct}**"
        unless user_info.nil?
          description += "\nKicked by #{event.bot.user(user_info[:doer]).name} with reason `#{user_info[:reason]}`"
          self.recorded_actions[user.id][:kick].last[:notified] = true
          Helper.save_xyz('actions', self.recorded_actions)
        end
        e.title = "A user #{verb} the server!"
        e.description = description
        e.colour = '#FFEB3B'
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
    end

    user_ban do |event|
      time = Time.now.getutc
      user = event.user
      e = Discordrb::Webhooks::Embed.new
      e.title = 'A user was banned from the server!'
      description = "User: #{user.mention} | **#{user.distinct}**\n"
      unless self.recorded_actions[user.id][:ban].nil?
        user_info = self.recorded_actions[user.id][:ban].last

        description += "Banned by #{event.bot.user(user_info[:doer]).name} with reason `#{user_info[:reason]}`"
        # look, a race condition due to leaving
        sleep(1)
        self.recorded_actions[user.id][:ban].last[:notified] = true
        Helper.save_xyz('actions', self.recorded_actions)
      end
      e.description = description
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

    def self.record_action(type, doer, recipient, reason)
      rep_id = recipient.id
      if self.recorded_actions[rep_id][type.to_sym].nil?
        self.recorded_actions[rep_id][type.to_sym] = Array.new
      end
      self.recorded_actions[rep_id][type.to_sym].push({
          'doer': doer.id,
          'reason': reason,
          'notified': false
                                 })
      Helper.save_xyz('actions', self.recorded_actions)
      nil
    end

    # For patching
    pm do |event|
      # Check that it's enabled.
      if Config.patch_mail
        file = event.message.attachments[0]
        # Check that this isn't the bot itself, and that this is actually a mail file.
        unless file.nil? || event.message.from_bot?
          if file.filename == 'nwc24msg.cfg' || file.filename == 'nwc24msg.cbk'
            status_message = event.respond('Downloading config...')

            # Download file to tmp/userid/nwc24msg.cfg
            Helper.download_file(file.url, "tmp/#{event.user.id}", 'nwc24msg.cfg')

            # Run the patcher
            status_message.edit('Patching config...')
            bot_tmp = Dir.pwd + '/tmp/'
            downloaded_cfg_path = bot_tmp + event.user.id.to_s + '/nwc24msg.cfg'
            if Config.debug
              puts 'Path is ' + downloaded_cfg_path
            end

            error = MailParse.convert_mail(downloaded_cfg_path)
            puts "Potential error: #{error}"
            case error
              when 2
                event.respond("Some voodoo magic occurred and I wasn't able to download your file. Let a Bot Helper or Developer know.")
              when 3
                event.respond('Are you sure this is a `nwc24msg.cfg` file? Let a Bot Helper or Developer know.')
              when 4
                event.respond("Some voodoo magic occurred and I wasn't able to save your file. Let a Bot Helper or Developer know.")
              else
                # Upload patched copy + remove .old from file extension
                event.channel.send_file(File.new([downloaded_cfg_path].sample), caption: "Here's your patched mail file, deleted from our server:")
                FileUtils.rm_r bot_tmp + '/' + event.user.id.to_s
            end

            status_message.delete
          end
        end
      end
    end
  end
end
