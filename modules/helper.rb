module SerieBot
    module Helper
        def self.isadmin?(member)
            Config.bot_owners.include?(member)
      end

        def self.quit
            puts 'Exiting...'
            exit
        end

        def self.save_codes
            File.open('data/codes.yml', 'w+') do |f|
f.write(Rii.codes.to_yaml)
            end
        end

        def self.load_codes
            Codes.codes = YAML.load(File.read('data/codes.yml'))
        end

        # Downloads an avatar when given a `user` object.
        # Returns the path of the downloaded file.
        def self.download_avatar(user, folder)
            url = user.avatar_url
            path = download_file(url, folder)
            path
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

            FileUtils.rm(path) if File.exist?(path)

            File.new path, 'w'
            File.open(path, 'wb') do |file|
file.write open(url).read
            end

            path
        end

        # If the user passed is a bot, it will be ignored.
        # Returns true if the user was a bot.
        def self.ignore_bots(user)
            if user.bot_account?
event.bot.ignore_user(event.user)
return true
            else
return false
            end
        end

        def self.upload_file(channel, filename)
            channel.send_file File.new([filename].sample)
            puts "Uploaded `#{filename} to \##{channel.name}!"
      end

        # Accepts a message, and returns the message content, with all mentions + channels replaced with @User#1234 or #channel-name
        def self.parse_mentions(bot, message, text = nil)
            text = message.content if text.nil?
            content = text
            # Replce user IDs with names
            message.mentions.each { |x| content = content.gsub("<@#{x.id}>", "@#{x.distinct}"); content = content.gsub("<@!#{x.id}>", "\@#{x.distinct}") }
            # Replace channel IDs with names
            # scan for some regex, /<#\d+>/ or something, then you can map ids.map { |id| bot.channel(id).name } or something
            somethingSomethingTextArray = []
            content = content.gsub(/<#\d+>/) { |id| get_channel_name(id, bot) }
            content
        end

        def self.get_channel_name(channel_id, bot)
            toReturn = nil
            begin
toReturn = '#' + bot.channel(channel_id.gsub(/[^0-9,.]/, '')).name
            rescue NoMethodError
toReturn = '#deleted-channel'
            end
            toReturn
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
hist_count_and_messages[1] = SerieBot.parse_history(hist_count_and_messages[0], hist_count_and_messages[1][0])
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

        def self.role_from_name(server, rolename)
            roles = server.roles
            role = roles.select { |r| r.name == rolename }.first
            role
        end

        # New lines need to stay to the side, so formatting stays correct.
        def self.get_help(user)
            help = "**__Using the bot__**

**Adding codes:**
`#{Config.prefix}code add wii | Wii Name | 1234-5678-9012-3456` (You can add multiple Wiis with different names)
`#{Config.prefix}code add game | Game Name | 1234-5678-9012`

**Editing codes**
`#{Config.prefix}code edit wii | Wii Name | 1234-5678-9012-3456`
`#{Config.prefix}code edit game | Game Name | 1234-5678-9012`

**Removing codes**
`#{Config.prefix}code remove wii | Wii Name`
`#{Config.prefix}code remove game | Game Name`

**Looking up codes**
`#{Config.prefix}code lookup @user`

**Adding a user's Wii**
`#{Config.prefix}add @user`
This will send you their codes, and then send them your Wii/game codes.

**Specific commands**
`#{Config.prefix}wads` may or may not DM you some WADs.
`#{Config.prefix}help` or `#{Config.prefix}code help` will send you this help message.
`#{Config.prefix}gametdb <platform> <title id>` will provide a GameTDB wiki page with the specified Title ID. Valid platforms are Wii, WiiU, PS3, 3DS, and DS.
`#{Config.prefix}error <error code>` will provide you information about the specified error code from Wiimmfi.
`#{Config.prefix}instructions` will reply with some setup instructions for RiiConnect24."
            if Helper.isadmin?(user)
              help += "\n\n**__Super secret admin commands__**
As this RiiConnect24 bot is a stripped down version of Yuu-Chan/Serie-Bot, you have a limited option of some moderation commands.

**Bot-specific commands**
`#{Config.prefix}wipecodes @user` will wipe all codes the specified user has added.
`#{Config.prefix}save` will save the current state of codes to data/codes.yml.

**General commands**
`#{Config.prefix}setavatar <file/URL>` will change the avatar to the provided URL/image.
`#{Config.prefix}ignore @user`/`#{Config.prefix}unignore @user` will respectively ignore and unignore the specified user.
`#{Config.prefix}status <statu>` changes the status of the bot to one of the options of idle, dnd, invisible or online.
`#{Config.prefix}shutdown` will do exactly as the name suggests to the bot.
`#{Config.prefix}eval <code>` will evaluate the specified Ruby string. !!! USE WITH CARE !!!
`#{Config.prefix}bash <command>` will run the specified command in a bash shell. As before, !!! USE WITH CARE !!!
`#{Config.prefix}dump <id>` will dump all messages from the channel represented by the specified ID.
`#{Config.prefix}about` will tell you information about the bot."
            end
            help
      end
  end
 end
