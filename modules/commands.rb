module SerieBot
    module Commands
        extend Discordrb::Commands::CommandContainer
        extend Discordrb::EventContainer
        require 'open-uri'
        class << self
          attr_accessor :local_codes
          end
        @local_codes = Config.settings["local_codes"]

        # Migrated from Yuu-Chan's Yuu module
        command (:wads) do |event|
            event.channel.start_typing
            wads = "**__ RiiConnect24 WADs: __**\n"
            wads << "Latest IOS31: http://pokeacer.xyz/owncloud/index.php/s/qMm01pal7hN2wDU/download\n"
            wads << "Latest IOS35: http://pokeacer.xyz/owncloud/index.php/s/S7uFituZzlt49oY/download\n"
            wads << "Latest IOS80: http://pokeacer.xyz/owncloud/index.php/s/m1K8KW8Tsbn4zTS/download\n"
            wads << 'Latest IOS251: http://pokeacer.xyz/owncloud/index.php/s/QxCideE7BGy2l5f/download'
            begin
                event.user.pm(wads)
            rescue Discordrb::Errors::NoPermission
                event.respond("❌ Sorry, but it looks like you're blocking DMs.")
                break
            end
            event.respond("👌")
        end

        command(:error, max_args: 1, min_args: 1) do |event, code|
            # Start typing so the user knows something's going on
            event.channel.start_typing
            # Check for local codes
            local_match = /(NEWS|FORE)0{4}\d{2}/.match(code)
            unless local_match.nil?
                # match'd
                error_num = code.gsub(local_match[1], '')
                error_text = @local_codes["news"][error_num.to_s]
                if error_text.nil? || error_text == ''
                  event.respond('❌ Could not find the specified app error code.')
                  break
                else
                  event.channel.send_embed do |e|
                      e.title = "Here's information about your error:"
                      e.description = error_text.to_s
                      e.colour = '#D32F2F'
                      e.footer = Discordrb::Webhooks::EmbedFooter.new(text: 'All information provided by RC24 Developers.')
                  end
                  break
                end
            end
            # No? Validate the code
            if code.to_i.to_s == code
                # 0 returns an empty array (see http://forum.wii-homebrew.com/index.php/Thread/57051-Wiimmfi-Error-API-has-an-error/?postID=680936#post680936)
                # We'll just treat it as an error.
                if code == '0'
                    event.respond('❌ Enter a valid error code!')
                    break
              end
                # Determine method
                # Per http://forum.wii-homebrew.com/index.php/Thread/57051-Wiimmfi-Error-API-has-an-error/?postID=680943#post680943
                # it was recommended to use "t=<code>" for dev and "e=<code>" for prod due to statistical reasons.
                method = if Config.debug
                             "t=#{code}"
                         else
                             "e=#{code}"
                         end
                # Grab JSON
                json_string = open("https://wiimmfi.de/error?#{method}&m=json").read
                array = JSON.parse(json_string, symbolize_names: true)

                message_to_send = ''
                # This is a hash wrapped in an array, so go grab it.
                if array[0][:found] == 1
                    data = array[0]
                    # Infolist will have all the table things
                    data[:infolist].each do |row|
                        info = row[:info]

                        other_link_info = info

                        # Cycle through all matches
                        loop do
                            # Links
                            link_matches = /<a href\s*=\s*"http([^"]*)">([^"]*)<\/a>/.match(other_link_info)
                            break if link_matches.nil?
                            # Replaces matches with [title](http<url>) (Discord embed thing)
                            # We start the URL with http because of the regex.
                            other_link = "[#{link_matches[2]}](http#{link_matches[1]})"
                            other_link_info = other_link_info.gsub(link_matches[0], other_link)
                        end
                        # For formatting.
                        one_bold = other_link_info.gsub('<b>', '**')
                        two_bold = one_bold.gsub('</b>', '**')
                        one_italic = two_bold.gsub('<i>', '*')
                        two_italic = one_italic.gsub('</i>', '*')

                        message_to_send += "#{row[:type]} for error #{row[:name]}: #{two_italic}\n"
                    end

                    # Check if there are any local error notes.
                    possible_note = @local_codes['notes'][code.to_i]
                    unless possible_note.nil? || possible_note == ''
                      message_to_send += "Note from RiiConnect24 devs: #{possible_note}\n"
                    end

                    event.channel.send_embed do |e|
                        e.title = "Here's information about your error:"
                        e.description = message_to_send.to_s
                        e.colour = '#D32F2F'
                        e.footer = Discordrb::Webhooks::EmbedFooter.new(text: 'All information is from Wiimmfi unless noted.')
                    end
                    # This break is super important, otherwise it messages all of data[:infolist]
                    # because Ruby default returns the last variable.
                    break
                else
                    event.respond('❌ Could not find the specified error from Wiimmfi.')
                    break
                end
            else
                event.respond('❌ Enter a valid error code!')
                break
          end
        end
        command(:gametdb, max_args: 2, min_args: 2) do |event, platform, code|
            platforms = %w(Wii WiiU PS3 3DS DS)
            if platform == '' || platform.nil?
                event.respond("❌ Enter a valid platform!")
                break
            end
            unless platforms.include?(platform)
                event.respond("❌ Enter a valid platform!")
                break
            end
            if code == '' || code.nil?
                event.respond("❌ Enter a valid game code!")
                break
            end
            event.respond("http://gametdb.com/#{platform}/#{code}")
        end

        command(:instructions, max_args: 0, min_args: 0) do |event|
            event.respond('**🔗 https://riiconnect24.net/instructions/**')
        end

        command(:dns) do |event|
            event.respond("`185.82.21.64` should be your primary DNS.
            `8.8.8.8` (Google's DNS) can be your secondary DNS server.")
        end
    end
end
