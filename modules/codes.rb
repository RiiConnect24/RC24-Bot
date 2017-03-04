module SerieBot
    module Codes
        require 'yaml'
        extend Discordrb::Commands::CommandContainer
        extend Discordrb::EventContainer
        class << self
            attr_accessor :codes
        end
        Helper.load_codes

        command(:code) do |event, option, *args|
            user_id = event.user.id
            Codes.codes[user_id] = {} if Codes.codes[user_id].nil?
            if option == 'add'
                if args[0] == 'wii'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:wiis] = {} if Codes.codes[user_id][:wiis].nil?
                    Codes.codes[user_id][:wiis][name] = code
                    Helper.save_codes
                    event.respond("✅ Added a code for `#{name}`")
                    
            	elsif args[0] == '3ds'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:n3dss] = {} if Codes.codes[user_id][:n3dss].nil?
                    Codes.codes[user_id][:n3dss][name] = code
                    Helper.save_codes
                    event.respond("✅ Added a code for `#{name}`")
                    
            	elsif args[0] == 'nnid'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:nnids] = {} if Codes.codes[user_id][:nnids].nil?
                    Codes.codes[user_id][:nnids][name] = code
                    Helper.save_codes
                    event.respond("✅ Added a code for `#{name}`")
                    
            	elsif args[0] == 'switch'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:switches] = {} if Codes.codes[user_id][:switches].nil?
                    Codes.codes[user_id][:switches][name] = code
                    Helper.save_codes
                    event.respond("✅ Added a code for `#{name}`")

                elsif args[0] == 'game'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:games] = {} if Codes.codes[user_id][:games].nil?
                    Codes.codes[user_id][:games][name] = begin
                                                           code
                                                       rescue
                                                           Helper.save_codes
                                                       end
                    event.respond("✅ Added a code for `#{name}`")

                else
                    event << '❌ Please enter a valid argument for the option `add`.'
                    event << 'Valid arguments: `wii`, `3ds`, `nnid`, `switch`, `game`.'
                end
            elsif option == 'edit'
                if args[0] == 'wii'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:wiis] = {} if Codes.codes[user_id][:wiis].nil?
                    if Codes.codes[user_id][:wiis][name].nil?
                        event << "❌ A code for Wii `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:wiis][name] = begin
                                                              code
                                                          rescue
                                                              Helper.save_codes
                                                          end
                        event.respond("✅ Edited the code for `#{name}`")
                    end
            	
                elsif args[0] == '3ds'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:n3dss] = {} if Codes.codes[user_id][:n3dss].nil?
                    if Codes.codes[user_id][:n3dss][name].nil?
                        event << "❌ A code for 3DS `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:n3dss][name] = begin
                                                              code
                                                          rescue
                                                              Helper.save_codes
                                                          end
                        event.respond("✅ Edited the code for `#{name}`")
                    end
            	
                elsif args[0] == 'nnid'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:nnids] = {} if Codes.codes[user_id][:n3dss].nil?
                    if Codes.codes[user_id][:nnids][name].nil?
                        event << "❌ A code for Nintendo Network ID `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:nnids][name] = begin
                                                              code
                                                          rescue
                                                              Helper.save_codes
                                                          end
                        event.respond("✅ Edited the code for `#{name}`")
                    end
            	
                elsif args[0] == 'switch'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:switches] = {} if Codes.codes[user_id][:n3dss].nil?
                    if Codes.codes[user_id][:switches][name].nil?
                        event << "❌ A code for Switch `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:switches][name] = begin
                                                              code
                                                          rescue
                                                              Helper.save_codes
                                                          end
                        event.respond("✅ Edited the code for `#{name}`")
                    end

                elsif args[0] == 'game'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:games] = {} if Codes.codes[user_id][:games].nil?
                    if Codes.codes[user_id][:games][name].nil?
                        event << "❌ A code for Wii `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:games][name] = begin
                                                               code
                                                           rescue
                                                               Helper.save_codes
                                                           end
                        event.respond("✅ Edited the code for `#{name}`")
                    end

                else
                    event << '❌ Please enter a valid argument for the option `edit`.'
                    event << 'Valid arguments: `wii`, `3ds`, `nnid`, `switch`, `game`.'
                end
            elsif option == 'remove'
                if args[0] == 'wii'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:wiis] = {} if Codes.codes[user_id][:wiis].nil?
                    if Codes.codes[user_id][:wiis][name].nil?
                        event << "❌ A code for Wii `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:wiis].delete(name)
                        Helper.save_codes
                        event.respond("✅ Deleted the code for `#{name}`")
                    end
                elsif args[0] == '3ds'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:n3dss] = {} if Codes.codes[user_id][:n3dss].nil?
                    if Codes.codes[user_id][:n3dss][name].nil?
                        event << "❌ A code for 3DS `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:n3dss].delete(name)
                        Helper.save_codes
                        event.respond("✅ Deleted the code for `#{name}`")
                    end
                elsif args[0] == 'nnid'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:nnids] = {} if Codes.codes[user_id][:n3dss].nil?
                    if Codes.codes[user_id][:nnids][name].nil?
                        event << "❌ A code for Nintendo Network ID `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:nnids].delete(name)
                        Helper.save_codes
                        event.respond("✅ Deleted the code for `#{name}`")
                    end
                elsif args[0] == 'switch'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:n3dss] = {} if Codes.codes[user_id][:n3dss].nil?
                    if Codes.codes[user_id][:n3dss][name].nil?
                        event << "❌ A code for Switch `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:n3dss].delete(name)
                        Helper.save_codes
                        event.respond("✅ Deleted the code for `#{name}`")
                    end
                elsif args[0] == 'game'
                    args.delete_at(0)
                    args = args.join(' ')
                    args.sub!('| ', '')
                    input = args.split(' | ')
                    code = input[1]
                    name = input[0]
                    user_id = event.user.id
                    if code.nil?
                        event << '❌ Please enter a valid code!'
                        next
                    end
                    if name.nil?
                        event << '❌ Please enter a valid name!'
                        next
                    end

                    Codes.codes[user_id][:games] = {} if Codes.codes[user_id][:games].nil?
                    if Codes.codes[user_id][:games][name].nil?
                        event << "❌ A code for Game `#{name}` is not registered."
                        next
                    else
                        Codes.codes[user_id][:games].delete(name)
                        Helper.save_codes
                        event.respond("✅ Deleted the code for `#{name}`")
                    end

                else
                    event << '❌ Please enter a valid argument for the option `remove`.'
                    event << 'Valid arguments: `wii`, `3ds`, `nnid`, `switch`, `game`.'
                end
            elsif option == 'lookup'

                user = begin
                           event.bot.parse_mention(args[0])
                       rescue
                           event.user
                       end
                user = event.user if args[0].nil?

                # Check if the user is on this server.
                begin
                    user_name = user.on(event.server).display_name
                rescue NoMethodError
                    user_name = user.username
                    # They may not have even used the bot, so make sure.
                    @codes[user.id] = {} if @codes[user.id].nil?
                end
                if @codes[user.id].nil? || codes[user.id] == {}
                    event.respond("❌ **#{user_name}** has not added any friend codes!")
                    break
                else
                    event << "**__👤 Profile for #{user_name}__**\n"
                    unless @codes[user.id][:wiis].nil?
                        event << '<:Wii:259081748007223296> **Wiis**:'
                        @codes[user.id][:wiis].each do |wii, code| #
                            code_output = code
                            event << "`#{code_output}` - #{wii}"
                        end
                        event << ''
                    end
                    unless @codes[user.id][:n3dss].nil?
                        event << '<:New3DSXL:287651327763283968> **3DSs**:'
                        @codes[user.id][:n3dss].each do |threeds, code| #
                            code_output = code
                            event << "`#{code_output}` - #{threeds}"
                        end
                        event << ''
                    end
                    unless @codes[user.id][:nnids].nil?
                        event << '<:NintendoNetworkID:287655797104836608> **Nintendo Network IDs**:'
                        @codes[user.id][:n3dss].each do |threeds, code| #
                            code_output = code
                            event << "`#{code_output}` - #{threeds}"
                        end
                        event << ''
                    end
                    unless @codes[user.id][:switches].nil?
                        event << '<:Switch:287652338791874560> **Switches**:'
                        @codes[user.id][:switches].each do |switch, code| #
                            code_output = code
                            event << "`#{code_output}` - #{switch}"
                        end
                        event << ''
                    end
                    unless @codes[user.id][:games].nil?
                        event << '🎮 **Games**:'
                        @codes[user.id][:games].each do |game, code|
                            code_output = code
                            event << "`#{code_output}` - #{game}"
                        end
                        nil
                    end
                end
            elsif option == 'help'
                event.respond(Helper.get_help)
            elsif option == 'disable'
                user = event.user
                Codes.codes[user.id][:enabled] = false
                Helper.save_codes
                event.respond("✅ Disabled `#{Config.prefix}add`! Turn back on adding with `#{Config.prefix}code enable`.")
            elsif option == 'enable'
                user = event.user
                Codes.codes[user.id][:enabled] = true
                Helper.save_codes
                event.respond("✅ Enabled `#{Config.prefix}add`! Turn back off adding with `#{Config.prefix}code disable`.")
            else
                event << '❌ Please enter a valid option for the command.'
                event << 'Valid options: `add`, `edit`, `remove`, `lookup`, `enable`, `disable`.'
            end
        end

        command(:add, min_args: 1, max_args: 1) do |event, mention|
            user = begin
                       event.bot.parse_mention(mention)
                   rescue
                       event.respond('❌ Enter a valid user!')
                       break
                   end
            if user.id == event.user.id
                event.respond("❌ You can't add yourself!")
                break
            end
            if @codes[user.id].nil? || @codes[user.id][:wiis].nil? || @codes[user.id][:n3dss].nil? || @codes[user.id][:nnids].nil? || @codes[user.id][:switches].nil?
                event.respond("❌ **#{user.on(event.server).display_name}** has not added any codes!")
                break
            end
            if @codes[event.user.id].nil?
                event.respond('❌ You have not added any friend codes!')
                break
            end
            if @codes[event.user.id][:wiis].nil?
                event.respond('❌ You have not added any Wii codes!')
                break
            end
            if @codes[event.user.id][:n3dss].nil?
                event.respond('❌ You have not added any 3DS codes!')
                break
            end
            if @codes[event.user.id][:nnids].nil?
                event.respond('❌ You have not added any Nintendo Network IDs!')
                break
            end
            if @codes[event.user.id][:switches].nil?
                event.respond('❌ You have not added any Switch codes!')
                break
            end
            unless @codes[user.id][:enabled].nil? || @codes[user.id][:enabled]
                event.respond('❌ The person you are trying to add has turned off adding!')
                break
            end
            event << "**You have requested to add  #{user.on(event.server).display_name}'s Wii.**\n"
            if !@codes[user.id][:wiis].nil?
                event << '<:Wii:259081748007223296> **Wiis**:'
                @codes[user.id][:wiis].each do |wii, code| #
                    code_output = code
                    event << "`#{code_output}` - #{wii}"
                end
                nil
                event << ''
                message = ''
                message << "#{event.user.on(event.server).display_name} has requested to add your Wii code!\n Their codes:\n"
                @codes[event.user.id][:wiis].each do |wii, code| #
                    code_output = code
                    message << "`#{code_output}` - #{wii}"
                end
                user.pm(message)
            else
                event << "❌ **#{user.on(event.server).display_name}** has not added any Wii codes!"
                next
            end
            if !@codes[user.id][:n3dss].nil?
                event << '<:New3DSXL:287651327763283968> **3DSs**:'
                @codes[user.id][:n3dss].each do |threeds, code| #
                    code_output = code
                    event << "`#{code_output}` - #{threeds}"
                end
                nil
                event << ''
                message = ''
                message << "#{event.user.on(event.server).display_name} has requested to add your 3DS code!\n Their codes:\n"
                @codes[event.user.id][:n3dss].each do |threeds, code| #
                    code_output = code
                    message << "`#{code_output}` - #{threeds}"
                end
                user.pm(message)
            else
                event << "❌ **#{user.on(event.server).display_name}** has not added any 3DS codes!"
                next
            end
            if !@codes[user.id][:nnids].nil?
                event << '<:NintendoNetworkID:287655797104836608> **Nintendo Network IDs**:'
                @codes[user.id][:nnids].each do |nnid, code| #
                    code_output = code
                    event << "`#{code_output}` - #{nnid}"
                end
                nil
                event << ''
                message = ''
                message << "#{event.user.on(event.server).display_name} has requested to add your Nintendo Network ID!\n Their Nintendo Network IDs:\n"
                @codes[event.user.id][:nnids].each do |nnid, code| #
                    code_output = code
                    message << "`#{code_output}` - #{nnid}"
                end
                user.pm(message)
            else
                event << "❌ **#{user.on(event.server).display_name}** has not added any Nintnedo Network IDs!"
                next
            end
            if !@codes[user.id][:switches].nil?
                event << '<:Switch:287652338791874560> **Switches**:'
                @codes[user.id][:switches].each do |switch, code| #
                    code_output = code
                    event << "`#{code_output}` - #{switch}"
                end
                nil
                event << ''
                message = ''
                message << "#{event.user.on(event.server).display_name} has requested to add your Switch code!\n Their codes:\n"
                @codes[event.user.id][:switches].each do |switch, code| #
                    code_output = code
                    message << "`#{code_output}` - #{switch}"
                end
                user.pm(message)
            else
                event << "❌ **#{user.on(event.server).display_name}** has not added any Switch codes!"
                next
            end
        end

        command(:wipecodes) do |event, *args|
            unless Helper.is_developer?(event) || Helper.is_moderator?(event) || Helper.is_admin?(event.user)
                event.respond("❌ You don't have permission for that!")
                break
            end
            user = begin
                       event.bot.parse_mention(args[0])
                   rescue
                       event.user
                   end
            user = event.user if args[0].nil?
            Codes.codes[user.id] = nil
            event << "Wiped all codes saved by `#{user.distinct}` (ID: #{user.id})"
        end

        command(:save) do |event|
            unless Helper.is_developer?(event) || Helper.is_bot_helper?(event) || Helper.is_admin?(event.user)
                event.respond("❌ You don't have permission for that!")
                break
            end
            message = event.respond 'Saving...'
            Helper.save_morpher
            Helper.save_codes
            message.edit('All saved!')
        end
    end
end
