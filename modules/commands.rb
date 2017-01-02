module SerieBot
    module Commands
        extend Discordrb::Commands::CommandContainer
        extend Discordrb::EventContainer
        require 'open-uri'

        # Migrated from Yuu-Chan's Yuu module
        command (:wads) do |event|
            event.channel.start_typing
            event.user.pm("**__ RiiConnect24 WADs: __** \nLatest IOS31: http://pokeacer.xyz/owncloud/index.php/s/qMm01pal7hN2wDU/download \nLatest IOS35: http://pokeacer.xyz/owncloud/index.php/s/S7uFituZzlt49oY/download \nLatest IOS80: http://pokeacer.xyz/owncloud/index.php/s/m1K8KW8Tsbn4zTS/download \nLatest IOS251: http://pokeacer.xyz/owncloud/index.php/s/QxCideE7BGy2l5f/download")
            event.respond("👌")
        end

        # Migrated from Yuu-Chan's Command module
        command(:error, max_args: 1, min_args: 1) do |event, code|
            # Start typing so the user knows something's going on
            event.channel.start_typing
            # Validate
            if code.to_i.to_s == code
                # 0 returns an empty array (see http://forum.wii-homebrew.com/index.php/Thread/57051-Wiimmfi-Error-API-has-an-error/?postID=680936#post680936)
                # We'll just treat it as an error.
                if code == '0'
                    event.respond("❌ Enter a valid error code!")
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
                # This is a hash wrapped in an array, so go grab it.
                if array[0][:found] == 1
                    data = array[0]
                    messageToSend = "Here's your issue:\n"
                    # Infolist will have all the table things
                    data[:infolist].each do |row|
                        info = row[:info]

                        wiimmfiLinkInfo = info
                        # For Wiimmfi self links
                        selfMatches = /<a href\s*=\s*"\/([^"]*)">([^"]*)<\/a>/.match(info)
                        unless selfMatches.nil?
                            # Replaces matches with url (title)
                            webLink = (selfMatches[2]).to_s
                            wiimmfiLinkInfo = info.gsub(selfMatches[0], webLink)
                      end

                        otherLinkInfo = wiimmfiLinkInfo
                        # Other links
                        linkMatches = /<a href\s*=\s*"https:\/\/([^"]*)">([^"]*)<\/a>/.match(wiimmfiLinkInfo)
                        unless linkMatches.nil?
                            # Replaces matches with url (title)
                            otherLink = (linkMatches[2]).to_s
                            otherLinkInfo = wiimmfiLinkInfo.gsub(linkMatches[0], otherLink)
                      end
                        # For formatting.
                        oneBold = otherLinkInfo.gsub('<b>', '**')
                        twoBold = oneBold.gsub('</b>', '**')
                        oneItalic = twoBold.gsub('<i>', '*')
                        twoItalic = oneItalic.gsub('</i>', '*')

                        messageToSend += "#{row[:type]} for code #{row[:name]}: #{twoItalic}\n"
                    end

                    event.respond(messageToSend)
                    # This break is super important, otherwise it messages all of data[:infolist]
                    # Why? I don't know, just please don't remove this
                    break
                else
                    event.respond("❌ Could not find the specified error from Wiimmfi.")
                    break
                end
            else
                event.respond("❌ Enter a valid error code!")
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
            event.respond('https://riiconnect24.net/instructions/')
        end
    end
end
