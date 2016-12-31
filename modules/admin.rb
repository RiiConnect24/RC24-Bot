module SerieBot
  module Admin
    extend Discordrb::Commands::CommandContainer

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

    # Migrated from the old Commands module
    command(:owners) do |event|
        event << "This bot instance is managed/owned by the following users. Please contact them for any issues."
        Config.bot_owners.each {|x| event << "`#{event.bot.user(x).distinct}`"}
        nil
    end

    command(:about, min_args: 0, max_args: 0) do |event|
      event << "`#{event.bot.user(event.bot.profile.id).distinct}` is running a modified version of **SerieBot**: \n**https://github.com/Seriell/Serie-Bot **"
    end

    # Migrated from the old Help module
    command(:help, description: 'Display a list of commands.') do |event|
        event.respond(Helper.get_help(event.user))
    end
  end
end
