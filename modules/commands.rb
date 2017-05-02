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
            wads = "**__ RiiConnect24 IOS WADs: __**\n"
            wads << "Latest IOS31: https://cdn.discordapp.com/attachments/206931077313462272/305447451471511562/IOS31.wad\n"
            wads << "Latest IOS80: https://cdn.discordapp.com/attachments/206931077313462272/305447453010821120/IOS80.wad\n\n"
            wads << "**__ Wiimmfi Patched Mario Kart Channel: __**\n"
            wads << "NTSC-K: https://cdn.discordapp.com/attachments/287740297923002368/308368095481823232/Mario_Kart_Channel_-_v1_-_Korea_-_RMCK.wad\n"
            wads << "NTSC-U: https://cdn.discordapp.com/attachments/287740297923002368/308368100477370368/Mario_Kart_Channel_-_v1_-_USA_-_RMCE.wad\n"
            wads << "PAL: https://cdn.discordapp.com/attachments/287740297923002368/308368097801404416/Mario_Kart_Channel_-_v1_-_Europe_-_RMCP.wad\n"
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
            event.respond('**🔗 https://rc24.xyz/instructions/**')
        end

        command(:dns) do |event|
            event.respond("`185.82.21.64` should be your primary DNS.
            `8.8.8.8` (Google's DNS) can be your secondary DNS server.")
        end
    
        command(:facts) do |event|
            facts = [
                                "The blue light on the Wii when you receive a message is meant to be timed exactly with a certain bird call - the Japanese bush warbler. Source: https://www.wired.com/2008/02/nintendos-takas/",
                                "While development of the Forecast Channel took place a thunderstorm hit Nintendo of America and Nintendo of Japan lost contact with them for a while. Source: https://rc24.xyz/story_forecast.php",
                                "During the final stage of News Channel development, a rainstorm hit Nintendo of Europe and Nintendo of Japan lost contact with them. Source: https://rc24.xyz/story_forecast.php",
                                "There's a clock in the News Channel and Forecast Channel in the Europe and Japan versions of the Channel in the top-left corner, but not in the American version.",
                                "The list of cities that appear in the Forecast Channel vary by country, each country has more regional cities to choose from.",
                                "While your Wii's in standby mode and the blue light's glowing, you can turn the light off by pressing the RESET button on your Wii. Source: iDroid",
                                "The Japanese version of the Forecast Channel looks very different than the Europe and American versions of the Channel. There's differences like precipitation shown, a 7-day forecast instead of a 5-day forecast, and a pollen count and a laundry index (which shows if it's appropriate to dry your clothes outside).",
                                "The Photo Channel cat and the News Channel cat are brothers and sisters. Source: https://rc24.xyz/story_photo.php",
                                "The developers of the Photo Channel named the two cats that appear in the Photo Channel and News Channel. The one in the News Channel is named Runda and is male, and the one in the Photo Channel is named Rassie and is female. They both are named that way because of the ways they end their sentences in Japanese. Source: https://rc24.xyz/story_photo.php",
                                "The Photo Channel and News Channel cats were made because one of the developers used a bunch of pictures of cats during development of the Photo Channel (he was a cat lover). So when they decided how to display the tips in the Channels, they decided they should use a cat. Source: https://rc24.xyz/story_photo.php",
                                "Nintendo made Mario, Luigi, Princess Peach, Yoshi, Toad, and Bowser Wii Remotes. https://www.gamesmen.com.au/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/w/i/wii_u_remote_6_pack.jpg",
                                "You can arrange contacts in the Wii Address Book by grabbing it with A and B and moving it to where you like, just like you can do it with moving Channels.",
                                "The Globe in the News Channel and the Forecast Channel is also used in Mario Kart Wii.",
                      ]

            event.respond("__**Did you know?**__ " + facts.sample + " Got any facts we should add? Ask Larsenv or another RiiConnect24 developer!")
        end
    end
end
