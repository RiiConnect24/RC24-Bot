# frozen_string_literal: true

module SerieBot
  # Helper functions for the bot.
  module BotHelper
    # TODO: perhaps save and stuff?
    def self.quit
      puts 'Exiting...'
      exit
    end

    # Downloads an avatar when given a `user` object.
    # Returns the path of the downloaded file.
    def self.download_avatar(user, folder)
      url = avatar_url(user)
      path = download_file(url, folder)
      path
    end

    def self.avatar_url(user, size = 256)
      url = user.avatar_url
      uri = URI.parse(url)
      filename = File.basename(uri.path)

      filename = if filename.start_with?('a_')
                   filename.gsub('.jpg', '.gif')
                 else
                   filename.gsub('.jpg', '.png')
                 end
      url = "https://cdn.discordapp.com/avatars/#{user.id}/#{filename}?size=#{size}"
      url
    end

    # Download a file from a url to a specified folder.
    # If no name is given, it will be taken from the url.
    # Returns the full path of the downloaded file.
    def self.download_file(url, folder, name = nil)
      if name.nil?
        uri = URI.parse(url)
        filename = File.basename(uri.path)
        name = filename if name.nil?
      end

      path = "#{folder}/#{name}"

      FileUtils.mkdir_p(folder) unless File.exist?(folder)
      FileUtils.rm(path) if File.exist?(path)

      File.new path, 'w'
      File.open(path, 'wb') do |file|
        file.write open(url).read
      end

      path
    end

    # If the user passed is a bot, it will be ignored.
    # Returns true if the user was a bot.
    def self.ignore_bots(event)
      if event.user.bot_account?
        id = event.user.id
        event.bot.ignore_user(id)
        # Add to persistent list
        unless Config.settings['ignored_bots'].include? id
          Config.settings['ignored_bots'].push(id)
        end
        RoleHelper.save_xyz('settings', Config.settings)
        true
      else
        false
      end
    end

    def self.upload_file(channel, filename)
      channel.send_file File.new([filename].sample)
    end

    # Accepts a message, and returns the message content, with all mentions + channels replaced with @user#1234 or #channel-name
    # Hopefully, at some point, will be <@!?&?\d{16,18}>
    # Test that regex with
    # <@&269923672913870848>
    # <@269923672913870848>
    # <@!269923672913870848>
    # <@!2699236729138701>
    def self.parse_mentions(bot, content)
      # Replce user IDs with names
      loop do
        match = /<@!?\d+>/.match(content)
        break if match.nil?
        # Get user
        id = match[0]
        num_id = /\d+/.match(id)[0]
        content = content.sub(id, get_user_name(num_id, bot))
      end
      # Replace channel IDs with names
      loop do
        match = /<#\d{18}>/.match(content)
        break if match.nil?
        # Get channel ID
        id = match[0]
        num_id = /\d+/.match(id)[0]
        content = content.sub(id, get_channel_name(num_id, bot))
      end
      content
    end

    # Returns a user-readable username for the specified ID.
    def self.get_user_name(user_id, bot)
      '@' + (begin
        bot.user(user_id).distinct
      rescue
        'invalid-user'
      end)
    end

    # Returns a user-readable channel name for the specified ID.
    def self.get_channel_name(channel_id, bot)
      '#' + (begin
        bot.channel(channel_id).name
      rescue
        'deleted-channel'
      end)
    end

    def self.filter_everyone(text)
      text.gsub('@everyone', "@\x00everyone")
    end

    # Dumps all messages in a given channel.
    # Returns the filepath of the file containing the dump.
    def self.dump_channel(channel, output_channel = nil, folder, timestamp)
      server = if channel.private?
                 'DMs'
               else
                 channel.server.name
               end
      message = "Dumping messages from channel \"#{channel.name.gsub('`', '\\`')}\" in #{server.gsub('`', '\\`')}, please wait...\n"
      output_channel&.send_message(message)
      puts message

      output_filename = if channel.private?
                          "#{folder}/output_" + server + '_' + channel.name + '_' + channel.id.to_s + '_' + timestamp.to_s + '.txt'
                        else
                          "#{folder}/output_" + server + '_' + channel.server.id.to_s + '_' + channel.name + '_' + channel.id.to_s + '_' + timestamp.to_s + '.txt'
                        end
      output_filename = output_filename.tr(' ', '_').delete('+').delete('\\').delete('/').delete(':').delete('*').delete('?').delete('"').delete('<').delete('>').delete('|')

      output_file = File.open(output_filename, 'w')

      # Start on first message
      offset_id = channel.history(1, 1, 1)[0].id # get first message id
      message_count = 0

      # Now let's dump!
      loop do
        # We can only go through 100 messages at a time, so grab 100.
        current_history = channel.history(100, nil, offset_id).reverse
        # Break if there are no other messages
        break if current_history == []

        # Have a working string so we don't flog up disk writes
        to_write = ''
        current_history.each do |dumped_message|
          next if dumped_message.nil?
          author = if dumped_message.author.nil?
                     'Unknown User'
                   else
                     dumped_message.author.distinct
                   end
          time = dumped_message.timestamp
          content = dumped_message.content

          attachments = dumped_message.attachments

          to_write += "#{time} #{author}: #{content}\n"
          to_write += "\n<Attachments: #{attachments[0].filename}: #{attachments[0].url}}>\n" unless attachments.empty?
          message_count += 1
        end

        output_file.write(to_write)
        output_file.flush

        # Set offset ID to last message in history that we saw
        # (this is the last message sent - 1 since Ruby has array offsets of 0)
        offset_id = current_history[current_history.length - 1].id
      end
      output_file.close
      message = "#{message_count} messages logged."
      output_channel&.send_message(message)
      puts message
      puts "Done. Dump file: #{output_filename}"
      output_filename
    end

    def self.role_from_name(server, role_name)
      roles = server.roles
      role = roles.select { |r| r.name == role_name }.first
      role
    end

    # Get the user's color
    def self.color_from_user(user, channel, default = 0)
      color = default
      return color if channel.private?

      # Attempt to grab member
      member = channel.server.member(user.id)
      unless member.nil?
        member.roles.sort_by(&:position).reverse.each do |role|
          next if role.color.combined.zero?
          if Config.debug
            puts 'Using ' + role.name + '\'s color ' + role.color.combined.to_s
          end
          color = role.color.combined
          break
        end
      end
      color
    end

    def self.channel_from_name(server, channel_name)
      channels = server.channels
      puts "Looking for #{channel_name}" if Config.debug
      channel = channels.select { |x| x.name == channel_name }.first
      puts "Found #{channel.name} (ID: #{channel.id})" if Config.debug
      channel
    end
  end
end
