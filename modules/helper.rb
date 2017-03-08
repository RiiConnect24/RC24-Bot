module SerieBot
  module Helper
    def self.is_admin?(member)
      Config.bot_owners.include?(member)
    end

    # Gets the role's ID based on the given parameters
    def self.get_role_id?(role_type, server_id)
      # Set all to defaults
      Config.settings[server_id] = {} if Config.settings[server_id].nil?
      Config.settings[server_id]['role'] = {} if Config.settings[server_id]['role'].nil?
      return Config.settings[server_id]['role'][role_type]
    end

    # Saves the role's ID based on the given parameters
    def self.save_role_id?(role_type, server_id, role_id)
      if Config.debug
        puts "Saving role type #{role_type} with role ID #{role_id} for server ID #{server_id}"
      end
      Config.settings[server_id]['role'][role_type] = role_id
      Helper.save_settings
    end

    # Checks to see if the user has the given role, and if not deals accordingly to fix it.
    def self.is_xxx_role?(event, role_type, full_name, show_message = true, other_user = nil)
      # Check if config already has a role
      xxx_role_id = get_role_id?(role_type, event.server.id)

      if xxx_role_id.nil?
        # Set to default
        begin
          xxx_role_id = role_from_name(event.server, full_name).id
          save_role_id?(role_type, event.server.id, xxx_role_id)
        rescue NoMethodError
          if show_message
            event.respond("I wasn't able to find the role \"#{full_name}\" for role-related tasks! See `#{Config.prefix}config help` for information.")
          end
          return false
        end
        event.respond("Role \"#{full_name}\" set to default. Use `#{Config.prefix}config setrole #{role_type} <role name>` to change otherwise.")
      end
      # Check if the member has the ID of said role
      user = if other_user.nil?
               event.user
             else
               other_user
             end
      return user.role?(event.server.role(xxx_role_id))
    end


    # The following commands are basically skeletons now. The work is done above.
    def self.is_developer?(event)
      return is_xxx_role?(event, 'dev', 'Developers')
    end

    def self.is_bot_helper?(event)
      # There's a very large chance the user won't need to be a bot helper.
      return is_xxx_role?(event, 'bot', 'Bot Helpers', false)
    end

    def self.is_moderator?(event)
      return is_xxx_role?(event, 'mod', 'Moderators')
    end

    def self.is_helper?(event)
      return is_xxx_role?(event, 'hlp', 'Helpers', false)
    end

    # We have to specify user here because we're checking if another user is verified
    def self.is_verified?(event, other_user = nil)
      user = if other_user.nil?
               event.user
             else
               other_user
             end
      return is_xxx_role?(event, 'vfd', 'Verified', true, user)
    end


    # TODO: perhaps save and stuff?
    def self.quit
      puts 'Exiting...'
      exit
    end


    # Loading/saving of morpher messages
    def self.load_morpher
      folder = 'data'
      codes_path = "#{folder}/morpher.yml"
      FileUtils.mkdir(folder) unless File.exist?(folder)
      unless File.exist?(codes_path)
        File.open(codes_path, "w") { |file| file.write("---\n:version: 1\n") }
      end
      Morpher.messages = YAML.load(File.read(codes_path))
    end

    def self.save_morpher
      File.open('data/morpher.yml', 'w+') do |f|
      f.write(Morpher.messages.to_yaml)
      end
    end

    # Loading/saving of settings
    def self.save_settings
      File.open('data/settings.yml', 'w+') do |f|
        f.write(Config.settings.to_yaml)
      end
    end

    def self.load_settings
      folder = 'data'
      settings_path = "#{folder}/settings.yml"
      FileUtils.mkdir(folder) unless File.exist?(folder)
      unless File.exist?(settings_path)
        puts "[ERROR] I wasn't able to find data/settings.yml! Please grab the example from the repo."
      end
      Config.settings = YAML.load(File.read(settings_path))
    end

    # Loading/saving of codes
    def self.load_codes
      folder = 'data'
      codes_path = "#{folder}/codes.yml"
      FileUtils.mkdir(folder) unless File.exist?(folder)
      unless File.exist?(codes_path)
      File.open(codes_path, "w") { |file| file.write("---\n:version: 1\n") }
      end
      Codes.codes = YAML.load(File.read(codes_path))
    end

    def self.save_codes
      File.open('data/codes.yml', 'w+') do |f|
      f.write(Codes.codes.to_yaml)
      end
    end

    # Downloads an avatar when given a `user` object.
    # Returns the path of the downloaded file.
    def self.download_avatar(user, folder)
      url = Helper.avatar_url(user)
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
      url << '?size=256'
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

      FileUtils.mkdir(folder) unless File.exist?(folder)
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
        event.bot.ignore_user(event.user)
        true
      else
        false
      end
    end

    def self.upload_file(channel, filename)
      channel.send_file File.new([filename].sample)
      puts "Uploaded `#{filename} to \##{channel.name}!"
    end

    # Accepts a message, and returns the message content, with all mentions + channels replaced with @user#1234 or #channel-name
    def self.parse_mentions(bot, content)
      # Replce user IDs with names
      loop do
      match = /<@\d+>/.match(content)
      break if match.nil?
      # Get user
      id = match[0]
      # We have to sub to just get the numerical ID.
      num_id = /\d+/.match(id)[0]
      content = content.sub(id, get_user_name(num_id, bot))
      end
      loop do
      match = /<@!\d+>/.match(content)
      break if match.nil?
      # Get user
      id = match[0]
      # We have to sub to just get the numerical ID.
      num_id = /\d+/.match(id)[0]
      content = content.sub(id, get_user_name(num_id, bot))
      end
      # Replace channel IDs with names
      loop do
      match = /<#\d+>/.match(content)
      break if match.nil?
      # Get channel
      id = match[0]
      # We have to gsub to just get the numerical ID.
      num_id = /\d+/.match(id)[0]
      content = content.sub(id, get_channel_name(num_id, bot))
      end
      content
    end

    # Returns a user-readable username for the specified ID.
    def self.get_user_name(user_id, bot)
      to_return = nil
      begin
      to_return = '@' + bot.user(user_id).distinct
      rescue NoMethodError
      to_return = '@invalid-user'
      end
      to_return
    end

    # Returns a user-readable channel name for the specified ID.
    def self.get_channel_name(channel_id, bot)
      to_return = nil
      begin
      to_return = '#' + bot.channel(channel_id).name
      rescue NoMethodError
      to_return = '#deleted-channel'
      end
      to_return
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
      message = "Dumping messages from channel \"#{channel.name.gsub('`', '\\`')}\" in #{server.gsub('`', '\\`')}, please wait..."
      output_channel.send_message(message) unless output_channel.nil?
      puts message

      if !channel.private?
        output_filename = "#{folder}/output_" + server + '_' + channel.server.id.to_s + '_' + channel.name + '_' + channel.id.to_s + '_' + timestamp.to_s + '.txt'
      else
        output_filename = "#{folder}/output_" + server + '_' + channel.name + '_' + channel.id.to_s + '_' + timestamp.to_s + '.txt'
      end
      output_filename = output_filename.tr(' ', '_').delete('+').delete('\\').delete('/').delete(':').delete('*').delete('?').delete('"').delete('<').delete('>').delete('|')
      hist_count_and_messages = [[], [0, []]]

      output_file = File.open(output_filename, 'w')
      offset_id = channel.history(1, 1, 1)[0].id # get first message id

      # Now let's dump!
      loop do
        hist_count_and_messages[0] = channel.history(100, nil, offset_id) # next 100
        break if hist_count_and_messages[0] == []
        hist_count_and_messages[1] = parse_history(hist_count_and_messages[0], hist_count_and_messages[1][0])
        output_file.write((hist_count_and_messages[1][1].reverse.join("\n") + "\n").encode('UTF-8')) # write to file right away, don't store everything in memory
        output_file.flush # make sure it gets written to the file
        offset_id = hist_count_and_messages[0][0].id
      end
      output_file.close
      message = "#{hist_count_and_messages[1][0]} messages logged."
      output_channel.send_message(message) unless output_channel.nil?
      puts message
      puts "Done. Dump file: #{output_filename}"
      output_filename
    end

    def self.parse_history(hist, count)
      messages = []
      i = 0
      until i == hist.length
      message = hist[i]
      if message.nil?
        # STTTOOOOPPPPPP
        puts 'nii'
        break
      end
      author = if message.author.nil?
             'Unknown Disconnected User'
           else
             message.author.distinct
           end
      time = message.timestamp
      content = message.content

      attachments = message.attachments
      # attachments.each { |u| attachments.push("#{u.filename}: #{u.url}") }

      messages[i] = "--#{time} #{author}: #{content}"
      messages[i] += "\n<Attachments: #{attachments[0].filename}: #{attachments[0].url}}>" unless attachments.empty?
      #			puts "Logged message #{i} ID:#{message.id}: #{messages[i]}"
      i += 1

      count += 1
      end
      return_value = [count, messages]
      return_value
    end

    def self.role_from_name(server, rolename)
      roles = server.roles
      role = roles.select { |r| r.name == rolename }.first
      role
    end

    def self.get_help()
      help = "**__Using the bot__**\n"
      help += "\n"
      help += "**Adding codes:**\n"
      help += "`!code add wii | Wii Name Goes here | 1234-5678-9012-3456` (You can add multiple Wiis with different names)\n"
      help += "`!code add game | Game Name | 1234-5678-9012`\n"
      help += "and many more types! Run `!code add` to see all supported code types right now, such as the 3DS and Switch.\n"
      help += "\n"
      help += "**Editing codes**\n"
      help += "`!code edit wii | Wii Name | 1234-5678-9012-3456`\n"
      help += "`!code edit game | Game Name | 1234-5678-9012`\n"
      help += "\n"
      help += "**Removing codes**\n"
      help += "`!code remove wii | Wii Name`\n"
      help += "`!code remove game | Game Name`\n"
      help += "\n"
      help += "**Looking up codes**\n"
      help += "`!code lookup @user`\n"
      help += "\n"
      help += "**Adding a user's Wii**\n"
      help += "`!add @user`\n"
      help += "This will send you their codes, and then DM them your Wii/game codes."
      help
    end

    # Load settings for all.
    self.load_settings
  end
 end
