module SerieBot
  module Rii
    require 'yaml'
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer
    class << self
        attr_accessor :codes
    end
    Helper.load_codes

    command(:code) do |event, option, *args|
      puts "Args: #{args.join(' ')}"
      if option == "add"
        if args[0]== "wii"
          args.delete_at(0)
          args = args.join(' ')
          args.sub!("| ", '')
          input = args.split(' | ')
          code = input[1]
          name = input[0]
          user_id = event.user.id
          if code.nil?
            event << "❌ Please enter a valid code!"
            next
          end
          if name.nil?
            event << "❌ Please enter a valid name!"
            next
          end
          if Rii.codes[user_id].nil?
            Rii.codes[user_id] = Hash.new
          end
          if Rii.codes[user_id][:wiis].nil?
            Rii.codes[user_id][:wiis] = Hash.new
          end
          Rii.codes[user_id][:wiis][name] = code
          Helper.save_codes
          event.respond("✅ Added a code for `#{name}`")
          puts "[DEBUG] ```#{input}\nCode as int: #{code}```"

        elsif args[0] == "game"
          args.delete_at(0)
          args = args.join(' ')
          args.sub!("| ", '')
          input = args.split(' | ')
          code = input[1]
          name = input[0]
          user_id = event.user.id
          if code.nil?
            event << "❌ Please enter a valid code!"
            next
          end
          if name.nil?
            event << "❌ Please enter a valid name!"
            next
          end
          if Rii.codes[user_id].nil?
            Rii.codes[user_id] = Hash.new
          end
          if Rii.codes[user_id][:games].nil?
            Rii.codes[user_id][:games] = Hash.new
          end
          Rii.codes[user_id][:games][name] = code rescue
          Helper.save_codes
          event.respond("✅ Added a code for `#{name}`")
          puts "[DEBUG] ```#{input}\nCode as int: #{code}```"

        else
          event << "❌ Please enter a valid argument for the option `add`."
          event << "Valid arguments: `wii`, `game`."
        end
      elsif option == "edit"
        if args[0] == "wii"
          args.delete_at(0)
          args = args.join(' ')
          args.sub!("| ", '')
          input = args.split(' | ')
          code = input[1]
          name = input[0]
          user_id = event.user.id
          if code.nil?
            event << "❌ Please enter a valid code!"
            next
          end
          if name.nil?
            event << "❌ Please enter a valid name!"
            next
          end
          if Rii.codes[user_id].nil?
            Rii.codes[user_id] = Hash.new
          end
          if Rii.codes[user_id][:wiis].nil?
            Rii.codes[user_id][:wiis] = Hash.new
          end
          if Rii.codes[user_id][:wiis][name].nil?
            event << "❌ A code for Wii `#{name}` is not registered."
            next
          else
            Rii.codes[user_id][:wiis][name] = code rescue
            Helper.save_codes
            event.respond("✅ Edited the code for `#{name}`")
          end



        elsif args[0] == "game"
          args.delete_at(0)
          args = args.join(' ')
          args.sub!("| ", '')
          input = args.split(' | ')
          code = input[1]
          name = input[0]
          user_id = event.user.id
          if code.nil?
            event << "❌ Please enter a valid code!"
            next
          end
          if name.nil?
            event << "❌ Please enter a valid name!"
            next
          end
          if Rii.codes[user_id].nil?
            Rii.codes[user_id] = Hash.new
          end
          if Rii.codes[user_id][:games].nil?
            Rii.codes[user_id][:games] = Hash.new
          end
          if Rii.codes[user_id][:games][name].nil?
            event << "❌ A code for Wii `#{name}` is not registered."
            next
          else
            Rii.codes[user_id][:games][name] = code rescue
            Helper.save_codes
            event.respond("✅ Edited the code for `#{name}`")
          end


        else
          event << "❌ Please enter a valid argument for the option `edit`."
          event << "Valid arguments: `wii`, `game`."
        end
      elsif option == "remove"
        if args[0] == "wii"
          args.delete_at(0)
          args = args.join(' ')
          args.sub!("| ", '')
          input = args.split(' | ')
          code = input[1]
          name = input[0]
          user_id = event.user.id
          if code.nil?
            event << "❌ Please enter a valid code!"
            next
          end
          if name.nil?
            event << "❌ Please enter a valid name!"
            next
          end
          if Rii.codes[user_id].nil?
            Rii.codes[user_id] = Hash.new
          end
          if Rii.codes[user_id][:wiis].nil?
            Rii.codes[user_id][:wiis] = Hash.new
          end
          if Rii.codes[user_id][:wiis][name].nil?
            event << "❌ A code for Wii `#{name}` is not registered."
            next
          else
            Rii.codes[user_id][:wiis].delete(name)
            Helper.save_codes
            event.respond("✅ Deleted the code for `#{name}`")
          end
        elsif args[0] == "game"
          args.delete_at(0)
          args = args.join(' ')
          args.sub!("| ", '')
          input = args.split(' | ')
          code = input[1]
          name = input[0]
          user_id = event.user.id
          if code.nil?
            event << "❌ Please enter a valid code!"
            next
          end
          if name.nil?
            event << "❌ Please enter a valid name!"
            next
          end
          if Rii.codes[user_id].nil?
            Rii.codes[user_id] = Hash.new
          end
          if Rii.codes[user_id][:games].nil?
            Rii.codes[user_id][:games] = Hash.new
          end
          if Rii.codes[user_id][:games][name].nil?
            event << "❌ A code for Wii `#{name}` is not registered."
            next
          else
            Rii.codes[user_id][:games].delete(name)
            Helper.save_codes
            event.respond("✅ Deleted the code for `#{name}`")
          end

        else
          event << "❌ Please enter a valid argument for the option `remove`."
          event << "Valid arguments: `wii`, `game`."
        end
      elsif option == "lookup"

        user = event.bot.parse_mention(args[0]) rescue event.user
        user = event.user if args[0].nil?
        if @codes[user.id].nil?
          event.respond("❌ **#{user.on(event.server).display_name}** has not added any friend codes!")
          next
        else
          event << "**__👤 Profile for #{user.on(event.server).display_name}__**\n"
          if !@codes[user.id][:wiis].nil?
            event << "<:Wii:259081748007223296> **Wiis**:"
            @codes[user.id][:wiis].each { |wii, code|#
            code_output = code
            event << "`#{code_output}` - #{wii}"
            }
            event << ""
          end
          if !@codes[user.id][:games].nil?
            event << "🎮 **Games**:"
            @codes[user.id][:games].each { |game, code|
              code_output = code
              event << "`#{code_output}` - #{game}"
            }
            nil
          end


        end
      elsif option == "help"
        event.respond("**__Using the bot__**

**Adding codes:**
`!code add wii | Wii Name | 1234-5678-9012-3456` (You can add multiple Wiis with different names)
`!code add game | Game Name | 1234-5678-9012`

**Editing codes**
`!code edit wii | Wii Name | 1234-5678-9012-3456`
`!code edit game | Game Name | 1234-5678-9012`

**Removing codes**
`!code remove wii | Wii Name`
`!code remove game | Game Name`

**Looking up codes**
`!code lookup @User`

**Adding a user's Wii**
`!add @user`
This will show their codes, and they will be notified you wish to add them, and send them your codes.")

      else
        event << "❌ Please enter a valid option for the command."
        event << "Valid options: `add`, `edit`, `remove`, `lookup`."
      end
    end

    command(:add, min_args: 1, max_args: 1) do |event, mention|
      user = event.bot.parse_mention(mention) rescue event.respond(":x: Enter a valid user!")
      if @codes[user.id].nil?
        event.respond("❌ **#{user.on(event.server).display_name}** has not added any friend codes!")
        next
      end
      if @codes[event.user.id].nil?
        event.respond("❌ You have not added any friend codes!")
        next
      end
      if @codes[event.user.id][:wiis].nil?
        event.respond("❌ You have not added any Wii codes!")
        next
      end
      event << "**You have requested to add  #{user.on(event.server).display_name}'s Wii.**\n"
      if !@codes[user.id][:wiis].nil?
        event << "<:Wii:259081748007223296> **Wiis**:"
        @codes[user.id][:wiis].each { |wii, code|#
        code_output = code
        event << "`#{code_output}` - #{wii}"

        }
        nil
        event << ""
        message = ""
        message << "#{event.user.on(event.server).display_name} has requested to add your Wii code!\n Their codes:\n"
        @codes[event.user.id][:wiis].each { |wii, code|#
          code_output = code
          message << "`#{code_output}` - #{wii}"
        }
        user.pm(message)
      else
        event << "❌ **#{user.on(event.server).display_name}** has not added any Wii codes!"
        next
      end
    end

    command(:wipecodes) do |event,*args|
      if !Helper.isadmin?(event.user)
          event.respond("❌ You don't have permission for that!")
          break
      end
      user = event.bot.parse_mention(args[0]) rescue event.user
      user = event.user if args[0].nil?
      Rii.codes[user.id] = nil
      event << "Wiped all codes saved by `#{user.distinct}` (ID: #{user.id})"
    end

  end
end
