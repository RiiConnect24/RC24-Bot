module SerieBot
  module Admin
    extend Discordrb::Commands::CommandContainer

    command(:message, description: "Send the result of an eval in PM. Admin only.",usage: '&message code') do |event, *pmwords|
      break if !Helper.isadmin?(event.user)

      puts pmwords
      message = pmwords.join(" ")
      puts message
      event.user.pm(eval message)
      event.respond("✅ PMed you the eval output 😉")
    end

    command(:setavatar) do |event, *url|
      if !Helper.isadmin?(event.user)
          event.respond("❌ You don't have permission for that!")
          break
      end
      url = url.join(' ')
      file = Helper.download_file(url, 'tmp')
      event.bot.profile.avatar = File.open(file)
      event.respond("✅ Avatar should be updated!")

    end

    command(:game, description: "Set the \"Playing\" status. Admin only.") do |event, *game|
    if !Helper.isadmin?(event.user)
        event.respond("❌ You don't have permission for that!")
        break
      end
      event.bot.game = game.join(" ")
      event.respond("✅ Game set to `#{game.join(" ")}`!")
    end

    command(:username, description: "Set the Bot's username. Admin only.",  min_args: 1) do |event, *name|
    if !Helper.isadmin?(event.user)
        event.respond("❌ You don't have permission for that!")
        break
      end
      username = name.join(' ')
      event.bot.profile.name = username rescue event.respond("An error has occured!")
      event.respond("Username set to `#{username}`!")
    end

    command(:ignore, description: "Temporarily ignore a given user", min_args: 1, max_args: 1 ) do |event, mention|
      event.channel.start_typing
      if !Helper.isadmin?(event.user)
          event.respond("❌ You don't have permission for that!")
          break
      end
      user = event.bot.parse_mention(mention) rescue event.respond("❌ `#{mention}` is not a valid user!")
      event.bot.ignore_user(user) rescue event.respond("❌ `#{mention}` is not a valid user!")
      event.respond("✅ #{user.mention} has been temporarily ignored!")
    end

    command(:unignore, description: "Unignores a given user", min_args: 1, max_args: 1 ) do |event, mention|
      event.channel.start_typing
      if !Helper.isadmin?(event.user)
          event.respond("❌ You don't have permission for that!")
          break
      end
      user = event.bot.parse_mention(mention) rescue event.respond("❌ `#{mention}` is not a valid user!")
      event.bot.unignore_user(user) rescue event.respond("❌ `#{mention}` is not a valid user!")
      event.respond("✅ #{user.mention} has been removed from the ignore list!")
    end


    command(:status, description: "Set the bot as idle or dnd or invisible status. Admin only.",min_args: 1, max_args: 1 ) do |event, status|
      if !Helper.isadmin?(event.user)
          event.respond("❌ You don't have permission for that!")
          break
      end
      if status == "idle"
        event.bot.idle
        event.respond("✅ Status set to **Idle**!")
      elsif status == "dnd"
        event.bot.dnd
        event.respond("✅ Status set to **Do No Disturb**!")
      elsif status == "online"
        event.bot.online
        event.respond("✅ Status set to **Online**!")
      elsif status == "invisible" or status == "offline"
        event.bot.invisible
        event.respond("✅ Status set to **Invisible**!")
      else
        event.respond("Enter a valid argument!")
      end
    end

    command(:owner, description: "Find the owner of a shared server.",usage: '&message code') do |event, id|
      id = event.server.id if id.nil?
      owner = event.bot.server(id).owner
      event.respond("👤 Owner of server `#{event.bot.server(id).name}` is **#{owner.distinct}** | ID: `#{owner.id}`")
    end

    command(:shutdown, description: "Shuts down the bot. Admin only.",usage: '&shutdown') do |event|
      puts "#{event.author.distinct}: \`#{event.message.content}\`"
      if !Helper.isadmin?(event.user)
        event.respond("❌ You don't have permission for that!")
        break
      end
        message = event.respond "Saving and exiting... "
        Helper.save_codes
        event.bot.invisible
        message.edit("All saved. Goodbye!")
        Helper.quit
    end

    command(:eval, description: "Evaluate a Ruby command. Admin only.",usage: '&eval code') do |event, *code|
      if !Helper.isadmin?(event.user)
        event.respond("❌ You don't have permission for that!")
        break
      end
      eval code.join(' ')
    end

    command(:spam, required_permissions: [:administrator],description: "Spam a message. Admin only.",usage: '&spam num text') do |event, num, *text|
      puts "#{event.author.distinct}: \`#{event.message.content}\`"
      if num.nil?
        event.respond("No argument specicied. Enter a valid positive number!")
        break
      end


      if !/\A\d+\z/.match(num)
        event.respond("`#{num}` is not a valid positive number!")
        break
      end

      num = num.to_i

      while num > 0
        event.respond("#{text.join(" ")}")
        puts "#{text.join(" ")}"
        num -= 1
      end
    end
    command(:bash, description: "Evaluate a Bash command. Admin only. Use with care.",usage: '&bash code') do |event, *code|
      if !Helper.isadmin?(event.user)
        event.respond("❌ You don't have permission for that!")
        break
      end
      bashcode = code.join(' ')
      # Capture all output, including STDERR.
      toBeRun = "#{bashcode} 2>&1"
      result = %x( #{toBeRun} )
        if result.nil? or result == "" or result == " " or result == "\n"
          event << "✅ Done! (No output)"
        else
          event << "Output: ```\n#{result}```"
        end
    end
    command(:upload, description: "Upload a file to Discord. Admin only.",usage: '&upload filename') do |event, *file|
      if !Helper.isadmin?(event.user)
        event << "❌ You don't have permission for that!"
        break
      end
      filename = file.join("")
      event.channel.send_file File.new([filename].sample)
    end


    command(:rehost) do |event, *url|
      url = url.join(' ')
      file = Helper.download_file(url, 'tmp')
      Helper.upload_file(event.channel, file)
      event.message.delete
    end

    command(:save) do |event, *url|
      if !Helper.isadmin?(event.user)
        event.respond("❌ You don't have permission for that!")
        break
      end
        message = event.respond "Saving and exiting... "
        Helper.save_codes
        message.edit("All saved!")
    end

    command(:dump, description: "Dumps a selected channel. Admin only.",usage: '&dump [id]') do |event, channel_id|
      if !Helper.isadmin?(event.user)
        event << "❌ You don't have permission for that!"
        break
      end
      channel_id = event.channel.id if channel_id.nil?
      channel = event.bot.channel(channel_id) rescue event.respond("❌ Enter a valid channel id!")

      output_filename = Helper.dump_channel(channel, event.channel, Config.dump_dir , event.message.timestamp)
      event.channel.send_file File.new([output_filename].sample)
    end

    command(:prune, required_permissions: [:manage_messages],max_args: 1) do |event, num|
      begin
        num = 50 if num.nil?
        count = 0
        event.channel.history(num).each { |x|
          if x.author.id == event.bot.profile.id
            x.delete
            count = count + 1
          end
        }
        message = event.respond("✅ Pruned #{count} messages!")
        sleep(10)
        message.delete
        event.message.delete
      rescue Discordrb::Errors::NoPermission
        event.channel.send_message("❌ I don't have permission to delete messages!")
        puts "The bot does not have the delete message permission!"
      end
     end


         command(:forceprune,max_args: 1) do |event, num|
           if !Helper.isadmin?(event.user)
             event.respond("❌ You don't have permission for that!")
             break
           end
           begin
             num = 50 if num.nil?
             count = 0
             event.channel.history(num).each { |x|
               if x.author.id == event.bot.profile.id
                 x.delete
                 count = count + 1
               end
             }
             message = event.respond("✅ Pruned #{count} messages!")
             sleep(10)
             message.delete
             event.message.delete
           rescue Discordrb::Errors::NoPermission
             event.channel.send_message("❌ I don't have permission to delete messages!")
             puts "The bot does not have the delete message permission!"
           end
          end

     command(:pruneuser,required_permissions: [:manage_messages], max_args: 1) do |event, user, num|
       begin
         user = event.bot.parse_mention(user)
         num = 50 if num.nil?
         count = 0
         event.channel.history(num).each { |x|
            if x.author.id == user.id
              x.delete
              count = count + 1
            end
          }
          message = event.respond("✅ Pruned #{count} messages!")
          sleep(10)
          message.delete
          event.message.delete
        rescue Discordrb::Errors::NoPermission
          event.channel.send_message("❌ I don't have permission to delete messages!")
          puts "The bot does not have the delete message permission!"
        end
      end
  end
end
